package com.xiaoshi2022.riderwatchcraft.rider.kuuga;

import com.xiaoshi2022.kamenriderweaponcraft.rider.core.CoreInteractionHandler;
import com.xiaoshi2022.kamenriderweaponcraft.rider.core.CoreSlotManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

@EventBusSubscriber(modid = "riderwatchcraft")
public class KuugaRegistry {

    @SubscribeEvent
    public static void onInit(FMLLoadCompleteEvent event) {
        CoreInteractionHandler.registerCoreItemFactory(coreId -> {
            if (KuugaCoreItem.CORE_ID.equals(coreId)) {
                return new ItemStack(BuiltInRegistries.ITEM.get(
                        ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "kuuga_mighty_core")
                ));
            }
            return ItemStack.EMPTY;
        });

        CoreInteractionHandler.registerCoreItemById("riderwatchcraft:kuuga_mighty_core", KuugaCoreItem.CORE_ID);

        CoreSlotManager.registerExternalCore(
                KuugaCoreItem.CORE_ID,
                "Kuuga Mighty Core",
                "riderwatchcraft",
                ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "geo/item/heiseisword_kuuga.geo.json"),
                () -> ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "animations/item/heiseisword_kuuga.animation.json"),
                new KuugaCoreEffectProvider(),
                500
        );
    }
}