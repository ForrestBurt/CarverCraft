package com.forrestb.carvercraft.registry;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.recipe.AlloyingRecipe;
import com.forrestb.carvercraft.recipe.AlloyingRecipeSerializer;
import com.forrestb.carvercraft.recipe.CabbingRecipe;
import com.forrestb.carvercraft.recipe.FacetingRecipe;
import com.forrestb.carvercraft.recipe.LapidaryRecipeSerializer;
import com.forrestb.carvercraft.recipe.SawingRecipe;
import com.forrestb.carvercraft.recipe.TumblingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, CarverCraft.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, CarverCraft.MODID);

    private static <T extends Recipe<?>> Supplier<RecipeType<T>> type(String name) {
        return RECIPE_TYPES.register(name, () -> RecipeType.<T>simple(
                ResourceLocation.fromNamespaceAndPath(CarverCraft.MODID, name)));
    }

    public static final Supplier<RecipeType<TumblingRecipe>> TUMBLING_TYPE = type("tumbling");
    public static final Supplier<RecipeType<SawingRecipe>> SAWING_TYPE = type("sawing");
    public static final Supplier<RecipeType<CabbingRecipe>> CABBING_TYPE = type("cabbing");
    public static final Supplier<RecipeType<FacetingRecipe>> FACETING_TYPE = type("faceting");
    public static final Supplier<RecipeType<AlloyingRecipe>> ALLOYING_TYPE = type("alloying");

    public static final Supplier<RecipeSerializer<TumblingRecipe>> TUMBLING_SERIALIZER =
            RECIPE_SERIALIZERS.register("tumbling", () -> new LapidaryRecipeSerializer<>(TumblingRecipe::new));
    public static final Supplier<RecipeSerializer<SawingRecipe>> SAWING_SERIALIZER =
            RECIPE_SERIALIZERS.register("sawing", () -> new LapidaryRecipeSerializer<>(SawingRecipe::new));
    public static final Supplier<RecipeSerializer<CabbingRecipe>> CABBING_SERIALIZER =
            RECIPE_SERIALIZERS.register("cabbing", () -> new LapidaryRecipeSerializer<>(CabbingRecipe::new));
    public static final Supplier<RecipeSerializer<FacetingRecipe>> FACETING_SERIALIZER =
            RECIPE_SERIALIZERS.register("faceting", () -> new LapidaryRecipeSerializer<>(FacetingRecipe::new));
    public static final Supplier<RecipeSerializer<AlloyingRecipe>> ALLOYING_SERIALIZER =
            RECIPE_SERIALIZERS.register("alloying", AlloyingRecipeSerializer::new);
}
