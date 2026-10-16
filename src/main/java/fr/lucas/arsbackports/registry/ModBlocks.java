package fr.lucas.arsbackports.registry;

import fr.lucas.arsbackports.ArsBackports;
import fr.lucas.arsbackports.block.ItemGrate;
import fr.lucas.arsbackports.block.SourceLamp;
import fr.lucas.arsbackports.block.StarbunclePlush;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

/** Blocks; their block items are registered in {@link ModItems}. */
public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ArsBackports.MODID);

    public static final RegistryObject<ItemGrate> ARCHWOOD_GRATE = BLOCKS.register("archwood_grate", ItemGrate::new);
    public static final RegistryObject<ItemGrate> GOLD_GRATE = BLOCKS.register("gold_grate", ItemGrate::new);
    public static final RegistryObject<ItemGrate> SOURCESTONE_GRATE = BLOCKS.register("sourcestone_grate", ItemGrate::new);
    public static final RegistryObject<ItemGrate> SMOOTH_SOURCESTONE_GRATE = BLOCKS.register("smooth_sourcestone_grate", ItemGrate::new);
    public static final RegistryObject<SourceLamp> SOURCE_LAMP = BLOCKS.register("source_lamp", SourceLamp::new);
    public static final RegistryObject<StarbunclePlush> STARBUNCLE_PLUSH = BLOCKS.register("starbuncle_plush", StarbunclePlush::new);

    /** Blocks that get a plain BlockItem and go into the creative tab, in display order. */
    public static final List<RegistryObject<? extends Block>> SIMPLE_BLOCK_ITEMS = List.of(
            ARCHWOOD_GRATE, GOLD_GRATE, SOURCESTONE_GRATE, SMOOTH_SOURCESTONE_GRATE, SOURCE_LAMP, STARBUNCLE_PLUSH);

    private ModBlocks() {
    }
}
