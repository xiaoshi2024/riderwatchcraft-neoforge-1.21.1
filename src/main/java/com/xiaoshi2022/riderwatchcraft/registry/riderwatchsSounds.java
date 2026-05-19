package com.xiaoshi2022.riderwatchcraft.registry;

import com.xiaoshi2022.riderwatchcraft.RiderWatchCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class riderwatchsSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, RiderWatchCraft.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> KUUGA_RIDER_WATCH = SOUND_EVENTS.register("kuuga_rider_watch",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(RiderWatchCraft.MODID, "kuuga_rider_watch")));

    public static final DeferredHolder<SoundEvent, SoundEvent> ZZZ_RIDER_WATCH = SOUND_EVENTS.register("zzz_rider_watch",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(RiderWatchCraft.MODID, "zzz_rider_watch")));

    public static final DeferredHolder<SoundEvent, SoundEvent> DECADE_RIDER_WATCH = SOUND_EVENTS.register("decade_rider_watch",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(RiderWatchCraft.MODID, "decade_rider_watch")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}