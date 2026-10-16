package fr.lucas.arsbackports.block;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.common.block.ModBlock;
import fr.lucas.arsbackports.ArsBackports;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Item grate, backported from Ars Nouveau 1.21.1 (archwood, gold, sourcestone and smooth sourcestone grates).
 * Liquid source blocks placed on top flow into / waterlog the block below (or fill a cauldron below); using the grate
 * uses the block below (so buckets work through it); items and projectiles fall through it.
 * <p>
 * 1.20.1 port: useItemOn/useWithoutItem are merged into {@code use}; the cauldron is used through the public
 * {@code BlockState#use} (1.21.1 needs a mixin accessor for it); BucketPickup / LiquidBlockContainer have no player
 * parameter in 1.20.1.
 */
// BlockBehaviour methods are deprecated in 1.20.1 only as "override, do not call" (vanilla convention).
@SuppressWarnings("deprecation")
public class ItemGrate extends ModBlock implements BucketPickup {
    public static final TagKey<EntityType<?>> ITEM_GRATE_PASSABLE = entityTag("item_grate_passable");
    public static final TagKey<EntityType<?>> ITEM_GRATE_COLLIDE = entityTag("item_grate_collide");

    protected static final VoxelShape BOTTOM_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
    protected static final VoxelShape TOP_AABB = Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EAST_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
    protected static final VoxelShape WEST_OPEN_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
    protected static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);

    public ItemGrate() {
        super(defaultProperties().noOcclusion());
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.FACING, Direction.NORTH));
    }

    private static TagKey<EntityType<?>> entityTag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ArsBackports.MODID, name));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockHitResult belowHit = new BlockHitResult(hit.getLocation().add(0, -1, 0), hit.getDirection(), pos.below(), hit.isInside());
        return level.getBlockState(pos.below()).use(level, player, hand, belowHit);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        BlockPos below = pos.below();
        BlockState stateAbove = level.getBlockState(pos.above());
        BlockState stateBelow = level.getBlockState(below);
        if (!stateAbove.getFluidState().isSource()) {
            return;
        }

        if (stateBelow.getBlock() instanceof LiquidBlockContainer blockContainer
                && blockContainer.canPlaceLiquid(level, below, stateBelow, stateAbove.getFluidState().getType())) {
            if (blockContainer.placeLiquid(level, below, stateBelow, stateAbove.getFluidState())) {
                if (stateAbove.getBlock() instanceof BucketPickup bucketPickup) {
                    bucketPickup.pickupBlock(level, pos.above(), stateAbove);
                }
                if (level.getBlockState(pos.above()).getFluidState().isSource()) {
                    level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
                }
            }
        } else if (stateBelow.canBeReplaced()) {
            if (stateAbove.getBlock() instanceof BucketPickup bucketPickup) {
                ItemStack bucket = bucketPickup.pickupBlock(level, pos.above(), stateAbove);
                if (bucket.getItem() instanceof BucketItem) {
                    level.setBlockAndUpdate(below, stateAbove.getFluidState().createLegacyBlock());
                }
            }
        } else if (stateBelow.getBlock() instanceof AbstractCauldronBlock cauldron && !cauldron.isFull(stateBelow)) {
            if (stateAbove.getBlock() instanceof BucketPickup bucketPickup) {
                ItemStack stack = bucketPickup.pickupBlock(level, pos.above(), stateAbove);
                if (!stack.isEmpty()) {
                    ANFakePlayer fakePlayer = ANFakePlayer.getPlayer(level);
                    fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
                    stateBelow.use(level, fakePlayer, InteractionHand.MAIN_HAND,
                            new BlockHitResult(new Vec3(below.getX(), below.getY(), below.getZ()), Direction.UP, below, false));
                    if (!ItemStack.isSameItem(fakePlayer.getItemInHand(InteractionHand.MAIN_HAND), stack)) {
                        if (level.getBlockState(pos.above()).getFluidState().isSource()) {
                            level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!level.isClientSide && level.getBlockState(pos.above()).getFluidState().isSource()) {
            level.scheduleTick(pos, this, 10);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(BlockStateProperties.FACING)) {
            case DOWN -> TOP_AABB;
            case WEST -> WEST_OPEN_AABB;
            case SOUTH -> SOUTH_OPEN_AABB;
            case NORTH -> NORTH_OPEN_AABB;
            case EAST -> EAST_OPEN_AABB;
            default -> BOTTOM_AABB;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity == null || entity.getType().is(ITEM_GRATE_COLLIDE)) {
                return super.getCollisionShape(state, level, pos, context);
            }
            if (entity instanceof ItemEntity || entity instanceof Projectile || entity.getType().is(ITEM_GRATE_PASSABLE)) {
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(state, level, pos, context);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(BlockStateProperties.FACING, rot.rotate(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockState belowState = level.getBlockState(pos.below());
        if (belowState.getBlock() instanceof BucketPickup bucketPickup) {
            return bucketPickup.pickupBlock(level, pos.below(), belowState);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getPickupSound(BlockState state) {
        return Optional.empty();
    }
}
