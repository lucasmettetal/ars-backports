package fr.lucas.arsbackports.mixin.jar;

import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.mob_jar.ChickenBehavior;
import fr.lucas.arsbackports.mobjar.JarBehaviors;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Backport of 1.21.1 "more jar behaviors" (#1644) for the chicken: eggs go into the inventories next to the jar
 * ({@code JarBehavior.insertOrCreateItem}) instead of being dropped at the chicken.
 */
@Mixin(ChickenBehavior.class)
public abstract class JarInventoryOutputMixin {

    @Redirect(method = "tick", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/world/entity/animal/Chicken;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity ars_backports$insertEgg(Chicken chicken, ItemLike item, MobJarTile tile) {
        JarBehaviors.insertOrCreateItem(tile, item.asItem().getDefaultInstance());
        return null;
    }
}
