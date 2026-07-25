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

/** Index wheel and dop. Transparent gems only, and the best gear in the mod. */
public class FacetingMachineBlockEntity extends AbstractLapidaryBlockEntity {

    public static final ResourceLocation GUI =
            ResourceLocation.fromNamespaceAndPath(CarverCraft.MODID, "textures/gui/faceting_machine.png");

    public FacetingMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FACETING_MACHINE.get(), pos, state);
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
        return 60;
    }

    @Override
    public int energyCapacity() {
        return 60000;
    }

    @Override
    public MenuType<LapidaryMenu> menuType() {
        return ModMenus.FACETING_MACHINE.get();
    }

    @Override
    public ResourceLocation guiTexture() {
        return GUI;
    }

    @Override
    protected Component defaultName() {
        return Component.translatable("block.carvercraft.faceting_machine");
    }

    @Override
    protected SoundEvent workingSound() {
        return SoundEvents.AMETHYST_CLUSTER_HIT;
    }
}
