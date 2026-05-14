package com.xiaoshi2022.riderwatchcraft.rider.zzz;

import com.xiaoshi2022.kamenriderweaponcraft.rider.effect.ExternalRiderEffectProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ZZZCoreEffectProvider implements ExternalRiderEffectProvider {

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
        for (int i = 0; i < 20; i++) {
            double offsetX = (Math.random() - 0.5) * 0.5;
            double offsetY = (Math.random() - 0.5) * 0.5;
            double offsetZ = (Math.random() - 0.5) * 0.5;

            level.addParticle(ParticleTypes.SOUL,
                    shooter.getX() + offsetX,
                    shooter.getY() + 1.5 + offsetY,
                    shooter.getZ() + offsetZ,
                    direction.x * 0.3 + (Math.random() - 0.5) * 0.2,
                    direction.y * 0.3 + (Math.random() - 0.5) * 0.2,
                    direction.z * 0.3 + (Math.random() - 0.5) * 0.2
            );
        }

        level.addParticle(ParticleTypes.WITCH,
                shooter.getX() + direction.x * 0.5,
                shooter.getY() + 1.5 + direction.y * 0.5,
                shooter.getZ() + direction.z * 0.5,
                0, 0, 0
        );
    }

    @Nullable
    @Override
    public ResourceLocation getExternalModelLocation() {
        return ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "geo/item/zzz_core.geo.json");
    }

    @Nullable
    @Override
    public Supplier<ResourceLocation> getExternalAnimController() {
        return () -> ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "controller/zzz_core.json");
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