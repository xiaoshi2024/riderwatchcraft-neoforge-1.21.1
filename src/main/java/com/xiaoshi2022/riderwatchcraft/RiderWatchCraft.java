package com.xiaoshi2022.riderwatchcraft;

import com.mojang.logging.LogUtils;
import com.xiaoshi2022.kamenriderweaponcraft.Item.custom.Heiseisword;
import com.xiaoshi2022.kamenriderweaponcraft.rider.core.CoreSlotManager;
import com.xiaoshi2022.kamenriderweaponcraft.rider.energy.HeiseiswordEnergyManager;
import com.xiaoshi2022.riderwatchcraft.network.NetworkHandler;
import com.xiaoshi2022.riderwatchcraft.rider.kuuga.KuugaCoreItem;
import com.xiaoshi2022.riderwatchcraft.rider.kuuga.KuugaRegistry;
import com.xiaoshi2022.riderwatchcraft.rider.zzz.ZZZCoreItem;
import com.xiaoshi2022.riderwatchcraft.rider.zzz.ZZZRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(RiderWatchCraft.MODID)
public class RiderWatchCraft {
    public static final String MODID = "riderwatchcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<ZZZCoreItem> ZZZ_DREAM_CORE = ITEMS.register("zzz_dream_core", ZZZCoreItem::new);
    public static final DeferredItem<KuugaCoreItem> KUUGA_MIGHTY_CORE = ITEMS.register("kuuga_mighty_core", KuugaCoreItem::new);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RIDERS_TAB = CREATIVE_MODE_TABS.register("riders_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.riderwatchcraft.riders"))
                    .withTabsBefore(CreativeModeTabs.BUILDING_BLOCKS)
                    .icon(() -> KUUGA_MIGHTY_CORE.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ZZZ_DREAM_CORE.get());
                        output.accept(KUUGA_MIGHTY_CORE.get());
                    }).build()
    );

    public RiderWatchCraft(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.register(ZZZRegistry.class);
        modEventBus.register(KuugaRegistry.class);
        modEventBus.register(NetworkHandler.class);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("RiderWatchCraft initialized");
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack mainhand = player.getMainHandItem();

        if (!Heiseisword.isHeiseisword(mainhand)) {
            return;
        }

        boolean hasExternalCore = CoreSlotManager.hasAttachedCore(mainhand);

        // 普通右键：有外部核心时触发技能
        if (hasExternalCore) {
            String coreId = CoreSlotManager.getAttachedCoreId(mainhand);
            CoreSlotManager.CoreSlotInfo coreInfo = CoreSlotManager.getCoreInfo(coreId);

            if (coreInfo != null && coreInfo.effectProvider != null) {
                var effectProvider = coreInfo.effectProvider;
                double energyCost = effectProvider.getEnergyCost();
                double currentEnergy = HeiseiswordEnergyManager.getCurrentEnergy(player);

                if (currentEnergy >= energyCost) {
                    HeiseiswordEnergyManager.consumeEnergy(player, energyCost);
                    effectProvider.executeSkill(player.level(), player, player.getLookAngle());
                    // 移除技能释放提示消息
                } else if (!player.level().isClientSide) {
                    // 可选：保留能量不足提示（或者也移除）
                    // player.sendSystemMessage(...);
                }
                event.setCanceled(true);
                return;
            }
        }

        // 没有外部核心时，让原有机制正常工作（不取消事件）
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack mainhand = player.getMainHandItem();

        if (!Heiseisword.isHeiseisword(mainhand)) {
            return;
        }

        boolean hasExternalCore = CoreSlotManager.hasAttachedCore(mainhand);

        if (hasExternalCore) {
            String coreId = CoreSlotManager.getAttachedCoreId(mainhand);
            CoreSlotManager.CoreSlotInfo coreInfo = CoreSlotManager.getCoreInfo(coreId);

            if (coreInfo != null && coreInfo.effectProvider != null) {
                var effectProvider = coreInfo.effectProvider;
                double energyCost = effectProvider.getEnergyCost();
                double currentEnergy = HeiseiswordEnergyManager.getCurrentEnergy(player);

                if (currentEnergy >= energyCost) {
                    HeiseiswordEnergyManager.consumeEnergy(player, energyCost);
                    effectProvider.executeSkill(player.level(), player, player.getLookAngle());

                    if (!player.level().isClientSide) {
                        player.sendSystemMessage(Component.literal("§a技能释放！"));
                    }
                }
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server starting");
    }
}