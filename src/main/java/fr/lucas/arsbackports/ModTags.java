package fr.lucas.arsbackports;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

/**
 * Tags that 1.21.1 added to existing Ars Nouveau mechanics. They keep the 1.21.1 ids ({@code ars_nouveau:...}) so that
 * datapacks written for 1.21.1 also work here; their default contents are in {@code data/ars_nouveau/tags/}.
 */
public final class ModTags {
    /** Blocks Intangible cannot phase (1.21.1 BlockTagProvider.INTANGIBLE_BLACKLIST, bedrock by default). */
    public static final TagKey<Block> INTANGIBLE_BLACKLIST = block("intangible_blacklist");
    /** Animals the Ritual of Fertility ignores. */
    public static final TagKey<EntityType<?>> BREED_BLACKLIST = entity("breed_blacklist");
    /** Baby mobs the Ritual of Overgrowth (bone block) ignores. */
    public static final TagKey<EntityType<?>> OVERGROWTH_BLACKLIST = entity("overgrowth_blacklist");
    /** Mobs the Ritual of Sanctuary never denies. */
    public static final TagKey<EntityType<?>> DENY_SPAWN_RITUAL_BLACKLIST = entity("deny_spawn_ritual_blacklist");
    /** Non-monster mobs the Ritual of Sanctuary also denies. */
    public static final TagKey<EntityType<?>> DENY_SPAWN_RITUAL_WHITELIST = entity("deny_spawn_ritual_whitelist");

    private ModTags() {
    }

    private static TagKey<Block> block(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("ars_nouveau", name));
    }

    private static TagKey<EntityType<?>> entity(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("ars_nouveau", name));
    }
}
