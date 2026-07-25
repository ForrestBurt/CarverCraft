package com.forrestb.carvercraft;

import org.slf4j.Logger;

import com.forrestb.carvercraft.registry.ModBlockEntities;
import com.forrestb.carvercraft.registry.ModBlocks;
import com.forrestb.carvercraft.registry.ModCreativeTabs;
import com.forrestb.carvercraft.registry.ModItems;
import com.forrestb.carvercraft.registry.ModLootModifiers;
import com.forrestb.carvercraft.registry.ModMenus;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(CarverCraft.MODID)
public class CarverCraft {
    public static final String MODID = "carvercraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CarverCraft(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("CarverCraft: from geology to magic.");
    }
}
