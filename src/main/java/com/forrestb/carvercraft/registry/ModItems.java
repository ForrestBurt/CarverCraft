package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.item.ConsumableItem;
import com.forrestb.carvercraft.item.JewelerHammerItem;
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

    // Jewelry wears like armor when its wearer is hit (see JewelryWearHandler).
    // Durability and enchantability follow the band and track the real metals:
    // fine silver is soft wire, sterling is work-hardened, pure gold is soft but
    // noble. Electrum and rose gold arrive as data components on gold-tier rings
    // (see RingItem.BAND) — electrum enchants best and wears fastest, rose gold
    // is the hardest gold alloy there is.
    public static final int TRINKET_DURABILITY = 120;
    public static final int STERLING_RING_DURABILITY = 280;
    public static final int GOLD_RING_DURABILITY = 160;
    public static final int SILVER_ENCHANTABILITY = 15;
    public static final int STERLING_ENCHANTABILITY = 18;
    public static final int GOLD_ENCHANTABILITY = 22;

    private static DeferredItem<Item> ring(String name, int durability, int enchantability, Bonus... bonuses) {
        return ITEMS.register(name, () -> new RingItem(
                new Item.Properties().stacksTo(1).durability(durability), enchantability, bonuses));
    }

    // --- Metals ------------------------------------------------------------
    public static final DeferredItem<Item> RAW_SILVER = simple("raw_silver");
    public static final DeferredItem<Item> SILVER_INGOT = simple("silver_ingot");
    public static final DeferredItem<Item> STERLING_SILVER_INGOT = simple("sterling_silver_ingot");
    public static final DeferredItem<Item> ELECTRUM_INGOT = simple("electrum_ingot");
    public static final DeferredItem<Item> ROSE_GOLD_INGOT = simple("rose_gold_ingot");

    // --- The bench --------------------------------------------------------
    // Draws ingots into wire. Stays in the grid, one point more worn per craft.
    public static final DeferredItem<Item> JEWELERS_HAMMER = ITEMS.register("jewelers_hammer",
            () -> new JewelerHammerItem(new Item.Properties().stacksTo(1).durability(256)));

    // Jewelry stock. Every band is drawn from wire, never struck from ingots —
    // that keeps the four-ingot ring pattern (modded Minecraft's most contested
    // shape) out of this mod entirely.
    public static final DeferredItem<Item> SILVER_WIRE = simple("silver_wire");
    public static final DeferredItem<Item> STERLING_SILVER_WIRE = simple("sterling_silver_wire");
    public static final DeferredItem<Item> GOLD_WIRE = simple("gold_wire");
    public static final DeferredItem<Item> ELECTRUM_WIRE = simple("electrum_wire");
    public static final DeferredItem<Item> ROSE_GOLD_WIRE = simple("rose_gold_wire");

    // Blank bands, one per finish tier.
    public static final DeferredItem<Item> SILVER_BAND = simple("silver_band");
    public static final DeferredItem<Item> STERLING_SILVER_BAND = simple("sterling_silver_band");
    public static final DeferredItem<Item> GOLD_BAND = simple("gold_band");
    // Gold-tier alternatives, made in the Alloyer. Electrum is the enchanter's
    // band; rose gold is the wearer's.
    public static final DeferredItem<Item> ELECTRUM_BAND = simple("electrum_band");
    public static final DeferredItem<Item> ROSE_GOLD_BAND = simple("rose_gold_band");

    // --- Shop supplies -------------------------------------------------------
    // Silicon carbide grit: sand and coal, the Acheson process in a crafting grid.
    // The tumbler charges one per lane cycle.
    public static final DeferredItem<Item> SILICON_CARBIDE_GRIT = ITEMS.register("silicon_carbide_grit",
            () -> new ConsumableItem(new Item.Properties(), "item.carvercraft.silicon_carbide_grit.tooltip"));
    // Jeweler's rouge — red iron oxide. The cabbing machine's final-stage polish.
    public static final DeferredItem<Item> POLISHING_COMPOUND = ITEMS.register("polishing_compound",
            () -> new ConsumableItem(new Item.Properties(), "item.carvercraft.polishing_compound.tooltip"));


    // --- Opaque stones: rough -> slab -> cabochon (and optionally tumbled) ---
    public static final DeferredItem<Item> ROUGH_AGATE = simple("rough_agate");
    public static final DeferredItem<Item> AGATE_SLAB = simple("agate_slab");
    public static final DeferredItem<Item> TUMBLED_AGATE = simple("tumbled_agate");
    public static final DeferredItem<Item> AGATE_CABOCHON = simple("agate_cabochon");
    public static final DeferredItem<Item> ROUGH_JASPER = simple("rough_jasper");
    public static final DeferredItem<Item> JASPER_SLAB = simple("jasper_slab");
    public static final DeferredItem<Item> TUMBLED_JASPER = simple("tumbled_jasper");
    public static final DeferredItem<Item> JASPER_CABOCHON = simple("jasper_cabochon");
    // Bruneau jasper: picture jasper from the Bruneau canyon, an hour out of Boise.
    // A distinct variety, not a recolor — its own chain and its own ring.
    public static final DeferredItem<Item> ROUGH_BRUNEAU_JASPER = simple("rough_bruneau_jasper");
    public static final DeferredItem<Item> BRUNEAU_JASPER_SLAB = simple("bruneau_jasper_slab");
    public static final DeferredItem<Item> TUMBLED_BRUNEAU_JASPER = simple("tumbled_bruneau_jasper");
    public static final DeferredItem<Item> BRUNEAU_JASPER_CABOCHON = simple("bruneau_jasper_cabochon");
    public static final DeferredItem<Item> ROUGH_CARNELIAN = simple("rough_carnelian");
    public static final DeferredItem<Item> CARNELIAN_SLAB = simple("carnelian_slab");
    public static final DeferredItem<Item> TUMBLED_CARNELIAN = simple("tumbled_carnelian");
    public static final DeferredItem<Item> CARNELIAN_CABOCHON = simple("carnelian_cabochon");
    public static final DeferredItem<Item> ROUGH_ROSE_QUARTZ = simple("rough_rose_quartz");
    public static final DeferredItem<Item> ROSE_QUARTZ_SLAB = simple("rose_quartz_slab");
    public static final DeferredItem<Item> TUMBLED_ROSE_QUARTZ = simple("tumbled_rose_quartz");
    public static final DeferredItem<Item> ROSE_QUARTZ_CABOCHON = simple("rose_quartz_cabochon");
    public static final DeferredItem<Item> ROUGH_MALACHITE = simple("rough_malachite");
    public static final DeferredItem<Item> MALACHITE_SLAB = simple("malachite_slab");
    public static final DeferredItem<Item> TUMBLED_MALACHITE = simple("tumbled_malachite");
    public static final DeferredItem<Item> MALACHITE_CABOCHON = simple("malachite_cabochon");

    // --- Transparent stones: rough -> slab -> faceted -----------------------
    public static final DeferredItem<Item> ROUGH_PERIDOT = simple("rough_peridot");
    public static final DeferredItem<Item> TUMBLED_PERIDOT = simple("tumbled_peridot");
    public static final DeferredItem<Item> FACETED_PERIDOT = simple("faceted_peridot");
    public static final DeferredItem<Item> ROUGH_GARNET = simple("rough_garnet");
    public static final DeferredItem<Item> FACETED_GARNET = simple("faceted_garnet");
    public static final DeferredItem<Item> ROUGH_TOPAZ = simple("rough_topaz");
    public static final DeferredItem<Item> FACETED_TOPAZ = simple("faceted_topaz");
    public static final DeferredItem<Item> ROUGH_RUBY = simple("rough_ruby");
    // The first item this mod ever had, kept as the faceted end of the ruby chain.
    public static final DeferredItem<Item> RUBY = simple("ruby");
    public static final DeferredItem<Item> ROUGH_SAPPHIRE = simple("rough_sapphire");
    public static final DeferredItem<Item> FACETED_SAPPHIRE = simple("faceted_sapphire");
    public static final DeferredItem<Item> ROUGH_STAR_GARNET = simple("rough_star_garnet");
    public static final DeferredItem<Item> FACETED_STAR_GARNET = simple("faceted_star_garnet");

    // --- Vanilla gems, faceting machine only --------------------------------
    public static final DeferredItem<Item> FACETED_DIAMOND = simple("faceted_diamond");
    public static final DeferredItem<Item> FACETED_EMERALD = simple("faceted_emerald");
    public static final DeferredItem<Item> FACETED_AMETHYST = simple("faceted_amethyst");

    // --- Trinkets: tumbled stones, the weak tier ----------------------------
    public static final DeferredItem<Item> AGATE_TRINKET = ring("agate_trinket", TRINKET_DURABILITY, SILVER_ENCHANTABILITY,
            Bonus.of(Attributes.ARMOR, 0.5D));
    public static final DeferredItem<Item> JASPER_TRINKET = ring("jasper_trinket", TRINKET_DURABILITY, SILVER_ENCHANTABILITY,
            Bonus.of(Attributes.MAX_HEALTH, 1.0D));
    // The canyon stone softens the canyon's hazard: longer safe falls.
    public static final DeferredItem<Item> BRUNEAU_JASPER_TRINKET = ring("bruneau_jasper_trinket", TRINKET_DURABILITY, SILVER_ENCHANTABILITY,
            Bonus.of(Attributes.SAFE_FALL_DISTANCE, 0.5D));
    public static final DeferredItem<Item> CARNELIAN_TRINKET = ring("carnelian_trinket", TRINKET_DURABILITY, SILVER_ENCHANTABILITY,
            Bonus.of(Attributes.ATTACK_SPEED, 0.05D));
    public static final DeferredItem<Item> ROSE_QUARTZ_TRINKET = ring("rose_quartz_trinket", TRINKET_DURABILITY, SILVER_ENCHANTABILITY,
            Bonus.of(Attributes.MAX_ABSORPTION, 1.0D));
    public static final DeferredItem<Item> MALACHITE_TRINKET = ring("malachite_trinket", TRINKET_DURABILITY, SILVER_ENCHANTABILITY,
            Bonus.of(Attributes.ARMOR_TOUGHNESS, 0.5D));
    public static final DeferredItem<Item> PERIDOT_TRINKET = ring("peridot_trinket", TRINKET_DURABILITY, SILVER_ENCHANTABILITY,
            new Bonus(Attributes.MOVEMENT_SPEED, 0.025D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    // --- Rings: cabochons (opaque) and faceted gems (transparent) -----------
    public static final DeferredItem<Item> AGATE_RING = ring("agate_ring", STERLING_RING_DURABILITY, STERLING_ENCHANTABILITY,
            Bonus.of(Attributes.ARMOR, 1.0D));
    public static final DeferredItem<Item> JASPER_RING = ring("jasper_ring", STERLING_RING_DURABILITY, STERLING_ENCHANTABILITY,
            Bonus.of(Attributes.MAX_HEALTH, 2.0D));
    public static final DeferredItem<Item> BRUNEAU_JASPER_RING = ring("bruneau_jasper_ring", STERLING_RING_DURABILITY, STERLING_ENCHANTABILITY,
            Bonus.of(Attributes.SAFE_FALL_DISTANCE, 1.0D));
    public static final DeferredItem<Item> CARNELIAN_RING = ring("carnelian_ring", STERLING_RING_DURABILITY, STERLING_ENCHANTABILITY,
            Bonus.of(Attributes.ATTACK_SPEED, 0.1D));
    public static final DeferredItem<Item> ROSE_QUARTZ_RING = ring("rose_quartz_ring", STERLING_RING_DURABILITY, STERLING_ENCHANTABILITY,
            Bonus.of(Attributes.MAX_ABSORPTION, 2.0D));
    public static final DeferredItem<Item> MALACHITE_RING = ring("malachite_ring", STERLING_RING_DURABILITY, STERLING_ENCHANTABILITY,
            Bonus.of(Attributes.ARMOR_TOUGHNESS, 1.0D));
    public static final DeferredItem<Item> PERIDOT_RING = ring("peridot_ring", GOLD_RING_DURABILITY, GOLD_ENCHANTABILITY,
            new Bonus(Attributes.MOVEMENT_SPEED, 0.05D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static final DeferredItem<Item> GARNET_RING = ring("garnet_ring", GOLD_RING_DURABILITY, GOLD_ENCHANTABILITY,
            Bonus.of(Attributes.ATTACK_DAMAGE, 0.75D));
    public static final DeferredItem<Item> TOPAZ_RING = ring("topaz_ring", GOLD_RING_DURABILITY, GOLD_ENCHANTABILITY,
            Bonus.of(Attributes.LUCK, 0.5D));
    public static final DeferredItem<Item> RUBY_RING = ring("ruby_ring", GOLD_RING_DURABILITY, GOLD_ENCHANTABILITY,
            Bonus.of(Attributes.ATTACK_DAMAGE, 1.25D));
    public static final DeferredItem<Item> SAPPHIRE_RING = ring("sapphire_ring", GOLD_RING_DURABILITY, GOLD_ENCHANTABILITY,
            Bonus.of(Attributes.KNOCKBACK_RESISTANCE, 0.1D));
    public static final DeferredItem<Item> STAR_GARNET_RING = ring("star_garnet_ring", GOLD_RING_DURABILITY, GOLD_ENCHANTABILITY,
            Bonus.of(Attributes.MAX_HEALTH, 2.0D),
            Bonus.of(Attributes.LUCK, 1.0D));
    public static final DeferredItem<Item> DIAMOND_RING = ring("diamond_ring", GOLD_RING_DURABILITY, GOLD_ENCHANTABILITY,
            Bonus.of(Attributes.ARMOR, 1.5D),
            Bonus.of(Attributes.ARMOR_TOUGHNESS, 1.0D));
    public static final DeferredItem<Item> EMERALD_RING = ring("emerald_ring", GOLD_RING_DURABILITY, GOLD_ENCHANTABILITY,
            Bonus.of(Attributes.LUCK, 1.0D));
    public static final DeferredItem<Item> AMETHYST_RING = ring("amethyst_ring", GOLD_RING_DURABILITY, GOLD_ENCHANTABILITY,
            Bonus.of(Attributes.MAX_ABSORPTION, 3.0D));

    // --- Block items -------------------------------------------------------
    public static final DeferredItem<BlockItem> SILVER_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("silver_ore", ModBlocks.SILVER_ORE);
    public static final DeferredItem<BlockItem> DEEPSLATE_SILVER_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("deepslate_silver_ore", ModBlocks.DEEPSLATE_SILVER_ORE);
    public static final DeferredItem<BlockItem> ROCK_TUMBLER_ITEM =
            ITEMS.registerSimpleBlockItem("rock_tumbler", ModBlocks.ROCK_TUMBLER);
    public static final DeferredItem<BlockItem> TRIM_SAW_ITEM =
            ITEMS.registerSimpleBlockItem("trim_saw", ModBlocks.TRIM_SAW);
    public static final DeferredItem<BlockItem> CABBING_MACHINE_ITEM =
            ITEMS.registerSimpleBlockItem("cabbing_machine", ModBlocks.CABBING_MACHINE);
    public static final DeferredItem<BlockItem> FACETING_MACHINE_ITEM =
            ITEMS.registerSimpleBlockItem("faceting_machine", ModBlocks.FACETING_MACHINE);
    public static final DeferredItem<BlockItem> ALLOYER_ITEM =
            ITEMS.registerSimpleBlockItem("alloyer", ModBlocks.ALLOYER);
    public static final DeferredItem<BlockItem> CREATIVE_CHARGER_ITEM =
            ITEMS.registerSimpleBlockItem("creative_charger", ModBlocks.CREATIVE_CHARGER);
}