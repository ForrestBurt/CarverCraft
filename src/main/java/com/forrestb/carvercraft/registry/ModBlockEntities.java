package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.block.entity.RockTumblerBlockEntity;
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
}
