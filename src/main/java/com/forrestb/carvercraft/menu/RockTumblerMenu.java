package com.forrestb.carvercraft.menu;

import com.forrestb.carvercraft.block.entity.RockTumblerBlockEntity;
import com.forrestb.carvercraft.registry.ModBlocks;
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

public class RockTumblerMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final ContainerData data;
    public final RockTumblerBlockEntity blockEntity;

    // Layout — shared with the screen so the two can never drift apart.
    public static final int LANES = RockTumblerBlockEntity.LANES;
    public static final int[] ROW_Y = {16, 34, 52, 70};
    public static final int INPUT_X = 44;
    public static final int OUTPUT_X = 98;
    public static final int ARROW_X = 69;
    public static final int ARROW_W = 22;
    public static final int ARROW_H = 16;

    private static final int MACHINE_SLOTS = RockTumblerBlockEntity.SLOT_COUNT; // 8
    private static final int PLAYER_START = MACHINE_SLOTS;
    private static final int PLAYER_END = MACHINE_SLOTS + 36;

    /** Output slots hand items out but never take them from the player. */
    private static class OutputSlot extends SlotItemHandler {
        OutputSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    // Client-side constructor: the MenuType hands us a buffer holding the block pos.
    public RockTumblerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, buf.readBlockPos()),
                new SimpleContainerData(LANES + 1));
    }

    // Server-side constructor: the block entity knows the real inventory and clocks.
    public RockTumblerMenu(int containerId, Inventory playerInventory, RockTumblerBlockEntity blockEntity, ContainerData data) {
        super(ModMenus.ROCK_TUMBLER.get(), containerId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.data = data;

        IItemHandler handler = blockEntity.getInventory();
        // Inputs first (indices 0-3), then outputs (4-7), matching the handler layout.
        for (int lane = 0; lane < LANES; lane++) {
            addSlot(new SlotItemHandler(handler, lane, INPUT_X, ROW_Y[lane]));
        }
        for (int lane = 0; lane < LANES; lane++) {
            addSlot(new OutputSlot(handler, RockTumblerBlockEntity.OUTPUT_OFFSET + lane, OUTPUT_X, ROW_Y[lane]));
        }

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

    /** 0.0 to 1.0 progress for one lane's arrow. */
    public float getLaneProgress(int lane) {
        if (lane < 0 || lane >= LANES) {
            return 0f;
        }
        int current = data.get(lane);
        int total = data.get(LANES);
        if (total <= 0 || current <= 0) {
            return 0f;
        }
        return Math.min(1f, (float) current / (float) total);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 108 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 166));
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
                // Out of the machine (input or output) into the player's inventory.
                if (!moveItemStackTo(raw, PLAYER_START, PLAYER_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(raw, quickMoved);
            } else {
                // From the player into the input lanes only — never into outputs.
                if (!moveItemStackTo(raw, 0, LANES, false)) {
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
        return stillValid(access, player, ModBlocks.ROCK_TUMBLER.get());
    }
}
