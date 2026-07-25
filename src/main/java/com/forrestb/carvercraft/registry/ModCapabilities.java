package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Exposes each machine's inventory and FE buffer to the outside world. This is the
 * whole reason a Mekanism cable or an Immersive Engineering wire can feed these
 * machines without either mod knowing the other exists.
 */
@EventBusSubscriber(modid = CarverCraft.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Item handlers — lets hoppers and item pipes load and unload every machine.
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ROCK_TUMBLER.get(), (be, side) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.TRIM_SAW.get(), (be, side) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.FACETING_STATION.get(), (be, side) -> be.getInventory());

        // Energy — only the powered machines have a buffer; the tumbler returns null.
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.TRIM_SAW.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.FACETING_STATION.get(), (be, side) -> be.getEnergyStorage());
    }
}
