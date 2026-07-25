package com.forrestb.carvercraft.recipe;

import com.forrestb.carvercraft.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * One rough stone in, one polished stone out, over a per-recipe number of ticks.
 *
 * The time field is per-recipe on purpose: harder stones belong on the wheel
 * longer. Corundum has no business finishing as fast as chalcedony.
 *
 * Uses vanilla's SingleRecipeInput since a tumbler lane is shaped exactly like
 * a furnace — one input slot, one output slot.
 */
public record TumblingRecipe(Ingredient input, ItemStack result, int time) implements Recipe<SingleRecipeInput> {

    public static final int DEFAULT_TIME = 600;

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.input.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        // Always copy: the recipe's result exists once, but each craft needs its own stack.
        return this.result.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.input);
        return list;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.TUMBLING_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TUMBLING_SERIALIZER.get();
    }
}
