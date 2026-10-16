package fr.lucas.arsbackports.glyph;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import fr.lucas.arsbackports.ArsBackports;
import fr.lucas.arsbackports.entity.BubbleEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Set;

/** Bubble effect, backported from Ars Nouveau 1.21.1 (same logic, costs and config defaults). */
public class EffectBubble extends AbstractEffect {
    public static final EffectBubble INSTANCE = new EffectBubble();

    private EffectBubble() {
        super(ResourceLocation.fromNamespaceAndPath(ArsBackports.MODID, "glyph_bubble"), "Bubble");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BubbleEntity bubble = createBubble(world, spellStats);
        bubble.setPos(rayTraceResult.getLocation().x, rayTraceResult.getLocation().y, rayTraceResult.getLocation().z);
        if (isNotFakePlayer(shooter)) {
            bubble.setOwner(shooter);
        }
        world.addFreshEntity(bubble);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BubbleEntity bubble = createBubble(world, spellStats);
        bubble.setPos(rayTraceResult.getLocation().x, rayTraceResult.getLocation().y, rayTraceResult.getLocation().z);
        if (isNotFakePlayer(shooter)) {
            bubble.setOwner(shooter);
        }
        world.addFreshEntity(bubble);
        bubble.tryCapturing(rayTraceResult.getEntity());
    }

    public BubbleEntity createBubble(Level level, SpellStats spellStats) {
        int maxAge = (int) (100 + spellStats.getDurationMultiplier() * EXTEND_TIME.get());
        float damage = (float) (DAMAGE.get() + spellStats.getAmpMultiplier() * AMP_VALUE.get() + spellStats.getDamageModifier());
        return new BubbleEntity(level, maxAge, damage);
    }

    @Override
    protected int getDefaultManaCost() {
        return 20;
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 5.0);
        addAmpConfig(builder, 2.0);
        // Same "extend_time" key and default as 1.21.1; the value is used in ticks (bubble lifetime).
        addExtendTimeTicksConfig(builder, 3);
    }

    @Override
    protected Set<AbstractAugment> getCompatibleAugments() {
        return Set.of(AugmentExtendTime.INSTANCE, AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentDurationDown.INSTANCE);
    }

    @Override
    protected Set<SpellSchool> getSchools() {
        return Set.of(SpellSchools.ELEMENTAL_WATER);
    }

    @Override
    public String getBookDescription() {
        return "Captures mobs and entities it touches, causing them to float upwards. If the bubble has been alive for at least one tick, damaging the entity trapped in the bubble will cause it to pop, dealing bonus damage to the entity inside. Extend time and amplify can be used to increase the duration and damage of the bubble.";
    }
}
