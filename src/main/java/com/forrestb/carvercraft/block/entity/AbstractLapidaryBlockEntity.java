package com.forrestb.carvercraft.block.entity;

import com.forrestb.carvercraft.block.LapidaryMachineBlock;
import com.forrestb.carvercraft.energy.MachineEnergyStorage;
import com.forrestb.carvercraft.menu.LapidaryMenu;
import com.forrestb.carvercraft.recipe.LapidaryRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Shared guts of every lapidary machine: four independent lanes, each with an input
 * slot, its own clock, and an output slot. Lanes never batch together.
 *
 * Subclasses supply their recipe type, their hardness rating, and how much FE (if any)
 * a running lane costs per tick. Everything else — ticking, recipe caching, energy,
 * saving, the menu — lives here so a new machine is a few dozen lines.
 */
public abstract class AbstractLapidaryBlockEntity extends BlockEntity implements MenuProvider {
    public static final int LANES = 4;
    public static final int OUTPUT_OFFSET = LANES;
    // 0-3 inputs, 4-7 outputs, 8 the shared consumable slot (grit, polish).
    // Machines that declare no consumable never expose slot 8 in their menu.
    public static final int CONSUMABLE_SLOT = LANES * 2;
    public static final int SLOT_COUNT = LANES * 2 + 1;

    // ContainerData layout: [0..3] lane clocks, [4..7] lane totals, [8] energy, [9] capacity.
    public static final int DATA_ENERGY = LANES * 2;
    public static final int DATA_CAPACITY = LANES * 2 + 1;
    public static final int DATA_COUNT = LANES * 2 + 2;

