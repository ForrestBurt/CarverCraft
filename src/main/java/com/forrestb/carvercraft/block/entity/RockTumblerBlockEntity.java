package com.forrestb.carvercraft.block.entity;

import com.forrestb.carvercraft.block.RockTumblerBlock;
import com.forrestb.carvercraft.menu.RockTumblerMenu;
import com.forrestb.carvercraft.registry.ModBlockEntities;
import com.forrestb.carvercraft.registry.ModItems;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * Four independent tumbling lanes. Each lane is furnace-shaped: input slot,
 * its own progress clock, output slot. Lanes do not batch together — drop a
 * gem in lane 2 and it starts its own timer without touching lane 1.
 *
 * Passive and unpowered by design: real tumbling takes about six weeks per
 * load, and the slow clock is the flavor.
 *
 * v0.3 TODO: convert the hardcoded rough->polished mapping into a JSON recipe type.
 */
public class RockTumblerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int LANES = 4;
    public static final int SLOT_COUNT = LANES * 2;      // 0-3 inputs, 4-7 outputs
    public static final int OUTPUT_OFFSET = LANES;
    public static final int TICKS_PER_GEM = 20 * 30;     // TESTING VALUE: 30s per gem

    private final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            // Only rough gems, and only in the input lanes.
            return slot < OUTPUT_OFFSET && isTumblable(stack);
        }
    };

    private final int[] progress = new int[LANES];

    // Syncs the four lane clocks (plus the shared batch length) to the open screen.
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            if (index >= 0 && index < LANES) {
                return progress[index];
            }
            if (index == LANES) {
                return TICKS_PER_GEM;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index >= 0 && index < LANES) {
                progress[index] = value;
            }
        }

        @Override
        public int getCount() {
            return LANES + 1;
        }
    };

    public RockTumblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROCK_TUMBLER.get(), pos, state);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Nullable
    public static Item polishedResult(Item rough) {
        if (rough == ModItems.ROUGH_JASPER.get()) return ModItems.POLISHED_JASPER.get();
        if (rough == ModItems.ROUGH_GARNET.get()) return ModItems.POLISHED_GARNET.get();
        if (rough == ModItems.ROUGH_AGATE.get()) return ModItems.POLISHED_AGATE.get();
        return null;
    }

    public static boolean isTumblable(ItemStack stack) {
        return !stack.isEmpty() && polishedResult(stack.getItem()) != null;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RockTumblerBlockEntity tumbler) {
        boolean anyRunning = false;
        boolean dirty = false;

        for (int lane = 0; lane < LANES; lane++) {
            if (tumbler.canRun(lane)) {
                anyRunning = true;
                tumbler.progress[lane]++;
                if (tumbler.progress[lane] >= TICKS_PER_GEM) {
                    tumbler.finishLane(level, pos, lane);
                }
                dirty = true;
            } else if (tumbler.progress[lane] != 0) {
                // Input pulled out or output backed up: reset that lane only.
                tumbler.progress[lane] = 0;
                dirty = true;
            }
        }

        if (dirty) {
            tumbler.setChanged();
        }

        // The drum grumbles while anything is turning.
        if (anyRunning && level.getGameTime() % 30 == 0) {
            level.playSound(null, pos, SoundEvents.GRAVEL_HIT, SoundSource.BLOCKS, 0.35F, 0.55F);
        }

        // Keep the client-visible blockstate in step so particles fire.
        if (state.getValue(RockTumblerBlock.RUNNING) != anyRunning) {
            level.setBlock(pos, state.setValue(RockTumblerBlock.RUNNING, anyRunning), Block.UPDATE_ALL);
        }
    }

    /** A lane runs when it holds a rough gem and its output slot can take the result. */
    private boolean canRun(int lane) {
        ItemStack input = inventory.getStackInSlot(lane);
        Item result = polishedResult(input.getItem());
        if (result == null) {
            return false;
        }
        ItemStack output = inventory.getStackInSlot(OUTPUT_OFFSET + lane);
        if (output.isEmpty()) {
            return true;
        }
        return output.is(result) && output.getCount() < output.getMaxStackSize();
    }

    private void finishLane(Level level, BlockPos pos, int lane) {
        ItemStack input = inventory.getStackInSlot(lane);
        Item result = polishedResult(input.getItem());
        if (result == null) {
            progress[lane] = 0;
            return;
        }

        int outSlot = OUTPUT_OFFSET + lane;
        ItemStack output = inventory.getStackInSlot(outSlot);
        if (output.isEmpty()) {
            inventory.setStackInSlot(outSlot, new ItemStack(result, 1));
        } else {
            output.grow(1);
            inventory.setStackInSlot(outSlot, output);
        }

        input.shrink(1);
        inventory.setStackInSlot(lane, input.isEmpty() ? ItemStack.EMPTY : input);

        progress[lane] = 0;
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.6F, 1.0F);
    }

    public boolean isAnyLaneRunning() {
        for (int lane = 0; lane < LANES; lane++) {
            if (canRun(lane)) {
                return true;
            }
        }
        return false;
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
        }
    }
}
