package com.forrestb.carvercraft.recipe;

import com.forrestb.carvercraft.registry.ModRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/** Rough stone cut down to a preform on the trim saw. */
public record SawingRecipe(Ingredient input, ItemStack result, int time, float hardness) implements LapidaryRecipe {

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SAWING_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SAWING_SERIALIZER.get();
    }
}
