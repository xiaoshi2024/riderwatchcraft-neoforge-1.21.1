package com.xiaoshi2022.riderwatchcraft.rider.zzz.entity.Capsem;

import com.xiaoshi2022.riderwatchcraft.registry.EntityRegister;
import com.xiaoshi2022.riderwatchcraft.rider.zzz.entity.skill.ImpactEarthquakeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;

public class CapsemEntity extends ThrowableProjectile implements GeoEntity {

    private static final EntityDataAccessor<Integer> CAPSEM_TYPE = SynchedEntityData.defineId(CapsemEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // 只保留 idle 动画，循环播放
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
        // 无需额外动画处理，idle 会自动循环播放
    }

    @Override
    protected void onHit(net.minecraft.world.phys.HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide && !hasExecuted) {
            hasExecuted = true;
            executeCapsemEffect(result.getLocation().x(), result.getLocation().y(), result.getLocation().z());
            this.discard();
        }
    }

    private void executeCapsemEffect(double x, double y, double z) {
        Vec3 position = new Vec3(x, y, z);

        switch (getCapsemType()) {
            case ERASE:
                executeEraseEffect(position);
                break;
            case IMPACT:
                executeImpactEffect(position);
                break;
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
        // 只有一个动画控制器，循环播放 idle
        controllers.add(new AnimationController<>(this, "idle_controller", 0, event -> {
            event.getController().setAnimation(IDLE_ANIMATION);
            return PlayState.CONTINUE;
        }));
    }
}