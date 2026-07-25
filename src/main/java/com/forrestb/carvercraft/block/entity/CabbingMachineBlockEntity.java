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

/** Grinds a slab to a dome, then sands and polishes it. Opaque stone only. */
public class CabbingMachineBlockEntity extends AbstractLapidaryBlockEntity {

    public static final ResourceLocation GUI =
            ResourceLocation.fromNamespaceAndPath(CarverCraft.MODID, "textures/gui/cabbing_machine.png");

    public CabbingMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABBING_MACHINE.get(), pos, state);
    }

    @Override
    protected RecipeType<? extends LapidaryRecipe> recipeType() {
        return ModRecipes.CABBING_TYPE.get();
    }

    @Override
    public float maxHardness() {
        return 10.0F;
    }

    @Override
    public int energyPerTick() {
        return 30;
    }

    @Override
    public int energyCapacity() {
        return 30000;
    }

    @Override
    public MenuType<LapidaryMenu> menuType() {
        return ModMenus.CABBING_MACHINE.get();
    }

    @Override
    public ResourceLocation guiTexture() {
        return GUI;
    }

    @Override
    protected Component defaultName() {
        return Component.translatable("block.carvercraft.cabbing_machine");
    }

    @Override
    protected SoundEvent workingSound() {
        return SoundEvents.GRINDSTONE_USE;
    }
}
