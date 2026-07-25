package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Exposes each machine's inventory and FE buffer. This is why a Mekanism cable or an
 * Immersive Engineering wire can feed these machines with no glue code on either side.
 */
@EventBusSubscriber(modid = CarverCraft.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ROCK_TUMBLER.get(), (be, side) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.TRIM_SAW.get(), (be, side) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.CABBING_MACHINE.get(), (be, side) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.FACETING_MACHINE.get(), (be, side) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ALLOYER.get(), (be, side) -> be.getInventory());

        // Energy — the tumbler is passive and returns null.
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.TRIM_SAW.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.CABBING_MACHINE.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.FACETING_MACHINE.get(), (be, side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ALLOYER.get(), (be, side) -> be.getEnergyStorage());

        // Testing block: an infinite source cables can pull from.
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.CREATIVE_CHARGER.get(), (be, side) -> be.getEnergyStorage());
    }
}
