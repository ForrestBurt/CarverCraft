package com.forrestb.carvercraft.block.entity;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.menu.LapidaryMenu;
import com.forrestb.carvercraft.recipe.LapidaryRecipe;
import com.forrestb.carvercraft.registry.ModBlockEntities;
import com.forrestb.carvercraft.registry.ModItems;
import com.forrestb.carvercraft.registry.ModMenus;
import com.forrestb.carvercraft.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

/** Barrel of grit. No cutting, no power, no skill — a baroque pebble in six weeks. */
public class RockTumblerBlockEntity extends AbstractLapidaryBlockEntity {

    public static final ResourceLocation GUI =
            ResourceLocation.fromNamespaceAndPath(CarverCraft.MODID, "textures/gui/rock_tumbler.png");

    public RockTumblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROCK_TUMBLER.get(), pos, state);
    }

    @Override
    protected RecipeType<? extends LapidaryRecipe> recipeType() {
        return ModRecipes.TUMBLING_TYPE.get();
    }

    @Override
    public float maxHardness() {
        return 7.0F;
    }

    @Override
    public int energyPerTick() {
        return 0;
    }

    @Override
    public int energyCapacity() {
        return 0;
    }

    @Override
    public MenuType<LapidaryMenu> menuType() {
        return ModMenus.ROCK_TUMBLER.get();
    }

    @Override
    public ResourceLocation guiTexture() {
        return GUI;
    }

    @Override
    protected Component defaultName() {
        return Component.translatable("block.carvercraft.rock_tumbler");
    }

    /** A barrel doesn't turn for free: silicon carbide grit, one per cycle. */
    @Override
    protected Item consumableItem() {
        return ModItems.SILICON_CARBIDE_GRIT.get();
    }

    @Override
    protected SoundEvent workingSound() {
        return SoundEvents.GRAVEL_HIT;
    }
}
