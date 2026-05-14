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
        final PayloadRegistrar registrar = event.registrar(RiderWatchCraft.MODID);

        registrar.playToServer(
                CoreOperationPacket.TYPE,
                CoreOperationPacket.STREAM_CODEC,
                CoreOperationPacket::handleServer
        );
    }
}