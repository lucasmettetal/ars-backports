package fr.lucas.arsbackports;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ArsBackports.MODID)
public class ArsBackports {
    public static final String MODID = "ars_backports";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ArsBackports(FMLJavaModLoadingContext context) {
        LOGGER.info("Ars Backports loading");
    }
}
