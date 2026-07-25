package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.recipe.TumblingRecipe;
import com.forrestb.carvercraft.recipe.TumblingRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, CarverCraft.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, CarverCraft.MODID);

    public static final Supplier<RecipeType<TumblingRecipe>> TUMBLING_TYPE =
            RECIPE_TYPES.register("tumbling", () -> RecipeType.<TumblingRecipe>simple(
                    ResourceLocation.fromNamespaceAndPath(CarverCraft.MODID, "tumbling")));

    public static final Supplier<RecipeSerializer<TumblingRecipe>> TUMBLING_SERIALIZER =
            RECIPE_SERIALIZERS.register("tumbling", TumblingRecipeSerializer::new);
}
