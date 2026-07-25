package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.block.entity.AlloyerBlockEntity;
import com.forrestb.carvercraft.block.entity.RockTumblerBlockEntity;
import com.forrestb.carvercraft.block.entity.TrimSawBlockEntity;
import com.forrestb.carvercraft.block.entity.CabbingMachineBlockEntity;
import com.forrestb.carvercraft.block.entity.CreativeChargerBlockEntity;
import com.forrestb.carvercraft.block.entity.FacetingMachineBlockEntity;
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

    public static final Supplier<BlockEntityType<CabbingMachineBlockEntity>> CABBING_MACHINE =
            BLOCK_ENTITY_TYPES.register("cabbing_machine", () ->
                    BlockEntityType.Builder.of(CabbingMachineBlockEntity::new, ModBlocks.CABBING_MACHINE.get()).build(null));

    public static final Supplier<BlockEntityType<FacetingMachineBlockEntity>> FACETING_MACHINE =
            BLOCK_ENTITY_TYPES.register("faceting_machine", () ->
                    BlockEntityType.Builder.of(FacetingMachineBlockEntity::new, ModBlocks.FACETING_MACHINE.get()).build(null));


    public static final Supplier<BlockEntityType<AlloyerBlockEntity>> ALLOYER =
            BLOCK_ENTITY_TYPES.register("alloyer", () ->
                    BlockEntityType.Builder.of(AlloyerBlockEntity::new, ModBlocks.ALLOYER.get()).build(null));

    public static final Supplier<BlockEntityType<CreativeChargerBlockEntity>> CREATIVE_CHARGER =
            BLOCK_ENTITY_TYPES.register("creative_charger", () ->
                    BlockEntityType.Builder.of(CreativeChargerBlockEntity::new, ModBlocks.CREATIVE_CHARGER.get()).build(null));
}
