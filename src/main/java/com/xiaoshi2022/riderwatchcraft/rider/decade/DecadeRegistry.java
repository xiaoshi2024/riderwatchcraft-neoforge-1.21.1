package com.xiaoshi2022.riderwatchcraft.rider.decade;

import com.xiaoshi2022.kamenriderweaponcraft.rider.core.CoreInteractionHandler;
import com.xiaoshi2022.kamenriderweaponcraft.rider.core.CoreSlotManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

@EventBusSubscriber(modid = "riderwatchcraft")
public class DecadeRegistry {

    @SubscribeEvent
    public static void onInit(FMLLoadCompleteEvent event) {
        CoreInteractionHandler.registerCoreItemFactory(coreId -> {
            if (DecadeCoreItem.CORE_ID.equals(coreId)) {
                return new ItemStack(BuiltInRegistries.ITEM.get(
                        ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "decade_form_core")
                ));
            }
            return ItemStack.EMPTY;
        });

        CoreInteractionHandler.registerCoreItemById("riderwatchcraft:decade_form_core", DecadeCoreItem.CORE_ID);

        CoreSlotManager.registerExternalCore(
                DecadeCoreItem.CORE_ID,
                "Decade Core",
                "riderwatchcraft",
                ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "geo/item/heiseisword_decade.geo.json"),
                () -> ResourceLocation.fromNamespaceAndPath("riderwatchcraft", "animations/item/heiseisword_decade.animation.json"),
                new DecadeCoreEffectProvider(),
                500
        );
    }
}