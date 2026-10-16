package fr.lucas.arsbackports;

import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import com.mojang.logging.LogUtils;
import fr.lucas.arsbackports.glyph.Glyphs;
import fr.lucas.arsbackports.mobjar.JarBehaviors;
import fr.lucas.arsbackports.registry.ModBlocks;
import fr.lucas.arsbackports.registry.ModDecorations;
import fr.lucas.arsbackports.registry.ModDispenserBehaviors;
import fr.lucas.arsbackports.registry.ModEntities;
import fr.lucas.arsbackports.registry.ModItems;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ArsBackports.MODID)
public class ArsBackports {
    public static final String MODID = "ars_backports";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ArsBackports(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModDecorations.PAINTINGS.register(modEventBus);
        ModDecorations.BANNER_PATTERNS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        Glyphs.register();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            JarBehaviors.register();
            ModDispenserBehaviors.register();
        });
    }

    // Our items go into Ars Nouveau's main tab ("ars_nouveau:general") next to its own caster tools.
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeTabRegistry.BLOCKS.getKey()) {
            event.accept(ModItems.ENCHANTERS_GAUNTLET);
            event.accept(ModItems.ENCHANTERS_FISHING_ROD);
            ModBlocks.SIMPLE_BLOCK_ITEMS.forEach(block -> event.accept(block.get()));
            event.accept(ModItems.ARS_STENCIL);
            ModDecorations.ALL_PAINTINGS.forEach(painting -> event.accept(ModDecorations.paintingItem(painting.getId())));
        }
    }
}
