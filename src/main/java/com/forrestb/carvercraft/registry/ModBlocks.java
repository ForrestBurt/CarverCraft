package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.block.CabbingMachineBlock;
import com.forrestb.carvercraft.block.CreativeChargerBlock;
import com.forrestb.carvercraft.block.FacetingMachineBlock;
import com.forrestb.carvercraft.block.RockTumblerBlock;
import com.forrestb.carvercraft.block.TrimSawBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CarverCraft.MODID);

    public static final DeferredBlock<Block> SILVER_ORE = BLOCKS.registerSimpleBlock("silver_ore",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 3.0F));

    public static final DeferredBlock<Block> DEEPSLATE_SILVER_ORE = BLOCKS.registerSimpleBlock("deepslate_silver_ore",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(4.5F, 3.0F)
                    .sound(SoundType.DEEPSLATE));

    public static final DeferredBlock<RockTumblerBlock> ROCK_TUMBLER = BLOCKS.registerBlock("rock_tumbler",
            RockTumblerBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(2.5F)
                    .sound(SoundType.METAL));

    public static final DeferredBlock<TrimSawBlock> TRIM_SAW = BLOCKS.registerBlock("trim_saw",
            TrimSawBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F)
                    .sound(SoundType.METAL));

    public static final DeferredBlock<CabbingMachineBlock> CABBING_MACHINE = BLOCKS.registerBlock("cabbing_machine",
            CabbingMachineBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F)
                    .sound(SoundType.METAL));

    public static final DeferredBlock<FacetingMachineBlock> FACETING_MACHINE = BLOCKS.registerBlock("faceting_machine",
            FacetingMachineBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F)
                    .sound(SoundType.METAL));

    /** Testing only — infinite FE, creative tab, no recipe. */
    public static final DeferredBlock<CreativeChargerBlock> CREATIVE_CHARGER = BLOCKS.registerBlock("creative_charger",
            CreativeChargerBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(-1.0F, 3600000.0F)
                    .sound(SoundType.METAL));
}
