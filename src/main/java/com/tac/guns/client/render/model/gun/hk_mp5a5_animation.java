package com.tac.guns.client.render.model.gun;

import static com.tac.guns.client.render.model.CommonComponents.BODY;
import static com.tac.guns.client.render.model.CommonComponents.BOLT;
import static com.tac.guns.client.render.model.CommonComponents.BOLT_HANDLE;
import static com.tac.guns.client.render.model.CommonComponents.HAND_GUARD_DEFAULT;
import static com.tac.guns.client.render.model.CommonComponents.HAND_GUARD_EXTENDED;
import static com.tac.guns.client.render.model.CommonComponents.LASER_BASIC;
import static com.tac.guns.client.render.model.CommonComponents.LASER_BASIC_DEVICE;
import static com.tac.guns.client.render.model.CommonComponents.RAIL_SCOPE;
import static com.tac.guns.client.render.model.CommonComponents.SIGHT_LIGHT;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.HkMp5a5AnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.render.model.SkinnedGunModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.gun.GunItem;
import com.tac.guns.weapon.Gun;
import com.tac.guns.weapon.attachment.IAttachment;

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
public class hk_mp5a5_animation extends SkinnedGunModel {

    @Override
    public void render(GunSkin skin, float partialTicks, ItemTransforms.TransformType transformType,
            ItemStack stack, LivingEntity entity, PoseStack matrices,
            MultiBufferSource renderBuffer, int light, int overlay) {
        HkMp5a5AnimationController controller = HkMp5a5AnimationController.getInstance();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                    HkMp5a5AnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getScope(stack) != null) {
                RenderUtil.renderModel(getComponentModel(skin, RAIL_SCOPE), stack, matrices,
                        renderBuffer, light, overlay);
            }

            renderStockWithDefault(stack, matrices, renderBuffer, light, overlay, skin);

            if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack) == ItemStack.EMPTY
                    && Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack) == ItemStack.EMPTY) {
                RenderUtil.renderModel(getComponentModel(skin, HAND_GUARD_DEFAULT), stack, matrices,
                        renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(getComponentModel(skin, HAND_GUARD_EXTENDED), stack,
                        matrices, renderBuffer, light, overlay);
            }

            renderGrip(stack, matrices, renderBuffer, light, overlay, skin);

            if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack)
                    .getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderLaserModuleModel(getComponentModel(skin, LASER_BASIC_DEVICE),
                        Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices,
                        renderBuffer, light, overlay);
                RenderUtil.renderLaserModuleModel(getComponentModel(skin, LASER_BASIC),
                        Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices,
                        renderBuffer, 15728880, overlay); // 15728880 For fixed max light
            }

            renderBarrel(stack, matrices, renderBuffer, light, overlay, skin);

            matrices.pushPose();
            if (transformType.firstPerson()) {
                Gun gun = ((GunItem) stack.getItem()).getGun();
                float cooldownOg = ShootingHandler.get().getshootMsGap()
                        / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1
                                : ShootingHandler.get().getshootMsGap() / ShootingHandler
                                        .calcShootTickGap(gun.getGeneral().getRate());

                matrices.translate(0, 0, 0.085f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
            }
            RenderUtil.renderModel(getComponentModel(skin, BOLT), stack, matrices, renderBuffer,
                    light, overlay);
            matrices.popPose();

            RenderUtil.renderModel(getComponentModel(skin, SIGHT_LIGHT), stack, matrices,
                    renderBuffer, 15728880, overlay);
            RenderUtil.renderModel(getComponentModel(skin, BODY), stack, matrices, renderBuffer,
                    light, overlay);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                    HkMp5a5AnimationController.INDEX_MAGAZINE, transformType, matrices);
            renderMag(stack, matrices, renderBuffer, light, overlay, skin);
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(getComponentModel(skin, BODY),
                    HkMp5a5AnimationController.INDEX_BOLT, transformType, matrices);
            RenderUtil.renderModel(getComponentModel(skin, BOLT_HANDLE), stack, matrices,
                    renderBuffer, light, overlay);
        }
        matrices.popPose();

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}
