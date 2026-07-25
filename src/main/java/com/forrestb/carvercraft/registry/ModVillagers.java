package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * The village jeweler. Job site is the cabbing machine — of the five machines it's
 * the one a working lapidary sits at all day — so any placed cabbing machine can
 * employ an unemployed villager, exactly like a vanilla workstation.
 */
public class ModVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, CarverCraft.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS =
            DeferredRegister.create(Registries.VILLAGER_PROFESSION, CarverCraft.MODID);

    public static final ResourceKey<PoiType> JEWELER_POI_KEY =
            ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE,
                    ResourceLocation.fromNamespaceAndPath(CarverCraft.MODID, "jeweler"));

    public static final Supplier<PoiType> JEWELER_POI = POI_TYPES.register("jeweler",
            () -> new PoiType(
                    ImmutableSet.copyOf(ModBlocks.CABBING_MACHINE.get().getStateDefinition().getPossibleStates()),
                    1, 1));

    public static final Supplier<VillagerProfession> JEWELER = PROFESSIONS.register("jeweler",
            () -> new VillagerProfession("jeweler",
                    holder -> holder.is(JEWELER_POI_KEY),
                    holder -> holder.is(JEWELER_POI_KEY),
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    SoundEvents.GRINDSTONE_USE));
}
