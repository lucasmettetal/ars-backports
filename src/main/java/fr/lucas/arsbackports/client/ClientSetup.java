package fr.lucas.arsbackports.client;

import fr.lucas.arsbackports.ArsBackports;
import fr.lucas.arsbackports.registry.ModDecorations;
import fr.lucas.arsbackports.registry.ModEntities;
import fr.lucas.arsbackports.registry.ModItems;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BannerPattern;
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
        event.enqueueWork(ClientSetup::ensureBannerMaterials);
    }

    // Sheets fills its banner/shield material maps once, from the banner pattern registry, when the class is first
    // initialised. Should that happen before our patterns are registered, add them so banners never get a null material.
    // Like 1.21.1, only banner textures exist for the Ars patterns (shields use the missing texture).
    private static void ensureBannerMaterials() {
        for (String name : ModDecorations.BANNER_PATTERN_NAMES) {
            ResourceKey<BannerPattern> key = ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath(ArsBackports.MODID, name));
            Sheets.BANNER_MATERIALS.computeIfAbsent(key, k -> new Material(Sheets.BANNER_SHEET, k.location().withPrefix("entity/banner/")));
            Sheets.SHIELD_MATERIALS.computeIfAbsent(key, k -> new Material(Sheets.SHIELD_SHEET, k.location().withPrefix("entity/shield/")));
        }
    }
}
