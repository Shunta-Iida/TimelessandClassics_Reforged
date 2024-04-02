package com.tac.guns.client.render.model.gun;

import static com.tac.guns.client.render.model.CommonComponents.BODY;
import static com.tac.guns.client.render.model.CommonComponents.BOLT;
import static com.tac.guns.client.render.model.CommonComponents.BULLET;
import static com.tac.guns.client.render.model.CommonComponents.GRIP_RAIL_COVER;
import static com.tac.guns.client.render.model.CommonComponents.HANDLE;
import static com.tac.guns.client.render.model.CommonComponents.LASER_BASIC;
import static com.tac.guns.client.render.model.CommonComponents.LASER_BASIC_DEVICE;
import static com.tac.guns.client.render.model.CommonComponents.SIDE_RAIL_COVER;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.Config;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.UDP9AnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.render.model.SkinnedGunModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.gun.GunItem;
import com.tac.guns.weapon.Gun;
import com.tac.guns.weapon.attachment.IAttachmentItem;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class udp_9_animation extends SkinnedGunModel {

    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType,
            ItemStack stack, LivingEntity entity, PoseStack matrices,
            MultiBufferSource renderBuffer, int light, int overlay) {
        UDP9AnimationController controller = UDP9AnimationController.getInstance();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                    UDP9AnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getAttachment(IAttachmentItem.Type.SIDE_RAIL, stack)
                    .getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderLaserModuleModel(getComponentModel(skin, LASER_BASIC_DEVICE),
                        Gun.getAttachment(IAttachmentItem.Type.SIDE_RAIL, stack), matrices,
                        renderBuffer, light, overlay);
                if (transformType.firstPerson()
                        || Config.COMMON.gameplay.canSeeLaserThirdSight.get())
                    RenderUtil.renderLaserModuleModel(getComponentModel(skin, LASER_BASIC),
                            Gun.getAttachment(IAttachmentItem.Type.SIDE_RAIL, stack), matrices,
                            renderBuffer, 15728880, overlay); // 15728880 For fixed max light
            } else {
                RenderUtil.renderModel(getComponentModel(skin, SIDE_RAIL_COVER), stack, matrices,
                        renderBuffer, light, overlay);
            }

            renderSight(stack, matrices, renderBuffer, light, overlay, skin);

            if (Gun.getAttachment(IAttachmentItem.Type.UNDER_BARREL, stack) == ItemStack.EMPTY) {
                RenderUtil.renderModel(getComponentModel(skin, GRIP_RAIL_COVER), stack, matrices,
                        renderBuffer, light, overlay);
            }

            renderStock(stack, matrices, renderBuffer, light, overlay, skin);

            renderBarrelWithDefault(stack, matrices, renderBuffer, light, overlay, skin);

            renderGrip(stack, matrices, renderBuffer, light, overlay, skin);

            RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer,
                    light, overlay);

            matrices.pushPose();
            {
                if (transformType.firstPerson()) {
                    Gun gun = ((GunItem) stack.getItem()).getGun();
                    float cooldownOg = ShootingHandler.get().getshootMsGap()
                            / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1
                                    : ShootingHandler.get().getshootMsGap() / ShootingHandler
                                            .calcShootTickGap(gun.getGeneral().getRate());

                    if (Gun.hasAmmo(stack)) {
                        // Math provided by Bomb787 on GitHub and Curseforge!!!
                        matrices.translate(0, 0,
                                0.095f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
                    } else if (!Gun.hasAmmo(stack)) {
                        matrices.translate(0, 0, 0.095f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                    }
                }
                RenderUtil.renderModel(getComponentModel(skin, BOLT), stack, matrices, renderBuffer,
                        light, overlay);
            }
            matrices.popPose();
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                    UDP9AnimationController.INDEX_MAG, transformType, matrices);
            renderMag(stack, matrices, renderBuffer, light, overlay, skin);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                    UDP9AnimationController.INDEX_BULLET, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, BULLET), stack, matrices, renderBuffer,
                    light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                    UDP9AnimationController.INDEX_HANDLE, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, HANDLE), stack, matrices, renderBuffer,
                    light, overlay);
        }
        matrices.popPose();

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}
