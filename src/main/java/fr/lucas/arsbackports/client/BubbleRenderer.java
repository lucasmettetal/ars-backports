package fr.lucas.arsbackports.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fr.lucas.arsbackports.ArsBackports;
import fr.lucas.arsbackports.entity.BubbleEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Backport of 1.21.1's BubbleRenderer: a camera-facing textured quad, scaled to the passenger, with 5 popping frames.
 * 1.21.1 draws it with Ars' own "world entity icon" shader (absent from 4.12.7); the vanilla translucent entity
 * render type is used instead.
 */
public class BubbleRenderer extends EntityRenderer<BubbleEntity> {
    private static final ResourceLocation TEXTURE = texture("bubble");
    private static final ResourceLocation[] POP = {
            texture("bubble_pop1"), texture("bubble_pop2"), texture("bubble_pop3"), texture("bubble_pop4"), texture("bubble_pop5")
    };

    public BubbleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(ArsBackports.MODID, "textures/entity/" + name + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(BubbleEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(BubbleEntity bubble, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        double y = bubble.getPassengers().isEmpty() ? 0.25f : bubble.getBbHeight();
        poseStack.pushPose();
        poseStack.translate(0, y, 0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(0.025F, -0.025F, 0.025F);

        float base = 2.0f;
        Entity passenger = bubble.getFirstPassenger();
        if (passenger != null) {
            base += 1f;
            // Compare the size difference between the bubble hitbox and the passenger
            base *= passenger.getBbWidth() / bubble.getBbWidth();
        }
        poseStack.scale(base, base, base);

        ResourceLocation texture = TEXTURE;
        if (bubble.poppingTicks > 0) {
            texture = POP[Math.min(bubble.poppingTicks, 5) - 1];
        }

        PoseStack.Pose last = poseStack.last();
        Matrix4f pose = last.pose();
        Matrix3f normal = last.normal();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
        vertex(consumer, pose, normal, -8, -8, 0, 0, packedLight);
        vertex(consumer, pose, normal, -8, 8, 0, 1, packedLight);
        vertex(consumer, pose, normal, 8, 8, 1, 1, packedLight);
        vertex(consumer, pose, normal, 8, -8, 1, 0, packedLight);
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, float x, float y, float u, float v, int light) {
        consumer.vertex(pose, x, y, 0).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light).normal(normal, 0, 1, 0).endVertex();
    }
}
