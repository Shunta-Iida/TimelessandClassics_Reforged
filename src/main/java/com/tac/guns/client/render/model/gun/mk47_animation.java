package com.tac.guns.client.render.model.gun;

import static com.tac.guns.client.render.model.CommonComponents.BODY;
import static com.tac.guns.client.render.model.CommonComponents.BOLT;
import static com.tac.guns.client.render.model.CommonComponents.MUZZLE_SILENCER;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.tac.guns.Config;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.MK47AnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.render.model.SkinnedGunModel;
import com.tac.guns.client.render.model.internal.TacGunComponents;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.item.gun.GunItem;
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
public class mk47_animation extends SkinnedGunModel {

    public mk47_animation() {
        extraOffset.put(MUZZLE_SILENCER, new Vector3d(0, 0, -0.1));
    }

    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType,
            ItemStack stack, LivingEntity entity, PoseStack matrices,
            MultiBufferSource renderBuffer, int light, int overlay) {
        MK47AnimationController controller = MK47AnimationController.getInstance();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                    MK47AnimationController.INDEX_BODY, transformType, matrices);

            renderSight(stack, matrices, renderBuffer, light, overlay, skin);

            renderStock(stack, matrices, renderBuffer, light, overlay, skin);

            renderBarrelWithDefault(stack, matrices, renderBuffer, light, overlay, skin);

            renderLaserDevice(stack, matrices, renderBuffer, light, overlay, skin);

            if (transformType.firstPerson() || Config.COMMON.gameplay.canSeeLaserThirdSight.get())
                renderLaser(stack, matrices, renderBuffer, light, overlay, skin);

            RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer,
                    light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                    MK47AnimationController.INDEX_MAGAZINE, transformType, matrices);
            renderMag(stack, matrices, renderBuffer, light, overlay, skin);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                    MK47AnimationController.INDEX_BOLT, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, TacGunComponents.PULL), stack, matrices,
                    renderBuffer, light, overlay);

            Gun gun = ((GunItem) stack.getItem()).getGun();
            float cooldownOg = ShootingHandler.get().getshootMsGap()
                    / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1
                            : ShootingHandler.get().getshootMsGap()
                                    / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

            if (transformType.firstPerson()) {
                matrices.translate(0, 0, 0.225f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1));
                matrices.translate(0, 0, 0.025f);
            }
            RenderUtil.renderModel(getComponentModel(skin, BOLT), stack, matrices, renderBuffer,
                    light, overlay);
        }
        matrices.popPose();
        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}
