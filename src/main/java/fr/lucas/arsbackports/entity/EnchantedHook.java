package fr.lucas.arsbackports.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;

/**
 * Hook of the Enchanter's Fishing Rod, backported from Ars Nouveau 1.21.1: a normal fishing hook that remembers
 * the rod's spell and casts it on the hooked entity.
 * <p>
 * 1.21.1 registers its own entity type but drops the rod's luck/lure values. Here the public vanilla
 * FishingHook(Player, Level, luck, lure) constructor is used instead: Luck of the Sea and Lure work, and clients
 * see a regular fishing bobber (vanilla renderer and line), no extra entity type needed. The spell only lives
 * on the server, which is where 1.21.1 uses it too.
 */
public class EnchantedHook extends FishingHook {
    private final SpellContext spellContext;

    public EnchantedHook(Player player, Level level, int luck, int lureSpeed, SpellContext spellContext) {
        super(player, level, luck, lureSpeed);
        this.spellContext = spellContext;
    }

    // SpellResolver.resolveAllEffects resets the context (cast counter, cancel) on every cast, so it can be reused.
    public void castSpell() {
        if (getHookedIn() != null && getOwner() instanceof LivingEntity player) {
            SpellResolver resolver = new SpellResolver(spellContext);
            resolver.onCastOnEntity(player.getMainHandItem(), getHookedIn(), InteractionHand.MAIN_HAND);
        }
    }
}
