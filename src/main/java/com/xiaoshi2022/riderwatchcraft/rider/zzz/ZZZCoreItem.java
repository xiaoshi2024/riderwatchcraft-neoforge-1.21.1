package com.xiaoshi2022.riderwatchcraft.rider.zzz;

import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ZZZCoreItem extends Item implements GeoItem {

    public static final String CORE_ID = "zzz_dream";
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ZZZCoreItem() {
        super(new Properties()
                .durability(500)
                .fireResistant()
        );
    }

    public static String getCoreId() {
        return CORE_ID;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private ZZZCoreItemRenderer renderer;

            @Override
            public GeoItemRenderer<ZZZCoreItem> getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new ZZZCoreItemRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}