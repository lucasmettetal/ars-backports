package fr.lucas.arsbackports.mobjar;

import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Item handler of a Mob Jar, delegating to the item handler of the entity it holds (1.21.1 SavingItemHandler).
 * The entity is looked up on every call; modifications mark the jar dirty so the jarred entity (saved from
 * MobJarTile#cachedEntity) is written to disk.
 */
public class JarEntityItemHandler implements IItemHandler {
    private final MobJarTile tile;
    private final @Nullable Direction side;

    public JarEntityItemHandler(MobJarTile tile, @Nullable Direction side) {
        this.tile = tile;
        this.side = side;
    }

    private @Nullable IItemHandler inner() {
        Entity entity = tile.getEntity();
        return entity == null ? null : entity.getCapability(ForgeCapabilities.ITEM_HANDLER, side).orElse(null);
    }

    @Override
    public int getSlots() {
        IItemHandler inner = inner();
        return inner == null ? 0 : inner.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        IItemHandler inner = inner();
        return inner == null || slot >= inner.getSlots() ? ItemStack.EMPTY : inner.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        IItemHandler inner = inner();
        if (inner == null || slot >= inner.getSlots()) {
            return stack;
        }
        ItemStack result = inner.insertItem(slot, stack, simulate);
        if (!simulate) {
            tile.setChanged();
        }
        return result;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        IItemHandler inner = inner();
        if (inner == null || slot >= inner.getSlots()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = inner.extractItem(slot, amount, simulate);
        if (!simulate) {
            tile.setChanged();
        }
        return result;
    }

    @Override
    public int getSlotLimit(int slot) {
        IItemHandler inner = inner();
        return inner == null || slot >= inner.getSlots() ? 0 : inner.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        IItemHandler inner = inner();
        return inner != null && slot < inner.getSlots() && inner.isItemValid(slot, stack);
    }
}
