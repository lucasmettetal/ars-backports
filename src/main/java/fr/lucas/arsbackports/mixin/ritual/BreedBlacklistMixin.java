package fr.lucas.arsbackports.mixin.ritual;

import com.hollingsworth.arsnouveau.common.ritual.RitualBreed;
import fr.lucas.arsbackports.ModTags;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Backport of 1.21.1 commit 258eec48: the Ritual of Fertility skips animals in {@code ars_nouveau:breed_blacklist}.
 * 1.21.1 adds {@code if (blacklisted) continue;} at the start of the loop, whose only test is
 * {@code a.getAge() == 0 && a.canFallInLove()}: reporting a non-zero age for blacklisted animals is equivalent.
 */
@Mixin(RitualBreed.class)
public abstract class BreedBlacklistMixin {

    @Redirect(method = "tick", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/world/entity/animal/Animal;getAge()I"))
    private int ars_backports$skipBlacklisted(Animal animal) {
        return animal.getType().is(ModTags.BREED_BLACKLIST) ? 1 : animal.getAge();
    }
}