    protected final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            if (slot < OUTPUT_OFFSET) {
                cachedInputItem[slot] = null;
            }
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == CONSUMABLE_SLOT) {
                Item required = consumableItem();
                return required != null && stack.is(required);
            }
            if (slot >= OUTPUT_OFFSET) {
                return false; // outputs are take-only
            }
            if (level == null) {
                return true;
            }
            return findRecipe(level, stack).isPresent();
        }
    };

    protected final int[] progress = new int[LANES];
    @Nullable
    protected final MachineEnergyStorage energy;

    // Per-lane resolved recipe, recomputed only when that lane's input item changes.
    private final Item[] cachedInputItem = new Item[LANES];
    private final ItemStack[] cachedResult = new ItemStack[LANES];
    private final int[] cachedTime = new int[LANES];

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            if (index >= 0 && index < LANES) {
                return progress[index];
            }
            if (index >= LANES && index < LANES * 2) {
                int lane = index - LANES;
                return cachedTime[lane] > 0 ? cachedTime[lane] : LapidaryRecipe.DEFAULT_TIME;
            }
            if (index == DATA_ENERGY) {
                return energy == null ? 0 : energy.getEnergyStored();
            }
            if (index == DATA_CAPACITY) {
                return energy == null ? 0 : energy.getCapacityRaw();
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index >= 0 && index < LANES) {
                progress[index] = value;
            } else if (index >= LANES && index < LANES * 2) {
                cachedTime[index - LANES] = value;
            } else if (index == DATA_ENERGY && energy != null) {
                energy.setEnergy(value);
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    protected AbstractLapidaryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.energy = energyCapacity() > 0
                ? new MachineEnergyStorage(energyCapacity(), Math.max(1, energyPerTick() * 20))
                : null;
    }

    // --- Subclass contract -------------------------------------------------

    protected abstract RecipeType<? extends LapidaryRecipe> recipeType();

    /** Highest Mohs hardness this machine can work. */
    public abstract float maxHardness();

    /** FE burned per tick per running lane. 0 means the machine is passive. */
    public abstract int energyPerTick();

    /** Total FE buffer. 0 means no energy handling at all. */
    public abstract int energyCapacity();

    public abstract MenuType<LapidaryMenu> menuType();

    public abstract ResourceLocation guiTexture();

    protected abstract Component defaultName();

    /**
     * The item one lane burns through per cycle (grit for the tumbler, polish for
     * the cabbing machine), or null for machines whose process consumes nothing.
     * Consumption happens when a lane's clock leaves zero — the grit goes in the
     * barrel at the start of the tumble, not at the end.
     */
    @Nullable
    protected Item consumableItem() {
        return null;
    }

    public final boolean usesConsumable() {
        return consumableItem() != null;
    }

    private boolean hasConsumable() {
        Item required = consumableItem();
        return required != null && inventory.getStackInSlot(CONSUMABLE_SLOT).is(required);
    }

    protected SoundEvent workingSound() {
        return SoundEvents.GRAVEL_HIT;
    }

    protected SoundEvent finishedSound() {
        return SoundEvents.AMETHYST_BLOCK_CHIME;
    }

    // --- Machinery ---------------------------------------------------------

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Nullable
    public MachineEnergyStorage getEnergyStorage() {
        return energy;
    }

    /** Ask the recipe manager what this machine makes of the stone, if anything. */
    public Optional<? extends RecipeHolder<? extends LapidaryRecipe>> findRecipe(Level level, ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        Optional<? extends RecipeHolder<? extends LapidaryRecipe>> found =
                level.getRecipeManager().getRecipeFor(castType(), new SingleRecipeInput(stack), level);
        // A machine refuses stone harder than it is rated for.
        return found.filter(holder -> holder.value().hardness() <= maxHardness() + 1.0e-4F);
    }

    @SuppressWarnings("unchecked")
    private RecipeType<LapidaryRecipe> castType() {
        return (RecipeType<LapidaryRecipe>) recipeType();
    }

    private boolean resolveLane(Level level, int lane) {
        ItemStack input = inventory.getStackInSlot(lane);
        if (input.isEmpty()) {
            cachedInputItem[lane] = null;
            cachedResult[lane] = null;
            cachedTime[lane] = 0;
            return false;
        }
        if (cachedInputItem[lane] == input.getItem() && cachedResult[lane] != null) {
            return true;
        }
        Optional<? extends RecipeHolder<? extends LapidaryRecipe>> found = findRecipe(level, input);
        if (found.isEmpty()) {
            cachedInputItem[lane] = input.getItem();
            cachedResult[lane] = null;
            cachedTime[lane] = 0;
            return false;
        }
        LapidaryRecipe recipe = found.get().value();
        cachedInputItem[lane] = input.getItem();
        cachedResult[lane] = recipe.assemble(new SingleRecipeInput(input), level.registryAccess());
        cachedTime[lane] = Math.max(1, recipe.time());
        return true;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AbstractLapidaryBlockEntity machine) {
        boolean anyRunning = false;
        boolean dirty = false;

        for (int lane = 0; lane < LANES; lane++) {
            if (machine.canRun(level, lane)) {
                if (machine.energy != null && !machine.energy.consume(machine.energyPerTick())) {
                    // Powered machine with an empty buffer: the lane holds where it is.
                    continue;
                }
                if (machine.progress[lane] == 0 && machine.usesConsumable()) {
                    // The cycle begins: charge the barrel. canRun just verified supply.
                    ItemStack supply = machine.inventory.getStackInSlot(CONSUMABLE_SLOT);
                    supply.shrink(1);
                    machine.inventory.setStackInSlot(CONSUMABLE_SLOT,
                            supply.isEmpty() ? ItemStack.EMPTY : supply);
                }
                anyRunning = true;
                machine.progress[lane]++;
                if (machine.progress[lane] >= machine.cachedTime[lane]) {
                    machine.finishLane(level, pos, lane);
                }
                dirty = true;
            } else if (machine.progress[lane] != 0) {
                machine.progress[lane] = 0;
                dirty = true;
            }
        }

        if (dirty) {
            machine.setChanged();
        }

        if (anyRunning && level.getGameTime() % 30 == 0) {
            level.playSound(null, pos, machine.workingSound(), SoundSource.BLOCKS, 0.35F, 0.55F);
        }

        if (state.getValue(LapidaryMachineBlock.RUNNING) != anyRunning) {
            level.setBlock(pos, state.setValue(LapidaryMachineBlock.RUNNING, anyRunning), Block.UPDATE_ALL);
        }
    }

    private boolean canRun(Level level, int lane) {
        if (!resolveLane(level, lane)) {
            return false;
        }
        // A lane about to start needs supply in the consumable slot; a lane already
        // past zero has its grit in the barrel and runs the cycle out.
        if (progress[lane] == 0 && usesConsumable() && !hasConsumable()) {
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
        level.playSound(null, pos, finishedSound(), SoundSource.BLOCKS, 0.6F, 1.0F);
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
        return defaultName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new LapidaryMenu(menuType(), containerId, playerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putIntArray("LaneProgress", progress.clone());
        if (energy != null) {
            tag.putInt("Energy", energy.getEnergyStored());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        // Machines saved by an older build could size the handler differently (8 slots
        // before the consumable slot existed); force the current size so lane indexing
        // stays in bounds. Growing 8 -> 9 just leaves the consumable slot empty.
        if (inventory.getSlots() != SLOT_COUNT) {
            inventory.setSize(SLOT_COUNT);
        }
        int[] saved = tag.getIntArray("LaneProgress");
        for (int lane = 0; lane < LANES; lane++) {
            progress[lane] = lane < saved.length ? saved[lane] : 0;
            cachedInputItem[lane] = null;
        }
        if (energy != null) {
            energy.setEnergy(tag.getInt("Energy"));
        }
    }
}
