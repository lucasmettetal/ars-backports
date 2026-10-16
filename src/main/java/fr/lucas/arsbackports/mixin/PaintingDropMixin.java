package fr.lucas.arsbackports.mixin;

import fr.lucas.arsbackports.ArsBackports;
import fr.lucas.arsbackports.registry.ModDecorations;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Backport of 1.21.1 PaintingMixin: a broken Ars Backports painting drops a painting item of the same variant
 * instead of a plain painting.
 */
@Mixin(Painting.class)
public abstract class PaintingDropMixin extends HangingEntity {

    protected PaintingDropMixin(EntityType<? extends HangingEntity> type, Level level) {
        super(type, level);
    }

    @Shadow
    public abstract Holder<PaintingVariant> getVariant();

    @Inject(method = "dropItem", cancellable = true, at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/world/entity/decoration/Painting;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private void ars_backports$dropVariant(Entity brokenEntity, CallbackInfo ci) {
        ResourceKey<PaintingVariant> key = this.getVariant().unwrapKey().orElse(null);
        if (key != null && key.location().getNamespace().equals(ArsBackports.MODID)) {
            this.spawnAtLocation(ModDecorations.paintingItem(key.location()));
            ci.cancel();
        }
    }
}
