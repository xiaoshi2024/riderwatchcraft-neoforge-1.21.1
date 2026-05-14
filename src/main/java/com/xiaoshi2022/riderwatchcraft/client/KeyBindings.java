package com.xiaoshi2022.riderwatchcraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "riderwatchcraft", value = Dist.CLIENT)
public class KeyBindings {

    public static final String KEY_CATEGORY = "key.category.riderwatchcraft";
    public static final String KEY_CORE_OPERATION = "key.riderwatchcraft.core_operation";

    public static KeyMapping CORE_OPERATION_KEY = new KeyMapping(
            KEY_CORE_OPERATION,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            KEY_CATEGORY
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(CORE_OPERATION_KEY);
    }
}