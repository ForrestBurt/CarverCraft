package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.loot.StoneGemLootModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, CarverCraft.MODID);

    public static final Supplier<MapCodec<StoneGemLootModifier>> STONE_GEM =
            LOOT_MODIFIER_SERIALIZERS.register("stone_gem", () -> StoneGemLootModifier.CODEC);
}
