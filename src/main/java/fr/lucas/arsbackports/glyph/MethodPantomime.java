package fr.lucas.arsbackports.glyph;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.CastResolveType;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.common.network.PacketAddFadingLight;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import fr.lucas.arsbackports.ArsBackports;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Pantomime form, backported from Ars Nouveau 1.21.1: resolves the spell on the block 2–3 blocks in front of the
 * caster's eyes (closer/farther with Dampen/Amplify), whatever was clicked.
 * <p>
 * 1.21.1 plays its "pantomime" particle timeline (a burst by default) and sound there; Ars 4.12.7 has no particle
 * timelines, so the burst and fading light sent by 4.12.7's own Touch form are used instead. The Sensitive outline
 * is drawn client side by {@code PantomimeOutlineRenderer}.
 */
public class MethodPantomime extends AbstractCastMethod {
    public static final MethodPantomime INSTANCE = new MethodPantomime();

    private MethodPantomime() {
        super(ResourceLocation.fromNamespaceAndPath(ArsBackports.MODID, "glyph_pantomime"), "Pantomime");
    }

    // Same computation as 1.21.1 (also used by the client outline).
    public BlockHitResult findPosition(LivingEntity shooter, SpellStats stats) {
        int offset = stats.getBuffCount(AugmentDampen.INSTANCE) > 0 ? -1 : stats.getBuffCount(AugmentAmplify.INSTANCE);
        Vec3 eyes = shooter.getEyePosition(1.0f);
        float viewXRot = shooter.getViewXRot(1.0f);
        double dist = viewXRot < 45.0f ? 2 + offset : 3 + offset;
        Vec3 to = eyes.add(shooter.getViewVector(1.0f).scale(dist));
        BlockPos toPos = BlockPos.containing(to);
        return new BlockHitResult(to, Direction.getNearest(to.x, to.y, to.z).getOpposite(), toPos, true);
    }

    public CastResolveType getTarget(Level world, LivingEntity shooter, SpellResolver resolver, SpellStats stats) {
        BlockHitResult res = findPosition(shooter, stats);
        resolver.onResolveEffect(world, res);
        BlockPos pos = res.getBlockPos();
        Networking.sendToNearby(world, pos, new PacketANEffect(PacketANEffect.EffectType.BURST, pos, resolver.spellContext.getColors()));
        Networking.sendToNearby(world, pos, new PacketAddFadingLight(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCast(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {
        return getTarget(world, playerEntity, resolver, spellStats);
    }

    @Override
    public CastResolveType onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        return getTarget(context.getLevel(), context.getPlayer(), resolver, spellStats);
    }

    @Override
    public CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        return getTarget(caster.getCommandSenderWorld(), caster, resolver, spellStats);
    }

    @Override
    public CastResolveType onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        return getTarget(caster.level(), caster, resolver, spellStats);
    }

    @Override
    protected int getDefaultManaCost() {
        return 5;
    }

    @Override
    protected Set<AbstractAugment> getCompatibleAugments() {
        return Set.of(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentSensitive.INSTANCE);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        super.addDefaultAugmentLimits(defaults);
        defaults.put(AugmentDampen.INSTANCE.getRegistryName(), 1);
    }

    @Override
    public String getBookDescription() {
        return "Applies spells to the nearest block in your line of sight. Dampen will reduce the distance, Amplify will increase it. Sensitive will highlight the selected block.";
    }
}
