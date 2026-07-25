package com.forrestb.carvercraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * A wearable band. Curios handles the slot and persistence; all this class does is
 * hand back the attribute modifier the stone is worth while it's equipped.
 *
 * The modifier id comes from Curios (one per slot), so wearing two different rings
 * stacks correctly instead of one silently overwriting the other.
 */
public class RingItem extends Item implements ICurioItem {
    @Nullable
    private final Holder<Attribute> attribute;
    private final double amount;
    private final AttributeModifier.Operation operation;

    /** A plain band with no stone set: wearable, does nothing. */
    public RingItem(Properties properties) {
        this(properties, null, 0.0D, AttributeModifier.Operation.ADD_VALUE);
    }

    public RingItem(Properties properties, @Nullable Holder<Attribute> attribute, double amount) {
        this(properties, attribute, amount, AttributeModifier.Operation.ADD_VALUE);
    }

    public RingItem(Properties properties, @Nullable Holder<Attribute> attribute, double amount,
                    AttributeModifier.Operation operation) {
        super(properties);
        this.attribute = attribute;
        this.amount = amount;
        this.operation = operation;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                                ResourceLocation id,
                                                                                ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();
        if (this.attribute != null) {
            modifiers.put(this.attribute, new AttributeModifier(id, this.amount, this.operation));
        }
        return modifiers;
    }
}
