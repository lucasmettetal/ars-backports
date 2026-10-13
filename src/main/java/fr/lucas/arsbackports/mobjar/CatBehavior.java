package fr.lucas.arsbackports.mobjar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.api.util.LevelPosMap;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import fr.lucas.arsbackports.ArsBackports;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Backport of Ars Nouveau 1.21.1's CatBehavior: a jarred cat emits a redstone signal of 15 while its owner is
 * online, and may leave its morning gift in the jar when its owner sleeps nearby.
 */
@Mod.EventBusSubscriber(modid = ArsBackports.MODID)
public class CatBehavior extends JarBehavior<Cat> {
    public static final LevelPosMap CAT_MAP = new LevelPosMap(
            (level, pos) -> !(level.getBlockEntity(pos) instanceof MobJarTile mobJarTile) || !(mobJarTile.getEntity() instanceof Cat)
    );

    @Override
    public void tick(MobJarTile tile) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide) {
            return;
        }

        if (level.getGameTime() % 20 == 0) {
            CAT_MAP.addPosition(level, tile.getBlockPos());
        }
    }

    @Override
    public int getSignalPower(MobJarTile tile) {
        if (!(tile.getLevel() instanceof ServerLevel)) {
            return super.getSignalPower(tile);
        }

        Cat cat = this.entityFromJar(tile);
        return cat.getOwner() == null ? 0 : 15;
    }

    // Re-evaluates the signal of the jars holding a cat owned by this player.
    private static void updateOwnedJars(Level level, UUID owner) {
        Set<BlockPos> positions = CAT_MAP.posMap.get(level.dimension().location().toString());
        if (positions == null) {
            return;
        }

        List<BlockPos> stale = new ArrayList<>();
        for (BlockPos pos : positions) {
            if (!level.isLoaded(pos)) {
                continue;
            }

            if (CAT_MAP.removeFunction.apply(level, pos)) {
                stale.add(pos);
                continue;
            }

            if (level.getBlockEntity(pos) instanceof MobJarTile jar && jar.getEntity() instanceof Cat cat && owner.equals(cat.getOwnerUUID())) {
                level.updateNeighborsAt(pos, jar.getBlockState().getBlock());
            }
        }

        positions.removeAll(stale);
    }

    @SubscribeEvent
    public static void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        updateOwnedJars(event.getEntity().level(), event.getEntity().getUUID());
    }

    // 1.21.1 uses Ars' PlayerPostLogOutEvent (fired once the player is gone). Forge 1.20.1's PlayerLoggedOutEvent
    // fires while the player is still in the level, so the update is deferred to the next server tick.
    @SubscribeEvent
    public static void onPlayerLeft(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        Level level = player.level();
        UUID owner = player.getUUID();
        server.tell(new TickTask(server.getTickCount(), () -> updateOwnedJars(level, owner)));
    }

    @SubscribeEvent
    public static void onSleepFinished(SleepFinishedTimeEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        String key = level.dimension().location().toString();
        Set<BlockPos> positions = CAT_MAP.posMap.getOrDefault(key, new HashSet<>());
        List<BlockPos> stale = new ArrayList<>();
        for (BlockPos pos : positions) {
            if (!level.isLoaded(pos)) {
                continue;
            }

            if (CAT_MAP.removeFunction.apply(level, pos)) {
                stale.add(pos);
                continue;
            }

            MobJarTile tile = (MobJarTile) level.getBlockEntity(pos);
            Cat cat = (Cat) tile.getEntity();

            // 1.21.1 returns here, which also skips every remaining jar; continue keeps the per-cat 70 % chance.
            if (!(cat.getOwner() instanceof Player owner) || cat.getRandom().nextFloat() > 0.7F) {
                continue;
            }

            if (owner.distanceToSqr(cat) <= 10 * 10 && owner.getSleepTimer() >= 100) {
                LootTable loottable = level.getServer().getLootData().getLootTable(BuiltInLootTables.CAT_MORNING_GIFT);
                LootParams lootparams = new LootParams.Builder(level)
                        .withParameter(LootContextParams.ORIGIN, cat.position())
                        .withParameter(LootContextParams.THIS_ENTITY, cat)
                        .create(LootContextParamSets.GIFT);

                for (ItemStack itemstack : loottable.getRandomItems(lootparams)) {
                    JarBehaviors.insertOrCreateItem(tile, itemstack);
                }
            }
        }

        positions.removeAll(stale);
    }
}
