package com.forrestb.carvercraft.block.entity;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.block.AlloyerBlock;
import com.forrestb.carvercraft.energy.MachineEnergyStorage;
import com.forrestb.carvercraft.menu.AlloyerMenu;
import com.forrestb.carvercraft.recipe.AlloyingRecipe;
import com.forrestb.carvercraft.recipe.AlloyingRecipeInput;
import com.forrestb.carvercraft.registry.ModBlockEntities;
import com.forrestb.carvercraft.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The crucible. Two metals in at a fixed ratio, one alloy out. Unlike the lapidary
 * machines this is not lane-based — an alloy is one melt, so there is one clock.
 *
 * The lapidary framework stays untouched: a 2-in-1-out ratio recipe doesn't fit a
 * lane (1 in, 1 out, count of one), and bending the lane model around counts would
 * complicate four working machines to serve one new one.
 */
public class AlloyerBlockEntity extends BlockEntity implements MenuProvider {

    public static final int SLOT_FIRST = 0;
    public static final int SLOT_SECOND = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int SLOT_COUNT = 3;

    public static final int ENERGY_PER_TICK = 80;
    public static final int ENERGY_CAPACITY = 80_000;

    // ContainerData layout: [0] progress, [1] total time, [2] energy, [3] capacity.
    public static final int DATA_PROGRESS = 0;
    public static final int DATA_TIME = 1;
    public static final int DATA_ENERGY = 2;
    public static final int DATA_CAPACITY = 3;
    public static final int DATA_COUNT = 4;

    public static final ResourceLocation GUI =
            ResourceLocation.fromNamespaceAndPath(CarverCraft.MODID, "textures/gui/alloyer.png");

    private final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            if (slot != SLOT_OUTPUT) {
                cachedFirst = null;
                cachedSecond = null;
            }
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == SLOT_OUTPUT) {
                return false; // take-only
            }
            if (level == null) {
                return true;
            }
            return level.getRecipeManager().getAllRecipesFor(ModRecipes.ALLOYING_TYPE.get()).stream()
                    .anyMatch(holder -> holder.value().isIngredient(stack));
        }
    };

    private final MachineEnergyStorage energy =
            new MachineEnergyStorage(ENERGY_CAPACITY, ENERGY_PER_TICK * 20);

    private int progress;

    // Resolved recipe, recomputed only when an input slot's item changes.
    @Nullable
    private Item cachedFirst;
    @Nullable
    private Item cachedSecond;
    @Nullable
    private AlloyingRecipe cachedRecipe;
    private int cachedTime;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_PROGRESS -> progress;
                case DATA_TIME -> cachedTime > 0 ? cachedTime : AlloyingRecipe.DEFAULT_TIME;
                case DATA_ENERGY -> energy.getEnergyStored();
                case DATA_CAPACITY -> energy.getCapacityRaw();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case DATA_PROGRESS -> progress = value;
                case DATA_TIME -> cachedTime = value;
                case DATA_ENERGY -> energy.setEnergy(value);
                default -> { }
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public AlloyerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALLOYER.get(), pos, state);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public MachineEnergyStorage getEnergyStorage() {
        return energy;
    }

    private boolean resolveRecipe(Level level) {
        ItemStack first = inventory.getStackInSlot(SLOT_FIRST);
        ItemStack second = inventory.getStackInSlot(SLOT_SECOND);
        if (first.isEmpty() || second.isEmpty()) {
            cachedFirst = null;
            cachedSecond = null;
            cachedRecipe = null;
            cachedTime = 0;
            return false;
        }
        if (cachedFirst == first.getItem() && cachedSecond == second.getItem()) {
            return cachedRecipe != null;
        }
        cachedFirst = first.getItem();
        cachedSecond = second.getItem();
        Optional<RecipeHolder<AlloyingRecipe>> found = level.getRecipeManager()
                .getRecipeFor(ModRecipes.ALLOYING_TYPE.get(), new AlloyingRecipeInput(first, second), level);
        if (found.isEmpty()) {
            cachedRecipe = null;
            cachedTime = 0;
            return false;
        }
        cachedRecipe = found.get().value();
        cachedTime = Math.max(1, cachedRecipe.time());
        return true;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AlloyerBlockEntity machine) {
        boolean running = false;
        boolean dirty = false;

        if (machine.canRun(level)) {
            if (machine.energy.consume(ENERGY_PER_TICK)) {
                running = true;
                machine.progress++;
                if (machine.progress >= machine.cachedTime) {
                    machine.finishMelt(level, pos);
                }
                dirty = true;
            }
            // Empty buffer: the melt holds where it is, like the powered lapidary lanes.
        } else if (machine.progress != 0) {
            machine.progress = 0;
            dirty = true;
        }

        if (dirty) {
            machine.setChanged();
        }

        if (running && level.getGameTime() % 30 == 0) {
            level.playSound(null, pos, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.4F, 0.9F);
        }

        if (state.getValue(AlloyerBlock.RUNNING) != running) {
            level.setBlock(pos, state.setValue(AlloyerBlock.RUNNING, running), Block.UPDATE_ALL);
        }
    }

    private boolean canRun(Level level) {
        if (!resolveRecipe(level) || cachedRecipe == null) {
            return false;
        }
        ItemStack first = inventory.getStackInSlot(SLOT_FIRST);
        ItemStack second = inventory.getStackInSlot(SLOT_SECOND);
        if (!cachedRecipe.matchesWithCounts(first, second)) {
            return false;
        }
        ItemStack result = cachedRecipe.result();
        ItemStack output = inventory.getStackInSlot(SLOT_OUTPUT);
        if (output.isEmpty()) {
            return true;
        }
        return ItemStack.isSameItemSameComponents(output, result)
                && output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    private void finishMelt(Level level, BlockPos pos) {
        if (cachedRecipe == null) {
            progress = 0;
            return;
        }
        ItemStack first = inventory.getStackInSlot(SLOT_FIRST);
        ItemStack second = inventory.getStackInSlot(SLOT_SECOND);
        int fromFirst = cachedRecipe.consumeFromFirst(first, second);
        int fromSecond = cachedRecipe.consumeFromSecond(first, second);

        ItemStack result = cachedRecipe.result();
        ItemStack output = inventory.getStackInSlot(SLOT_OUTPUT);
        if (output.isEmpty()) {
            inventory.setStackInSlot(SLOT_OUTPUT, result.copy());
        } else {
            output.grow(result.getCount());
            inventory.setStackInSlot(SLOT_OUTPUT, output);
        }

        first.shrink(fromFirst);
        second.shrink(fromSecond);
        inventory.setStackInSlot(SLOT_FIRST, first.isEmpty() ? ItemStack.EMPTY : first);
        inventory.setStackInSlot(SLOT_SECOND, second.isEmpty() ? ItemStack.EMPTY : second);

        progress = 0;
        // The quench hiss.
        level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.1F);
    }

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
        return Component.translatable("block.carvercraft.alloyer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AlloyerMenu(containerId, playerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Progress", progress);
        tag.putInt("Energy", energy.getEnergyStored());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        // Same guard as the lapidary machines: never let old NBT shrink the handler.
        if (inventory.getSlots() != SLOT_COUNT) {
            inventory.setSize(SLOT_COUNT);
        }
        progress = tag.getInt("Progress");
        energy.setEnergy(tag.getInt("Energy"));
        cachedFirst = null;
        cachedSecond = null;
    }
}
