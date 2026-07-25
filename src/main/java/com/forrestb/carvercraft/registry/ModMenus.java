package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.menu.AlloyerMenu;
import com.forrestb.carvercraft.menu.LapidaryMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, CarverCraft.MODID);

    private static Supplier<MenuType<LapidaryMenu>> machine(String name) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(LapidaryMenu::new));
    }

    public static final Supplier<MenuType<LapidaryMenu>> ROCK_TUMBLER = machine("rock_tumbler");
    public static final Supplier<MenuType<LapidaryMenu>> TRIM_SAW = machine("trim_saw");
    public static final Supplier<MenuType<LapidaryMenu>> CABBING_MACHINE = machine("cabbing_machine");
    public static final Supplier<MenuType<LapidaryMenu>> FACETING_MACHINE = machine("faceting_machine");
    public static final Supplier<MenuType<AlloyerMenu>> ALLOYER =
            MENUS.register("alloyer", () -> IMenuTypeExtension.create(AlloyerMenu::new));
}
