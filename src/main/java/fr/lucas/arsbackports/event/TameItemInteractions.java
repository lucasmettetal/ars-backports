package fr.lucas.arsbackports.event;

import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import com.hollingsworth.arsnouveau.common.entity.EntityDrygmy;
import com.hollingsworth.arsnouveau.common.entity.GiftStarbuncle;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import fr.lucas.arsbackports.ArsBackports;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Backport of 1.21.1 "right click to give tame items to starbies and drygmies" (#1681): instead of throwing the item
 * on the ground, players can hand a gold nugget to a wild (or gift) Starbuncle and a Wilden drop to a wild Drygmy.
 * 1.21.1 adds this at the start of each mobInteract; Forge's EntityInteract event fires right before mobInteract, so
 * no mixin is needed. Server side only, like 1.21.1.
 */
@Mod.EventBusSubscriber(modid = ArsBackports.MODID)
public final class TameItemInteractions {

    private TameItemInteractions() {
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if (event.getHand() != InteractionHand.MAIN_HAND || player.level().isClientSide) {
            return;
        }

        ItemStack stack = player.getItemInHand(event.getHand());
        boolean infinite = player.getAbilities().instabuild;

        if (event.getTarget() instanceof Starbuncle starbuncle) {
            if (!starbuncle.isTamed() && starbuncle.getHeldStack().isEmpty() && stack.is(Tags.Items.NUGGETS_GOLD)) {
                starbuncle.setHeldStack(infinite ? stack.copyWithCount(1) : stack.split(1));
                succeed(event);
            }
        } else if (event.getTarget() instanceof GiftStarbuncle gift) {
            if (!gift.isTaming() && gift.getMainHandItem().isEmpty() && stack.is(Tags.Items.NUGGETS_GOLD)) {
                gift.setItemInHand(InteractionHand.MAIN_HAND, infinite ? stack.copyWithCount(1) : stack.split(1));
                gift.setTaming(true);
                succeed(event);
            }
        } else if (event.getTarget() instanceof EntityDrygmy drygmy) {
            if (!drygmy.isTamed() && !drygmy.getEntityData().get(EntityDrygmy.BEING_TAMED) && stack.is(ItemTagProvider.WILDEN_DROP_TAG)) {
                drygmy.getEntityData().set(EntityDrygmy.BEING_TAMED, true);
                if (!infinite) {
                    stack.shrink(1);
                }
                succeed(event);
            }
        }
    }

    private static void succeed(PlayerInteractEvent.EntityInteract event) {
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
