package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, CarverCraft.MODID);

    /**
     * Which metal a ring was set in, when it isn't the tier default ("electrum" or
     * "rose_gold"). A component rather than new items on purpose: every stone keeps
     * exactly one ring item — the design rule that keeps X_ring unambiguous — and
     * the band only changes enchantability (RingItem) and max damage (set by the
     * crafting recipe's result components).
     */
    public static final Supplier<DataComponentType<String>> BAND =
            COMPONENTS.register("band", () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .build());
}
