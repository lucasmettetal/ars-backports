package fr.lucas.arsbackports.mixin.jar;

import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.mob_jar.ChickenBehavior;
import com.hollingsworth.arsnouveau.common.mob_jar.FrogBehavior;
import com.hollingsworth.arsnouveau.common.mob_jar.PandaBehavior;
import com.hollingsworth.arsnouveau.common.mob_jar.PiglinBehavior;
import com.hollingsworth.arsnouveau.common.mob_jar.PufferfishBehavior;
import com.hollingsworth.arsnouveau.common.mob_jar.SheepBehavior;
import com.hollingsworth.arsnouveau.common.mob_jar.VillagerBehavior;
import fr.lucas.arsbackports.mobjar.JarBehaviors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Backport of Ars Nouveau 1.21.1 "disable mob jar behaviors with redstone power" (commit a97a3389): these passive
 * behaviors (eggs, slime balls, sneezes, bartering, puffing, wool regrowth, villager trades/work) pause while the jar
 * is powered. 1.21.1 adds {@code || isPowered(tile)} to the early return of each tick; their ticks do nothing on the
 * client in 4.12.7 either, so cancelling the whole tick is equivalent.
 */
@Mixin({ChickenBehavior.class, FrogBehavior.class, PandaBehavior.class, PiglinBehavior.class,
        PufferfishBehavior.class, SheepBehavior.class, VillagerBehavior.class})
public abstract class PassiveJarBehaviorMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void ars_backports$pauseWhenPowered(MobJarTile tile, CallbackInfo ci) {
        if (JarBehaviors.isPowered(tile)) {
            ci.cancel();
        }
    }
}
