package fr.lucas.arsbackports.mixin.ritual;

import com.hollingsworth.arsnouveau.common.ritual.DenySpawnRitual;
import fr.lucas.arsbackports.ModTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Backport of 1.21.1 commits 258eec48 and c0278fa4 for the Ritual of Sanctuary: natural spawns of mobs in
 * {@code ars_nouveau:deny_spawn_ritual_whitelist} are denied too (not only monsters), mobs in
 * {@code ars_nouveau:deny_spawn_ritual_blacklist} never are. The condition is the 1.21.1 one; the rest of
 * denySpawn (cancel + deniedSpawn flag) is unchanged.
 */
@Mixin(DenySpawnRitual.class)
public abstract class DenySpawnTagsMixin {

    @Inject(method = "denySpawn", at = @At("HEAD"), cancellable = true, remap = false)
    private void ars_backports$denySpawn(MobSpawnEvent.FinalizeSpawn checkSpawn, CallbackInfoReturnable<Boolean> cir) {
        DenySpawnRitual self = (DenySpawnRitual) (Object) this;
        Mob entity = checkSpawn.getEntity();
        boolean shouldDeny = checkSpawn.getSpawnType() == MobSpawnType.NATURAL
                && (entity instanceof Enemy || entity.getType().is(ModTags.DENY_SPAWN_RITUAL_WHITELIST))
                && !entity.getType().is(ModTags.DENY_SPAWN_RITUAL_BLACKLIST)
                && entity.distanceToSqr(self.getPos().getX(), self.getPos().getY(), self.getPos().getZ()) <= self.radius * self.radius;
        if (shouldDeny) {
            checkSpawn.setSpawnCancelled(true);
            self.deniedSpawn = true;
        }
        cir.setReturnValue(shouldDeny);
    }
}
