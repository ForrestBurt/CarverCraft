package com.forrestb.carvercraft.block;

import com.forrestb.carvercraft.block.entity.FacetingStationBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FacetingStationBlock extends LapidaryMachineBlock {
    public static final MapCodec<FacetingStationBlock> CODEC = simpleCodec(FacetingStationBlock::new);

    public FacetingStationBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FacetingStationBlockEntity(pos, state);
    }

    @Override
    protected ParticleOptions workParticle() {
        return ParticleTypes.ELECTRIC_SPARK;
    }
}
