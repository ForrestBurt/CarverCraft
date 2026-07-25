package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 1.21 enchantments are datapack entries, not registered objects — the JSON in
 * data/carvercraft/enchantment/ is the enchantment. Code only needs the keys.
 */
public class ModEnchantments {

    /** Brilliance I-III: a better polish returns more light — the stone's effect
     *  grows 15% per level. Applies to every ring and trinket. */
    public static final ResourceKey<Enchantment> BRILLIANCE = key("brilliance");

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT,
                ResourceLocation.fromNamespaceAndPath(CarverCraft.MODID, name));
    }
}
