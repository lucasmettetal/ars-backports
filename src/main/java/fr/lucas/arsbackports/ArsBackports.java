package fr.lucas.arsbackports;

import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import com.mojang.logging.LogUtils;
import fr.lucas.arsbackports.registry.ModItems;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ArsBackports.MODID)
public class ArsBackports {
    public static final String MODID = "ars_backports";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ArsBackports(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ModItems.ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    // Our items go into Ars Nouveau's main tab ("ars_nouveau:general") next to its own caster tools.
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeTabRegistry.BLOCKS.getKey()) {
            event.accept(ModItems.ENCHANTERS_GAUNTLET);
        }
    }
}
