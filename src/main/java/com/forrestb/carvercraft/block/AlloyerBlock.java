package com.forrestb.carvercraft.block;

import com.forrestb.carvercraft.block.entity.AlloyerBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
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
 * The crucible block. Not a LapidaryMachineBlock — that hierarchy tickets and casts
 * to AbstractLapidaryBlockEntity, and the Alloyer deliberately isn't one.
 */
public class AlloyerBlock extends BaseEntityBlock {
    public static final MapCodec<AlloyerBlock> CODEC = simpleCodec(AlloyerBlock::new);
    public static final BooleanProperty RUNNING = BlockStateProperties.LIT;

    public AlloyerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(RUNNING, Boolean.FALSE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlloyerBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return (tickLevel, pos, tickState, be) -> {
            if (be instanceof AlloyerBlockEntity machine) {
                AlloyerBlockEntity.serverTick(tickLevel, pos, tickState, machine);
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
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof AlloyerBlockEntity machine) {
            machine.dropContents();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    /** A crucible at temperature: flame licks and the occasional molten spit. */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(RUNNING)) {
            return;
        }
        double cx = pos.getX() + 0.5;
        double cz = pos.getZ() + 0.5;
        if (random.nextInt(2) == 0) {
            level.addParticle(ParticleTypes.FLAME,
                    cx + (random.nextDouble() - 0.5) * 0.4,
                    pos.getY() + 0.9 + random.nextDouble() * 0.1,
                    cz + (random.nextDouble() - 0.5) * 0.4,
                    0.0, 0.01, 0.0);
        }
        if (random.nextInt(10) == 0) {
            level.addParticle(ParticleTypes.LAVA,
                    cx + (random.nextDouble() - 0.5) * 0.3,
                    pos.getY() + 1.0,
                    cz + (random.nextDouble() - 0.5) * 0.3,
                    0.0, 0.0, 0.0);
        }
    }
}
