package com.xiaoshi2022.riderwatchcraft.rider.kuuga;

import com.xiaoshi2022.kamenriderweaponcraft.rider.effect.ExternalRiderEffectProvider;
import com.xiaoshi2022.kamenriderweaponcraft.rider.heisei.kuuga.KuugaRiderEntity;
import com.xiaoshi2022.kamenriderweaponcraft.rider.sound.RiderSounds;
import com.xiaoshi2022.riderwatchcraft.RiderWatchCraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class KuugaCoreEffectProvider implements ExternalRiderEffectProvider {

    @Override
    public String getExternalRiderId() {
        return "kuuga_mighty";
    }

    @Override
    public String getExternalRiderName() {
        return "Kuuga Mighty";
    }

    @Override
    public float getAttackDamage() {
        return 45.0f;
    }

    @Override
    public float getEffectRange() {
        return 15.0f;
    }

    @Override
    public double getEnergyCost() {
        return 20.0;
    }

    @Override
    public String getActivationSoundName() {
        return "kamenriderweaponcraft:name_kuuga";
    }

    @Override
    public void executeSkill(Level level, LivingEntity shooter, Vec3 direction) {
        // 客户端：播放特效和音效
        if (level.isClientSide) {
            if (shooter instanceof Player player) {
                // 播放空我音效
                RiderSounds.playSound(level, player, RiderSounds.NAME_KUUGA);

                // 添加技能准备特效 - 金色光环
                for (int i = 0; i < 36; i++) {
                    double angle = 2 * Math.PI * i / 36;
                    double offsetX = Math.cos(angle) * 1.5;
                    double offsetZ = Math.sin(angle) * 1.5;
                    level.addParticle(ParticleTypes.GLOW,
                            shooter.getX() + offsetX,
                            shooter.getY() + 0.5,
                            shooter.getZ() + offsetZ,
                            0, 0.1, 0);
                }
            }
            return;
        }

        // 服务端：调用前置模组已有的空我骑士踢效果
        KuugaRiderEntity.trySpawnEffect(level, shooter, direction, getAttackDamage());

        // 日志记录
        RiderWatchCraft.LOGGER.info("Kuuga Mighty skill executed by: {}", shooter.getName().getString());
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