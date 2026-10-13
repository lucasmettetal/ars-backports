package fr.lucas.arsbackports.mobjar;

import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.mob_jar.JarBehaviorRegistry;
import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Mob Jar behaviors backported from Ars Nouveau 1.21.1, plus the two JarBehavior helpers they rely on
 * (isPowered / insertOrCreateItem), which do not exist in Ars 4.12.7's JarBehavior.
 */
public final class JarBehaviors {

    private JarBehaviors() {
    }

    // Same registrations as 1.21.1's APIRegistry; Ars 4.12.7 registers no behavior for these entity types.
    public static void register() {
        JarBehaviorRegistry.register(EntityType.CAT, new CatBehavior());
        JarBehaviorRegistry.register(EntityType.SNOW_GOLEM, new SnowGolemBehavior());
        JarBehaviorRegistry.register(EntityType.SNIFFER, new SnifferBehavior());
    }

    public static boolean isPowered(MobJarTile tile) {
        return tile.getBlockState().getValue(MobJar.POWERED);
    }

    // Inserts into the inventories adjacent to the jar, dropping the remainder above it.
    public static boolean insertOrCreateItem(MobJarTile tile, ItemStack stack) {
        if (!(tile.getLevel() instanceof ServerLevel level)) {
            return false;
        }

        ItemStack remaining = InventoryManager.fromTile(tile).insertStack(stack);
        if (!remaining.isEmpty()) {
            Vec3 center = tile.getBlockPos().above().getCenter();
            ItemEntity item = new ItemEntity(level, center.x, center.y, center.z, remaining);
            item.setDefaultPickUpDelay();
            level.addFreshEntity(item);
        }
        return true;
    }
}
