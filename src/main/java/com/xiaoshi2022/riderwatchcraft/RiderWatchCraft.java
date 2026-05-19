package com.xiaoshi2022.riderwatchcraft;

import com.mojang.logging.LogUtils;
import com.xiaoshi2022.kamenriderweaponcraft.Item.custom.Heiseisword;
import com.xiaoshi2022.kamenriderweaponcraft.rider.core.CoreSlotManager;
import com.xiaoshi2022.kamenriderweaponcraft.rider.energy.HeiseiswordEnergyManager;
import com.xiaoshi2022.riderwatchcraft.network.NetworkHandler;
import com.xiaoshi2022.riderwatchcraft.registry.EntityRegister;
import com.xiaoshi2022.riderwatchcraft.registry.riderwatchsSounds;
import com.xiaoshi2022.riderwatchcraft.rider.decade.DecadeCoreItem;
import com.xiaoshi2022.riderwatchcraft.rider.kuuga.KuugaCoreItem;
import com.xiaoshi2022.riderwatchcraft.rider.kuuga.KuugaRegistry;
import com.xiaoshi2022.riderwatchcraft.rider.zzz.ZZZCoreItem;
import com.xiaoshi2022.riderwatchcraft.rider.zzz.ZZZRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
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

import java.lang.reflect.Method;

@Mod(RiderWatchCraft.MODID)
public class RiderWatchCraft {
    public static final String MODID = "riderwatchcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<ZZZCoreItem> ZZZ_DREAM_CORE = ITEMS.register("zzz_dream_core", ZZZCoreItem::new);
    public static final DeferredItem<KuugaCoreItem> KUUGA_MIGHTY_CORE = ITEMS.register("kuuga_mighty_core", KuugaCoreItem::new);
    public static final DeferredItem<DecadeCoreItem> DECADE_FORM_CORE = ITEMS.register("decade_form_core", DecadeCoreItem::new);


    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RIDERS_TAB = CREATIVE_MODE_TABS.register("riders_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.riderwatchcraft.riders"))
                    .withTabsBefore(CreativeModeTabs.BUILDING_BLOCKS)
                    .icon(() -> KUUGA_MIGHTY_CORE.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ZZZ_DREAM_CORE.get());
                        output.accept(KUUGA_MIGHTY_CORE.get());
                        output.accept(DECADE_FORM_CORE.get());
                    }).build()
    );

    // 存储上一次的蓄力释放标记，用于判断是蓄力释放还是普通右键
    private static final java.util.Map<Player, Boolean> lastWasSneaking = new java.util.HashMap<>();

    public RiderWatchCraft(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        EntityRegister.register(modEventBus);
        riderwatchsSounds.register(modEventBus);

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

        // 获取 Heiseisword 实例以便检查状态
        Heiseisword heiseisword = (Heiseisword) mainhand.getItem();

        // 通过反射获取 isFinishTimeMode 状态（因为不能直接修改前置mod）
        boolean isFinishTimeMode = getFinishTimeMode(heiseisword, mainhand);

        // 记录当前是否按着 Shift
        boolean isSneaking = player.isShiftKeyDown();
        lastWasSneaking.put(player, isSneaking);

        // ========== 情况1: Shift+右键（进入/退出必杀时刻模式）==========
        // 让前置模组正常处理 Shift+右键的逻辑，我们不干预
        if (isSneaking) {
            // 不取消事件，让 Heiseisword 正常处理 Shift+右键
            return;
        }

        // ========== 情况2: 普通右键（非Shift）==========
        if (hasExternalCore) {
            String coreId = CoreSlotManager.getAttachedCoreId(mainhand);
            CoreSlotManager.CoreSlotInfo coreInfo = CoreSlotManager.getCoreInfo(coreId);

            if (coreInfo != null && coreInfo.effectProvider != null) {
                var effectProvider = coreInfo.effectProvider;
                double energyCost = effectProvider.getEnergyCost();
                double currentEnergy = HeiseiswordEnergyManager.getCurrentEnergy(player);

                if (currentEnergy >= energyCost) {
                    HeiseiswordEnergyManager.consumeEnergy(player, energyCost);

                    if (isFinishTimeMode) {
                        // 必杀时刻模式：只执行外部表盘特效（骑士特效由前置模组的蓄力释放处理）
                        // 注意：这里只做特效，不重复扣能量
                        effectProvider.executeSkill(player.level(), player, player.getLookAngle());
                        // 让前置模组继续处理骑士技能（会叠加）
                    } else {
                        // 普通模式：只执行外部表盘特效，阻止前置模组的骑士技能
                        effectProvider.executeSkill(player.level(), player, player.getLookAngle());
                        mainhand.hurtAndBreak(1, player, LivingEntity.getSlotForHand(InteractionHand.MAIN_HAND));
                        event.setCanceled(true);  // 取消事件，阻止前置模组释放骑士技能
                        return;
                    }
                } else if (!isFinishTimeMode) {
                    // 能量不足时阻止技能释放
                    event.setCanceled(true);
                    return;
                }
            }
        }

        // 没有外部表盘或必杀时刻模式：让前置模组正常处理
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack mainhand = player.getMainHandItem();

        if (!Heiseisword.isHeiseisword(mainhand)) {
            return;
        }

        boolean hasExternalCore = CoreSlotManager.hasAttachedCore(mainhand);

        // 获取 Heiseisword 实例
        Heiseisword heiseisword = (Heiseisword) mainhand.getItem();
        boolean isFinishTimeMode = getFinishTimeMode(heiseisword, mainhand);

        boolean isSneaking = player.isShiftKeyDown();

        // Shift+右键：不干预
        if (isSneaking) {
            return;
        }

        // 普通右键
        if (hasExternalCore) {
            String coreId = CoreSlotManager.getAttachedCoreId(mainhand);
            CoreSlotManager.CoreSlotInfo coreInfo = CoreSlotManager.getCoreInfo(coreId);

            if (coreInfo != null && coreInfo.effectProvider != null) {
                var effectProvider = coreInfo.effectProvider;
                double energyCost = effectProvider.getEnergyCost();
                double currentEnergy = HeiseiswordEnergyManager.getCurrentEnergy(player);

                if (currentEnergy >= energyCost) {
                    HeiseiswordEnergyManager.consumeEnergy(player, energyCost);

                    if (isFinishTimeMode) {
                        // 必杀时刻模式：执行外部表盘特效（会与骑士技能叠加）
                        effectProvider.executeSkill(player.level(), player, player.getLookAngle());
                        // 不取消事件，让前置模组也执行
                    } else {
                        // 普通模式：只执行外部表盘特效
                        effectProvider.executeSkill(player.level(), player, player.getLookAngle());
                        mainhand.hurtAndBreak(1, player, LivingEntity.getSlotForHand(InteractionHand.MAIN_HAND));
                        event.setCanceled(true);
                        return;
                    }
                } else if (!isFinishTimeMode) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    /**
     * 通过反射获取 Heiseisword 的 isFinishTimeMode 状态
     */
    private boolean getFinishTimeMode(Heiseisword heiseisword, ItemStack stack) {
        try {
            Method method = Heiseisword.class.getDeclaredMethod("isFinishTimeMode", ItemStack.class);
            method.setAccessible(true);
            return (boolean) method.invoke(heiseisword, stack);
        } catch (Exception e) {
            // 反射失败，尝试通过其他方式判断
            LOGGER.debug("Failed to get isFinishTimeMode via reflection", e);
            return false;
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server starting");
    }
}