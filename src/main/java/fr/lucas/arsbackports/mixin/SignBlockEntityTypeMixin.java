package fr.lucas.arsbackports.mixin;

import fr.lucas.arsbackports.registry.ModBlocks;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The vanilla sign / hanging sign block entity types accept the archwood signs, as 1.21.1 does with NeoForge's
 * BlockEntityTypeAddBlocksEvent (absent from Forge 1.20.1). This keeps the vanilla block entities, renderers and
 * edit screens for them.
 */
@Mixin(BlockEntityType.class)
public abstract class SignBlockEntityTypeMixin {

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void ars_backports$acceptArchwoodSigns(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!(state.getBlock() instanceof SignBlock sign) || sign.type() != ModBlocks.ARCHWOOD) {
            return;
        }
        Object type = this;
        if (type == BlockEntityType.SIGN && (sign instanceof StandingSignBlock || sign instanceof WallSignBlock)) {
            cir.setReturnValue(true);
        } else if (type == BlockEntityType.HANGING_SIGN && (sign instanceof CeilingHangingSignBlock || sign instanceof WallHangingSignBlock)) {
            cir.setReturnValue(true);
        }
    }
}
