package com.xiaoshi2022.riderwatchcraft.network;

import com.xiaoshi2022.riderwatchcraft.RiderWatchCraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = RiderWatchCraft.MODID)
public class NetworkHandler {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(RiderWatchCraft.MODID)
                .versioned("1.0.0");

        // 服务端 -> 客户端：地震方块翻动数据包
        registrar.playToClient(
                ImpactEarthquakeBlockPacket.TYPE,
                ImpactEarthquakeBlockPacket.STREAM_CODEC,
                ImpactEarthquakeBlockPacket::handleClient
        );

        // 客户端 -> 服务端：核心操作数据包
        registrar.playToServer(
                CoreOperationPacket.TYPE,
                CoreOperationPacket.STREAM_CODEC,
                CoreOperationPacket::handleServer
        );
    }
}