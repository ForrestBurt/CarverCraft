package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.item.RingItem;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CarverCraft.MODID);

    // The hello-world gem from session one; joins the real gem set later (rough/cut ruby).
    public static final DeferredItem<Item> RUBY = ITEMS.registerSimpleItem("ruby", new Item.Properties());

    // Silver
    public static final DeferredItem<Item> RAW_SILVER = ITEMS.registerSimpleItem("raw_silver", new Item.Properties());
    public static final DeferredItem<Item> SILVER_INGOT = ITEMS.registerSimpleItem("silver_ingot", new Item.Properties());

    // Rough gems (found in stone, tumbler input)
    public static final DeferredItem<Item> ROUGH_JASPER = ITEMS.registerSimpleItem("rough_jasper", new Item.Properties());
    public static final DeferredItem<Item> ROUGH_GARNET = ITEMS.registerSimpleItem("rough_garnet", new Item.Properties());
    public static final DeferredItem<Item> ROUGH_AGATE = ITEMS.registerSimpleItem("rough_agate", new Item.Properties());

    // Polished gems (tumbler output)
    public static final DeferredItem<Item> POLISHED_JASPER = ITEMS.registerSimpleItem("polished_jasper", new Item.Properties());
    public static final DeferredItem<Item> POLISHED_GARNET = ITEMS.registerSimpleItem("polished_garnet", new Item.Properties());
    public static final DeferredItem<Item> POLISHED_AGATE = ITEMS.registerSimpleItem("polished_agate", new Item.Properties());

    // Jewelry — a plain silver band, then one ring per stone.
    // Effects lean on the traditional lapidary associations for each stone.
    public static final DeferredItem<Item> SILVER_RING = ITEMS.register("silver_ring",
            () -> new RingItem(new Item.Properties().stacksTo(1)));

    // Jasper, "the supreme nurturer" — endurance.
    public static final DeferredItem<Item> JASPER_RING = ITEMS.register("jasper_ring",
            () -> new RingItem(new Item.Properties().stacksTo(1), Attributes.MAX_HEALTH, 4.0D));

    // Garnet — vitality and energy.
    public static final DeferredItem<Item> GARNET_RING = ITEMS.register("garnet_ring",
            () -> new RingItem(new Item.Properties().stacksTo(1), Attributes.ATTACK_DAMAGE, 1.0D));

    // Agate — stability and protection.
    public static final DeferredItem<Item> AGATE_RING = ITEMS.register("agate_ring",
            () -> new RingItem(new Item.Properties().stacksTo(1), Attributes.ARMOR, 2.0D));

    // Ruby — passion; the hello-world stone gets fortune's favor.
    public static final DeferredItem<Item> RUBY_RING = ITEMS.register("ruby_ring",
            () -> new RingItem(new Item.Properties().stacksTo(1), Attributes.LUCK, 1.0D));

    // Block items
    public static final DeferredItem<BlockItem> SILVER_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("silver_ore", ModBlocks.SILVER_ORE);
    public static final DeferredItem<BlockItem> DEEPSLATE_SILVER_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("deepslate_silver_ore", ModBlocks.DEEPSLATE_SILVER_ORE);
    public static final DeferredItem<BlockItem> ROCK_TUMBLER_ITEM =
            ITEMS.registerSimpleBlockItem("rock_tumbler", ModBlocks.ROCK_TUMBLER);
}
