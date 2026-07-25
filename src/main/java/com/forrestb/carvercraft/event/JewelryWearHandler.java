package com.forrestb.carvercraft.event;

import com.forrestb.carvercraft.CarverCraft;
import com.forrestb.carvercraft.item.RingItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * Jewelry wears like armor: every equipped ring and trinket takes durability when its
 * wearer takes a hit that armor would care about. Same wear formula as armor —
 * max(1, damage / 4) — and the same exemptions: damage tagged bypasses_armor (falls,
 * starving, the void) spares the jewelry, which keeps the Bruneau ring's long-fall
 * gift from grinding the ring away.
 *
 * On break the stone's bonus vanishes with the item; Curios recalculates modifiers
 * when the slot empties. Unbreaking and Mending both apply, because the rings are in
 * minecraft's enchantable/durability item tag.
 */
@EventBusSubscriber(modid = CarverCraft.MODID, bus = EventBusSubscriber.Bus.GAME)
public class JewelryWearHandler {

    @SubscribeEvent
    static void onLivingDamage(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || event.getNewDamage() <= 0.0F) {
            return;
        }
        if (event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) {
            return;
        }
        if (entity instanceof Player player && player.getAbilities().instabuild) {
            return;
        }

        int wear = Math.max(1, (int) (event.getNewDamage() / 4.0F));
        ServerPlayer serverPlayer = entity instanceof ServerPlayer sp ? sp : null;

        CuriosApi.getCuriosInventory(entity).ifPresent(curios -> {
            IItemHandlerModifiable equipped = curios.getEquippedCurios();
            for (int slot = 0; slot < equipped.getSlots(); slot++) {
                ItemStack stack = equipped.getStackInSlot(slot);
                if (!(stack.getItem() instanceof RingItem) || !stack.isDamageableItem()) {
                    continue;
                }
                stack.hurtAndBreak(wear, (ServerLevel) entity.level(), serverPlayer,
                        item -> entity.playSound(SoundEvents.ITEM_BREAK, 0.8F, 0.9F));
                // Write back explicitly so Curios notices and syncs the slot.
                equipped.setStackInSlot(slot, stack.isEmpty() ? ItemStack.EMPTY : stack);
            }
        });
    }
}
