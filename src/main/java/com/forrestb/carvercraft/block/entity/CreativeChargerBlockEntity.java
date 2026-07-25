package com.forrestb.carvercraft.block.entity;

import com.forrestb.carvercraft.energy.CreativeEnergyStorage;
import com.forrestb.carvercraft.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Testing block: shoves FE into every adjacent machine, forever, and also exposes
 * itself as an infinite source so cables can pull from it.
 *
 * Exists so the powered machines are testable without a full power setup. Creative
 * tab only, and it should stay that way.
 */
public class CreativeChargerBlockEntity extends BlockEntity {
    private static final int PUSH_PER_TICK = 100_000;
    private final CreativeEnergyStorage energy = new CreativeEnergyStorage();

    public CreativeChargerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_CHARGER.get(), pos, state);
    }

    public IEnergyStorage getEnergyStorage() {
        return energy;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CreativeChargerBlockEntity charger) {
        for (Direction dir : Direction.values()) {
            BlockPos neighbour = pos.relative(dir);
            IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighbour, dir.getOpposite());
            if (target != null && target.canReceive()) {
                target.receiveEnergy(PUSH_PER_TICK, false);
            }
        }
    }
}
