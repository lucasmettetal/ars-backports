package fr.lucas.arsbackports.registry;

import fr.lucas.arsbackports.ArsBackports;
import fr.lucas.arsbackports.block.ItemGrate;
import fr.lucas.arsbackports.block.SourceLamp;
import fr.lucas.arsbackports.block.StarbunclePlush;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

/** Blocks; their block items are registered in {@link ModItems}. */
public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ArsBackports.MODID);

    /**
     * Archwood wood type, needed by signs (1.21.1 BlockRegistry.ARCHWOOD; Ars 4.12.7 has none). Sign textures:
     * {@code textures/entity/signs/archwood.png} and {@code .../signs/hanging/archwood.png} (added client side to Sheets).
     */
    public static final WoodType ARCHWOOD = WoodType.register(new WoodType(ArsBackports.MODID + ":archwood", BlockSetType.OAK));

    public static final RegistryObject<ItemGrate> ARCHWOOD_GRATE = BLOCKS.register("archwood_grate", ItemGrate::new);
    public static final RegistryObject<ItemGrate> GOLD_GRATE = BLOCKS.register("gold_grate", ItemGrate::new);
    public static final RegistryObject<ItemGrate> SOURCESTONE_GRATE = BLOCKS.register("sourcestone_grate", ItemGrate::new);
    public static final RegistryObject<ItemGrate> SMOOTH_SOURCESTONE_GRATE = BLOCKS.register("smooth_sourcestone_grate", ItemGrate::new);
    public static final RegistryObject<SourceLamp> SOURCE_LAMP = BLOCKS.register("source_lamp", SourceLamp::new);
    public static final RegistryObject<StarbunclePlush> STARBUNCLE_PLUSH = BLOCKS.register("starbuncle_plush", StarbunclePlush::new);

    // Vanilla sign blocks with the archwood wood type, as in 1.21.1; the vanilla sign block entities accept them
    // through mixin/SignBlockEntityTypeMixin (1.21.1 uses NeoForge's BlockEntityTypeAddBlocksEvent).
    public static final RegistryObject<StandingSignBlock> ARCHWOOD_SIGN = BLOCKS.register("archwood_sign",
            () -> new StandingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SIGN), ARCHWOOD));
    public static final RegistryObject<WallSignBlock> ARCHWOOD_WALL_SIGN = BLOCKS.register("archwood_wall_sign",
            () -> new WallSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_SIGN), ARCHWOOD));
    public static final RegistryObject<CeilingHangingSignBlock> ARCHWOOD_HANGING_SIGN = BLOCKS.register("archwood_hanging_sign",
            () -> new CeilingHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_HANGING_SIGN), ARCHWOOD));
    public static final RegistryObject<WallHangingSignBlock> ARCHWOOD_HANGING_WALL_SIGN = BLOCKS.register("archwood_hanging_wall_sign",
            () -> new WallHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WALL_HANGING_SIGN), ARCHWOOD));

    /** Blocks that get a plain BlockItem and go into the creative tab, in display order. */
    public static final List<RegistryObject<? extends Block>> SIMPLE_BLOCK_ITEMS = List.of(
            ARCHWOOD_GRATE, GOLD_GRATE, SOURCESTONE_GRATE, SMOOTH_SOURCESTONE_GRATE, SOURCE_LAMP, STARBUNCLE_PLUSH);

    private ModBlocks() {
    }
}
