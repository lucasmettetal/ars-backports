package fr.lucas.arsbackports.item;

import com.hollingsworth.arsnouveau.common.items.ModItem;

/**
 * Enchanter's Gauntlet, backported from Ars Nouveau 1.21.1.
 * Step B: plain item only; tool, spell and mana behaviour are added in later steps.
 */
public class EnchantersGauntlet extends ModItem {

    public EnchantersGauntlet(Properties properties) {
        super(properties.stacksTo(1));
    }
}
