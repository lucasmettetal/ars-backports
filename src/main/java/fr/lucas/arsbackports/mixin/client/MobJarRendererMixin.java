package fr.lucas.arsbackports.mixin.client;

import com.hollingsworth.arsnouveau.client.renderer.tile.MobJarRenderer;
import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Backport of 1.21.1 "more jar behaviors" (#1644) render fix: in a jar facing down, the entity is moved back inside
 * the jar ({@code translate(0, bbHeight + 0.75, 0)}), at the same point as 1.21.1 (just before the facing rotation).
 */
@Mixin(MobJarRenderer.class)
public abstract class MobJarRendererMixin {

    @Inject(method = "render(Lcom/hollingsworth/arsnouveau/common/block/tile/MobJarTile;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            remap = false, at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/core/Direction;getRotation()Lorg/joml/Quaternionf;"))
    private void ars_backports$facingDownOffset(MobJarTile tile, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                                               int light, int overlay, CallbackInfo ci) {
        Entity entity = tile.getEntity();
        if (entity != null && tile.getBlockState().getValue(MobJar.FACING) == Direction.DOWN) {
            poseStack.translate(0, entity.getBbHeight() + 0.75, 0);
        }
    }
}
