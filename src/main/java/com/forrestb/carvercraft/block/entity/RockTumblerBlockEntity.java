package com.forrestb.carvercraft.block.entity;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * Passive, unpowered tumbling: load rough gems, wait, collect polished gems.
 * Real tumbling takes about six weeks per load; we compress that to a few minutes.
 * v0.3 TODO: convert the hardcoded rough->polished mapping into a JSON recipe type.
 */
public class RockTumblerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int CAPACITY = 4;
    public static final int TICKS_PER_BATCH = 20 * 60; // 60s per load — snappy for testing

    private final ItemStackHandler inventory = new ItemStackHandler(CAPACITY) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isTumblable(stack);
        }
    };

    private int progress = 0;

    // Bridges the two integers the screen needs across the client/server boundary.
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> RockTumblerBlockEntity.this.progress;
                case 1 -> TICKS_PER_BATCH;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                RockTumblerBlockEntity.this.progress = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
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
        if (!tumbler.hasRoughGems()) {
            if (tumbler.progress != 0) {
                tumbler.progress = 0;
                tumbler.setChanged();
            }
            return;
        }
        tumbler.progress++;
        if (tumbler.progress % 20 == 0) {
            tumbler.setChanged(); // periodic save; keeps the data slot moving for observers
        }
        // The drum grumbles as it turns.
        if (tumbler.progress % 30 == 0) {
            level.playSound(null, pos, SoundEvents.GRAVEL_HIT, SoundSource.BLOCKS, 0.35F, 0.55F);
        }
        if (tumbler.progress >= TICKS_PER_BATCH) {
            tumbler.finishBatch(level, pos);
        }
    }

    private boolean hasRoughGems() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (isTumblable(inventory.getStackInSlot(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean isRunning() {
        return progress > 0;
    }

    private void finishBatch(Level level, BlockPos pos) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            Item result = polishedResult(stack.getItem());
            if (result != null) {
                inventory.setStackInSlot(i, new ItemStack(result, stack.getCount()));
            }
        }
        progress = 0;
        setChanged();
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.8F, 1.0F);
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
        tag.putInt("Progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        progress = tag.getInt("Progress");
    }
}
