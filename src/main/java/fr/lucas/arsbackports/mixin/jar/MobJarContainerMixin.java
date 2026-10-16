package fr.lucas.arsbackports.mixin.jar;

import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Backport of 1.21.1 "more jar behaviors" (#1644): sneak-using a jar that holds a container entity (chest minecart,
 * chest boat...) opens its inventory. Same place as 1.21.1: right before the jar behaviors are dispatched; a PASS
 * result falls through to the normal behaviors.
 */
@Mixin(MobJar.class)
public abstract class MobJarContainerMixin {

    @Inject(method = "use", cancellable = true, at = @At(value = "INVOKE", remap = false,
            target = "Lcom/hollingsworth/arsnouveau/common/block/tile/MobJarTile;dispatchBehavior(Ljava/util/function/Consumer;)V"))
    private void ars_backports$openContainerEntity(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                                  BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.isSecondaryUseActive() && level.getBlockEntity(pos) instanceof MobJarTile tile
                && tile.getEntity() instanceof ContainerEntity container) {
            InteractionResult result = container.interactWithContainerVehicle(player);
            if (result != InteractionResult.PASS) {
                cir.setReturnValue(result);
            }
        }
    }
}
