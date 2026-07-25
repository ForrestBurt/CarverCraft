package com.forrestb.carvercraft.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * One serializer implementation for all three lapidary recipe types — they carry
 * identical fields, so triplicating codecs would just be three places to fix a bug.
 */
public class LapidaryRecipeSerializer<T extends LapidaryRecipe> implements RecipeSerializer<T> {

    @FunctionalInterface
    public interface Factory<T extends LapidaryRecipe> {
        T create(Ingredient input, ItemStack result, int time, float hardness);
    }

    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public LapidaryRecipeSerializer(Factory<T> factory) {
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(LapidaryRecipe::input),
                ItemStack.CODEC.fieldOf("result").forGetter(LapidaryRecipe::result),
                Codec.INT.optionalFieldOf("time", LapidaryRecipe.DEFAULT_TIME).forGetter(LapidaryRecipe::time),
                Codec.FLOAT.optionalFieldOf("hardness", LapidaryRecipe.DEFAULT_HARDNESS).forGetter(LapidaryRecipe::hardness)
        ).apply(instance, factory::create));

        this.streamCodec = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, LapidaryRecipe::input,
                ItemStack.STREAM_CODEC, LapidaryRecipe::result,
                ByteBufCodecs.VAR_INT, LapidaryRecipe::time,
                ByteBufCodecs.FLOAT, LapidaryRecipe::hardness,
                factory::create
        );
    }

    @Override
    public MapCodec<T> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return this.streamCodec;
    }
}
