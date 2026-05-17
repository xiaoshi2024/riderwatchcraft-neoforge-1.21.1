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

    // ==================== 可配置伤害字段 ====================
    // ERASE 消除型 - 范围破坏，不直接造成实体伤害
    public static int ERASE_RADIUS = 3;           // 消除半径（格）

    // IMPACT 冲击型 - 范围震动伤害
    public static double IMPACT_RADIUS = 12.0;    // 冲击半径
    public static int IMPACT_DURATION = 60;       // 冲击持续时间（tick）

    // PLASMA 等离子型 - 雷电范围伤害（打残铁傀儡主要靠这个）
    public static double PLASMA_RADIUS = 6.5;     // 等离子半径（格）
    public static float PLASMA_BASE_DAMAGE = 45.0f;   // 基础伤害（中心点伤害）
    public static float PLASMA_MIN_DAMAGE = 8.0f;     // 最小伤害（边缘保底伤害）
    public static float PLASMA_DAMAGE_EXPONENT = 1.0f; // 衰减曲线指数（1.0=线性，越大边缘伤害越低）

    // PLASMA 击飞力度
    public static double PLASMA_KNOCKBACK_HORIZONTAL = 2.0;  // 水平击飞系数
    public static double PLASMA_KNOCKBACK_VERTICAL = 1.2;    // 垂直击飞系数

    // 弹道参数
    public static double PROJECTILE_SPEED = 1.8;   // 投射物速度（原2.5）
    public static int PLASMA_DELAY_TICKS = 50;     // PLASMA延迟爆炸时间（tick）

    // 多重投射散布
    public static double SPREAD_RANGE = 0.6;       // 多重投射随机偏移范围

    // 粒子数量
    public static int PARTICLE_COUNT = 300;        // 粒子数量

    // 声音参数
    public static float ERASE_VOLUME = 1.2f;
    public static float PLASMA_VOLUME = 1.5f;
    // ====================================================

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

        if (!this.level().isClientSide && !hasExecuted && getCapsemType() == CapsemType.PLASMA && this.tickCount > PLASMA_DELAY_TICKS) {
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
                net.minecraft.sounds.SoundSource.HOSTILE, ERASE_VOLUME, 0.6F + level().random.nextFloat() * 0.6F);

        int radius = ERASE_RADIUS;
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
        ImpactEarthquakeEntity.create(level(), position, IMPACT_RADIUS, IMPACT_DURATION);
    }

    private void executePlasmaEffect(Vec3 position) {
        level().playSound(null, position.x, position.y, position.z,
                net.minecraft.sounds.SoundEvents.LIGHTNING_BOLT_THUNDER,
                net.minecraft.sounds.SoundSource.HOSTILE, PLASMA_VOLUME, 0.8F + level().random.nextFloat() * 0.4F);

        double radius = PLASMA_RADIUS;

        java.util.List<LivingEntity> entities = level().getEntitiesOfClass(
                LivingEntity.class,
                new net.minecraft.world.phys.AABB(
                        position.x - radius, position.y - radius, position.z - radius,
                        position.x + radius, position.y + radius, position.z + radius
                ),
                entity -> entity != getOwner() && entity.isAlive() && entity.distanceToSqr(position) <= radius * radius
        );

        for (LivingEntity entity : entities) {
            double dx = entity.getX() - position.x;
            double dy = entity.getY() - position.y;
            double dz = entity.getZ() - position.z;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double ratio = distance / radius;

            // 使用指数衰减公式：damage = baseDamage * (1 - ratio^exponent)
            float damage = PLASMA_BASE_DAMAGE * (float)(1 - Math.pow(ratio, PLASMA_DAMAGE_EXPONENT));
            damage = Math.max(PLASMA_MIN_DAMAGE, damage);

            entity.hurt(level().damageSources().lightningBolt(), damage);

            Vec3 pushDir = new Vec3(dx, dy, dz).normalize();
            entity.setDeltaMovement(
                    pushDir.x * PLASMA_KNOCKBACK_HORIZONTAL,
                    PLASMA_KNOCKBACK_VERTICAL,
                    pushDir.z * PLASMA_KNOCKBACK_HORIZONTAL
            );
            entity.hurtMarked = true;
        }

        if (level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < PARTICLE_COUNT; i++) {
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
                    direction.x * PROJECTILE_SPEED,
                    direction.y * PROJECTILE_SPEED,
                    direction.z * PROJECTILE_SPEED,
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
                    (RANDOM.nextDouble() - 0.5) * SPREAD_RANGE,
                    (RANDOM.nextDouble() - 0.5) * SPREAD_RANGE,
                    (RANDOM.nextDouble() - 0.5) * SPREAD_RANGE
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