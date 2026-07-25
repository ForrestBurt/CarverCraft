package com.forrestb.carvercraft.menu;

import com.forrestb.carvercraft.block.entity.AlloyerBlockEntity;
import com.forrestb.carvercraft.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * Crucible layout: two metal slots stacked on the left, one alloy slot on the right,
 * an arrow between and the usual energy gauge along the panel's left edge.
 */
public class AlloyerMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final ContainerData data;
    public final AlloyerBlockEntity blockEntity;

    public static final int IN_FIRST_X = 49;
    public static final int IN_FIRST_Y = 18;
    public static final int IN_SECOND_X = 49;
    public static final int IN_SECOND_Y = 54;
    public static final int OUT_X = 117;
    public static final int OUT_Y = 36;
    public static final int ARROW_X = 76;
    public static final int ARROW_Y = 36;
    public static final int ARROW_W = 22;
    public static final int ARROW_H = 16;
    public static final int ENERGY_X = 12;
    public static final int ENERGY_Y = 17;
    public static final int ENERGY_W = 12;
    public static final int ENERGY_H = 54;

    private static final int MACHINE_SLOTS = AlloyerBlockEntity.SLOT_COUNT;
    private static final int PLAYER_START = MACHINE_SLOTS;
    private static final int PLAYER_END = MACHINE_SLOTS + 36;

    private static class OutputSlot extends SlotItemHandler {
        OutputSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    /** Client-side: the buffer carries the block pos — read exactly once. */
    public AlloyerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, resolve(playerInventory, buf.readBlockPos()), null);
    }

    public AlloyerMenu(int containerId, Inventory playerInventory,
                       AlloyerBlockEntity blockEntity, ContainerData data) {
        super(ModMenus.ALLOYER.get(), containerId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.data = data != null ? data : new SimpleContainerData(AlloyerBlockEntity.DATA_COUNT);

        IItemHandler handler = blockEntity.getInventory();
        addSlot(new SlotItemHandler(handler, AlloyerBlockEntity.SLOT_FIRST, IN_FIRST_X, IN_FIRST_Y));
        addSlot(new SlotItemHandler(handler, AlloyerBlockEntity.SLOT_SECOND, IN_SECOND_X, IN_SECOND_Y));
        addSlot(new OutputSlot(handler, AlloyerBlockEntity.SLOT_OUTPUT, OUT_X, OUT_Y));

        addPlayerInventory(playerInventory);
        addDataSlots(this.data);
    }

    private static AlloyerBlockEntity resolve(Inventory playerInventory, BlockPos pos) {
        var be = playerInventory.player.level().getBlockEntity(pos);
        if (be instanceof AlloyerBlockEntity machine) {
            return machine;
        }
        throw new IllegalStateException("No alloyer at " + pos);
    }

    public float getProgress() {
        int current = data.get(AlloyerBlockEntity.DATA_PROGRESS);
        int total = data.get(AlloyerBlockEntity.DATA_TIME);
        if (total <= 0 || current <= 0) {
            return 0f;
        }
        return Math.min(1f, (float) current / (float) total);
    }

    public float getEnergyFraction() {
        int capacity = data.get(AlloyerBlockEntity.DATA_CAPACITY);
        if (capacity <= 0) {
            return 0f;
        }
        return Math.min(1f, (float) data.get(AlloyerBlockEntity.DATA_ENERGY) / (float) capacity);
    }

    public int getEnergyStored() {
        return data.get(AlloyerBlockEntity.DATA_ENERGY);
    }

    public int getEnergyCapacity() {
        return data.get(AlloyerBlockEntity.DATA_CAPACITY);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack quickMoved = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack raw = slot.getItem();
            quickMoved = raw.copy();
            if (index < MACHINE_SLOTS) {
                if (!moveItemStackTo(raw, PLAYER_START, PLAYER_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(raw, quickMoved);
            } else {
                if (!moveItemStackTo(raw, AlloyerBlockEntity.SLOT_FIRST, AlloyerBlockEntity.SLOT_OUTPUT, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (raw.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (raw.getCount() == quickMoved.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, raw);
        }
        return quickMoved;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, blockEntity.getBlockState().getBlock());
    }
}
