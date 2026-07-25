package com.forrestb.carvercraft.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/** The two metal slots of the Alloyer, in slot order (not recipe order). */
public record AlloyingRecipeInput(ItemStack first, ItemStack second) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> first;
            case 1 -> second;
            default -> throw new IllegalArgumentException("No slot " + index + " in an alloying input");
        };
    }

    @Override
    public int size() {
        return 2;
    }
}
