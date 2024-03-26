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
public class BazookaPose extends WeaponPose {
    @Override
    protected AimPose getUpPose() {
        final AimPose pose = new AimPose();
        pose.getIdle().setRenderYawOffset(35F).setItemRotation(new Vector3f(10F, 0F, 0F))
                .setRightArm(new LimbPose().setRotationAngleX(-170F).setRotationAngleY(-35F)
                        .setRotationAngleZ(0F).setRotationPointY(4).setRotationPointZ(-2))
                .setLeftArm(new LimbPose().setRotationAngleX(-130F).setRotationAngleY(65F)
                        .setRotationAngleZ(0F).setRotationPointX(3).setRotationPointY(2)
                        .setRotationPointZ(1));
        return pose;
    }

    @Override
    protected AimPose getForwardPose() {
        final AimPose pose = new AimPose();
        pose.getIdle().setRenderYawOffset(35F)
                .setRightArm(new LimbPose().setRotationAngleX(-90F).setRotationAngleY(-35F)
                        .setRotationAngleZ(0F).setRotationPointY(2).setRotationPointZ(0))
                .setLeftArm(new LimbPose().setRotationAngleX(-91F).setRotationAngleY(35F)
                        .setRotationAngleZ(0F).setRotationPointX(4).setRotationPointY(2)
                        .setRotationPointZ(0));
        return pose;
    }

    @Override
    protected AimPose getDownPose() {
        final AimPose pose = new AimPose();
        pose.getIdle().setRenderYawOffset(35F)
                .setRightArm(new LimbPose().setRotationAngleX(-10F).setRotationAngleY(-35F)
                        .setRotationAngleZ(0F).setRotationPointY(2).setRotationPointZ(0))
                .setLeftArm(new LimbPose().setRotationAngleX(-10F).setRotationAngleY(15F)
                        .setRotationAngleZ(30F).setRotationPointX(4).setRotationPointY(2)
                        .setRotationPointZ(0));
        return pose;
    }

    @Override
    protected boolean hasAimPose() {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyPlayerModelRotation(final Player player, final PlayerModel<?> model,
            final InteractionHand hand, final float aimProgress) {
        if (Config.CLIENT.display.oldAnimations.get()) {
            final boolean right = Minecraft.getInstance().options.mainHand == HumanoidArm.RIGHT
                    ? hand == InteractionHand.MAIN_HAND
                    : hand == InteractionHand.OFF_HAND;
            final ModelPart mainArm = right ? model.rightArm : model.leftArm;
            final ModelPart secondaryArm = right ? model.leftArm : model.rightArm;
            mainArm.xRot = (float) Math.toRadians(-90F);
            mainArm.yRot = (float) Math.toRadians(-35F) * (right ? 1F : -1F);
            mainArm.zRot = (float) Math.toRadians(0F);
            secondaryArm.xRot = (float) Math.toRadians(-91F);
            secondaryArm.yRot = (float) Math.toRadians(45F) * (right ? 1F : -1F);
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
            player.yBodyRotO = player.yRotO + 35F * (right ? 1F : -1F);
            player.yBodyRot = player.getYRot() + 35F * (right ? 1F : -1F);
        } else {
            super.applyPlayerPreRender(player, hand, aimProgress, matrixStack, buffer);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyHeldItemTransforms(final Player player, final InteractionHand hand,
            final float aimProgress, final PoseStack matrixStack, final MultiBufferSource buffer) {
        if (!Config.CLIENT.display.oldAnimations.get()) {
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
