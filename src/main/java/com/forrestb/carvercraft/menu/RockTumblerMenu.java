package com.forrestb.carvercraft.menu;

import com.forrestb.carvercraft.block.entity.RockTumblerBlockEntity;
import com.forrestb.carvercraft.registry.ModBlocks;
import com.forrestb.carvercraft.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class RockTumblerMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final ContainerData data;
    public final RockTumblerBlockEntity blockEntity;

    // The 4 tumbler slots, laid out in a 2x2, centered.
    private static final int SLOT_X = 71;
    private static final int SLOT_Y = 25;

    // Client-side constructor: called from the registered MenuType with a buffer holding the block pos.
    public RockTumblerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, buf.readBlockPos()), new SimpleContainerData(2));
    }

    // Server-side constructor: called from the block entity, which knows the real inventory + data.
    public RockTumblerMenu(int containerId, Inventory playerInventory, RockTumblerBlockEntity blockEntity, ContainerData data) {
        super(ModMenus.ROCK_TUMBLER.get(), containerId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.data = data;

        IItemHandler handler = blockEntity.getInventory();
        addSlot(new SlotItemHandler(handler, 0, SLOT_X, SLOT_Y));
        addSlot(new SlotItemHandler(handler, 1, SLOT_X + 18, SLOT_Y));
        addSlot(new SlotItemHandler(handler, 2, SLOT_X, SLOT_Y + 18));
        addSlot(new SlotItemHandler(handler, 3, SLOT_X + 18, SLOT_Y + 18));

        addPlayerInventory(playerInventory);
        addDataSlots(data);
    }

    private static RockTumblerBlockEntity getBlockEntity(Inventory playerInventory, BlockPos pos) {
        var be = playerInventory.player.level().getBlockEntity(pos);
        if (be instanceof RockTumblerBlockEntity tumbler) {
            return tumbler;
        }
        throw new IllegalStateException("Block entity is not a RockTumblerBlockEntity at " + pos);
    }

    /** 0.0 to 1.0 progress toward the current batch finishing, for the arrow. */
    public float getProgressFraction() {
        int progress = data.get(0);
        int total = data.get(1);
        if (total == 0 || progress == 0) {
            return 0f;
        }
        return Math.min(1f, (float) progress / (float) total);
    }

    public boolean isRunning() {
        return data.get(0) > 0;
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

    // Slot index map: 0-3 tumbler, 4-30 player main, 31-39 hotbar.
    private static final int TUMBLER_START = 0;
    private static final int TUMBLER_END = 4;
    private static final int PLAYER_START = 4;
    private static final int PLAYER_END = 40;

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack quickMoved = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack raw = slot.getItem();
            quickMoved = raw.copy();
            if (index < TUMBLER_END) {
                // From tumbler out to player inventory.
                if (!moveItemStackTo(raw, PLAYER_START, PLAYER_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player inventory into the tumbler slots.
                if (!moveItemStackTo(raw, TUMBLER_START, TUMBLER_END, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (raw.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return quickMoved;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.ROCK_TUMBLER.get());
    }
}
