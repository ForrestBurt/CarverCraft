package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.block.entity.FacetingStationBlockEntity;
import com.forrestb.carvercraft.block.entity.RockTumblerBlockEntity;
import com.forrestb.carvercraft.block.entity.TrimSawBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CarverCraft.MODID);

    public static final Supplier<BlockEntityType<RockTumblerBlockEntity>> ROCK_TUMBLER =
            BLOCK_ENTITY_TYPES.register("rock_tumbler", () ->
                    BlockEntityType.Builder.of(RockTumblerBlockEntity::new, ModBlocks.ROCK_TUMBLER.get()).build(null));

    public static final Supplier<BlockEntityType<TrimSawBlockEntity>> TRIM_SAW =
            BLOCK_ENTITY_TYPES.register("trim_saw", () ->
                    BlockEntityType.Builder.of(TrimSawBlockEntity::new, ModBlocks.TRIM_SAW.get()).build(null));

    public static final Supplier<BlockEntityType<FacetingStationBlockEntity>> FACETING_STATION =
            BLOCK_ENTITY_TYPES.register("faceting_station", () ->
                    BlockEntityType.Builder.of(FacetingStationBlockEntity::new, ModBlocks.FACETING_STATION.get()).build(null));
}
