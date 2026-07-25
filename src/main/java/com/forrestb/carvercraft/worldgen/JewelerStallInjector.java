package com.forrestb.carvercraft.worldgen;

import com.forrestb.carvercraft.CarverCraft;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Injects the jeweler's stall into village generation.
 *
 * Vanilla has no datapack hook for ADDING to another namespace's template pool —
 * overwriting minecraft's houses.json wholesale would fight every other mod — so
 * this does the established runtime dance: when the server is about to start,
 * append our stall to each village biome's houses pool. The pool's two lists are
 * private; `templates` (the expanded, weighted list the generator actually draws
 * from) is mutable in place, `rawTemplates` is an immutable codec product and is
 * swapped via reflection purely so a re-serialized pool stays honest. If Mojang
 * renames those fields in some future version this logs and villages simply
 * generate without stalls — it must never crash a server start.
 */
@EventBusSubscriber(modid = CarverCraft.MODID, bus = EventBusSubscriber.Bus.GAME)
public class JewelerStallInjector {

    private static final String STALL = "carvercraft:village/jeweler_stall";
    private static final int WEIGHT = 3;
    private static final List<String> VILLAGE_BIOMES = List.of("plains", "desert", "savanna", "snowy", "taiga");

    @SubscribeEvent
    static void onServerAboutToStart(ServerAboutToStartEvent event) {
        Registry<StructureTemplatePool> pools =
                event.getServer().registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        Registry<StructureProcessorList> processors =
                event.getServer().registryAccess().registryOrThrow(Registries.PROCESSOR_LIST);
        Holder<StructureProcessorList> empty = processors.getHolderOrThrow(
                ResourceKey.create(Registries.PROCESSOR_LIST, ResourceLocation.withDefaultNamespace("empty")));

        for (String biome : VILLAGE_BIOMES) {
            inject(pools, empty, ResourceLocation.withDefaultNamespace("village/" + biome + "/houses"));
        }
    }

    private static void inject(Registry<StructureTemplatePool> pools,
                               Holder<StructureProcessorList> emptyProcessors,
                               ResourceLocation poolId) {
        StructureTemplatePool pool = pools.get(poolId);
        if (pool == null) {
            CarverCraft.LOGGER.warn("Village pool {} not found; no jeweler stall there", poolId);
            return;
        }
        StructurePoolElement stall = SinglePoolElement.single(STALL, emptyProcessors)
                .apply(StructureTemplatePool.Projection.RIGID);
        try {
            Field templatesField = StructureTemplatePool.class.getDeclaredField("templates");
            templatesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<StructurePoolElement> templates = (List<StructurePoolElement>) templatesField.get(pool);
            for (int i = 0; i < WEIGHT; i++) {
                templates.add(stall);
            }

            Field rawField = StructureTemplatePool.class.getDeclaredField("rawTemplates");
            rawField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Pair<StructurePoolElement, Integer>> raw =
                    (List<Pair<StructurePoolElement, Integer>>) rawField.get(pool);
            List<Pair<StructurePoolElement, Integer>> mutable = new ArrayList<>(raw);
            mutable.add(Pair.of(stall, WEIGHT));
            rawField.set(pool, mutable);
        } catch (ReflectiveOperationException | UnsupportedOperationException e) {
            CarverCraft.LOGGER.error("Could not inject the jeweler stall into {}", poolId, e);
        }
    }
}
