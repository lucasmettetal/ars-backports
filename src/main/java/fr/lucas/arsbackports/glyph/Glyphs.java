package fr.lucas.arsbackports.glyph;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;

/** Glyphs backported from Ars Nouveau 1.21.1. */
public final class Glyphs {

    private Glyphs() {
    }

    // Must run during mod construction: GlyphRegistry.registerSpell also creates the glyph's server config in the
    // active mod container, and Ars creates the glyph items from this registry when items are registered.
    public static void register() {
        GlyphRegistry.registerSpell(MethodPantomime.INSTANCE);
        GlyphRegistry.registerSpell(EffectBubble.INSTANCE);
    }
}
