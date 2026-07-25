package com.forrestb.carvercraft.block;

import com.forrestb.carvercraft.block.entity.RockTumblerBlockEntity;
import com.forrestb.carvercraft.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
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

public class RockTumblerBlock extends BaseEntityBlock {
    public static final MapCodec<RockTumblerBlock> CODEC = simpleCodec(RockTumblerBlock::new);

    // Reuse vanilla's LIT property so the client knows when the drum is turning.
    // Block states sync to the client automatically, which fixes the particle problem.
    public static final BooleanProperty RUNNING = BlockStateProperties.LIT;

    public RockTumblerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(RUNNING, Boolean.FALSE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(RUNNING);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RockTumblerBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, ModBlockEntities.ROCK_TUMBLER.get(), RockTumblerBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof MenuProvider menuProvider) {
            player.openMenu(menuProvider, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // Ambient particles while the drum runs. Reads the block state (client-synced),
    // not the block entity, so it now actually fires on the client.
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(RUNNING)) {
            return;
        }
        double cx = pos.getX() + 0.5;
        double cy = pos.getY();
        double cz = pos.getZ() + 0.5;
        // Dust puffing off the top.
        if (random.nextInt(2) == 0) {
            double px = cx + (random.nextDouble() - 0.5) * 0.5;
            double pz = cz + (random.nextDouble() - 0.5) * 0.5;
            level.addParticle(ParticleTypes.POOF, px, cy + 0.9 + random.nextDouble() * 0.15, pz,
                    0.0, 0.015, 0.0);
        }
        // Occasional spark from the grinding.
        if (random.nextInt(6) == 0) {
            double px = cx + (random.nextDouble() - 0.5) * 0.4;
            double pz = cz + (random.nextDouble() - 0.5) * 0.4;
            level.addParticle(ParticleTypes.CRIT, px, cy + 0.6, pz, 0.0, 0.0, 0.0);
        }
    }
}
