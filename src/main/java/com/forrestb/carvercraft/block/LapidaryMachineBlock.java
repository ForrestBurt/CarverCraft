package com.forrestb.carvercraft.block;

import com.forrestb.carvercraft.block.entity.AbstractLapidaryBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Shared block for every lapidary machine: opens its menu, ticks its block entity,
 * puffs particles while running, and drops its contents when broken.
 *
 * RUNNING reuses vanilla's LIT property. Block states sync to the client for free,
 * which is what makes the client-side particle tick work at all.
 */
public abstract class LapidaryMachineBlock extends BaseEntityBlock {
    public static final BooleanProperty RUNNING = BlockStateProperties.LIT;

    protected LapidaryMachineBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(RUNNING, Boolean.FALSE));
    }

    @Override
    protected abstract MapCodec<? extends BaseEntityBlock> codec();

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RUNNING);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return (tickLevel, pos, tickState, be) -> {
            if (be instanceof AbstractLapidaryBlockEntity machine) {
                AbstractLapidaryBlockEntity.serverTick(tickLevel, pos, tickState, machine);
            }
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof MenuProvider menuProvider) {
            player.openMenu(menuProvider, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof AbstractLapidaryBlockEntity machine) {
            machine.dropContents();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    /** Overridable so a saw can throw sparks where a tumbler throws dust. */
    protected ParticleOptions workParticle() {
        return ParticleTypes.POOF;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(RUNNING)) {
            return;
        }
        double cx = pos.getX() + 0.5;
        double cz = pos.getZ() + 0.5;
        if (random.nextInt(2) == 0) {
            level.addParticle(workParticle(),
                    cx + (random.nextDouble() - 0.5) * 0.5,
                    pos.getY() + 0.9 + random.nextDouble() * 0.15,
                    cz + (random.nextDouble() - 0.5) * 0.5,
                    0.0, 0.015, 0.0);
        }
        if (random.nextInt(6) == 0) {
            level.addParticle(ParticleTypes.CRIT,
                    cx + (random.nextDouble() - 0.5) * 0.4,
                    pos.getY() + 0.6,
                    cz + (random.nextDouble() - 0.5) * 0.4,
                    0.0, 0.0, 0.0);
        }
    }
}
