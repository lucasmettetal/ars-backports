package fr.lucas.arsbackports.item;

import com.hollingsworth.arsnouveau.api.mana.IManaDiscountEquipment;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Enchanter's Gauntlet, backported from Ars Nouveau 1.21.1.
 * <p>
 * Spell casting follows Ars 4.12.7's EnchantersMirror (single spell slot, inscribed at the Scribes Table,
 * no form allowed) with Touch instead of Self as the implicit form (see {@link TouchCaster}).
 * <p>
 * Tool behaviour: 1.21.1 uses a TOOL data component; 1.20.1 has no equivalent, so the same rules are
 * expressed with the Forge item hooks. Unlike 1.21.1 (where the diamond deny rule never applies),
 * the diamond tier is really enforced through {@link TierSortingRegistry}.
 * The item has no durability (no max damage), so it is unbreakable.
 */
public class EnchantersGauntlet extends ModItem implements TouchCaster, IManaDiscountEquipment {
    private static final float MINEABLE_SPEED = 8.0F;
    private static final float SWORD_EFFICIENT_SPEED = 1.5F;
    private static final int ENCHANTMENT_VALUE = 15;
    private static final double MANA_DISCOUNT = 0.25;

    private static final Set<ToolAction> TOOL_ACTIONS = Set.of(
            ToolActions.PICKAXE_DIG, ToolActions.AXE_DIG, ToolActions.SHOVEL_DIG,
            ToolActions.HOE_DIG, ToolActions.SWORD_DIG, ToolActions.SHEARS_DIG);

    public EnchantersGauntlet(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public String invalidSpellKey() {
        return "ars_backports.gauntlet.invalid";
    }

    // Ray trace, BlockEntity/Scribes Table/sneak handling, mana and resolution are all done by Ars' castSpell.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ISpellCaster caster = getSpellCaster(player.getItemInHand(hand));
        return caster.castSpell(level, player, hand, Component.translatable(invalidSpellKey()), caster.getSpell());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        appendSpellTooltip(stack, level, tooltip, flag);
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return spellTooltipImage(stack);
    }

    // 25 % of the full spell cost (Touch included), added by ManaUtil to armor/curio discounts: Touch + Break = 15 -> 12.
    @Override
    public int getManaDiscount(ItemStack stack, Spell spell) {
        return (int) (spell.getCost() * MANA_DISCOUNT);
    }

    private static boolean isMineable(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                || state.is(BlockTags.MINEABLE_WITH_AXE)
                || state.is(BlockTags.MINEABLE_WITH_SHOVEL)
                || state.is(BlockTags.MINEABLE_WITH_HOE);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (isMineable(state)) {
            return MINEABLE_SPEED;
        }
        return state.is(BlockTags.SWORD_EFFICIENT) ? SWORD_EFFICIENT_SPEED : 1.0F;
    }

    // Same check as Forge's DiggerItem: right tool tag AND diamond tier (forge:needs_netherite_tool blocks are refused).
    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return isMineable(state) && TierSortingRegistry.isCorrectTierForDrops(Tiers.DIAMOND, state);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return TOOL_ACTIONS.contains(toolAction);
    }

    // Vanilla only allows enchanting damageable items; the gauntlet has no durability.
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return ENCHANTMENT_VALUE;
    }

    // Mining enchantments only (Efficiency, Fortune, Silk Touch): no Unbreaking/Mending on an unbreakable item.
    // Forge routes both the enchanting table and the anvil (Enchantment#canEnchant) through this hook.
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.category == EnchantmentCategory.DIGGER;
    }
}
