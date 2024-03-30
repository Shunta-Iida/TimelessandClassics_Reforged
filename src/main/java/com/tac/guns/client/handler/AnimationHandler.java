package com.tac.guns.client.handler;

import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.tac.guns.Reference;
import com.tac.guns.client.Keys;
import com.tac.guns.client.render.animation.AA12AnimationController;
import com.tac.guns.client.render.animation.AWPAnimationController;
import com.tac.guns.client.render.animation.Ak47AnimationController;
import com.tac.guns.client.render.animation.COLTPYTHONAnimationController;
import com.tac.guns.client.render.animation.CZ75AnimationController;
import com.tac.guns.client.render.animation.CZ75AutoAnimationController;
import com.tac.guns.client.render.animation.DBShotgunAnimationController;
import com.tac.guns.client.render.animation.Deagle50AnimationController;
import com.tac.guns.client.render.animation.Dp28AnimationController;
import com.tac.guns.client.render.animation.FNFALAnimationController;
import com.tac.guns.client.render.animation.Glock17AnimationController;
import com.tac.guns.client.render.animation.Glock18AnimationController;
import com.tac.guns.client.render.animation.HK416A5AnimationController;
import com.tac.guns.client.render.animation.HK_G3AnimationController;
import com.tac.guns.client.render.animation.HkMp5a5AnimationController;
import com.tac.guns.client.render.animation.M1014AnimationController;
import com.tac.guns.client.render.animation.M16A4AnimationController;
import com.tac.guns.client.render.animation.M1911AnimationController;
import com.tac.guns.client.render.animation.M1A1AnimationController;
import com.tac.guns.client.render.animation.M249AnimationController;
import com.tac.guns.client.render.animation.M24AnimationController;
import com.tac.guns.client.render.animation.M4AnimationController;
import com.tac.guns.client.render.animation.M60AnimationController;
import com.tac.guns.client.render.animation.M82A2AnimationController;
import com.tac.guns.client.render.animation.M870AnimationController;
import com.tac.guns.client.render.animation.M92FSAnimationController;
import com.tac.guns.client.render.animation.MAC10AnimationController;
import com.tac.guns.client.render.animation.MK14AnimationController;
import com.tac.guns.client.render.animation.MK18MOD1AnimationController;
import com.tac.guns.client.render.animation.MK23AnimationController;
import com.tac.guns.client.render.animation.MK47AnimationController;
import com.tac.guns.client.render.animation.MP9AnimationController;
import com.tac.guns.client.render.animation.MRADAnimationController;
import com.tac.guns.client.render.animation.Mp7AnimationController;
import com.tac.guns.client.render.animation.P90AnimationController;
import com.tac.guns.client.render.animation.RPG7AnimationController;
import com.tac.guns.client.render.animation.RPKAnimationController;
import com.tac.guns.client.render.animation.SCAR_HAnimationController;
import com.tac.guns.client.render.animation.SCAR_LAnimationController;
import com.tac.guns.client.render.animation.SCAR_MK20AnimationController;
import com.tac.guns.client.render.animation.SIGMCXAnimationController;
import com.tac.guns.client.render.animation.SKSTacticalAnimationController;
import com.tac.guns.client.render.animation.SPR15AnimationController;
import com.tac.guns.client.render.animation.STI2011AnimationController;
import com.tac.guns.client.render.animation.TEC9AnimationController;
import com.tac.guns.client.render.animation.Timeless50AnimationController;
import com.tac.guns.client.render.animation.TtiG34AnimationController;
import com.tac.guns.client.render.animation.Type191AnimationController;
import com.tac.guns.client.render.animation.Type81AnimationController;
import com.tac.guns.client.render.animation.Type95LAnimationController;
import com.tac.guns.client.render.animation.UDP9AnimationController;
import com.tac.guns.client.render.animation.UZIAnimationController;
import com.tac.guns.client.render.animation.Vector45AnimationController;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.AnimationSoundManager;
import com.tac.guns.client.render.animation.module.Animations;
import com.tac.guns.client.render.animation.module.BoltActionAnimationController;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PumpShotgunAnimationController;
import com.tac.guns.event.GunFireEvent;
import com.tac.guns.event.GunReloadEvent;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.gun.GunItem;
import com.tac.guns.util.GunEnchantmentHelper;
import com.tac.guns.util.GunModifierHelper;
import com.tac.guns.weapon.Gun;

