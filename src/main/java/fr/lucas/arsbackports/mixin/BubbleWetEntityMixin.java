package fr.lucas.arsbackports.mixin;

import fr.lucas.arsbackports.entity.BubbleEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.21.1 EntityMixin, bubble part: a living entity riding a bubble counts as wet (as if under rain) and cannot be set
 * on fire. Ars 4.12.7's own EntityMixin already does the same for the Soaked effect; both injections coexist.
 */
@Mixin(Entity.class)
public abstract class BubbleWetEntityMixin {

    private boolean ars_backports$isInBubble() {
        return ((Object) this) instanceof LivingEntity living && living.getVehicle() instanceof BubbleEntity;
    }

    @Inject(method = "isInWaterOrRain", at = @At("RETURN"), cancellable = true)
    private void ars_backports$bubbleIsWet(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && ars_backports$isInBubble()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "setRemainingFireTicks", at = @At("HEAD"), cancellable = true)
    private void ars_backports$bubbleNoFire(int remainingFireTicks, CallbackInfo ci) {
        if (remainingFireTicks > 0 && ars_backports$isInBubble()) {
            ((Entity) (Object) this).clearFire();
            ci.cancel();
        }
    }
}
