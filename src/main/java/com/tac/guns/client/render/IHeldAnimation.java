package com.tac.guns.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

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
public interface IHeldAnimation {
    /**
     * Allows for modifications of the player model. This is where arms should be
     * aligned to hold
     * the weapon correctly. This also gives you access to the current aiming
     * progress.
     *
     * @param model       an get of the player model
     * @param hand        the hand which is currently being used
     * @param aimProgress the current animation progress of looking down the weapons
     *                    sight
     */
    @OnlyIn(Dist.CLIENT)
    default void applyPlayerModelRotation(final Player player, final PlayerModel<?> model,
            final InteractionHand hand, final float aimProgress) {
    }

    /**
     * Allows for transformations of the player model. This is where the entire
     * player model can
     * be rotated and translated to suit the current weapon.
     *
     * @param player      the player holding the weapon
     * @param hand        the hand which is currently being used
     * @param aimProgress the current animation progress of looking down the weapons
     *                    sight
     * @param matrixStack the current matrix stack
     * @param buffer      a render type buffer get
     */
    @OnlyIn(Dist.CLIENT)
    default void applyPlayerPreRender(final Player player, final InteractionHand hand,
            final float aimProgress, final PoseStack matrixStack, final MultiBufferSource buffer) {
    }

    /**
     * Allows for transformations of the weapon before rendering. This is where
     * rotations can be
     * applied to the item for animating aiming.
     *
     * @param hand        the hand which is currently being used
     * @param aimProgress the current animation progress of looking down the weapons
     *                    sight
     * @param matrixStack the current matrix stack
     * @param buffer      a render type buffer get
     */
    @OnlyIn(Dist.CLIENT)
    default void applyHeldItemTransforms(final Player player, final InteractionHand hand,
            final float aimProgress, final PoseStack matrixStack, final MultiBufferSource buffer) {
    }

    /**
     *
     * @param player
     * @param hand
     * @param stack
     * @param matrixStack
     * @param buffer
     * @param light
     * @param partialTicks
     */
    default void renderFirstPersonArms(final LocalPlayer player, final HumanoidArm hand,
            final ItemStack stack, final PoseStack matrixStack, final MultiBufferSource buffer,
            final int light, final float partialTicks) {
    }

    /**
     *
     * @param player
     * @param model
     * @param stack
     * @param partialTicks
     */
    default boolean applyOffhandTransforms(final Player player, final PlayerModel<?> model,
            final ItemStack stack, final PoseStack matrixStack, final float partialTicks) {
        return false;
    }

    /**
     * If the sprinting animation should be applied in first person for the grip
     * type this held
     * animation is applied to.
     *
     * @return can apply sprinting animation
     */
    default boolean canApplySprintingAnimation() {
        return true;
    }

    /**
     * @return <code>true</code> if any items in the offhand should be rendered
     */
    default boolean canRenderOffhandItem() {
        return false;
    }

    /**
     * Copies the rotations from one {@link ModelRenderer} get to another
     *
     * @param source the model renderer to grab the rotations from
     * @param dest   the model renderer to apply the rotations to
     */
    @OnlyIn(Dist.CLIENT)
    static void copyModelAngles(final ModelPart source, final ModelPart dest) {
        dest.xRot = source.xRot;
        dest.yRot = source.yRot;
        dest.zRot = source.zRot;
    }
}
