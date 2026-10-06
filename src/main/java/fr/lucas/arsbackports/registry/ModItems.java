package fr.lucas.arsbackports.registry;

import fr.lucas.arsbackports.ArsBackports;
import fr.lucas.arsbackports.item.EnchantersGauntlet;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ArsBackports.MODID);

    public static final RegistryObject<EnchantersGauntlet> ENCHANTERS_GAUNTLET =
            ITEMS.register("enchanters_gauntlet", () -> new EnchantersGauntlet(new Item.Properties()));

    private ModItems() {
    }
}
