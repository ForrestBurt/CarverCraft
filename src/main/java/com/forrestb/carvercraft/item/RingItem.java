package com.forrestb.carvercraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * A wearable band. Curios handles the slot and persistence; this class only reports
 * what the stone is worth while it's equipped.
 *
 * The modifier id comes from Curios (one per slot), so two different rings stack
 * instead of one silently overwriting the other. A ring may carry more than one
 * modifier — the star garnet does.
 */
public class RingItem extends Item implements ICurioItem {

    /** One attribute grant. */
    public record Bonus(Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation) {
        public static Bonus of(Holder<Attribute> attribute, double amount) {
            return new Bonus(attribute, amount, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private final List<Bonus> bonuses;

    public RingItem(Properties properties, Bonus... bonuses) {
        super(properties);
        this.bonuses = List.of(bonuses);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                                ResourceLocation id,
                                                                                ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();
        for (int i = 0; i < bonuses.size(); i++) {
            Bonus bonus = bonuses.get(i);
            // Each bonus needs its own id, or a multi-bonus ring would collapse into one.
            ResourceLocation modifierId = i == 0 ? id : id.withSuffix("_" + i);
            modifiers.put(bonus.attribute(), new AttributeModifier(modifierId, bonus.amount(), bonus.operation()));
        }
        return modifiers;
    }
}
