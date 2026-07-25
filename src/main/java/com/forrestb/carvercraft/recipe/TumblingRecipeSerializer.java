package com.forrestb.carvercraft.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class TumblingRecipeSerializer implements RecipeSerializer<TumblingRecipe> {

    public static final MapCodec<TumblingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(TumblingRecipe::input),
            ItemStack.CODEC.fieldOf("result").forGetter(TumblingRecipe::result),
            com.mojang.serialization.Codec.INT
                    .optionalFieldOf("time", TumblingRecipe.DEFAULT_TIME)
                    .forGetter(TumblingRecipe::time)
    ).apply(instance, TumblingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TumblingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, TumblingRecipe::input,
            ItemStack.STREAM_CODEC, TumblingRecipe::result,
            ByteBufCodecs.VAR_INT, TumblingRecipe::time,
            TumblingRecipe::new
    );

    @Override
    public MapCodec<TumblingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, TumblingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
