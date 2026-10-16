package fr.lucas.arsbackports.mixin.jar;

import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import fr.lucas.arsbackports.mobjar.JarEntityItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Backport of 1.21.1 "more jar behaviors" (#1644): a jar exposes the item handler of the entity it holds (chest
 * minecart, chest boat, donkey...), so hoppers and pipes can insert into / extract from it. 1.21.1 registers a
 * NeoForge block capability; on Forge 1.20.1 the jar's block entity gets a getCapability override (neither MobJarTile
 * nor ModdedTile defines one). The handler resolves the jarred entity on every call, so a cached LazyOptional never
 * points to a stale entity.
 */
@Mixin(MobJarTile.class)
public abstract class MobJarItemHandlerMixin extends ModdedTile {

    private MobJarItemHandlerMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            MobJarTile tile = (MobJarTile) (Object) this;
            Entity entity = tile.getEntity();
            if (entity != null && entity.getCapability(ForgeCapabilities.ITEM_HANDLER, side).isPresent()) {
                return LazyOptional.of(() -> new JarEntityItemHandler(tile, side)).cast();
            }
        }
        return super.getCapability(cap, side);
    }
}
