//package com.xiaoshi2022.riderwatchcraft.rider.kuuga.entity;
//
//import com.xiaoshi2022.kamenriderweaponcraft.rider.heisei.kuuga.KuugaRiderEntity;
//import com.xiaoshi2022.kamenriderweaponcraft.rider.sound.RiderSounds;
//import com.xiaoshi2022.riderwatchcraft.registry.EntityRegister;
//import net.minecraft.core.particles.ParticleTypes;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.world.effect.MobEffectInstance;
//import net.minecraft.world.effect.MobEffects;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.phys.Vec3;
//
//public class EnhancedKuugaRiderEntity extends KuugaRiderEntity {
//
//    private boolean isSuperMode = false;
//    private int chargeLevel = 0;
//    private static final int MAX_CHARGE = 100;
//    private float attackDamage = 45.0f;  // 存储伤害值
//
//    // 无参构造器（供 EntityType 使用）
//    public EnhancedKuugaRiderEntity(EntityType<? extends KuugaRiderEntity> type, Level level) {
//        super(type, level);
//    }
//
//    // 完整参数构造器
//    public EnhancedKuugaRiderEntity(Level level, double x, double y, double z, double dx, double dy, double dz) {
//        this(EntityRegister.ENHANCED_KUUGA_RIDER.get(), level);
//        this.setPos(x, y, z);
//        this.setDeltaMovement(dx, dy, dz);
//    }
//
//    public void setSuperMode(boolean superMode) {
//        this.isSuperMode = superMode;
//    }
//
//    public void setChargeLevel(int charge) {
//        this.chargeLevel = Math.min(MAX_CHARGE, Math.max(0, charge));
//    }
//
//    public void setAttackDamage(float damage) {
//        this.attackDamage = damage;
//    }
//
//    public float getAttackDamage() {
//        return this.attackDamage;
//    }
//
//    private float getDamageMultiplier() {
//        float multiplier = 1.0f;
//        if (isSuperMode) multiplier *= 1.5f;
//        multiplier *= (1.0f + (chargeLevel / 200.0f));
//        return multiplier;
//    }
//
//    @Override
//    public void tick() {
//        super.tick();
//
//        // 增强的粒子特效（客户端）
//        if (this.level().isClientSide && !this.isRemoved()) {
//            Vec3 pos = this.position();
//
//            // 基础金色粒子
//            for (int i = 0; i < 5; i++) {
//                this.level().addParticle(ParticleTypes.GLOW,
//                        pos.x + (random.nextDouble() - 0.5) * 0.8,
//                        pos.y + random.nextDouble() * 1.5,
//                        pos.z + (random.nextDouble() - 0.5) * 0.8,
//                        (random.nextDouble() - 0.5) * 0.3,
//                        0.1 + random.nextDouble() * 0.2,
//                        (random.nextDouble() - 0.5) * 0.3);
//            }
//
//            // 超级模式：红色闪电
//            if (isSuperMode) {
//                for (int i = 0; i < 8; i++) {
//                    double angle = random.nextDouble() * Math.PI * 2;
//                    double radius = 0.8;
//                    double offsetX = Math.cos(angle) * radius;
//                    double offsetZ = Math.sin(angle) * radius;
//
//                    this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME,
//                            pos.x + offsetX,
//                            pos.y + random.nextDouble() * 1.2,
//                            pos.z + offsetZ,
//                            (random.nextDouble() - 0.5) * 0.5,
//                            0.2,
//                            (random.nextDouble() - 0.5) * 0.5);
//                }
//            }
//        }
//    }
//
//    // 注意：移除 @Override，因为父类方法可能是 private 或不存在
//    private void executeMightyBlast() {
//        Vec3 position = this.position();
//
//        if (!this.level().isClientSide) {
//            float multiplier = getDamageMultiplier();
//            float baseDamage = this.getAttackDamage() * multiplier;
//            float explosionRadius = isSuperMode ? 6.0F : 4.0F;
//
//            // 执行爆炸
//            executeSuperExplosion(position, baseDamage, explosionRadius);
//
//            // 播放音效
//            playEnhancedSounds();
//
//            // 生成粒子特效
//            spawnSuperParticles(position);
//        }
//    }
//
//    // 覆盖父类的 onHit 方法
//    @Override
//    protected void onHit(net.minecraft.world.phys.HitResult result) {
//        if (!this.level().isClientSide) {
//            executeMightyBlast();
//            this.discard();
//        }
//    }
//
//    private void executeSuperExplosion(Vec3 position, float damage, float radius) {
//        // 主爆炸
//        this.level().explode(
//                this,
//                this.damageSources().explosion(this, this.getOwner()),
//                null,
//                position.x, position.y, position.z,
//                radius,
//                false,
//                Level.ExplosionInteraction.NONE
//        );
//
//        // 范围伤害
//        this.level().getEntitiesOfClass(LivingEntity.class,
//                        this.getBoundingBox().inflate(radius + 2.0),
//                        entity -> entity != this.getOwner() && entity.isAlive())
//                .forEach(entity -> {
//                    double distance = entity.distanceToSqr(this);
//                    float damageFactor = 1.0F - (float)Math.min(distance, 36.0) / 36.0F;
//                    float finalDamage = damage * damageFactor;
//
//                    entity.hurt(this.damageSources().explosion(this, this.getOwner()), finalDamage);
//
//                    Vec3 knockback = entity.position().subtract(position).normalize();
//                    double knockbackPower = 2.0 + (chargeLevel / 50.0);
//                    entity.setDeltaMovement(
//                            entity.getDeltaMovement().x + knockback.x * knockbackPower,
//                            0.5 + (chargeLevel / 100.0),
//                            entity.getDeltaMovement().z + knockback.z * knockbackPower
//                    );
//                    entity.hurtMarked = true;
//
//                    // Minecraft 1.21.1 中使用 setRemainingFireTicks 代替 setSecondsOnFire
//                    if (isSuperMode) {
//                        entity.setRemainingFireTicks(100);  // 5秒 = 100 ticks
//                    }
//                });
//    }
//
//    private void playEnhancedSounds() {
//        if (this.getOwner() instanceof Player player) {
//            if (isSuperMode) {
//                // 直接使用 ResourceLocation 创建 SoundEvent
//                RiderSounds.playSound(level(), player,
//                        net.minecraft.sounds.SoundEvent.createVariableRangeEvent(
//                                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("kamenriderweaponcraft", "ultimate_time_break")
//                        ));
//            } else if (chargeLevel >= MAX_CHARGE) {
//                RiderSounds.playSound(level(), player,
//                        net.minecraft.sounds.SoundEvent.createVariableRangeEvent(
//                                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("kamenriderweaponcraft", "finish_time")
//                        ));
//            } else {
//                RiderSounds.playSound(level(), player, SoundEvents.GENERIC_EXPLODE.value());
//            }
//        }
//    }
//
//    private void spawnSuperParticles(Vec3 position) {
//        if (!(this.level() instanceof ServerLevel serverLevel)) return;
//
//        int particleCount = isSuperMode ? 200 : 100;
//
//        for (int i = 0; i < particleCount; i++) {
//            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
//                    position.x + (random.nextDouble() - 0.5) * 5,
//                    position.y + random.nextDouble() * 4,
//                    position.z + (random.nextDouble() - 0.5) * 5,
//                    1, 0, 0, 0, 0);
//        }
//    }
//
//    // 静态工厂方法
//    public static void trySpawnEnhancedEffect(Level level, LivingEntity shooter, Vec3 direction, float damage) {
//        if (!level.isClientSide) {
//            EnhancedKuugaRiderEntity entity = new EnhancedKuugaRiderEntity(
//                    level,
//                    shooter.getX(),
//                    shooter.getY() + shooter.getEyeHeight(),
//                    shooter.getZ(),
//                    direction.x * 2.5,
//                    direction.y * 2.5,
//                    direction.z * 2.5
//            );
//
//            entity.setOwner(shooter);
//            entity.setAttackDamage(damage);
//
//            level.addFreshEntity(entity);
//        }
//    }
//}