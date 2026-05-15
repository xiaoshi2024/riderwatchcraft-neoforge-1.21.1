package com.xiaoshi2022.riderwatchcraft.registry;

import com.xiaoshi2022.riderwatchcraft.RiderWatchCraft;
import com.xiaoshi2022.riderwatchcraft.rider.zzz.entity.Capsem.CapsemEntity;
import com.xiaoshi2022.riderwatchcraft.rider.zzz.entity.skill.ImpactEarthquakeEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EntityRegister {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, RiderWatchCraft.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<CapsemEntity>> CAPSEM =
            ENTITY_TYPES.register("capsem",
                    () -> EntityType.Builder.<CapsemEntity>of(
                                    CapsemEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(10)
                            .updateInterval(20)
                            .build("capsem"));

    public static final DeferredHolder<EntityType<?>, EntityType<ImpactEarthquakeEntity>> IMPACT_EARTHQUAKE =
            ENTITY_TYPES.register("impact_earthquake",
                    () -> EntityType.Builder.<ImpactEarthquakeEntity>of(
                                    ImpactEarthquakeEntity::new, MobCategory.MISC)
                            .sized(0.1F, 0.1F)
                            .clientTrackingRange(20)
                            .updateInterval(1)
                            .build("impact_earthquake"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}