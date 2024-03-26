package com.tac.guns.client.render.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.tac.guns.Config;
import com.tac.guns.common.GripType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class MiniGunPose extends WeaponPose {
    @Override
    protected AimPose getUpPose() {
        final AimPose pose = new AimPose();
        pose.getIdle().setRenderYawOffset(45F).setItemRotation(new Vector3f(10F, 0F, 0F))
                .setRightArm(new LimbPose().setRotationAngleX(-100F).setRotationAngleY(-45F)
                        .setRotationAngleZ(0F).setRotationPointY(2))
                .setLeftArm(new LimbPose().setRotationAngleX(-150F).setRotationAngleY(40F)
                        .setRotationAngleZ(-10F).setRotationPointY(1));
        return pose;
    }

    @Override
    protected AimPose getForwardPose() {
        final AimPose pose = new AimPose();
        pose.getIdle().setRenderYawOffset(45F)
                .setRightArm(new LimbPose().setRotationAngleX(-15F).setRotationAngleY(-45F)
                        .setRotationAngleZ(0F).setRotationPointY(2))
                .setLeftArm(new LimbPose().setRotationAngleX(-45F).setRotationAngleY(30F)
                        .setRotationAngleZ(0F).setRotationPointY(2));
        return pose;
    }

    @Override
    protected AimPose getDownPose() {
        final AimPose pose = new AimPose();
        pose.getIdle().setRenderYawOffset(45F).setItemRotation(new Vector3f(-50F, 0F, 0F))
                .setItemTranslate(new Vector3f(0F, 0F, 1F))
                .setRightArm(new LimbPose().setRotationAngleX(0F).setRotationAngleY(-45F)
                        .setRotationAngleZ(0F).setRotationPointY(1))
                .setLeftArm(new LimbPose().setRotationAngleX(-25F).setRotationAngleY(30F)
                        .setRotationAngleZ(15F).setRotationPointY(4));
        return pose;
    }

    @Override
    protected boolean hasAimPose() {
        return false;
    }

    @Override
    public void applyPlayerModelRotation(final Player player, final PlayerModel<?> model,
            final InteractionHand hand, final float aimProgress) {
        if (Config.CLIENT.display.oldAnimations.get()) {
            final boolean right = Minecraft.getInstance().options.mainHand == HumanoidArm.RIGHT
                    ? hand == InteractionHand.MAIN_HAND
                    : hand == InteractionHand.OFF_HAND;
            final ModelPart mainArm = right ? model.rightArm : model.leftArm;
            final ModelPart secondaryArm = right ? model.leftArm : model.rightArm;
            mainArm.xRot = (float) Math.toRadians(-15F);
            mainArm.yRot = (float) Math.toRadians(-45F) * (right ? 1F : -1F);
            mainArm.zRot = (float) Math.toRadians(0F);
            secondaryArm.xRot = (float) Math.toRadians(-45F);
            secondaryArm.yRot = (float) Math.toRadians(30F) * (right ? 1F : -1F);
            secondaryArm.zRot = (float) Math.toRadians(0F);
        } else {
            super.applyPlayerModelRotation(player, model, hand, aimProgress);
        }
    }

    @Override
    public void applyPlayerPreRender(final Player player, final InteractionHand hand,
            final float aimProgress, final PoseStack matrixStack, final MultiBufferSource buffer) {
        if (Config.CLIENT.display.oldAnimations.get()) {
            final boolean right = Minecraft.getInstance().options.mainHand == HumanoidArm.RIGHT
                    ? hand == InteractionHand.MAIN_HAND
                    : hand == InteractionHand.OFF_HAND;
            player.yBodyRotO = player.yRotO + 45F * (right ? 1F : -1F);
            player.yBodyRot = player.getYRot() + 45F * (right ? 1F : -1F);
        } else {
            super.applyPlayerPreRender(player, hand, aimProgress, matrixStack, buffer);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyHeldItemTransforms(final Player player, final InteractionHand hand,
            final float aimProgress, final PoseStack matrixStack, final MultiBufferSource buffer) {
        if (Config.CLIENT.display.oldAnimations.get()) {
            if (hand == InteractionHand.OFF_HAND) {
                matrixStack.translate(0, -10 * 0.0625F, 0);
                matrixStack.translate(0, 0, -2 * 0.0625F);
            }
        } else {
            super.applyHeldItemTransforms(player, hand, aimProgress, matrixStack, buffer);
        }
    }

    @Override
    public boolean applyOffhandTransforms(final Player player, final PlayerModel<?> model,
            final ItemStack stack, final PoseStack matrixStack, final float partialTicks) {
        return GripType.applyBackTransforms(player, matrixStack);
    }

    @Override
    public boolean canApplySprintingAnimation() {
        return false;
    }
}
