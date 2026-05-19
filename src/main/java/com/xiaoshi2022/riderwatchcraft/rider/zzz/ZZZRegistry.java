package com.xiaoshi2022.riderwatchcraft.rider.zzz;

import com.xiaoshi2022.kamenriderweaponcraft.rider.core.CoreInteractionHandler;
import com.xiaoshi2022.kamenriderweaponcraft.rider.core.CoreSlotManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

@EventBusSubscriber(modid = "riderwatchcraft")
public class ZZZRegistry {

    @SubscribeEvent
    public static void onInit(FMLLoadCompleteEvent event) {
        CoreInteractionHandler.registerCoreItemFactory(coreId -> {
            if ("zzz_dream".equals(coreId)) {
                return new ItemStack(BuiltInRegistries.ITEM.get(
                        ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "zzz_dream_core")
                ));
            }
            return ItemStack.EMPTY;
        });

        CoreInteractionHandler.registerCoreItemById("riderwatchcraft:zzz_dream_core", "zzz_dream");

        CoreSlotManager.registerExternalCore(
                ZZZCoreItem.CORE_ID,
                "ZZZ Dream Core",
                "riderwatchcraft",
                ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "geo/item/heiseisword_zzz.geo.json"),
                () -> ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "animations/item/heiseisword_zzz.animation.json"),
                new ZZZCoreEffectProvider(),
                500
        );
    }
}