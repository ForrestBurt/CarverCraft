package com.forrestb.carvercraft.item;

import com.forrestb.carvercraft.registry.ModDataComponents;
import com.forrestb.carvercraft.registry.ModEnchantments;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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

    // Alternative band metals, stored in the BAND data component by the crafting
    // recipe. Electrum is the enchanter's metal; rose gold the hardest gold alloy.
    public static final String BAND_ELECTRUM = "electrum";
    public static final String BAND_ROSE_GOLD = "rose_gold";
    private static final int ELECTRUM_ENCHANTABILITY = 32;
    private static final int ROSE_GOLD_ENCHANTABILITY = 26;

    // Tints multiplied over a faceted ring's band layer. Each is its metal ramp's
    // brightest entry, which is what the band layer was normalised against — so the
    // default gold case reproduces the original texture exactly.
    public static final int TINT_GOLD = 0xFAE296;
    public static final int TINT_ELECTRUM = 0xF0E8B4;
    public static final int TINT_ROSE_GOLD = 0xF4CCBC;

    /** Colour for a faceted ring's band layer, from whatever metal it was set in. */
    public static int bandTint(ItemStack stack) {
        return switch (stack.getOrDefault(ModDataComponents.BAND.get(), "")) {
            case BAND_ELECTRUM -> TINT_ELECTRUM;
            case BAND_ROSE_GOLD -> TINT_ROSE_GOLD;
            default -> TINT_GOLD;
        };
    }

    /** One attribute grant. */
    public record Bonus(Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation) {
        public static Bonus of(Holder<Attribute> attribute, double amount) {
            return new Bonus(attribute, amount, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private final List<Bonus> bonuses;
    private final int enchantability;

    public RingItem(Properties properties, int enchantability, Bonus... bonuses) {
        super(properties);
        this.enchantability = enchantability;
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

    /** The tier's base metal: silver 15, sterling 18, gold 22. */
    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    /**
     * Stack-sensitive enchantability (NeoForge hook the enchanting table consults):
     * an electrum or rose gold setting overrides the tier's base metal.
     */
    @Override
    public int getEnchantmentValue(ItemStack stack) {
        String band = stack.getOrDefault(ModDataComponents.BAND.get(), "");
        return switch (band) {
            case BAND_ELECTRUM -> ELECTRUM_ENCHANTABILITY;
            case BAND_ROSE_GOLD -> ROSE_GOLD_ENCHANTABILITY;
            default -> enchantability;
        };
    }

    /** Name the metal when it isn't the tier default. */
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        String band = stack.getOrDefault(ModDataComponents.BAND.get(), "");
        if (!band.isEmpty()) {
            tooltipComponents.add(Component.translatable("tooltip.carvercraft.band." + band)
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
