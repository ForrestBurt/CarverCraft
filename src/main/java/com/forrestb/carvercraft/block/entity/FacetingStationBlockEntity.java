package com.forrestb.carvercraft.block.entity;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.menu.LapidaryMenu;
import com.forrestb.carvercraft.recipe.LapidaryRecipe;
import com.forrestb.carvercraft.registry.ModBlockEntities;
import com.forrestb.carvercraft.registry.ModMenus;
import com.forrestb.carvercraft.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

/** Powered faceting head. The only thing here that can finish corundum. */
public class FacetingStationBlockEntity extends AbstractLapidaryBlockEntity {

    public static final ResourceLocation GUI =
            ResourceLocation.fromNamespaceAndPath(CarverCraft.MODID, "textures/gui/faceting_station.png");

    public FacetingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FACETING_STATION.get(), pos, state);
    }

    @Override
    protected RecipeType<? extends LapidaryRecipe> recipeType() {
        return ModRecipes.FACETING_TYPE.get();
    }

    @Override
    public float maxHardness() {
        return 10.0F;
    }

    @Override
    public int energyPerTick() {
        return 40;
    }

    @Override
    public int energyCapacity() {
        return 40000;
    }

    @Override
    public MenuType<LapidaryMenu> menuType() {
        return ModMenus.FACETING_STATION.get();
    }

    @Override
    public ResourceLocation guiTexture() {
        return GUI;
    }

    @Override
    protected Component defaultName() {
        return Component.translatable("block.carvercraft.faceting_station");
    }

    @Override
    protected SoundEvent workingSound() {
        return SoundEvents.AMETHYST_CLUSTER_HIT;
    }
}
