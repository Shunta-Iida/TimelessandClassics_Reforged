package com.tac.guns.client.render.model.gun;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.MAC10AnimationController;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.render.model.CommonComponents;
import com.tac.guns.client.render.model.SkinnedGunModel;
import com.tac.guns.client.render.model.internal.TacGunComponents;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.item.gun.GunItem;
import com.tac.guns.item.gun.GunItemHelper;
import com.tac.guns.weapon.Gun;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/*
 * Because the revolver has a rotating chamber, we need to render it in a different way than normal items. In this case
 * we are overriding the model.
 */

/**
 * Author: Timeless Development, and associates.
 */
public class micro_uzi_animation extends SkinnedGunModel {

    // The render method, similar to what is in DartEntity. We can render the item
    @Override
    public void render(final GunSkin skin, final float partialTicks,
            final ItemTransforms.TransformType transformType, final ItemStack stack,
            final LivingEntity entity, final PoseStack matrices,
            final MultiBufferSource renderBuffer, final int light, final int overlay) {
        final MAC10AnimationController controller = MAC10AnimationController.getInstance();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(
                    this.getComponentModel(skin, CommonComponents.BODY),
                    MAC10AnimationController.INDEX_BODY, transformType, matrices);
            if (GunItemHelper.of(stack).getAmmoCapacityWeight() > 0) {
                RenderUtil.renderModel(this.getComponentModel(skin, CommonComponents.STOCK_DEFAULT),
                        stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(this.getComponentModel(skin, TacGunComponents.STOCK_FOLDED),
                        stack, matrices, renderBuffer, light, overlay);
            }
            RenderUtil.renderModel(this.getComponentModel(skin, CommonComponents.BODY), stack,
                    matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(
                    this.getComponentModel(skin, CommonComponents.BODY),
                    MAC10AnimationController.INDEX_MAGAZINE, transformType, matrices);
            this.renderMag(stack, matrices, renderBuffer, light, overlay, skin);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(
                    this.getComponentModel(skin, CommonComponents.BODY),
                    MAC10AnimationController.INDEX_BOLT, transformType, matrices);

            final Gun gun = ((GunItem) stack.getItem()).getGun();
            final float cooldownOg = ShootingHandler.get().getshootMsGap()
                    / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1
                            : ShootingHandler.get().getshootMsGap()
                                    / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

            if (transformType.firstPerson()) {
                final AnimationMeta reloadEmpty = controller
                        .getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
                final boolean shouldOffset =
                        reloadEmpty != null && reloadEmpty.equals(controller.getPreviousAnimation())
                                && controller.isAnimationRunning();
                if (!shouldOffset && !Gun.hasAmmo(stack)) {
                    matrices.translate(0, 0, -0.25);
                } else {
                    matrices.translate(0, 0, -0.25 + Math.pow(cooldownOg - 0.5, 2));
                }
            }
            RenderUtil.renderModel(this.getComponentModel(skin, CommonComponents.BOLT), stack,
                    matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }

    // TODO comments
}
