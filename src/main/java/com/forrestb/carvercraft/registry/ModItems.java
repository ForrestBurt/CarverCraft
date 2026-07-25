package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.item.RingItem;
import com.forrestb.carvercraft.item.RingItem.Bonus;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CarverCraft.MODID);

    private static DeferredItem<Item> simple(String name) {
        return ITEMS.registerSimpleItem(name, new Item.Properties());
    }

    private static DeferredItem<Item> ring(String name, Bonus... bonuses) {
        return ITEMS.register(name, () -> new RingItem(new Item.Properties().stacksTo(1), bonuses));
    }

    // --- Metals ------------------------------------------------------------
    public static final DeferredItem<Item> RAW_SILVER = simple("raw_silver");
    public static final DeferredItem<Item> SILVER_INGOT = simple("silver_ingot");
    // Real jeweller's alloys, at roughly real ratios.
    public static final DeferredItem<Item> STERLING_SILVER_INGOT = simple("sterling_silver_ingot");
    public static final DeferredItem<Item> ELECTRUM_INGOT = simple("electrum_ingot");
    public static final DeferredItem<Item> ROSE_GOLD_INGOT = simple("rose_gold_ingot");

    // --- Opaque stones: rough -> polished, on the tumbler -------------------
    public static final DeferredItem<Item> ROUGH_AGATE = simple("rough_agate");
    public static final DeferredItem<Item> POLISHED_AGATE = simple("polished_agate");
    public static final DeferredItem<Item> ROUGH_JASPER = simple("rough_jasper");
    public static final DeferredItem<Item> POLISHED_JASPER = simple("polished_jasper");
    public static final DeferredItem<Item> ROUGH_CARNELIAN = simple("rough_carnelian");
    public static final DeferredItem<Item> POLISHED_CARNELIAN = simple("polished_carnelian");
    public static final DeferredItem<Item> ROUGH_ROSE_QUARTZ = simple("rough_rose_quartz");
    public static final DeferredItem<Item> POLISHED_ROSE_QUARTZ = simple("polished_rose_quartz");
    public static final DeferredItem<Item> ROUGH_MALACHITE = simple("rough_malachite");
    public static final DeferredItem<Item> POLISHED_MALACHITE = simple("polished_malachite");

    // --- Transparent stones: rough -> preform -> faceted --------------------
    public static final DeferredItem<Item> ROUGH_GARNET = simple("rough_garnet");
    public static final DeferredItem<Item> GARNET_PREFORM = simple("garnet_preform");
    public static final DeferredItem<Item> FACETED_GARNET = simple("faceted_garnet");

    public static final DeferredItem<Item> ROUGH_PERIDOT = simple("rough_peridot");
    public static final DeferredItem<Item> PERIDOT_PREFORM = simple("peridot_preform");
    public static final DeferredItem<Item> FACETED_PERIDOT = simple("faceted_peridot");

    public static final DeferredItem<Item> ROUGH_TOPAZ = simple("rough_topaz");
    public static final DeferredItem<Item> TOPAZ_PREFORM = simple("topaz_preform");
    public static final DeferredItem<Item> FACETED_TOPAZ = simple("faceted_topaz");

    public static final DeferredItem<Item> ROUGH_SAPPHIRE = simple("rough_sapphire");
    public static final DeferredItem<Item> SAPPHIRE_PREFORM = simple("sapphire_preform");
    public static final DeferredItem<Item> FACETED_SAPPHIRE = simple("faceted_sapphire");

    public static final DeferredItem<Item> ROUGH_RUBY = simple("rough_ruby");
    public static final DeferredItem<Item> RUBY_PREFORM = simple("ruby_preform");
    // The first item this mod ever had, now folded in as the faceted end of the ruby chain.
    public static final DeferredItem<Item> RUBY = simple("ruby");

    // Idaho's state gem. Asterism from rutile inclusions, found in commercial
    // quantity in two places on Earth.
    public static final DeferredItem<Item> ROUGH_STAR_GARNET = simple("rough_star_garnet");
    public static final DeferredItem<Item> STAR_GARNET_PREFORM = simple("star_garnet_preform");
    public static final DeferredItem<Item> FACETED_STAR_GARNET = simple("faceted_star_garnet");

    // --- Jewelry -----------------------------------------------------------
    public static final DeferredItem<Item> SILVER_RING = ring("silver_ring");
    public static final DeferredItem<Item> GOLD_RING = ring("gold_ring");

    // Silver-band rings, set with tumbled opaque stones.
    public static final DeferredItem<Item> AGATE_RING = ring("agate_ring",
            Bonus.of(Attributes.ARMOR, 2.0D));
    public static final DeferredItem<Item> JASPER_RING = ring("jasper_ring",
            Bonus.of(Attributes.MAX_HEALTH, 4.0D));
    public static final DeferredItem<Item> CARNELIAN_RING = ring("carnelian_ring",
            Bonus.of(Attributes.ATTACK_SPEED, 0.2D));
    public static final DeferredItem<Item> ROSE_QUARTZ_RING = ring("rose_quartz_ring",
            Bonus.of(Attributes.MAX_ABSORPTION, 4.0D));
    public static final DeferredItem<Item> MALACHITE_RING = ring("malachite_ring",
            Bonus.of(Attributes.ARMOR_TOUGHNESS, 2.0D));

    // Gold-band rings, set with faceted gems.
    public static final DeferredItem<Item> GARNET_RING = ring("garnet_ring",
            Bonus.of(Attributes.ATTACK_DAMAGE, 1.0D));
    public static final DeferredItem<Item> PERIDOT_RING = ring("peridot_ring",
            new Bonus(Attributes.MOVEMENT_SPEED, 0.10D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static final DeferredItem<Item> TOPAZ_RING = ring("topaz_ring",
            Bonus.of(Attributes.LUCK, 1.0D));
    public static final DeferredItem<Item> RUBY_RING = ring("ruby_ring",
            Bonus.of(Attributes.ATTACK_DAMAGE, 2.0D));
    public static final DeferredItem<Item> SAPPHIRE_RING = ring("sapphire_ring",
            Bonus.of(Attributes.KNOCKBACK_RESISTANCE, 0.2D));
    // The endgame ring, and the only one carrying two modifiers.
    public static final DeferredItem<Item> STAR_GARNET_RING = ring("star_garnet_ring",
            Bonus.of(Attributes.MAX_HEALTH, 4.0D),
            Bonus.of(Attributes.LUCK, 2.0D));

    // --- Block items -------------------------------------------------------
    public static final DeferredItem<BlockItem> SILVER_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("silver_ore", ModBlocks.SILVER_ORE);
    public static final DeferredItem<BlockItem> DEEPSLATE_SILVER_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("deepslate_silver_ore", ModBlocks.DEEPSLATE_SILVER_ORE);
    public static final DeferredItem<BlockItem> ROCK_TUMBLER_ITEM =
            ITEMS.registerSimpleBlockItem("rock_tumbler", ModBlocks.ROCK_TUMBLER);
    public static final DeferredItem<BlockItem> TRIM_SAW_ITEM =
            ITEMS.registerSimpleBlockItem("trim_saw", ModBlocks.TRIM_SAW);
    public static final DeferredItem<BlockItem> FACETING_STATION_ITEM =
            ITEMS.registerSimpleBlockItem("faceting_station", ModBlocks.FACETING_STATION);
}
