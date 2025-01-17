package com.tac.guns.client.render.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.tac.guns.Config;
import com.tac.guns.client.handler.ReloadHandler;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.GripType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
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
public class TwoHandedPose extends WeaponPose {
    @Override
    protected AimPose getUpPose() {
        final AimPose upPose = new AimPose();
        upPose.getIdle().setRenderYawOffset(45F).setItemRotation(new Vector3f(60F, 0F, 10F))
                .setRightArm(new LimbPose().setRotationAngleX(-120F).setRotationAngleY(-55F)
                        .setRotationPointX(-5).setRotationPointY(3).setRotationPointZ(0))
                .setLeftArm(new LimbPose().setRotationAngleX(-160F).setRotationAngleY(-20F)
                        .setRotationAngleZ(-30F).setRotationPointY(2).setRotationPointZ(-1));
        upPose.getAiming().setRenderYawOffset(45F).setItemRotation(new Vector3f(40F, 0F, 30F))
                .setItemTranslate(new Vector3f(-1, 0, 0))
                .setRightArm(new LimbPose().setRotationAngleX(-140F).setRotationAngleY(-55F)
                        .setRotationPointX(-5).setRotationPointY(3).setRotationPointZ(0))
                .setLeftArm(new LimbPose().setRotationAngleX(-170F).setRotationAngleY(-20F)
                        .setRotationAngleZ(-35F).setRotationPointY(1).setRotationPointZ(0));
        return upPose;
    }

    @Override
    protected AimPose getForwardPose() {
        final AimPose forwardPose = new AimPose();
        forwardPose.getIdle().setRenderYawOffset(45F).setItemRotation(new Vector3f(30F, -11F, 0F))
                .setRightArm(new LimbPose().setRotationAngleX(-60F).setRotationAngleY(-55F)
                        .setRotationAngleZ(0F).setRotationPointX(-5).setRotationPointY(2)
                        .setRotationPointZ(1))
                .setLeftArm(new LimbPose().setRotationAngleX(-65F).setRotationAngleY(-10F)
                        .setRotationAngleZ(5F).setRotationPointY(2).setRotationPointZ(-1));
        forwardPose.getAiming().setRenderYawOffset(45F).setItemRotation(new Vector3f(5F, -21F, 0F))
                .setRightArm(new LimbPose().setRotationAngleX(-85F).setRotationAngleY(-65F)
                        .setRotationAngleZ(0F).setRotationPointX(-5).setRotationPointY(2))
                .setLeftArm(new LimbPose().setRotationAngleX(-90F).setRotationAngleY(-15F)
                        .setRotationAngleZ(0F).setRotationPointY(2).setRotationPointZ(0));
        return forwardPose;
    }

