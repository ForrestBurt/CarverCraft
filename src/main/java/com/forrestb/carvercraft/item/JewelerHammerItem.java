package com.forrestb.carvercraft.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * The bench hammer. Every jewelry material is drawn or planished with it, so it sits
 * in the crafting grid as a tool rather than an ingredient: NeoForge's stack-sensitive
 * crafting remainder hands it back one point more worn, and returns nothing once it's
 * spent.
 *
 * This exists for recipe compatibility as much as flavor. Bands used to be four ingots
 * in the classic ring pattern — the single most contested shape in modded Minecraft.
 * Routing every band through mod-specific wire means CarverCraft cannot collide with
 * anyone else's recipes, no matter whose silver you feed it.
 */
public class JewelerHammerItem extends Item {

    public JewelerHammerItem(Properties properties) {
        super(properties);
    }

    /** Survives the craft, one point worse for it. */
    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        ItemStack remainder = stack.copy();
        remainder.setCount(1);
        int damage = remainder.getDamageValue() + 1;
        if (damage >= remainder.getMaxDamage()) {
            return ItemStack.EMPTY;
        }
        remainder.setDamageValue(damage);
        return remainder;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.carvercraft.jewelers_hammer.tooltip")
                .withStyle(ChatFormatting.GRAY));
    }
}
