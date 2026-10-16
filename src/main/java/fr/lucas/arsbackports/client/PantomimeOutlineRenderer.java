package fr.lucas.arsbackports.client;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.lucas.arsbackports.ArsBackports;
import fr.lucas.arsbackports.glyph.MethodPantomime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Backport of 1.21.1's PantomimeRenderer: with a Pantomime + Sensitive spell in the main-hand caster, outlines
 * the block the spell will target, in the spell's color.
 */
@Mod.EventBusSubscriber(modid = ArsBackports.MODID, value = Dist.CLIENT)
public final class PantomimeOutlineRenderer {

    private PantomimeOutlineRenderer() {
    }

    // Same stage and priority as 1.21.1's ClientEvents.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack heldItem = player.getMainHandItem();
        if (!(heldItem.getItem() instanceof ICasterTool)) {
            return;
        }

        Spell spell = CasterUtil.getCaster(heldItem).getSpell();
        if (!(spell.getCastMethod() instanceof MethodPantomime pantomime)) {
            return;
        }
        SpellStats stats = new SpellStats.Builder()
                .setAugments(spell.getAugments(0, player))
                .addItemsFromEntity(player)
                .build(pantomime, null, player.level(), player, new SpellContext(player.level(), spell, player, new PlayerCaster(player), heldItem));
        if (!stats.isSensitive()) {
            return;
        }

        BlockPos pos = pantomime.findPosition(player, stats).getBlockPos();
        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);

        // 1.21.1 takes the color of the spell's pantomime particle timeline; 4.12.7 spells have a single color.
        ParticleColor color = spell.color;
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        LevelRenderer.renderLineBox(poseStack, buffers.getBuffer(RenderType.lines()), 0, 0, 0, 1, 1, 1,
                color.getRed(), color.getGreen(), color.getBlue(), 1);
        poseStack.popPose();
        buffers.endBatch(RenderType.lines());
    }
}
