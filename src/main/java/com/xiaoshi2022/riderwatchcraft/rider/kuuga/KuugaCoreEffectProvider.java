package com.xiaoshi2022.riderwatchcraft.rider.kuuga;

import com.xiaoshi2022.kamenriderweaponcraft.rider.effect.ExternalRiderEffectProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class KuugaCoreEffectProvider implements ExternalRiderEffectProvider {

    @Override
    public String getExternalRiderId() {
        return KuugaCoreItem.CORE_ID;
    }

    @Override
    public String getExternalRiderName() {
        return "Kuuga Mighty";
    }

    @Override
    public float getAttackDamage() {
        return 35.0f;
    }

    @Override
    public float getEffectRange() {
        return 6.0f;
    }

    @Override
    public double getEnergyCost() {
        return 20.0;
    }

    @Override
    public String getActivationSoundName() {
        return "entity.player.attack.strong";
    }

    @Override
    public void executeSkill(Level level, LivingEntity shooter, Vec3 direction) {
        for (int i = 0; i < 3; i++) {
            level.addParticle(ParticleTypes.GLOW,
                    shooter.getX() + direction.x() * i,
                    shooter.getY() + 1.0,
                    shooter.getZ() + direction.z() * i,
                    direction.x(),
                    0.2,
                    direction.z());
        }
    }

    @Override
    public ResourceLocation getExternalModelLocation() {
        return ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "geo/item/heiseisword_kuuga.geo.json");
    }

    @Override
    public Supplier<ResourceLocation> getExternalAnimController() {
        return () -> ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "animations/item/heiseisword_kuuga.animation.json");
    }

    @Override
    public boolean isExternal() {
        return true;
    }

    @Override
    public boolean supportsScrambleMode() {
        return true;
    }

    @Override
    public int getScrambleModeMaxLayers() {
        return 4;
    }
}