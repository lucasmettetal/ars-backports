package fr.lucas.arsbackports.registry;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import fr.lucas.arsbackports.ArsBackports;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.function.Function;

/**
 * Dispenser behaviors added by Ars Nouveau 1.21.1 (DispenserBehaviorRegistry, "dominion dispenser" #1783 and
 * "push source/containment jars w/ piston to break & dispense to place" #1661). Ars 4.12.7 only registers ritual
 * tablets. Same logic as 1.21.1, with the 1.20.1 BlockSource accessors (getLevel/getPos/getBlockState).
 */
public final class ModDispenserBehaviors {

    private ModDispenserBehaviors() {
    }

    /** Must run on the main thread (FMLCommonSetupEvent#enqueueWork): DispenserBlock's registry is not thread safe. */
    public static void register() {
        // Source jars are placed upright, containment jars facing away from the dispenser.
        registerPlacement(BlockRegistry.SOURCE_JAR, facing -> Direction.DOWN, facing -> Direction.UP);
        registerPlacement(BlockRegistry.CREATIVE_SOURCE_JAR, facing -> Direction.DOWN, facing -> Direction.UP);
        registerPlacement(BlockRegistry.MOB_JAR, Direction::getOpposite, Direction::getOpposite);

        DispenserBlock.registerBehavior(ItemsRegistry.DOMINION_ROD, new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack item) {
                if (!(item.getItem() instanceof DominionWand wand)) {
                    this.setSuccess(false);
                    return item;
                }

                ServerLevel level = source.getLevel();
                Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
                BlockPos pos = source.getPos().relative(direction);
                Player player = ANFakePlayer.getPlayer(level);
                player.setItemInHand(InteractionHand.MAIN_HAND, item);

                List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos));
                if (!entities.isEmpty()) {
                    this.setSuccess(wand.interactLivingEntity(item, player, entities.get(0), InteractionHand.MAIN_HAND) != InteractionResult.FAIL);
                    return item;
                }

                if (level.getBlockState(pos).isAir()) {
                    this.setSuccess(false);
                    return item;
                }

                BlockHitResult hit = new BlockHitResult(pos.getCenter(), direction, pos, true);
                this.setSuccess(wand.useOn(new UseOnContext(level, player, InteractionHand.MAIN_HAND, item, hit)) != InteractionResult.FAIL);
                return item;
            }
        });
    }

    private static void registerPlacement(ItemLike block, Function<Direction, Direction> lookDirection, Function<Direction, Direction> face) {
        DispenserBlock.registerBehavior(block, new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack item) {
                Direction facing = source.getBlockState().getValue(DispenserBlock.FACING);
                BlockPos pos = source.getPos().relative(facing);
                if (item.getItem() instanceof BlockItem blockItem) {
                    try {
                        this.setSuccess(blockItem.place(new UnbiasedDirectionalPlaceContext(source.getLevel(), pos,
                                lookDirection.apply(facing), item, face.apply(facing))).consumesAction());
                    } catch (Exception exception) {
                        ArsBackports.LOGGER.error("Error trying to place {} at {}", item, pos, exception);
                    }
                }
                return item;
            }
        });
    }

    /** 1.21.1's UnbiasedDirectionalPlaceContext: the given direction is always the nearest looking direction. */
    public static class UnbiasedDirectionalPlaceContext extends DirectionalPlaceContext {
        private final Direction direction;

        public UnbiasedDirectionalPlaceContext(Level level, BlockPos pos, Direction direction, ItemStack itemStack, Direction face) {
            super(level, pos, direction, itemStack, face);
            this.direction = direction;
        }

        @Override
        public Direction getNearestLookingDirection() {
            return this.direction;
        }

        @Override
        public Direction[] getNearestLookingDirections() {
            return switch (this.direction) {
                default -> new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
                case UP -> new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
                case NORTH -> new Direction[]{Direction.NORTH, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH};
                case SOUTH -> new Direction[]{Direction.SOUTH, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH};
                case WEST -> new Direction[]{Direction.WEST, Direction.DOWN, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.EAST};
                case EAST -> new Direction[]{Direction.EAST, Direction.DOWN, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.WEST};
            };
        }
    }
}
