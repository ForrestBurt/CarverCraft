package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.menu.RockTumblerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, CarverCraft.MODID);

    public static final Supplier<MenuType<RockTumblerMenu>> ROCK_TUMBLER =
            MENUS.register("rock_tumbler", () ->
                    IMenuTypeExtension.create(RockTumblerMenu::new));
}
