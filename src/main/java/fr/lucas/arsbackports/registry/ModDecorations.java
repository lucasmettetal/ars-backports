package fr.lucas.arsbackports.registry;

import fr.lucas.arsbackports.ArsBackports;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

/**
 * Paintings and banner patterns backported from Ars Nouveau 1.21.1. In 1.21 both are data-driven (datapack
 * registries); in 1.20.1 they are static registries, so they are registered here.
 */
public final class ModDecorations {
    public static final DeferredRegister<PaintingVariant> PAINTINGS = DeferredRegister.create(Registries.PAINTING_VARIANT, ArsBackports.MODID);
    public static final DeferredRegister<BannerPattern> BANNER_PATTERNS = DeferredRegister.create(Registries.BANNER_PATTERN, ArsBackports.MODID);

    // 1.21.1 painting_variant/*.json: starbuncle 1x1 (animated texture), resting_drygmy 4x4. Sizes are in pixels in 1.20.1.
    public static final RegistryObject<PaintingVariant> STARBUNCLE_PAINTING = PAINTINGS.register("starbuncle", () -> new PaintingVariant(16, 16));
    public static final RegistryObject<PaintingVariant> RESTING_DRYGMY_PAINTING = PAINTINGS.register("resting_drygmy", () -> new PaintingVariant(64, 64));
    public static final List<RegistryObject<PaintingVariant>> ALL_PAINTINGS = List.of(STARBUNCLE_PAINTING, RESTING_DRYGMY_PAINTING);

    /** Patterns unlocked at the loom by the Ars Stencil (1.21.1 BannerRegistry). */
    public static final List<String> BANNER_PATTERN_NAMES = List.of("fire", "water", "earth", "air", "manipulation", "conjuration", "abjuration", "spirals");
    public static final TagKey<BannerPattern> ARS_STENCIL_PATTERNS =
            TagKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath(ArsBackports.MODID, "pattern_item/ars_stencil"));

    static {
        // The hash name is what banners store in NBT ("Pattern"): it only has to be unique.
        for (String name : BANNER_PATTERN_NAMES) {
            BANNER_PATTERNS.register(name, () -> new BannerPattern(ArsBackports.MODID + "_" + name));
        }
    }

    private ModDecorations() {
    }

    /** A painting item that places this variant (1.20.1 form of 1.21.1 PaintingUtil#getPainting). */
    public static ItemStack paintingItem(ResourceLocation variant) {
        ItemStack stack = new ItemStack(Items.PAINTING);
        CompoundTag entityTag = new CompoundTag();
        entityTag.putString("variant", variant.toString());
        stack.getOrCreateTag().put("EntityTag", entityTag);
        return stack;
    }
}
