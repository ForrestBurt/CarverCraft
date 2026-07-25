package com.forrestb.carvercraft.recipe;

import com.forrestb.carvercraft.recipe.AlloyingRecipe.CountedIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * JSON shape:
 * {
 *   "type": "carvercraft:alloying",
 *   "primary":   { "ingredient": { "tag": "c:ingots/silver" }, "count": 7 },
 *   "secondary": { "ingredient": { "tag": "c:ingots/copper" } },
 *   "result": { "id": "carvercraft:sterling_silver_ingot", "count": 8 },
 *   "time": 400
 * }
 */
public class AlloyingRecipeSerializer implements RecipeSerializer<AlloyingRecipe> {

    private static final MapCodec<AlloyingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CountedIngredient.CODEC.fieldOf("primary").forGetter(AlloyingRecipe::primary),
            CountedIngredient.CODEC.fieldOf("secondary").forGetter(AlloyingRecipe::secondary),
            ItemStack.CODEC.fieldOf("result").forGetter(AlloyingRecipe::result),
            Codec.INT.optionalFieldOf("time", AlloyingRecipe.DEFAULT_TIME).forGetter(AlloyingRecipe::time)
    ).apply(instance, AlloyingRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, AlloyingRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    CountedIngredient.STREAM_CODEC, AlloyingRecipe::primary,
                    CountedIngredient.STREAM_CODEC, AlloyingRecipe::secondary,
                    ItemStack.STREAM_CODEC, AlloyingRecipe::result,
                    ByteBufCodecs.VAR_INT, AlloyingRecipe::time,
                    AlloyingRecipe::new);

    @Override
    public MapCodec<AlloyingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AlloyingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
