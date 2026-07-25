package com.forrestb.carvercraft.recipe;

import com.forrestb.carvercraft.registry.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * Two metals fused in a crucible at real jeweller's ratios: 7 silver + 1 copper is
 * sterling, silver + gold is electrum, 3 gold + 1 copper is rose gold.
 *
 * Matching is order-agnostic across the two input slots — a player should never have
 * to care which slot the copper went in. Counts are checked when the machine runs,
 * not when the recipe is looked up, so a short stack holds the clock at zero rather
 * than hiding the recipe.
 */
public record AlloyingRecipe(CountedIngredient primary, CountedIngredient secondary,
                             ItemStack result, int time) implements Recipe<AlloyingRecipeInput> {

    public static final int DEFAULT_TIME = 400;

    /** An ingredient that must arrive in quantity — the ratio is the recipe. */
    public record CountedIngredient(Ingredient ingredient, int count) {
        public static final Codec<CountedIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(CountedIngredient::ingredient),
                Codec.INT.optionalFieldOf("count", 1).forGetter(CountedIngredient::count)
        ).apply(instance, CountedIngredient::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CountedIngredient> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, CountedIngredient::ingredient,
                        ByteBufCodecs.VAR_INT, CountedIngredient::count,
                        CountedIngredient::new);

        public boolean test(ItemStack stack) {
            return ingredient.test(stack);
        }

        public boolean testWithCount(ItemStack stack) {
            return ingredient.test(stack) && stack.getCount() >= count;
        }
    }

    /** True if this stack could be either metal of this recipe — slot filtering. */
    public boolean isIngredient(ItemStack stack) {
        return primary.test(stack) || secondary.test(stack);
    }

    @Override
    public boolean matches(AlloyingRecipeInput input, Level level) {
        return (primary.test(input.first()) && secondary.test(input.second()))
                || (primary.test(input.second()) && secondary.test(input.first()));
    }

    /** Identity and quantity, in either slot order — the gate for actually running. */
    public boolean matchesWithCounts(ItemStack a, ItemStack b) {
        return (primary.testWithCount(a) && secondary.testWithCount(b))
                || (primary.testWithCount(b) && secondary.testWithCount(a));
    }

    /** How much to consume from slot stack {@code a}, resolving which metal it is. */
    public int consumeFromFirst(ItemStack a, ItemStack b) {
        return primary.testWithCount(a) && secondary.testWithCount(b) ? primary.count() : secondary.count();
    }

    public int consumeFromSecond(ItemStack a, ItemStack b) {
        return primary.testWithCount(a) && secondary.testWithCount(b) ? secondary.count() : primary.count();
    }

    @Override
    public ItemStack assemble(AlloyingRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(primary.ingredient());
        list.add(secondary.ingredient());
        return list;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALLOYING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ALLOYING_TYPE.get();
    }
}
