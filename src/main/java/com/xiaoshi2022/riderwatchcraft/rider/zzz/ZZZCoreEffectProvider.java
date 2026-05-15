package com.xiaoshi2022.riderwatchcraft.rider.zzz;

import com.xiaoshi2022.kamenriderweaponcraft.rider.effect.ExternalRiderEffectProvider;
import com.xiaoshi2022.riderwatchcraft.rider.zzz.entity.Capsem.CapsemEntity;
import com.xiaoshi2022.riderwatchcraft.rider.zzz.entity.Capsem.CapsemType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ZZZCoreEffectProvider implements ExternalRiderEffectProvider {

    private static final CapsemType[] ALL_CAPSEM_TYPES = CapsemType.values();

    @Override
    public String getExternalRiderId() {
        return "zzz_dream";
    }

    @Override
    public String getExternalRiderName() {
        return "ZZZ Dream";
    }

    @Override
    public float getAttackDamage() {
        return 48.0f;
    }

    @Override
    public float getEffectRange() {
        return 15.0f;
    }

    @Override
    public double getEnergyCost() {
        return 25.0;
    }

    @Override
    public String getActivationSoundName() {
        return "zzz_dream_activate";
    }

    @Override
    public void executeSkill(Level level, LivingEntity shooter, Vec3 direction) {
        // 每次只生成一个胶囊
        spawnSingleCapsule(level, shooter, direction);
    }

    private void spawnSingleCapsule(Level level, LivingEntity shooter, Vec3 direction) {
        // 只生成一个胶囊，类型随机但不会重复（因为只有一个）
        CapsemType type = ALL_CAPSEM_TYPES[level.random.nextInt(ALL_CAPSEM_TYPES.length)];

        // 添加微小的随机偏移，让胶囊稍微散开（可选）
        Vec3 offsetDirection = direction.add(
                (level.random.nextDouble() - 0.5) * 0.3,
                (level.random.nextDouble() - 0.5) * 0.2,
                (level.random.nextDouble() - 0.5) * 0.3
        ).normalize();

        CapsemEntity.trySpawnEffect(level, shooter, offsetDirection, type);
    }

    @Nullable
    @Override
    public ResourceLocation getExternalModelLocation() {
        return ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "geo/item/heiseisword_zzz.geo.json");
    }

    @Nullable
    @Override
    public Supplier<ResourceLocation> getExternalAnimController() {
        return () -> ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "animations/item/heiseisword_zzz.animation.json");
    }

    @Override
    public boolean isExternal() {
        return true;
    }

    @Override
    public boolean supportsScrambleMode() {
        return false;
    }
}