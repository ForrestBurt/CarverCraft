package com.forrestb.carvercraft.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

/**
 * Adds a chance for one extra item (a rough gem) when a matching loot table rolls.
 * Which blocks it applies to is decided by the conditions in the JSON files under
 * data/carvercraft/loot_modifiers/.
 */
public class StoneGemLootModifier extends LootModifier {
    public static final MapCodec<StoneGemLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance).and(instance.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(m -> m.chance)
            )).apply(instance, StoneGemLootModifier::new));

    private final Item item;
    private final float chance;

    public StoneGemLootModifier(LootItemCondition[] conditions, Item item, float chance) {
        super(conditions);
        this.item = item;
        this.chance = chance;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() < this.chance) {
            generatedLoot.add(new ItemStack(this.item));
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
