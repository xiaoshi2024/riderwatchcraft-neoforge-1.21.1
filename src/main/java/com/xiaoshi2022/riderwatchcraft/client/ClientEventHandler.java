package com.xiaoshi2022.riderwatchcraft.client;

import com.xiaoshi2022.kamenriderweaponcraft.Item.custom.Heiseisword;
import com.xiaoshi2022.kamenriderweaponcraft.rider.core.CoreInteractionHandler;
import com.xiaoshi2022.kamenriderweaponcraft.rider.core.CoreSlotManager;
import com.xiaoshi2022.riderwatchcraft.RiderWatchCraft;
import com.xiaoshi2022.riderwatchcraft.network.CoreOperationPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = RiderWatchCraft.MODID, value = Dist.CLIENT)
public class ClientEventHandler {

    private static boolean wasKeyPressed = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        boolean isPressed = KeyBindings.CORE_OPERATION_KEY.isDown();

        if (isPressed && !wasKeyPressed) {
            handleCoreOperation(mc.player);
        }

        wasKeyPressed = isPressed;
    }

    private static void handleCoreOperation(Player player) {
        ItemStack mainhand = player.getMainHandItem();

        if (!Heiseisword.isHeiseisword(mainhand)) {
            return;
        }

        ItemStack offhand = player.getOffhandItem();

        // 副手有核心 → 安装
        if (CoreInteractionHandler.isCoreItem(offhand)) {
            String coreId = CoreInteractionHandler.getCoreIdFromItem(offhand);
            if (coreId != null && CoreSlotManager.getCoreInfo(coreId) != null) {
                PacketDistributor.sendToServer(new CoreOperationPacket("install", coreId));
                // 移除提示消息
            }
        }
        // 有核心 → 拆下
        else if (CoreSlotManager.hasAttachedCore(mainhand)) {
            String coreId = CoreSlotManager.getAttachedCoreId(mainhand);
            PacketDistributor.sendToServer(new CoreOperationPacket("detach", coreId));
            // 移除提示消息
        }
    }
}