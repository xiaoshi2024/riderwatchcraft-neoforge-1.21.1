package com.xiaoshi2022.riderwatchcraft.rider.zzz.entity.Capsem;

import com.xiaoshi2022.riderwatchcraft.registry.EntityRegister;
import com.xiaoshi2022.riderwatchcraft.rider.zzz.entity.skill.ImpactEarthquakeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;

public class CapsemEntity extends ThrowableProjectile implements GeoEntity {

    private static final EntityDataAccessor<Integer> CAPSEM_TYPE = SynchedEntityData.defineId(CapsemEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");

    private boolean hasExecuted = false;
    private static final Random RANDOM = new Random();

    public CapsemEntity(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    public CapsemEntity(Level level, double x, double y, double z, double dx, double dy, double dz, CapsemType type) {
        super(EntityRegister.CAPSEM.get(), level);
        this.setPos(x, y, z);
        this.setDeltaMovement(dx, dy, dz);
        this.entityData.set(CAPSEM_TYPE, type.ordinal());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(CAPSEM_TYPE, CapsemType.ERASE.ordinal());
    }

    public CapsemType getCapsemType() {
        return CapsemType.values()[entityData.get(CAPSEM_TYPE)];
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && !hasExecuted && getCapsemType() == CapsemType.PLASMA && this.tickCount > 40) {
            hasExecuted = true;
            executePlasmaEffect(this.position());
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide && !hasExecuted) {
            hasExecuted = true;

            switch (getCapsemType()) {
                case ERASE:
                    executeEraseEffect(result.getLocation());
                    break;
                case IMPACT:
                    executeImpactEffect(result.getLocation());
                    break;
                case PLASMA:
                    executePlasmaEffect(result.getLocation());
                    break;
            }

            this.discard();
        }
    }

    private void executeEraseEffect(Vec3 position) {
        level().playSound(null, position.x, position.y, position.z,
                net.minecraft.sounds.SoundEvents.GLASS_BREAK,
                net.minecraft.sounds.SoundSource.HOSTILE, 0.8F, 0.8F + level().random.nextFloat() * 0.4F);

        int radius = 2;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos blockPos = new BlockPos(
                            (int) position.x + dx,
                            (int) position.y + dy,
                            (int) position.z + dz
                    );
                    BlockState state = level().getBlockState(blockPos);
                    Block block = state.getBlock();

                    if (isRedstoneStructure(block)) {
                        level().destroyBlock(blockPos, false);
                    }
                }
            }
        }
    }

    private void executeImpactEffect(Vec3 position) {
        ImpactEarthquakeEntity.create(level(), position, 8.0, 40);
    }

    private void executePlasmaEffect(Vec3 position) {
        level().playSound(null, position.x, position.y, position.z,
                net.minecraft.sounds.SoundEvents.LIGHTNING_BOLT_THUNDER,
                net.minecraft.sounds.SoundSource.HOSTILE, 1.5F, 0.8F + level().random.nextFloat() * 0.4F);

        double radius = 5.0;

        java.util.List<net.minecraft.world.entity.LivingEntity> entities = level().getEntitiesOfClass(
                net.minecraft.world.entity.LivingEntity.class,
                new net.minecraft.world.phys.AABB(
                        position.x - radius, position.y - radius, position.z - radius,
                        position.x + radius, position.y + radius, position.z + radius
                ),
                entity -> entity != getOwner() && entity.isAlive() && entity.distanceToSqr(position) <= radius * radius
        );

        for (net.minecraft.world.entity.LivingEntity entity : entities) {
            double dx = entity.getX() - position.x;
            double dy = entity.getY() - position.y;
            double dz = entity.getZ() - position.z;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            float damage = 12.0f * (float)(1 - distance / radius);
            entity.hurt(level().damageSources().lightningBolt(), Math.max(4.0f, damage));

            Vec3 pushDir = new Vec3(dx, dy, dz).normalize();
            entity.setDeltaMovement(pushDir.x * 1.5, 0.8, pushDir.z * 1.5);
            entity.hurtMarked = true;
        }

        if (level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 150; i++) {
                double offsetX = (level().random.nextDouble() - 0.5) * radius * 2;
                double offsetY = (level().random.nextDouble() - 0.5) * radius;
                double offsetZ = (level().random.nextDouble() - 0.5) * radius * 2;
                serverLevel.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK,
                        position.x + offsetX, position.y + offsetY, position.z + offsetZ,
                        1,
                        (level().random.nextDouble() - 0.5) * 0.3,
                        level().random.nextDouble() * 0.3,
                        (level().random.nextDouble() - 0.5) * 0.3,
                        0.2
                );
            }
        }
    }

    private boolean isRedstoneStructure(Block block) {
        return block instanceof LeverBlock ||
                block.getDescriptionId().contains("button") ||
                block.getDescriptionId().contains("redstone") ||
                block.getDescriptionId().contains("repeater") ||
                block.getDescriptionId().contains("comparator") ||
                block.getDescriptionId().contains("observer") ||
                block.getDescriptionId().contains("dispenser") ||
                block.getDescriptionId().contains("dropper") ||
                block.getDescriptionId().contains("hopper");
    }

    public static void trySpawnEffect(Level level, LivingEntity shooter, Vec3 direction, CapsemType type) {
        if (!level.isClientSide) {
            CapsemEntity entity = new CapsemEntity(
                    level,
                    shooter.getX(),
                    shooter.getY() + shooter.getEyeHeight(),
                    shooter.getZ(),
                    direction.x * 2.5,
                    direction.y * 2.5,
                    direction.z * 2.5,
                    type
            );

            entity.setOwner(shooter);
            level.addFreshEntity(entity);

            level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                    net.minecraft.sounds.SoundEvents.SOUL_ESCAPE,
                    net.minecraft.sounds.SoundSource.HOSTILE, 0.8F, 0.9F + level.random.nextFloat() * 0.2F);
        }
    }

    public static void trySpawnMultipleEffects(Level level, LivingEntity shooter, Vec3 direction, CapsemType... types) {
        for (CapsemType type : types) {
            trySpawnEffect(level, shooter, direction.add(
                    (RANDOM.nextDouble() - 0.5) * 0.3,
                    (RANDOM.nextDouble() - 0.5) * 0.3,
                    (RANDOM.nextDouble() - 0.5) * 0.3
            ), type);
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle_controller", 0, event -> {
            event.getController().setAnimation(IDLE_ANIMATION);
            return PlayState.CONTINUE;
        }));
    }
}