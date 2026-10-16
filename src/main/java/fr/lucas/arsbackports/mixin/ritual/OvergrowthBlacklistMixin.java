package fr.lucas.arsbackports.mixin.ritual;

import com.hollingsworth.arsnouveau.common.ritual.RitualOvergrowth;
import fr.lucas.arsbackports.ModTags;
import net.minecraft.world.entity.AgeableMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Backport of 1.21.1 commit 258eec48: the Ritual of Overgrowth (bone block, ages babies up) skips mobs in
 * {@code ars_nouveau:overgrowth_blacklist}. The loop's only test is {@code a.isBaby()} (the only isBaby call of the
 * tick, checked in the 4.12.7 bytecode): reporting blacklisted mobs as adults is equivalent to 1.21.1's continue.
 */
@Mixin(RitualOvergrowth.class)
public abstract class OvergrowthBlacklistMixin {

    @Redirect(method = "tick", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/world/entity/AgeableMob;isBaby()Z"))
    private boolean ars_backports$skipBlacklisted(AgeableMob mob) {
        return !mob.getType().is(ModTags.OVERGROWTH_BLACKLIST) && mob.isBaby();
    }
}
