package fr.lucas.arsbackports.item;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import fr.lucas.arsbackports.entity.EnchantedHook;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Enchanter's Fishing Rod, backported from Ars Nouveau 1.21.1: a fishing rod holding one formless spell
 * (Touch is prepended, see {@link TouchCaster}). While an entity is hooked, using the rod casts the spell on it
 * instead of reeling in. The rod has no durability.
 */
public class EnchantersFishingRod extends ModItem implements TouchCaster {

    public EnchantersFishingRod(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public String invalidSpellKey() {
        return "ars_backports.fishing_rod.invalid";
    }

    // Same flow as 1.21.1 (itself vanilla FishingRodItem#use), with the 1.20.1 vanilla calls.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.fishing != null) {
            if (!level.isClientSide) {
                if (player.fishing.getHookedIn() != null && player.fishing instanceof EnchantedHook enchantedHook) {
                    enchantedHook.castSpell();
                } else {
                    int damage = player.fishing.retrieve(itemstack);
                    itemstack.hurtAndBreak(damage, player, p -> p.broadcastBreakEvent(hand));
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE,
                            SoundSource.NEUTRAL, 1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
                    player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                }
            }
        } else {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW,
                    SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            if (level instanceof ServerLevel serverLevel) {
                int lureSpeed = EnchantmentHelper.getFishingSpeedBonus(itemstack);
                int luck = EnchantmentHelper.getFishingLuckBonus(itemstack);
                ISpellCaster caster = getSpellCaster(itemstack);
                Spell spell = caster.modifySpellBeforeCasting(serverLevel, player, hand, caster.getSpell());
                if (!spell.isValid()) {
                    PortUtil.sendMessageNoSpam(player, Component.translatable(invalidSpellKey()));
                    return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
                }
                SpellContext context = new SpellContext(level, spell, player, new PlayerCaster(player), itemstack);
                level.addFreshEntity(new EnchantedHook(player, level, luck, lureSpeed, context));
            }
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_FISHING_ROD_ACTIONS.contains(toolAction);
    }

    // Vanilla only allows enchanting damageable items; the rod has no durability.
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 1;
    }

    // 1.21.1 puts the rod in enchantable/fishing only: Luck of the Sea and Lure.
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.category == EnchantmentCategory.FISHING_ROD;
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
}
