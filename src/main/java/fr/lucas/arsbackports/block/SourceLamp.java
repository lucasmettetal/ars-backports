package fr.lucas.arsbackports.block;

import com.hollingsworth.arsnouveau.api.spell.ILightable;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Source Lamp, backported from Ars Nouveau 1.21.1. 1.21.1 extends the copper bulb (1.21 only), so its behaviour is
 * reimplemented here from 1.21's CopperBulbBlock: each rising redstone edge toggles the lamp; the Light glyph sets its
 * brightness (15, minus 1 per Dampen); comparators read the brightness while lit.
 * The copper bulb toggle sounds do not exist in 1.20.1: the lamp toggles silently.
 */
// BlockBehaviour methods are deprecated in 1.20.1 only as "override, do not call" (vanilla convention).
@SuppressWarnings("deprecation")
public class SourceLamp extends Block implements ILightable {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light_level", 0, 15);

    public SourceLamp() {
        super(BlockBehaviour.Properties.of().lightLevel(state -> state.getValue(LIT) ? state.getValue(LIGHT_LEVEL) : 0));
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(LIGHT_LEVEL, 15).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, POWERED, LIGHT_LEVEL);
    }

    // CopperBulbBlock#onPlace
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (oldState.getBlock() != state.getBlock() && level instanceof ServerLevel serverLevel) {
            checkAndFlip(state, serverLevel, pos);
        }
    }

    // CopperBulbBlock#neighborChanged
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level instanceof ServerLevel serverLevel) {
            checkAndFlip(state, serverLevel, pos);
        }
    }

    // CopperBulbBlock#checkAndFlip (without the 1.21-only sounds)
    public void checkAndFlip(BlockState state, ServerLevel level, BlockPos pos) {
        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(POWERED)) {
            BlockState newState = state;
            if (!state.getValue(POWERED)) {
                newState = newState.cycle(LIT);
            }
            level.setBlock(pos, newState.setValue(POWERED, powered), Block.UPDATE_ALL);
        }
    }

    @Override
    public void onLight(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats augments, SpellContext spellContext) {
        if (rayTraceResult instanceof BlockHitResult blockHitResult) {
            BlockState state = world.getBlockState(blockHitResult.getBlockPos())
                    .setValue(LIGHT_LEVEL, Math.min(Math.max(0, 15 - augments.getBuffCount(AugmentDampen.INSTANCE)), 15));
            world.setBlock(blockHitResult.getBlockPos(), state, Block.UPDATE_ALL);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockState(pos).getValue(LIT) ? state.getValue(LIGHT_LEVEL) : 0;
    }
}