    @Override
    protected AimPose getDownPose() {
        final AimPose downPose = new AimPose();
        downPose.getIdle().setRenderYawOffset(45F).setItemRotation(new Vector3f(-15F, -5F, 0F))
                .setItemTranslate(new Vector3f(0, -0.5F, 0.5F))
                .setRightArm(new LimbPose().setRotationAngleX(-30F).setRotationAngleY(-65F)
                        .setRotationAngleZ(0F).setRotationPointX(-5).setRotationPointY(2))
                .setLeftArm(new LimbPose().setRotationAngleX(-5F).setRotationAngleY(-20F)
                        .setRotationAngleZ(20F).setRotationPointY(5).setRotationPointZ(0));
        downPose.getAiming().setRenderYawOffset(45F).setItemRotation(new Vector3f(-20F, -5F, -10F))
                .setItemTranslate(new Vector3f(0, -0.5F, 1F))
                .setRightArm(new LimbPose().setRotationAngleX(-30F).setRotationAngleY(-65F)
                        .setRotationAngleZ(0F).setRotationPointX(-5).setRotationPointY(1))
                .setLeftArm(new LimbPose().setRotationAngleX(-10F).setRotationAngleY(-20F)
                        .setRotationAngleZ(30F).setRotationPointY(5).setRotationPointZ(0));
        return downPose;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyPlayerModelRotation(final Player player, final PlayerModel<?> model,
            final InteractionHand hand, final float aimProgress) {
        if (Config.CLIENT.display.oldAnimations.get()) {
            final Minecraft mc = Minecraft.getInstance();
            final boolean right =
                    mc.options.mainHand == HumanoidArm.RIGHT ? hand == InteractionHand.MAIN_HAND
                            : hand == InteractionHand.OFF_HAND;
            final ModelPart mainArm = right ? model.rightArm : model.leftArm;
            final ModelPart secondaryArm = right ? model.leftArm : model.rightArm;
            mainArm.xRot = model.head.xRot;
            mainArm.yRot = model.head.yRot;
            mainArm.zRot = model.head.zRot;
            secondaryArm.xRot = model.head.xRot;
            secondaryArm.yRot = model.head.yRot;
            secondaryArm.zRot = model.head.zRot;
            mainArm.xRot = (float) Math.toRadians(-55F + aimProgress * -30F);
            mainArm.yRot = (float) Math.toRadians((-45F + aimProgress * -20F) * (right ? 1F : -1F));
            secondaryArm.xRot = (float) Math.toRadians(-42F + aimProgress * -48F);
            secondaryArm.yRot =
                    (float) Math.toRadians((-15F + aimProgress * 5F) * (right ? 1F : -1F));
        } else {
            super.applyPlayerModelRotation(player, model, hand, aimProgress);
            final float angle = this.getPlayerPitch(player);
            model.head.xRot = (float) Math.toRadians(angle > 0.0 ? angle * 70F : angle * 90F);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyPlayerPreRender(final Player player, final InteractionHand hand,
            final float aimProgress, final PoseStack matrixStack, final MultiBufferSource buffer) {
        if (Config.CLIENT.display.oldAnimations.get()) {
            final boolean right = Minecraft.getInstance().options.mainHand == HumanoidArm.RIGHT
                    ? hand == InteractionHand.MAIN_HAND
                    : hand == InteractionHand.OFF_HAND;
            player.yBodyRotO =
                    player.yRotO + (right ? 25F : -25F) + aimProgress * (right ? 20F : -20F);
            player.yBodyRot =
                    player.getYRot() + (right ? 25F : -25F) + aimProgress * (right ? 20F : -20F);
        } else {
            super.applyPlayerPreRender(player, hand, aimProgress, matrixStack, buffer);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyHeldItemTransforms(final Player player, final InteractionHand hand,
            final float aimProgress, final PoseStack matrixStack, final MultiBufferSource buffer) {
        if (Config.CLIENT.display.oldAnimations.get()) {
            if (hand == InteractionHand.MAIN_HAND) {
                final boolean right = Minecraft.getInstance().options.mainHand == HumanoidArm.RIGHT
                        ? hand == InteractionHand.MAIN_HAND
                        : hand == InteractionHand.OFF_HAND;
                matrixStack.translate(0, 0, 0.05);
                final float invertRealProgress = 1.0F - aimProgress;
                matrixStack.mulPose(Vector3f.ZP
                        .rotationDegrees((25F * invertRealProgress) * (right ? 1F : -1F)));
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(
                        (30F * invertRealProgress + aimProgress * -20F) * (right ? 1F : -1F)));
                matrixStack.mulPose(
                        Vector3f.XP.rotationDegrees(25F * invertRealProgress + aimProgress * 5F));
            }
        } else {
            super.applyHeldItemTransforms(player, hand, aimProgress, matrixStack, buffer);
        }
    }

    @Override
    public void renderFirstPersonArms(final LocalPlayer player, final HumanoidArm hand,
            final ItemStack stack, final PoseStack matrixStack, final MultiBufferSource buffer,
            final int light, final float partialTicks) {
        matrixStack.translate(0, 0, -1);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));

        matrixStack.pushPose();

        final float reloadProgress = ReloadHandler.get().getReloadProgress(partialTicks, stack);
        matrixStack.translate(reloadProgress * 0.5, -reloadProgress, -reloadProgress * 0.5);

        final int side = hand.getOpposite() == HumanoidArm.RIGHT ? 1 : -1;
        matrixStack.translate(6 * side * 0.0625, -0.585, -0.5);

        if (Minecraft.getInstance().player.getModelName().equals("slim")
                && hand.getOpposite() == HumanoidArm.LEFT) {
            matrixStack.translate(0.03125F * -side, 0, 0);
        }

        matrixStack.mulPose(Vector3f.XP.rotationDegrees(80F));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(15F * -side));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(15F * -side));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-35F));
        matrixStack.scale(0.5F, 0.5F, 0.5F);

        RenderUtil.renderFirstPersonArm(player, hand.getOpposite(), matrixStack, buffer, light);

        matrixStack.popPose();

        double centerOffset = 2.5;
        if (Minecraft.getInstance().player.getModelName().equals("slim")) {
            centerOffset += hand == HumanoidArm.RIGHT ? 0.2 : 0.8;
        }
        centerOffset = hand == HumanoidArm.RIGHT ? -centerOffset : centerOffset;
        matrixStack.translate(centerOffset * 0.0625, -0.4, -0.975);

        matrixStack.mulPose(Vector3f.XP.rotationDegrees(80F));
        matrixStack.scale(0.5F, 0.5F, 0.5F);

        RenderUtil.renderFirstPersonArm(player, hand, matrixStack, buffer, light);
    }

    @Override
    public boolean applyOffhandTransforms(final Player player, final PlayerModel<?> model,
            final ItemStack stack, final PoseStack matrixStack, final float partialTicks) {
        return GripType.applyBackTransforms(player, matrixStack);
    }
}
