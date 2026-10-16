package fr.lucas.arsbackports.mixin.jar;

import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.mob_jar.PandaBehavior;
import fr.lucas.arsbackports.mobjar.JarBehaviors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Backport of 1.21.1 "more jar behaviors" (#1644) for the panda: the sneezed slime ball goes into the inventories
 * next to the jar instead of being spawned above it.
 */
@Mixin(PandaBehavior.class)
public abstract class PandaInventoryOutputMixin {

    @Redirect(method = "afterSneeze", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean ars_backports$insertSlimeBall(Level level, Entity entity, Panda panda, MobJarTile tile) {
        if (entity instanceof ItemEntity item) {
            return JarBehaviors.insertOrCreateItem(tile, item.getItem());
        }
        return level.addFreshEntity(entity);
    }
}
