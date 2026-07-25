package com.forrestb.carvercraft.block.entity;

import com.forrestb.carvercraft.block.RockTumblerBlock;
import com.forrestb.carvercraft.menu.RockTumblerMenu;
import com.forrestb.carvercraft.recipe.TumblingRecipe;
import com.forrestb.carvercraft.registry.ModBlockEntities;
import com.forrestb.carvercraft.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Four independent tumbling lanes. Each lane is furnace-shaped: input slot,
 * its own progress clock, output slot. Lanes do not batch together.
 *
 * What a lane can polish, and how long it takes, is data now — see
 * data/carvercraft/recipe/tumbling/. Adding a stone is a JSON file.
 *
 * Passive and unpowered by design: real tumbling takes about six weeks per
 * load, and the slow clock is the flavor.
 */
public class RockTumblerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int LANES = 4;
    public static final int SLOT_COUNT = LANES * 2;      // 0-3 inputs, 4-7 outputs
    public static final int OUTPUT_OFFSET = LANES;

    private final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            if (slot < OUTPUT_OFFSET) {
                // Input changed: drop the cached recipe for that lane.
                cachedInputItem[slot] = null;
            }
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot >= OUTPUT_OFFSET) {
                return false; // outputs are take-only
            }
            if (level == null) {
                return true; // can't check yet; let the recipe gate it on tick
            }
            return findRecipe(level, stack).isPresent();
        }
    };

    private final int[] progress = new int[LANES];

    // Per-lane resolved recipe, recomputed only when that lane's input item changes.
    // A RecipeManager lookup every tick, per lane, per tumbler would be real cost.
    private final Item[] cachedInputItem = new Item[LANES];
    private final ItemStack[] cachedResult = new ItemStack[LANES];
    private final int[] cachedTime = new int[LANES];

    // Indices 0..LANES-1 are the lane clocks; LANES..2*LANES-1 are each lane's
    // total time, which now varies per recipe.
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            if (index >= 0 && index < LANES) {
                return progress[index];
            }
            if (index >= LANES && index < LANES * 2) {
                int lane = index - LANES;
                return cachedTime[lane] > 0 ? cachedTime[lane] : TumblingRecipe.DEFAULT_TIME;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index >= 0 && index < LANES) {
                progress[index] = value;
            } else if (index >= LANES && index < LANES * 2) {
                cachedTime[index - LANES] = value;
            }
        }

        @Override
        public int getCount() {
            return LANES * 2;
        }
    };

    public RockTumblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROCK_TUMBLER.get(), pos, state);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    /** Ask the recipe manager what this stone tumbles into, if anything. */
    public static Optional<RecipeHolder<TumblingRecipe>> findRecipe(Level level, ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.TUMBLING_TYPE.get(), new SingleRecipeInput(stack), level);
    }

    /** Resolve and cache the recipe for one lane. Returns false if there's nothing to do. */
    private boolean resolveLane(Level level, int lane) {
        ItemStack input = inventory.getStackInSlot(lane);
        if (input.isEmpty()) {
            cachedInputItem[lane] = null;
            cachedResult[lane] = null;
            cachedTime[lane] = 0;
            return false;
        }
        if (cachedInputItem[lane] == input.getItem() && cachedResult[lane] != null) {
            return true; // cache hit
        }
        Optional<RecipeHolder<TumblingRecipe>> found = findRecipe(level, input);
        if (found.isEmpty()) {
            cachedInputItem[lane] = input.getItem();
            cachedResult[lane] = null;
            cachedTime[lane] = 0;
            return false;
        }
        TumblingRecipe recipe = found.get().value();
        cachedInputItem[lane] = input.getItem();
        cachedResult[lane] = recipe.assemble(new SingleRecipeInput(input), level.registryAccess());
        cachedTime[lane] = Math.max(1, recipe.time());
        return true;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RockTumblerBlockEntity tumbler) {
        boolean anyRunning = false;
        boolean dirty = false;

        for (int lane = 0; lane < LANES; lane++) {
            if (tumbler.canRun(level, lane)) {
                anyRunning = true;
                tumbler.progress[lane]++;
                if (tumbler.progress[lane] >= tumbler.cachedTime[lane]) {
                    tumbler.finishLane(level, pos, lane);
                }
                dirty = true;
            } else if (tumbler.progress[lane] != 0) {
                tumbler.progress[lane] = 0;
                dirty = true;
            }
        }

        if (dirty) {
            tumbler.setChanged();
        }

        if (anyRunning && level.getGameTime() % 30 == 0) {
            level.playSound(null, pos, SoundEvents.GRAVEL_HIT, SoundSource.BLOCKS, 0.35F, 0.55F);
        }

        if (state.getValue(RockTumblerBlock.RUNNING) != anyRunning) {
            level.setBlock(pos, state.setValue(RockTumblerBlock.RUNNING, anyRunning), Block.UPDATE_ALL);
        }
    }

    /** A lane runs when it has a matching recipe and its output can accept the result. */
    private boolean canRun(Level level, int lane) {
        if (!resolveLane(level, lane)) {
            return false;
        }
        ItemStack result = cachedResult[lane];
        ItemStack output = inventory.getStackInSlot(OUTPUT_OFFSET + lane);
        if (output.isEmpty()) {
            return true;
        }
        return ItemStack.isSameItemSameComponents(output, result)
                && output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    private void finishLane(Level level, BlockPos pos, int lane) {
        ItemStack result = cachedResult[lane];
        if (result == null) {
            progress[lane] = 0;
            return;
        }

        int outSlot = OUTPUT_OFFSET + lane;
        ItemStack output = inventory.getStackInSlot(outSlot);
        if (output.isEmpty()) {
            inventory.setStackInSlot(outSlot, result.copy());
        } else {
            output.grow(result.getCount());
            inventory.setStackInSlot(outSlot, output);
        }

        ItemStack input = inventory.getStackInSlot(lane);
        input.shrink(1);
        inventory.setStackInSlot(lane, input.isEmpty() ? ItemStack.EMPTY : input);

        progress[lane] = 0;
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.6F, 1.0F);
    }

    /** Dump contents into the world (used when the block is broken). */
    public void dropContents() {
        if (level == null) {
            return;
        }
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
                inventory.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.carvercraft.rock_tumbler");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new RockTumblerMenu(containerId, playerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putIntArray("LaneProgress", progress.clone());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        // Tumblers saved by the old 4-slot version would shrink the handler on load;
        // force it back to the current size so lane indexing stays in bounds.
        if (inventory.getSlots() != SLOT_COUNT) {
            inventory.setSize(SLOT_COUNT);
        }
        int[] saved = tag.getIntArray("LaneProgress");
        for (int lane = 0; lane < LANES; lane++) {
            progress[lane] = lane < saved.length ? saved[lane] : 0;
            cachedInputItem[lane] = null; // force a fresh recipe lookup after load
        }
    }
}
