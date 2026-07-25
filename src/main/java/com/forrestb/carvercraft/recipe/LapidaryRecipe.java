package com.forrestb.carvercraft.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * Shared shape for every lapidary process: one stone in, one stone out, over some
 * number of ticks, with a Mohs hardness that decides which machines can handle it.
 *
 * Hardness is the tier axis for the whole mod. A machine refuses anything above its
 * rating — a rubber barrel and silicon carbide grit genuinely cannot touch corundum.
 */
public interface LapidaryRecipe extends Recipe<SingleRecipeInput> {

    int DEFAULT_TIME = 600;
    float DEFAULT_HARDNESS = 7.0F;

    Ingredient input();

    ItemStack result();

    int time();

    /** Mohs hardness of the stone being worked. */
    float hardness();

    @Override
    default boolean matches(SingleRecipeInput input, Level level) {
        return input().test(input.item());
    }

    @Override
    default ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        // Always copy: the recipe's result exists once, each craft needs its own stack.
        return result().copy();
    }

    @Override
    default ItemStack getResultItem(HolderLookup.Provider registries) {
        return result();
    }

    @Override
    default NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input());
        return list;
    }

    @Override
    default boolean canCraftInDimensions(int width, int height) {
        return true;
    }
}
