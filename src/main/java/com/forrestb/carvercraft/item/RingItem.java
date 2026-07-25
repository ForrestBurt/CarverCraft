package com.forrestb.carvercraft.item;

import com.forrestb.carvercraft.registry.ModEnchantments;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
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
 *
 * Brilliance is the one place enchantments touch code: vanilla only applies
 * enchantment attribute effects in vanilla equipment slots, and a Curios slot
 * isn't one, so the ring reads its own enchantment level and scales the stone's
 * modifiers itself. The enchantment definition lives entirely in JSON.
 */
public class RingItem extends Item implements ICurioItem {

    /** Effect growth per level of Brilliance: I +15%, II +30%, III +45%. */
    private static final double BRILLIANCE_PER_LEVEL = 0.15D;

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
        double scale = 1.0D + BRILLIANCE_PER_LEVEL * brillianceLevel(stack);
        for (int i = 0; i < bonuses.size(); i++) {
            Bonus bonus = bonuses.get(i);
            // Each bonus needs its own id, or a multi-bonus ring would collapse into one.
            ResourceLocation modifierId = i == 0 ? id : id.withSuffix("_" + i);
            modifiers.put(bonus.attribute(),
                    new AttributeModifier(modifierId, bonus.amount() * scale, bonus.operation()));
        }
        return modifiers;
    }

    private static int brillianceLevel(ItemStack stack) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(ModEnchantments.BRILLIANCE)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    /** Rings take enchantments at the table; the default gate wants damageable items. */
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    /** Silverwork enchants about like gold armor does. */
    @Override
    public int getEnchantmentValue() {
        return 18;
    }
}
