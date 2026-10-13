package fr.lucas.arsbackports.item;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Shared behaviour of the 1.21.1 casters that hold a single formless spell cast with an implicit Touch
 * (Enchanter's Gauntlet, Enchanter's Fishing Rod): spells with a form are refused at the Scribes Table and
 * Touch is prepended to the inscribed spell.
 */
public interface TouchCaster extends ICasterTool {

    /** Translation key of the "invalid spell" message. */
    String invalidSpellKey();

    @Override
    default boolean isScribedSpellValid(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        return spell.recipe.stream().noneMatch(part -> part instanceof AbstractCastMethod);
    }

    @Override
    default void sendInvalidMessage(Player player) {
        PortUtil.sendMessageNoSpam(player, Component.translatable(invalidSpellKey()));
    }

    // Stores Touch + the inscribed parts in a copy, leaving the spell read from the book/parchment untouched.
    @Override
    default boolean setSpell(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        List<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(MethodTouch.INSTANCE);
        recipe.addAll(spell.recipe);
        return ICasterTool.super.setSpell(caster, player, hand, stack, spell.clone().setRecipe(recipe));
    }

    // Tooltip as in 1.21.1 and Ars 4.12.7 casters: glyph icons by default, text with Shift (or if glyph tooltips are off).
    // Only called on the client, so referencing Screen here is safe on a dedicated server (same as Ars' own items).
    default void appendSpellTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown() || !Config.GLYPH_TOOLTIPS.get()) {
            getInformation(stack, level, tooltip, flag);
        }
    }

    default Optional<TooltipComponent> spellTooltipImage(ItemStack stack) {
        ISpellCaster caster = getSpellCaster(stack);
        if (Config.GLYPH_TOOLTIPS.get() && !Screen.hasShiftDown() && !caster.isSpellHidden() && !caster.getSpell().isEmpty()) {
            return Optional.of(new SpellTooltip(caster));
        }
        return Optional.empty();
    }
}
