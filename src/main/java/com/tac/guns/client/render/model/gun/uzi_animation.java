package com.tac.guns.client.render.model.gun;

import static com.tac.guns.client.render.model.CommonComponents.BODY;
import static com.tac.guns.client.render.model.CommonComponents.BOLT;
import static com.tac.guns.client.render.model.CommonComponents.BULLET;
import static com.tac.guns.client.render.model.CommonComponents.HANDLE;
import static com.tac.guns.client.render.model.CommonComponents.MUZZLE_SILENCER;
import static com.tac.guns.client.render.model.CommonComponents.SIGHT_LIGHT;
import static com.tac.guns.client.render.model.CommonComponents.STOCK_DEFAULT;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.UZIAnimationController;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.render.model.SkinnedGunModel;
import com.tac.guns.client.render.model.internal.TacGunComponents;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.gun.GunItem;
import com.tac.guns.weapon.Gun;
import com.tac.guns.weapon.attachment.IAttachmentItem;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class uzi_animation extends SkinnedGunModel {

    // The render method, similar to what is in DartEntity. We can render the item
    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType,
            ItemStack stack, LivingEntity entity, PoseStack matrices,
            MultiBufferSource renderBuffer, int light, int overlay) {
        UZIAnimationController controller = UZIAnimationController.getInstance();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                    UZIAnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getScope(stack) != null) {
                RenderUtil.renderModel(getComponentModel(skin, STOCK_DEFAULT), stack, matrices,
                        renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.STOCK_FOLDED),
                        stack, matrices, renderBuffer, light, overlay);
            }
            if (Gun.getAttachment(IAttachmentItem.Type.PISTOL_BARREL, stack)
                    .getItem() == ModItems.PISTOL_SILENCER.get()) {
                RenderUtil.renderModel(getComponentModel(skin, MUZZLE_SILENCER), stack, matrices,
                        renderBuffer, light, overlay);
            }
            RenderUtil.renderModel(getComponentModel(skin, SIGHT_LIGHT), stack, matrices,
                    renderBuffer, 15728880, overlay);
            RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer,
                    light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                    UZIAnimationController.INDEX_MAGAZINE, transformType, matrices);
            renderMag(stack, matrices, renderBuffer, light, overlay, skin);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                    UZIAnimationController.INDEX_BOLT, transformType, matrices);
            Gun gun = ((GunItem) stack.getItem()).getGun();
            float cooldownOg = ShootingHandler.get().getshootMsGap()
                    / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1
                            : ShootingHandler.get().getshootMsGap()
                                    / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

            if (transformType.firstPerson()) {
                AnimationMeta reloadEmpty = controller
                        .getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
                boolean shouldOffset =
                        reloadEmpty != null && reloadEmpty.equals(controller.getPreviousAnimation())
                                && controller.isAnimationRunning();
                if (!shouldOffset && !Gun.hasAmmo(stack)) {
                    matrices.translate(0, 0, -0.175f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                } else {
                    matrices.translate(0, 0,
                            -0.175f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
                }
            }
            RenderUtil.renderModel(getComponentModel(skin, BOLT), stack, matrices, renderBuffer,
                    light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                    UZIAnimationController.INDEX_HANDLE, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, HANDLE), stack, matrices, renderBuffer,
                    light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                    UZIAnimationController.INDEX_BULLET1, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, BULLET), stack, matrices, renderBuffer,
                    light, overlay);
        }
        matrices.popPose();

        if (controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_NORMAL)
                .equals(controller.getPreviousAnimation()) && transformType.firstPerson()) {
            matrices.pushPose();
            {
                controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                        UZIAnimationController.INDEX_EXTENDED_MAGAZINE, transformType, matrices);
                renderMag(stack, matrices, renderBuffer, light, overlay, skin);
            }
            matrices.popPose();

            matrices.pushPose();
            {
                controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                        UZIAnimationController.INDEX_BULLET2, transformType, matrices);
                RenderUtil.renderModel(getComponentModel(skin, BULLET), stack, matrices,
                        renderBuffer, light, overlay);
            }
            matrices.popPose();
        }

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}
