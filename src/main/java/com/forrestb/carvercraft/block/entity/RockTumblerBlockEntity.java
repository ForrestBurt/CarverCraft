package com.forrestb.carvercraft.block.entity;

import com.forrestb.carvercraft.registry.ModBlockEntities;
import com.forrestb.carvercraft.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
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
public class RockTumblerBlockEntity extends BlockEntity {
    public static final int CAPACITY = 4;
    public static final int TICKS_PER_BATCH = 20 * 180; // 3 minutes per load

    private final ItemStackHandler inventory = new ItemStackHandler(CAPACITY) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private int progress = 0;

    public RockTumblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROCK_TUMBLER.get(), pos, state);
    }

    @Nullable
    public static Item polishedResult(Item rough) {
        if (rough == ModItems.ROUGH_JASPER.get()) return ModItems.POLISHED_JASPER.get();
        if (rough == ModItems.ROUGH_GARNET.get()) return ModItems.POLISHED_GARNET.get();
        if (rough == ModItems.ROUGH_AGATE.get()) return ModItems.POLISHED_AGATE.get();
        return null;
    }

    public static boolean isTumblable(ItemStack stack) {
        return polishedResult(stack.getItem()) != null;
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
        if (tumbler.progress % 100 == 0) {
            tumbler.setChanged();
        }
        // The drum grumbles away.
        if (tumbler.progress % 40 == 0) {
            level.playSound(null, pos, SoundEvents.GRAVEL_STEP, SoundSource.BLOCKS, 0.25F, 0.6F);
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

    /** Insert one item from the held stack into the first free slot. Call server-side only. */
    public void tryInsert(ItemStack held, Player player) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i).isEmpty()) {
                inventory.setStackInSlot(i, held.copyWithCount(1));
                if (!player.hasInfiniteMaterials()) {
                    held.shrink(1);
                }
                if (level != null) {
                    level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.8F, 0.9F);
                }
                setChanged();
                return;
            }
        }
    }

    /** Hand everything to the player. Call server-side only. */
    public void emptyTo(Player player) {
        boolean gaveAnything = false;
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
                inventory.setStackInSlot(i, ItemStack.EMPTY);
                gaveAnything = true;
            }
        }
        if (gaveAnything) {
            progress = 0;
            setChanged();
            if (level != null) {
                level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.6F, 1.0F);
            }
        }
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
