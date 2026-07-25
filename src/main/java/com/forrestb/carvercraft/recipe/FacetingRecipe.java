package com.forrestb.carvercraft.recipe;

import com.forrestb.carvercraft.registry.ModRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public record FacetingRecipe(Ingredient input, ItemStack result, int time, float hardness) implements LapidaryRecipe {

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.FACETING_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FACETING_SERIALIZER.get();
    }
}
