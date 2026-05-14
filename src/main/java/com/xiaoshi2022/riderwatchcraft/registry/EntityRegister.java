package com.xiaoshi2022.riderwatchcraft.registry;

import com.xiaoshi2022.riderwatchcraft.RiderWatchCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityRegister {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, RiderWatchCraft.MODID);

//    public static final DeferredHolder<EntityType<?>, EntityType<EnhancedKuugaRiderEntity>> ENHANCED_KUUGA_RIDER =
//            ENTITY_TYPES.register("enhanced_kuuga_rider",
//                    () -> EntityType.Builder.<EnhancedKuugaRiderEntity>of(
//                                    EnhancedKuugaRiderEntity::new, MobCategory.MISC)
//                            .sized(1.0F, 2.0F)
//                            .clientTrackingRange(16)
//                            .updateInterval(1)
//                            .build("enhanced_kuuga_rider"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}