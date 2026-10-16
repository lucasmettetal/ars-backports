package fr.lucas.arsbackports.registry;

import fr.lucas.arsbackports.ArsBackports;
import fr.lucas.arsbackports.entity.BubbleEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ArsBackports.MODID);

    // Same size and tracking range as 1.21.1's ModEntities.BUBBLE.
    public static final RegistryObject<EntityType<BubbleEntity>> BUBBLE = ENTITIES.register("bubble",
            () -> EntityType.Builder.<BubbleEntity>of(BubbleEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .build(ArsBackports.MODID + ":bubble"));

    private ModEntities() {
    }
}
