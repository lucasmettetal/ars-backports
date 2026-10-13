package fr.lucas.arsbackports.mobjar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.phys.Vec3;

/** Backport of Ars Nouveau 1.21.1's SnowGolemBehavior: fires a snowball out of the jar's facing side on a redstone pulse. */
public class SnowGolemBehavior extends JarBehavior<SnowGolem> {
    @Override
    public void onRedstonePower(MobJarTile tile) {
        if (!(tile.getLevel() instanceof ServerLevel level)) {
            return;
        }

        SnowGolem golem = this.entityFromJar(tile);
        Snowball snowball = new Snowball(level, golem);
        Vec3 dir = new Vec3(tile.getBlockState().getValue(MobJar.FACING).step());
        snowball.setPos(tile.getBlockPos().getCenter().add(dir.scale(0.6)));
        snowball.shoot(dir.x, dir.y, dir.z, 1.6F, 12.0F);
        golem.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (golem.getRandom().nextFloat() * 0.4F + 0.8F));
        level.addFreshEntity(snowball);
    }
}
