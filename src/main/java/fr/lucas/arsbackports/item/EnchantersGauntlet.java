package fr.lucas.arsbackports.item;

import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import java.util.Set;

/**
 * Enchanter's Gauntlet, backported from Ars Nouveau 1.21.1.
 * <p>
 * Tool behaviour: 1.21.1 uses a TOOL data component; 1.20.1 has no equivalent, so the same rules are
 * expressed with the Forge item hooks. Unlike 1.21.1 (where the diamond deny rule never applies),
 * the diamond tier is really enforced through {@link TierSortingRegistry}.
 * The item has no durability (no max damage), so it is unbreakable.
 */
public class EnchantersGauntlet extends ModItem {
    private static final float MINEABLE_SPEED = 8.0F;
    private static final float SWORD_EFFICIENT_SPEED = 1.5F;
    private static final int ENCHANTMENT_VALUE = 15;

    private static final Set<ToolAction> TOOL_ACTIONS = Set.of(
            ToolActions.PICKAXE_DIG, ToolActions.AXE_DIG, ToolActions.SHOVEL_DIG,
            ToolActions.HOE_DIG, ToolActions.SWORD_DIG, ToolActions.SHEARS_DIG);

    public EnchantersGauntlet(Properties properties) {
        super(properties.stacksTo(1));
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
