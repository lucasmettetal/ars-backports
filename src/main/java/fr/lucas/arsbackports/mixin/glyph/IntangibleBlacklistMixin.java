package fr.lucas.arsbackports.mixin.glyph;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IntangibleAirTile;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectIntangible;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import fr.lucas.arsbackports.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Backport of 1.21.1 "intangible not checking additional blocks from AOE, add blacklist tag" (#1961): the hard-coded
 * bedrock check becomes the block tag {@code ars_nouveau:intangible_blacklist} (bedrock by default), and the harvest
 * check uses each AOE position instead of the targeted one. The (short) method body is replaced by the 1.21.1 one;
 * everything else is identical to Ars 4.12.7.
 */
@Mixin(EffectIntangible.class)
public abstract class IntangibleBlacklistMixin {

    @Inject(method = "onResolveBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void ars_backports$onResolveBlock(BlockHitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats,
                                              SpellContext spellContext, SpellResolver resolver, CallbackInfo ci) {
        EffectIntangible self = (EffectIntangible) (Object) this;
        int duration = (int) (self.GENERIC_INT.get() + self.EXTEND_TIME.get() * spellStats.getDurationMultiplier());

        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats);
        for (BlockPos pos1 : posList) {
            if (world.getBlockEntity(pos1) != null || world.getBlockState(pos1).isAir()
                    || world.getBlockState(pos1).is(ModTags.INTANGIBLE_BLACKLIST)
                    || !self.canBlockBeHarvested(spellStats, world, pos1)
                    // Entity overload: the LivingEntity one is deprecated in 4.12.7 and only delegates to it.
                    || !BlockUtil.destroyRespectsClaim((Entity) self.getPlayer(shooter, (ServerLevel) world), world, pos1)) {
                continue;
            }

            BlockState state = world.getBlockState(pos1);
            int id = Block.getId(state);
            world.setBlockAndUpdate(pos1, BlockRegistry.INTANGIBLE_AIR.get().defaultBlockState());
            if (world.getBlockEntity(pos1) instanceof IntangibleAirTile tile) {
                tile.stateID = id;
                tile.maxLength = duration * 20;
            }
        }
        ci.cancel();
    }
}
