package com.tac.guns.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.network.CommonStateBox;
import com.tac.guns.util.GunEnchantmentHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemInHandRenderer.class)
public class FirstPersonRendererMixin {
    private ItemStack prevItemStack = ItemStack.EMPTY;
    private int prevSlot = 0;

    @Shadow
    private ItemStack mainHandItem;

    @Shadow
    private float mainHandHeight;

    @Shadow
    private float oMainHandHeight;

    @Inject(method = "tick", at = @At("HEAD"))
    public void applyDrawAndHolster(final CallbackInfo ci) {
        if (Minecraft.getInstance().player == null)
            return;
        final ItemStack mainHandItemStack = Minecraft.getInstance().player.getMainHandItem();
        final GunAnimationController controller =
                GunAnimationController.fromItem(mainHandItemStack.getItem());
        final GunAnimationController controller1 =
                GunAnimationController.fromItem(this.prevItemStack.getItem());
        if (this.prevItemStack.sameItem(mainHandItemStack)
                && (this.prevSlot == Minecraft.getInstance().player.getInventory().selected
                        && !CommonStateBox.isSwapped))
            return;
        this.prevItemStack = mainHandItemStack;
        this.prevSlot = Minecraft.getInstance().player.getInventory().selected;
        CommonStateBox.isSwapped = false;
        // if(isSameWeapon(Minecraft.getInstance().player)) return;
        if (controller1 != null && controller1.isAnimationRunning()) {
            controller1.stopAnimation();
        }
        if (controller != null && controller == controller1) {
            // Stop the previous item's animation
            final AnimationMeta meta =
                    controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.DRAW);
            if (!controller.getPreviousAnimation().equals(meta))
                controller.stopAnimation();
            controller.runAnimation(GunAnimationController.AnimationLabel.DRAW,
                    GunEnchantmentHelper.getReloadSpeed(mainHandItemStack));
        } else if (controller != null && controller
                .getAnimationFromLabel(GunAnimationController.AnimationLabel.DRAW) != null) {
            this.mainHandItem = mainHandItemStack;
            controller.runAnimation(GunAnimationController.AnimationLabel.DRAW,
                    GunEnchantmentHelper.getReloadSpeed(mainHandItemStack));
        }
    }

    /*
             */
    @Inject(method = "tick", at = @At("RETURN"))
    public void cancelEquippedProgress(final CallbackInfo ci) {
        if (Minecraft.getInstance().player == null)
            return;
        final ItemStack mainHandItemStack = Minecraft.getInstance().player.getMainHandItem();
        final GunAnimationController controller =
                GunAnimationController.fromItem(mainHandItemStack.getItem());
        if (controller == null)
            return;
        this.mainHandHeight = 1.0f;
        this.oMainHandHeight = 1.0f;
    }
}
