package com.forrestb.carvercraft.event;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.registry.ModItems;
import com.forrestb.carvercraft.registry.ModVillagers;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

/**
 * What the jeweler deals in. The shape follows the mod's economy: they buy what the
 * ground gives you (rough, tumbled, cabbed), sell the consumables and the bands,
 * and at master rank part with corundum rough — but never a star garnet. Idaho's
 * stone is found, not bought.
 */
@EventBusSubscriber(modid = CarverCraft.MODID, bus = EventBusSubscriber.Bus.GAME)
public class JewelerTrades {

    @SubscribeEvent
    static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() != ModVillagers.JEWELER.get()) {
            return;
        }
        var trades = event.getTrades();

        // Novice: rough stone changes hands, grit is always in stock.
        trades.get(1).add(buy(ModItems.ROUGH_JASPER.get(), 6, 1, 16, 2));
        trades.get(1).add(buy(ModItems.ROUGH_AGATE.get(), 6, 1, 16, 2));
        trades.get(1).add(sell(ModItems.SILICON_CARBIDE_GRIT.get(), 6, 1, 16, 1));

        // Apprentice: the polishing bench.
        trades.get(2).add(buy(ModItems.TUMBLED_JASPER.get(), 4, 1, 12, 5));
        trades.get(2).add(sell(ModItems.POLISHING_COMPOUND.get(), 4, 1, 12, 5));
        trades.get(2).add(sell(ModItems.SILVER_BAND.get(), 1, 1, 12, 5));

        // Journeyman: finished stock.
        trades.get(3).add(buy(ModItems.JASPER_CABOCHON.get(), 2, 1, 12, 10));
        trades.get(3).add(sell(ModItems.STERLING_SILVER_BAND.get(), 1, 3, 12, 10));

        // Expert: gold work.
        trades.get(4).add(sell(ModItems.GOLD_BAND.get(), 1, 3, 8, 15));
        trades.get(4).add(sell(ModItems.ROSE_GOLD_BAND.get(), 1, 5, 8, 15));
        trades.get(4).add(buy(ModItems.ROUGH_TOPAZ.get(), 1, 2, 8, 15));

        // Master: the enchanter's band, and corundum rough at a collector's price.
        trades.get(5).add(sell(ModItems.ELECTRUM_BAND.get(), 1, 6, 4, 30));
        trades.get(5).add(sell(ModItems.ROUGH_RUBY.get(), 1, 8, 3, 30));
        trades.get(5).add(sell(ModItems.ROUGH_SAPPHIRE.get(), 1, 8, 3, 30));
    }

    /** Player hands over {@code count} of {@code item} for {@code emeralds}. */
    private static VillagerTrades.ItemListing buy(Item item, int count, int emeralds, int maxUses, int xp) {
        return (trader, random) -> new MerchantOffer(
                new ItemCost(item, count), new ItemStack(Items.EMERALD, emeralds), maxUses, xp, 0.05F);
    }

    /** Player pays {@code emeralds} for {@code count} of {@code item}. */
    private static VillagerTrades.ItemListing sell(Item item, int count, int emeralds, int maxUses, int xp) {
        return (trader, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, emeralds), new ItemStack(item, count), maxUses, xp, 0.05F);
    }
}
