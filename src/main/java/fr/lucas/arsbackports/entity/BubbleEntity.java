package fr.lucas.arsbackports.entity;

import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import fr.lucas.arsbackports.ArsBackports;
import fr.lucas.arsbackports.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bubble of the Bubble glyph, backported from Ars Nouveau 1.21.1: floats upwards, captures the first entity it
 * touches as a passenger and pops when it expires, is hit, or its passenger is hurt (dealing bonus damage).
 * <p>
 * Differences with 1.21.1, which uses mixins / Ars-internal events for these:
 * <ul>
 *   <li>entities are boarded with {@code startRiding(this, true)}, which skips only Entity#canRide (sneaking +
 *       boarding cooldown); the sneaking check is kept, so this matches 1.21.1's "ignore boardingCooldown" accessor;</li>
 *   <li>the passenger's death is detected in {@link #removePassenger} (Entity#setRemoved sets the removal reason
 *       before dismounting) instead of Ars' EntityPreRemovalEvent;</li>
 *   <li>passengers are not made "wet" (1.21.1 EntityMixin) — not reproducible without a mixin.</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = ArsBackports.MODID)
public class BubbleEntity extends Projectile {
    public static final TagKey<EntityType<?>> BUBBLE_BLACKLIST =
            TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ArsBackports.MODID, "bubble_blacklist"));
    public static final EntityDataAccessor<Boolean> HAS_POPPED = SynchedEntityData.defineId(BubbleEntity.class, EntityDataSerializers.BOOLEAN);

    int maxAge;
    int age;
    float damage;
    public int poppingTicks;
    // Never set, as in 1.21.1 (popped state lives in HAS_POPPED); kept for the same damage condition.
    public boolean hasPopped;
    final List<UUID> hasDismounted = new ArrayList<>();

    public BubbleEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public BubbleEntity(Level level, int maxAge, float damage) {
        super(ModEntities.BUBBLE.get(), level);
        this.maxAge = maxAge;
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(HAS_POPPED, false);
    }

    @Override
    public void tick() {
        super.tick();
        Level level = level();

        if (entityData.get(HAS_POPPED)) {
            poppingTicks++;
        }

        if (!level.isClientSide) {
            age++;
            if (age > maxAge) {
                this.pop();
            }

            if (this.getPassengers().isEmpty()) {
                for (Entity entity : level.getEntities(this, this.getBoundingBox().inflate(0.5f), this::canHitEntity)) {
                    if (!entity.isShiftKeyDown() && entity.startRiding(this, true)) {
                        break;
                    }
                }
            }
        }

        if (poppingTicks > 5 && !level.isClientSide) {
            this.remove(RemovalReason.DISCARDED);
        }

        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        this.setDeltaMovement(random.nextDouble() * 0.02 - 0.01, 0.1, random.nextDouble() * 0.02 - 0.01);
        this.setPos(getNextHitPosition());
    }

    public boolean tryCapturing(Entity target) {
        if (!this.entityData.get(HAS_POPPED) && this.getPassengers().isEmpty() && this.canHitEntity(target)) {
            target.startRiding(this);
            return !this.getPassengers().isEmpty();
        }
        return false;
    }

    public Vec3 getNextHitPosition() {
        return this.position().add(this.getDeltaMovement());
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        if (passenger != null && passenger.getUUID() != null) {
            this.hasDismounted.add(passenger.getUUID());
        }
        return super.getDismountLocationForPassenger(passenger);
    }

    // Replaces 1.21.1's EntityPreRemovalEvent handler: a passenger that dies or is destroyed pops the bubble (silently).
    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (!level().isClientSide && passenger.isRemoved() && passenger.getRemovalReason() != null && passenger.getRemovalReason().shouldDestroy()) {
            this.entityData.set(HAS_POPPED, true);
        }
    }

    public void pop() {
        if (this.level().isClientSide || this.entityData.get(HAS_POPPED)) {
            return;
        }
        this.entityData.set(HAS_POPPED, true);
        level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, this.getSoundSource(), 3.0F, 1.0F);
    }

    // Prevents the default attack noise: hitting an empty bubble (or one carrying an item, or yourself) pops it,
    // hitting a bubble that carries a creature hits the creature.
    @SubscribeEvent
    public static void onAttacked(AttackEntityEvent event) {
        if (event.getTarget() instanceof BubbleEntity bubble) {
            if (bubble.getPassengers().isEmpty()
                    || bubble.getFirstPassenger() instanceof ItemEntity
                    || bubble.getFirstPassenger() == event.getEntity()) {
                bubble.pop();
                event.setCanceled(true);
            } else if (bubble.getFirstPassenger() instanceof LivingEntity passenger) {
                event.getEntity().attack(passenger);
            }
        }
    }

    // 1.21.1 uses LivingDamageEvent.Pre (damage after reductions); Forge 1.20.1's equivalent is LivingDamageEvent.
    @SubscribeEvent
    public static void onPassengerHurt(LivingDamageEvent event) {
        if (event.getEntity().getVehicle() instanceof BubbleEntity bubble) {
            if (bubble.age > 1 && !bubble.hasPopped && bubble.getFirstPassenger() != bubble.getOwner()) {
                float damage = bubble.damage;
                if (bubble.getOwner() instanceof LivingEntity shooter && shooter.getAttributes().hasAttribute(PerkAttributes.SPELL_DAMAGE_BONUS.get())) {
                    damage += (float) shooter.getAttributeValue(PerkAttributes.SPELL_DAMAGE_BONUS.get());
                }
                event.setAmount(event.getAmount() + damage);
            }
            bubble.pop();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        tryCapturing(result.getEntity());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.getPassengers().isEmpty() || this.getFirstPassenger() instanceof ItemEntity) {
            this.pop();
        }
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (target != null && target.getUUID() != null && this.hasDismounted.contains(target.getUUID())) {
            return false;
        }
        if (target instanceof Player player && player.isSleeping()) {
            return false;
        }
        return !target.getType().is(BUBBLE_BLACKLIST) && !(target.getVehicle() instanceof BubbleEntity);
    }

    // 1.21.1 getPassengerRidingPosition: items slightly above the bubble's origin, everything else at it.
    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            callback.accept(passenger, this.getX(), this.getY() + (passenger instanceof ItemEntity ? 0.25 : 0), this.getZ());
        }
    }

    @Override
    protected boolean canRide(Entity vehicle) {
        return !(vehicle instanceof BubbleEntity) && super.canRide(vehicle);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("maxAge", this.maxAge);
        tag.putFloat("damage", this.damage);
        tag.putInt("age", this.age);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.maxAge = tag.getInt("maxAge");
        this.damage = tag.getFloat("damage");
        this.age = tag.getInt("age");
    }

    @Override
    public boolean canBeHitByProjectile() {
        return this.getPassengers().isEmpty() || this.getFirstPassenger() instanceof ItemEntity && (this.isAlive() && age > 1);
    }

    @Override
    public boolean mayInteract(Level level, BlockPos pos) {
        return true;
    }
}
