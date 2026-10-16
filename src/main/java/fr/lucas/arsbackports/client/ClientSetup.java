package fr.lucas.arsbackports.client;

import fr.lucas.arsbackports.ArsBackports;
import fr.lucas.arsbackports.registry.ModEntities;
import fr.lucas.arsbackports.registry.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/** Client-only setup; never loaded on a dedicated server. */
@Mod.EventBusSubscriber(modid = ArsBackports.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BUBBLE.get(), BubbleRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Switches the rod to its "cast" model while the holder is fishing (same predicate as the vanilla fishing rod;
        // 1.21.1 swaps the texture in its GeckoLib renderer, based on the local player only).
        event.enqueueWork(() -> ItemProperties.register(ModItems.ENCHANTERS_FISHING_ROD.get(), ResourceLocation.withDefaultNamespace("cast"),
                (stack, level, entity, seed) -> {
                    if (entity == null) {
                        return 0.0F;
                    }
                    boolean mainHand = entity.getMainHandItem() == stack;
                    boolean offHand = entity.getOffhandItem() == stack;
                    if (entity.getMainHandItem().canPerformAction(ToolActions.FISHING_ROD_CAST)) {
                        offHand = false;
                    }
                    return (mainHand || offHand) && entity instanceof Player player && player.fishing != null ? 1.0F : 0.0F;
                }));
    }
}
