package fr.lucas.arsbackports.mobjar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

/**
 * Backport of Ars Nouveau 1.21.1's SnifferBehavior: an adult sniffer digs up a sniffer_digging loot roll
 * every 8 minutes while the jar is not powered.
 * <p>
 * 1.21.1 keeps the timer in Sniffer.DATA_DROP_SEED_AT_TICK, which is private in 1.20.1; the elapsed ticks are
 * stored in the jar's extra data tag instead (saved with the jar). The 1.21-only entity event 63 is not sent.
 */
public class SnifferBehavior extends JarBehavior<Sniffer> {
    private static final int DROP_INTERVAL = 8 * 60 * 20;
    private static final String TIMER_KEY = "ars_backports:sniffer_ticks";

    @Override
    public Vec3 scaleOffset(MobJarTile pBlockEntity) {
        float scale = -0.45F;
        return new Vec3(scale, scale, scale);
    }

    @Override
    public void tick(MobJarTile tile) {
        if (!(tile.getLevel() instanceof ServerLevel level) || JarBehaviors.isPowered(tile)) {
            return;
        }

        Sniffer sniffer = this.entityFromJar(tile);
        CompoundTag data = tile.getExtraDataTag();
        int ticks = data.getInt(TIMER_KEY) + 1;

        if (!isEntityBaby(sniffer) && ticks >= DROP_INTERVAL) {
            ticks = 0;
            LootTable loottable = level.getServer().getLootData().getLootTable(BuiltInLootTables.SNIFFER_DIGGING);
            LootParams lootparams = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, sniffer.position())
                    .withParameter(LootContextParams.THIS_ENTITY, sniffer)
                    .create(LootContextParamSets.GIFT);

            for (ItemStack stack : loottable.getRandomItems(lootparams)) {
                JarBehaviors.insertOrCreateItem(tile, stack);
            }

            sniffer.playSound(SoundEvents.SNIFFER_DROP_SEED, 1.0F, 1.0F);
        }

        data.putInt(TIMER_KEY, ticks);
        tile.setExtraDataTag(data);
    }
}
