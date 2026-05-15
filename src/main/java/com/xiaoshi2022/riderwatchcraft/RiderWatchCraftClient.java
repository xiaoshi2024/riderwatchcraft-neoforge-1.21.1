package com.xiaoshi2022.riderwatchcraft;

import com.xiaoshi2022.riderwatchcraft.registry.EntityRegister;
import com.xiaoshi2022.riderwatchcraft.rider.zzz.entity.Capsem.CapsemRenderer;
import com.xiaoshi2022.riderwatchcraft.rider.zzz.entity.skill.ImpactEarthquakeRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;  // ← 添加这个导入
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = RiderWatchCraft.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = RiderWatchCraft.MODID, value = Dist.CLIENT)  // ← 关键！添加这行
public class RiderWatchCraftClient {

    public RiderWatchCraftClient() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {  // ← 改为 static
        event.registerEntityRenderer(EntityRegister.CAPSEM.get(), CapsemRenderer::new);
        event.registerEntityRenderer(EntityRegister.IMPACT_EARTHQUAKE.get(), ImpactEarthquakeRenderer::new);
    }
}