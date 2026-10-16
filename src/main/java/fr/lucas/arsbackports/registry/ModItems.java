package fr.lucas.arsbackports.registry;

import fr.lucas.arsbackports.ArsBackports;
import fr.lucas.arsbackports.item.EnchantersFishingRod;
import fr.lucas.arsbackports.item.EnchantersGauntlet;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ArsBackports.MODID);

    public static final RegistryObject<EnchantersGauntlet> ENCHANTERS_GAUNTLET =
            ITEMS.register("enchanters_gauntlet", () -> new EnchantersGauntlet(new Item.Properties()));

    public static final RegistryObject<EnchantersFishingRod> ENCHANTERS_FISHING_ROD =
            ITEMS.register("enchanters_fishing_rod", () -> new EnchantersFishingRod(new Item.Properties()));

    // 1.21.1 ars_stencil: unlocks the Ars banner patterns at the loom.
    public static final RegistryObject<BannerPatternItem> ARS_STENCIL =
            ITEMS.register("ars_stencil", () -> new BannerPatternItem(ModDecorations.ARS_STENCIL_PATTERNS, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<SignItem> ARCHWOOD_SIGN = ITEMS.register("archwood_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.ARCHWOOD_SIGN.get(), ModBlocks.ARCHWOOD_WALL_SIGN.get()));
    public static final RegistryObject<HangingSignItem> ARCHWOOD_HANGING_SIGN = ITEMS.register("archwood_hanging_sign",
            () -> new HangingSignItem(ModBlocks.ARCHWOOD_HANGING_SIGN.get(), ModBlocks.ARCHWOOD_HANGING_WALL_SIGN.get(), new Item.Properties().stacksTo(16)));

    static {
        // A plain BlockItem, with the block's id, for each block of ModBlocks.SIMPLE_BLOCK_ITEMS.
        for (RegistryObject<? extends Block> block : ModBlocks.SIMPLE_BLOCK_ITEMS) {
            ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
        }
    }

    private ModItems() {
    }
}
