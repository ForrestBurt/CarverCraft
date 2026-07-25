package com.forrestb.carvercraft.menu;

import com.forrestb.carvercraft.block.entity.AbstractLapidaryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * One menu class for every lapidary machine. The machines share a layout, so the only
 * thing that varies is which MenuType they were opened under — and the block entity
 * already knows that.
 */
public class LapidaryMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final ContainerData data;
    public final AbstractLapidaryBlockEntity blockEntity;

    public static final int LANES = AbstractLapidaryBlockEntity.LANES;
    public static final int[] ROW_Y = {16, 34, 52, 70};
    public static final int INPUT_X = 44;
    public static final int OUTPUT_X = 98;
    public static final int ARROW_X = 69;
    public static final int ARROW_W = 22;
    public static final int ARROW_H = 16;
    // Energy gauge, drawn only when the machine actually has a buffer.
    public static final int ENERGY_X = 12;
    public static final int ENERGY_Y = 16;
    public static final int ENERGY_W = 12;
    public static final int ENERGY_H = 72;
    // Shared consumable slot (grit, polish), right of the lanes, on machines that use one.
    public static final int CONSUMABLE_X = 134;
    public static final int CONSUMABLE_Y = 52;

    // 8 lane slots, plus the consumable slot on machines that declare one.
    private final int machineSlots;
    private final int playerStart;
    private final int playerEnd;

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

    /** Client-side: the buffer holds the block pos, and the block entity knows its own type. */
    public LapidaryMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        // readBlockPos() must happen exactly once — hence the delegation through a
        // private constructor rather than resolving inline twice.
        this(containerId, playerInventory, resolve(playerInventory, buf.readBlockPos()));
    }

    private LapidaryMenu(int containerId, Inventory playerInventory, AbstractLapidaryBlockEntity blockEntity) {
        this(blockEntity.menuType(), containerId, playerInventory, blockEntity, null);
    }

    public LapidaryMenu(MenuType<LapidaryMenu> type, int containerId, Inventory playerInventory,
                        AbstractLapidaryBlockEntity blockEntity, ContainerData data) {
        super(type, containerId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.data = data != null ? data : new SimpleContainerData(AbstractLapidaryBlockEntity.DATA_COUNT);

        IItemHandler handler = blockEntity.getInventory();
        for (int lane = 0; lane < LANES; lane++) {
            addSlot(new SlotItemHandler(handler, lane, INPUT_X, ROW_Y[lane]));
        }
        for (int lane = 0; lane < LANES; lane++) {
            addSlot(new OutputSlot(handler, AbstractLapidaryBlockEntity.OUTPUT_OFFSET + lane, OUTPUT_X, ROW_Y[lane]));
        }
        if (blockEntity.usesConsumable()) {
            addSlot(new SlotItemHandler(handler, AbstractLapidaryBlockEntity.CONSUMABLE_SLOT,
                    CONSUMABLE_X, CONSUMABLE_Y));
        }
        this.machineSlots = LANES * 2 + (blockEntity.usesConsumable() ? 1 : 0);
        this.playerStart = machineSlots;
        this.playerEnd = machineSlots + 36;

        addPlayerInventory(playerInventory);
        addDataSlots(this.data);
    }

    private static AbstractLapidaryBlockEntity resolve(Inventory playerInventory, BlockPos pos) {
        var be = playerInventory.player.level().getBlockEntity(pos);
        if (be instanceof AbstractLapidaryBlockEntity machine) {
            return machine;
        }
        throw new IllegalStateException("No lapidary machine at " + pos);
    }

    /** 0.0 to 1.0 progress for one lane's arrow. */
    public float getLaneProgress(int lane) {
        if (lane < 0 || lane >= LANES) {
            return 0f;
        }
        int current = data.get(lane);
        int total = data.get(LANES + lane);
        if (total <= 0 || current <= 0) {
            return 0f;
        }
        return Math.min(1f, (float) current / (float) total);
    }

    public boolean hasEnergy() {
        return data.get(AbstractLapidaryBlockEntity.DATA_CAPACITY) > 0;
    }

    public float getEnergyFraction() {
        int capacity = data.get(AbstractLapidaryBlockEntity.DATA_CAPACITY);
        if (capacity <= 0) {
            return 0f;
        }
        return Math.min(1f, (float) data.get(AbstractLapidaryBlockEntity.DATA_ENERGY) / (float) capacity);
    }

    public int getEnergyStored() {
        return data.get(AbstractLapidaryBlockEntity.DATA_ENERGY);
    }

    public int getEnergyCapacity() {
        return data.get(AbstractLapidaryBlockEntity.DATA_CAPACITY);
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
            if (index < machineSlots) {
                if (!moveItemStackTo(raw, playerStart, playerEnd, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(raw, quickMoved);
            } else {
                // Lanes first; anything they refuse may still be the machine's
                // consumable (the slots themselves police what they accept).
                boolean moved = moveItemStackTo(raw, 0, LANES, false);
                if (!moved && blockEntity.usesConsumable()) {
                    moved = moveItemStackTo(raw, machineSlots - 1, machineSlots, false);
                }
                if (!moved) {
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
