package com.xiaoshi2022.riderwatchcraft.rider.decade;

import com.xiaoshi2022.kamenriderweaponcraft.rider.effect.ExternalRiderEffectProvider;
import com.xiaoshi2022.kamenriderweaponcraft.rider.heisei.decade.DecadeRiderEntity;
import com.xiaoshi2022.riderwatchcraft.RiderWatchCraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class DecadeCoreEffectProvider implements ExternalRiderEffectProvider {

    @Override
    public String getExternalRiderId() {
        return "decade_form";
    }

    @Override
    public String getExternalRiderName() {
        return "Decade";
    }

    @Override
    public float getAttackDamage() {
        return 50.0f;
    }

    @Override
    public float getEffectRange() {
        return 20.0f;
    }

    @Override
    public double getEnergyCost() {
        return 30.0;
    }

    @Override
    public String getActivationSoundName() {
        return "riderwatchcraft:name_decade";
    }

    @Override
    public void executeSkill(Level level, LivingEntity shooter, Vec3 direction) {
        if (level.isClientSide) {
            if (shooter instanceof Player player) {
                for (int i = 0; i < 36; i++) {
                    double angle = 2 * Math.PI * i / 36;
                    double offsetX = Math.cos(angle) * 1.5;
                    double offsetZ = Math.sin(angle) * 1.5;
                    level.addParticle(ParticleTypes.END_ROD,
                            shooter.getX() + offsetX,
                            shooter.getY() + 0.5,
                            shooter.getZ() + offsetZ,
                            0, 0.1, 0);
                }
            }
            return;
        }

        // 服务端：调用前置模组已有的Decade骑士踢效果
        DecadeRiderEntity.trySpawnEffect(level, shooter, direction, getAttackDamage());

        RiderWatchCraft.LOGGER.info("Decade skill executed by: {}", shooter.getName().getString());
    }

    @Override
    public ResourceLocation getExternalModelLocation() {
        return ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "geo/item/heiseisword_decade.geo.json");
    }

    @Override
    public Supplier<ResourceLocation> getExternalAnimController() {
        return () -> ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "animations/item/heiseisword_decade.animation.json");
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