import de.javagl.jgltf.model.animation.AnimationRunner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Mainly controls when the animation should play.
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public enum AnimationHandler {
    INSTANCE;

    public static void preloadAnimations() {
        // TODO: Make automatic or have some sort of check for this
        AA12AnimationController.getInstance();
        Dp28AnimationController.getInstance();
        Glock17AnimationController.getInstance();
        HkMp5a5AnimationController.getInstance();
        HK416A5AnimationController.getInstance();
        M870AnimationController.getInstance();
        Mp7AnimationController.getInstance();
        Type81AnimationController.getInstance();
        Ak47AnimationController.getInstance();
        AWPAnimationController.getInstance();
        M60AnimationController.getInstance();
        M1014AnimationController.getInstance();
        TtiG34AnimationController.getInstance();
        MK18MOD1AnimationController.getInstance();
        M4AnimationController.getInstance();
        STI2011AnimationController.getInstance();
        M1911AnimationController.getInstance();
        MK47AnimationController.getInstance();
        MK14AnimationController.getInstance();
        SCAR_HAnimationController.getInstance();
        SCAR_LAnimationController.getInstance();
        CZ75AnimationController.getInstance();
        CZ75AutoAnimationController.getInstance();
        DBShotgunAnimationController.getInstance();
        FNFALAnimationController.getInstance();
        M16A4AnimationController.getInstance();
        SPR15AnimationController.getInstance();
        Deagle50AnimationController.getInstance();
        Type95LAnimationController.getInstance();
        Type191AnimationController.getInstance();
        MAC10AnimationController.getInstance();
        Vector45AnimationController.getInstance();
        SKSTacticalAnimationController.getInstance();
        M24AnimationController.getInstance();
        M82A2AnimationController.getInstance();
        // TODO: RPK redo due to static animation issue
        RPKAnimationController.getInstance();
        M249AnimationController.getInstance();
        M1A1AnimationController.getInstance();
        Glock18AnimationController.getInstance();
        SIGMCXAnimationController.getInstance();
        M92FSAnimationController.getInstance();
        MP9AnimationController.getInstance();
        MK23AnimationController.getInstance();
        RPG7AnimationController.getInstance();
        UDP9AnimationController.getInstance();
        COLTPYTHONAnimationController.getInstance();
        HK_G3AnimationController.getInstance();
        MRADAnimationController.getInstance();
        P90AnimationController.getInstance();
        SCAR_MK20AnimationController.getInstance();
        TEC9AnimationController.getInstance();
        Timeless50AnimationController.getInstance();
        UZIAnimationController.getInstance();
    }

    public void onGunReload(final boolean reloading, final ItemStack itemStack) {
        final Player player = Minecraft.getInstance().player;
        if (player == null)
            return;
        if (itemStack.getItem() instanceof GunItem) {
            final GunItem gunItem = (GunItem) itemStack.getItem();
            final CompoundTag tag = itemStack.getOrCreateTag();
            final int reloadingAmount =
                    GunModifierHelper.getAmmoCapacity(itemStack, gunItem.getGun())
                            - tag.getInt("AmmoCount");
            if (reloadingAmount <= 0)
                return;
        }
        final GunAnimationController controller =
                GunAnimationController.fromItem(itemStack.getItem());
        if (controller == null)
            return;
        if (!reloading)
            return;
        final float reloadSpeed = GunEnchantmentHelper.getReloadSpeed(itemStack);
        final AnimationMeta reloadEmptyMeta = controller
                .getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
        final AnimationMeta reloadNormalMeta = controller
                .getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL);
        if (Gun.hasAmmo(itemStack)) {
            if (controller.getPreviousAnimation() != null
                    && !controller.getPreviousAnimation().equals(reloadNormalMeta))
                controller.stopAnimation();
            controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_NORMAL,
                    reloadSpeed);
        } else {
            if (controller.getPreviousAnimation() != null
                    && !controller.getPreviousAnimation().equals(reloadEmptyMeta))
                controller.stopAnimation();

            if (GunAnimationController
                    .fromItem(itemStack.getItem()) instanceof PumpShotgunAnimationController) {
                ((PumpShotgunAnimationController) GunAnimationController
                        .fromItem(itemStack.getItem())).setEmpty(true);
            }

            controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_EMPTY,
                    reloadSpeed);
        }
    }

    @SubscribeEvent
    public void onGunFire(final GunFireEvent.Pre event) {
        if (!event.isClient())
            return;
        if (Minecraft.getInstance().player == null)
            return;
        if (!event.getPlayer().getUUID().equals(Minecraft.getInstance().player.getUUID()))
            return;
        final GunAnimationController controller =
                GunAnimationController.fromItem(event.getStack().getItem());
        if (controller == null)
            return;
        if (controller.isAnimationRunning()) {
            final AnimationMeta meta = controller.getPreviousAnimation();
            if (meta == null)
                return;
            if (meta.equals(
                    controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.INSPECT))
                    || meta.equals(controller.getAnimationFromLabel(
                            GunAnimationController.AnimationLabel.INSPECT_EMPTY)))
                controller.stopAnimation();
            else {
                final AnimationRunner runner =
                        Animations.getAnimationRunner(meta.getResourceLocation());
                if (runner == null)
                    return;
                final float current = runner.getAnimationManager().getCurrentTimeS();
                final float max = runner.getAnimationManager().getMaxEndTimeS();
                if (!(meta
                        .equals(controller
                                .getAnimationFromLabel(GunAnimationController.AnimationLabel.PUMP))
                        || meta.equals(controller.getAnimationFromLabel(
                                GunAnimationController.AnimationLabel.PULL_BOLT))))
                    if (max - current <= 0.25f)
                        return;
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPumpShotgunFire(final GunFireEvent.Post event) {
        if (!event.isClient())
            return;
        if (Minecraft.getInstance().player == null)
            return;
        if (!event.getPlayer().getUUID().equals(Minecraft.getInstance().player.getUUID()))
            return;
        final GunAnimationController controller =
                GunAnimationController.fromItem(event.getStack().getItem());
        if (controller instanceof PumpShotgunAnimationController) {
            controller.runAnimation(GunAnimationController.AnimationLabel.PUMP);
        }
    }

    @SubscribeEvent
    public void onBoltActionRifleFire(final GunFireEvent.Post event) {
        if (!event.isClient())
            return;
        if (Minecraft.getInstance().player == null)
            return;
        if (!event.getPlayer().getUUID().equals(Minecraft.getInstance().player.getUUID()))
            return;
        final GunAnimationController controller =
                GunAnimationController.fromItem(event.getStack().getItem());
        if (controller instanceof BoltActionAnimationController) {
            controller.runAnimation(GunAnimationController.AnimationLabel.PULL_BOLT);
        }
    }

    static {
        Keys.INSPECT.addPressCallback(() -> {
            if (!Keys.noConflict(Keys.INSPECT))
                return;

            final Player player = Minecraft.getInstance().player;
            if (player == null)
                return;

            if (AimingHandler.get().getNormalisedAdsProgress() != 0)
                return;

            final ItemStack stack = player.getInventory().getSelected();
            final GunAnimationController controller =
                    GunAnimationController.fromItem(stack.getItem());
            if (controller != null && !controller.isAnimationRunning()) {
                controller.stopAnimation();
                if (Gun.hasAmmo(stack)) {
                    controller.runAnimation(GunAnimationController.AnimationLabel.INSPECT);
                } else {
                    controller.runAnimation(GunAnimationController.AnimationLabel.INSPECT_EMPTY);
                }
            }
        });
    }

    @SubscribeEvent
    public void onPlayerLogout(final PlayerEvent.PlayerLoggedOutEvent event) {
        AnimationSoundManager.INSTANCE.onPlayerDeath(event.getPlayer());
    }

    @SubscribeEvent
    public void onClientPlayerReload(final GunReloadEvent.Pre event) {
        if (event.isClient()) {
            final GunAnimationController controller =
                    GunAnimationController.fromItem(event.getStack().getItem());
            if (controller != null) {
                if (controller.isAnimationRunning(GunAnimationController.AnimationLabel.DRAW)
                        || controller
                                .isAnimationRunning(GunAnimationController.AnimationLabel.PUMP))
                    event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRenderHand(final RenderHandEvent event) {
        final LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;
        final ItemStack itemStack = player.getInventory().getSelected();
        final GunAnimationController controller =
                GunAnimationController.fromItem(itemStack.getItem());
        if (controller == null)
            return;
        if (controller.isAnimationRunning()) {

        }
    }

    public boolean isReloadingIntro(final Item item) {
        final GunAnimationController controller = GunAnimationController.fromItem(item);
        if (controller == null)
            return false;
        return controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_INTRO);
    }

    public void onReloadLoop(final Item item, final float speed) {
        final GunAnimationController controller = GunAnimationController.fromItem(item);
        if (controller == null)
            return;
        controller.stopAnimation();
        controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_LOOP, speed);
    }

    public void onReloadEnd(final Item item) {
        final GunAnimationController controller = GunAnimationController.fromItem(item);
        if (controller == null)
            return;
        if (controller instanceof PumpShotgunAnimationController) {
            if (controller.getAnimationFromLabel(
                    GunAnimationController.AnimationLabel.RELOAD_NORMAL_END) != null) {
                if (SyncedEntityData.instance().get(Minecraft.getInstance().player,
                        ModSyncedDataKeys.STOP_ANIMA))
                    controller.stopAnimation();
                // controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_NORMAL_END);
            }
        } else {
            if (SyncedEntityData.instance().get(Minecraft.getInstance().player,
                    ModSyncedDataKeys.STOP_ANIMA)) {
                controller.stopAnimation();
                controller.runAnimation(GunAnimationController.AnimationLabel.STATIC);
                controller.stopAnimation();
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().player == null)
            return;
        final ItemStack stack = Minecraft.getInstance().player.getMainHandItem();
        final GunAnimationController controller = GunAnimationController.fromItem(stack.getItem());
        if (controller instanceof PumpShotgunAnimationController) {
            if (controller.getPreviousAnimation() != null
                    && controller.getPreviousAnimation()
                            .equals(controller.getAnimationFromLabel(
                                    GunAnimationController.AnimationLabel.RELOAD_LOOP))
                    && !ReloadHandler.get().isReloading()) {
                if (!controller.isAnimationRunning()) {
                    if (((PumpShotgunAnimationController) controller).isEmpty()) {
                        controller.runAnimation(
                                GunAnimationController.AnimationLabel.RELOAD_EMPTY_END,
                                GunEnchantmentHelper.getReloadSpeed(stack));
                        ((PumpShotgunAnimationController) controller).setEmpty(false);
                    } else
                        controller.runAnimation(
                                GunAnimationController.AnimationLabel.RELOAD_NORMAL_END,
                                GunEnchantmentHelper.getReloadSpeed(stack));
                }
            }
        }
    }

    /*
     * @SubscribeEvent
     * public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event){
     * AnimationSoundManager.INSTANCE.onPlayerDeath(event.getPlayer());
     * }
     * 
     * @SubscribeEvent
     * public void onClientPlayerReload(GunReloadEvent.Pre event){
     * if(event.isClient()){
     * GunAnimationController controller =
     * GunAnimationController.fromItem(event.getStack().getItem());
     * if(controller != null){
     * if(controller.isAnimationRunning(GunAnimationController.AnimationLabel.DRAW)
     * ||
     * controller.isAnimationRunning(GunAnimationController.AnimationLabel.PUMP))
     * event.setCanceled(true);
     * }
     * }
     * }
     * 
     * @SubscribeEvent
     * public void onRenderHand(RenderHandEvent event){
     * ClientPlayerEntity player = Minecraft.getInstance().player;
     * if(player == null) return;
     * ItemStack itemStack = player.getInventory().getCurrentItem();
     * GunAnimationController controller =
     * GunAnimationController.fromItem(itemStack.getItem());
     * if(controller == null) return;
     * if(controller.isAnimationRunning()){
     * 
     * }
     * }
     * 
     * public boolean isReloadingIntro(Item item){
     * GunAnimationController controller = GunAnimationController.fromItem(item);
     * if(controller == null) return false;
     * return controller.isAnimationRunning(GunAnimationController.AnimationLabel.
     * RELOAD_INTRO);
     * }
     * 
     * public void onReloadLoop(Item item){
     * GunAnimationController controller = GunAnimationController.fromItem(item);
     * if(controller == null) return;
     * controller.stopAnimation();
     * controller.runAnimation(GunAnimationController.AnimationLabel.RELOAD_LOOP);
     * }
     * 
     * public void onReloadEnd(Item item){
     * GunAnimationController controller = GunAnimationController.fromItem(item);
     * if(controller == null) return;
     * if(controller instanceof PumpShotgunAnimationController ) {
     * if(controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.
     * RELOAD_NORMAL_END) != null) {
     * controller.stopAnimation();
     * controller.runAnimation(GunAnimationController.AnimationLabel.
     * RELOAD_NORMAL_END);
     * }
     * }else{
     * controller.stopAnimation();
     * controller.runAnimation(GunAnimationController.AnimationLabel.STATIC);
     * controller.stopAnimation();
     * }
     * }
     */
}
