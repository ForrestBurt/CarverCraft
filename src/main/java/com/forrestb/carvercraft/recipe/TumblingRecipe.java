package com.forrestb.carvercraft.recipe;

import com.forrestb.carvercraft.registry.ModRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/** Rough stone in a barrel with grit, out smooth. Opaque stones only. */
public record TumblingRecipe(Ingredient input, ItemStack result, int time, float hardness) implements LapidaryRecipe {

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.TUMBLING_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TUMBLING_SERIALIZER.get();
    }
}
