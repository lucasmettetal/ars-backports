package fr.lucas.arsbackports.mixin.glyph;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.method.MethodUnderfoot;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

/**
 * Backport of 1.21.1 "Underfoot Entity targeting" (#1957): when cast in the air or on an entity, Underfoot targets
 * the caster's mount, else the first entity right below/around the caster, else (as before) the block below.
 * Same methods as 1.21.1 (onCast, onCastOnEntity); the block variants are unchanged.
 */
@Mixin(MethodUnderfoot.class)
public abstract class UnderfootEntityMixin {

    private static final String ON_RESOLVE_EFFECT =
            "Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;onResolveEffect(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/HitResult;)V";

    private static HitResult ars_backports$target(LivingEntity caster, HitResult blockBelow) {
        if (caster.getVehicle() != null) {
            return new EntityHitResult(caster.getVehicle());
        }
        List<Entity> nearbyEntities = caster.level().getEntities(caster, caster.getBoundingBox().inflate(0.1, 1.5, 0.1));
        return nearbyEntities.isEmpty() ? blockBelow : new EntityHitResult(nearbyEntities.get(0));
    }

    @Redirect(method = "onCast", remap = false, at = @At(value = "INVOKE", target = ON_RESOLVE_EFFECT))
    private void ars_backports$onCast(SpellResolver resolver, Level level, HitResult hit, ItemStack stack, LivingEntity caster,
                                      Level world, SpellStats spellStats, SpellContext context, SpellResolver resolverArg) {
        resolver.onResolveEffect(level, ars_backports$target(caster, hit));
    }

    @Redirect(method = "onCastOnEntity", remap = false, at = @At(value = "INVOKE", target = ON_RESOLVE_EFFECT))
    private void ars_backports$onCastOnEntity(SpellResolver resolver, Level level, HitResult hit, ItemStack stack, LivingEntity caster,
                                              Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext,
                                              SpellResolver resolverArg) {
        resolver.onResolveEffect(level, ars_backports$target(caster, hit));
    }
}
