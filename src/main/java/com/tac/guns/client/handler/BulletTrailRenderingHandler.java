package com.tac.guns.client.handler;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.tac.guns.Config;
import com.tac.guns.client.BulletTrail;
import com.tac.guns.client.GunRenderType;
import com.tac.guns.client.util.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class BulletTrailRenderingHandler {
    private static BulletTrailRenderingHandler instance;

    public static BulletTrailRenderingHandler get() {
        if (BulletTrailRenderingHandler.instance == null) {
            BulletTrailRenderingHandler.instance = new BulletTrailRenderingHandler();
        }
        return BulletTrailRenderingHandler.instance;
    }

    private final Map<Integer, BulletTrail> bullets = new HashMap<>();

    private BulletTrailRenderingHandler() {
    }

    /**
     * Adds a bullet trail to render into the world
     *
     * @param trail the bullet trail get
     */
    public void add(final BulletTrail trail) {
        // if(Config.CLIENT.particle.)

        // Prevents trails being added when not in a world
        final ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            this.bullets.put(trail.getEntityId(), trail);
        }
    }

    /**
     * Removes the bullet for the given entity id.
     *
     * @param entityId the entity id of the bullet
     */
    public void remove(final int entityId) {
        this.bullets.remove(entityId);
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        final ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            if (event.phase == TickEvent.Phase.END) {
                this.bullets.values().forEach(BulletTrail::tick);
                this.bullets.values().removeIf(BulletTrail::isDead);
            }
        } else if (!this.bullets.isEmpty()) {
            this.bullets.clear();
        }
    }

    public void render(final PoseStack stack, final float partialSticks) {
        for (final BulletTrail bulletTrail : this.bullets.values()) {
            this.renderBulletTrail(bulletTrail, stack, partialSticks);
        }
    }

    @SubscribeEvent
    public void onRespawn(final ClientPlayerNetworkEvent.RespawnEvent event) {
        this.bullets.clear();
    }

    @SubscribeEvent
    public void onLoggedOut(final ClientPlayerNetworkEvent.LoggedOutEvent event) {
        this.bullets.clear();
    }

    // TODO: Clean-up this entire method...

    /**
     * @param bulletTrail
     * @param matrixStack
     * @param partialTicks
     */
    private void renderBulletTrail(final BulletTrail bulletTrail, final PoseStack matrixStack,
            final float partialTicks) {

        final Minecraft mc = Minecraft.getInstance();
        final Entity entity = mc.getCameraEntity();
        if (entity == null || bulletTrail.isDead() || bulletTrail.getAge() < 2)
            return;

        if (entity == null || bulletTrail.isDead() || !Config.CLIENT.display.showBulletTrails.get())
            return;
        if (!Config.CLIENT.display.showFirstPersonBulletTrails.get()
                && Minecraft.getInstance().player.is(entity))
            return;
        matrixStack.pushPose();
        final Vec3 view = mc.gameRenderer.getMainCamera().getPosition();
        final Vec3 position = bulletTrail.getPosition();
        final Vec3 motion = bulletTrail.getMotion();

        final double bulletX = position.x + motion.x * partialTicks;
        final double bulletY = position.y + motion.y * partialTicks;
        final double bulletZ = position.z + motion.z * partialTicks;
        // TODO: Use muzzle flash location of entity render as the render position for muzzle flash start
        final Vec3 motionVec = new Vec3(motion.x, motion.y, motion.z);
        float length = (float) motionVec.length();

        if (Minecraft.getInstance().player.is(entity)) {
            if (mc.player.getLookAngle().y > 0.945) // max 1.0
                length *= 0.25;
            else if (mc.player.getLookAngle().y > 0.385)
                matrixStack.translate(0, -0.115f * mc.player.getLookAngle().y, 0);
        }
        if (ShootingHandler.get().isShooting() && Minecraft.getInstance().player.is(entity)
                && bulletTrail.getAge() < 1) {
            matrixStack.translate(bulletX - (view.x()), bulletY - view.y() - 0.145f,
                    (bulletZ - view.z()));
            if (AimingHandler.get().isAiming()) {
                matrixStack.translate(0, -0.685f, 0);
            }
        } else {
            matrixStack.translate(bulletX - view.x(), bulletY - view.y() - 0.125f,
                    bulletZ - view.z());

        }
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(bulletTrail.getYaw()));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-bulletTrail.getPitch() + 90.105f));

        float trailLength = (float) ((length / 3f) * bulletTrail.getTrailLengthMultiplier());
        final float red = (float) (bulletTrail.getTrailColor() >> 16 & 255) / 255.0F;
        final float green = (float) (bulletTrail.getTrailColor() >> 8 & 255) / 255.0F;
        final float blue = (float) (bulletTrail.getTrailColor() & 255) / 255.0F;
        final float alpha = Config.CLIENT.display.bulletTrailOpacity.get().floatValue();

        // Prevents the trail length from being longer than the distance to shooter
        final Entity shooter = bulletTrail.getShooter();
        if (shooter != null && Minecraft.getInstance().player.is(shooter)) {
            if (AimingHandler.get().getNormalisedAdsProgress() > 0.4) {
                trailLength =
                        (float) Math.min(trailLength + 0.6f, shooter.getEyePosition(partialTicks)
                                .distanceTo(new Vec3(bulletX, bulletY, bulletZ)) / 1.175f);
            } else
                trailLength =
                        (float) Math.min(trailLength + 0.6f, shooter.getEyePosition(partialTicks)
                                .distanceTo(new Vec3(bulletX, bulletY, bulletZ)) / 1.0975f); // 1.1125f
            // TODO: Add another value per trail to help give a maximum to player eyes distance
        }

        final Matrix4f matrix4f = matrixStack.last().pose();
        final MultiBufferSource.BufferSource renderTypeBuffer = mc.renderBuffers().bufferSource();

        /*
         * if(bulletTrail.isTrailVisible())
         * {
         */
        if (bulletTrail.getAge() < 1 && Minecraft.getInstance().player.is(entity)
                && !AimingHandler.get().isAiming()) {
            final RenderType bulletType = GunRenderType.getBulletTrail();
            final VertexConsumer builder = renderTypeBuffer.getBuffer(bulletType);

            // all 0.2f works
            // 0.6f static
            float posSize = 0.1f;
            posSize *= bulletTrail.getSize() * 10;

            builder.vertex(matrix4f, 0, trailLength / 1.325f, 0).color(red, green, blue, alpha)
                    .uv2(15728880).endVertex();
            // builder.pos(matrix4f, -posSize, 0, 0).color(red, green, blue, alpha).lightmap(15728880)
            //         .endVertex();
            builder.vertex(matrix4f, 0, 0, -posSize).color(red, green, blue, alpha).uv2(15728880)
                    .endVertex();

            matrixStack.scale(1.5F, 1.5F, 1.825F);
            matrixStack.translate(GunRenderingHandler.get().sizeZ / 17.25f,
                    -GunRenderingHandler.get().sizeZ / 2, 0);
            // matrixStack.translate(GunRenderingHandler.get().sizeZ / 2,
            //         GunRenderingHandler.get().sizeZ / 2, 0);
            matrixStack.translate(GunRenderingHandler.get().displayX,
                    GunRenderingHandler.get().displayY, GunRenderingHandler.get().displayZ);
            // Make customizable?
            matrixStack.translate(0, 0, GunRenderingHandler.get().adjustedTrailZ - 0.02);// 1.15f);

            builder.vertex(matrix4f, 0, -trailLength / 1.325f, 0).color(red, green, blue, alpha)
                    .uv2(15728880).endVertex();
            // builder.pos(matrix4f, posSize, 0, 0).color(red, green, blue, alpha).lightmap(15728880)
            //         .endVertex();
            builder.vertex(matrix4f, 0, 0, posSize).color(red, green, blue, alpha).uv2(15728880)
                    .endVertex();
            Minecraft.getInstance().renderBuffers().bufferSource().endBatch(bulletType);
        } else {
            final RenderType bulletType = GunRenderType.getBulletTrail();
            final VertexConsumer builder = renderTypeBuffer.getBuffer(bulletType);
            builder.vertex(matrix4f, 0, 0, 0.225F).color(red, green, blue, alpha).uv2(15728880)
                    .endVertex();
            builder.vertex(matrix4f, 0, 0, -0.225F).color(red, green, blue, alpha).uv2(15728880)
                    .endVertex();
            builder.vertex(matrix4f, 0, trailLength * 1.15f, 0).color(red, green, blue, alpha)
                    .uv2(15728880).endVertex();
            builder.vertex(matrix4f, 0, -trailLength * 1.15f, 0).color(red, green, blue, alpha)
                    .uv2(15728880).endVertex();
            builder.vertex(matrix4f, 0, 0, -0.225F).color(red, green, blue, alpha).uv2(15728880)
                    .endVertex();
            builder.vertex(matrix4f, 0, 0, 0.225F).color(red, green, blue, alpha).uv2(15728880)
                    .endVertex();
            builder.vertex(matrix4f, -0.225F, 0, 0).color(red, green, blue, alpha).uv2(15728880)
                    .endVertex();
            builder.vertex(matrix4f, 0.225F, 0, 0).color(red, green, blue, alpha).uv2(15728880)
                    .endVertex();
            builder.vertex(matrix4f, 0, trailLength * 1.15f, 0).color(red, green, blue, alpha)
                    .uv2(15728880).endVertex();
            builder.vertex(matrix4f, 0, -trailLength * 1.15f, 0).color(red, green, blue, alpha)
                    .uv2(15728880).endVertex();
            Minecraft.getInstance().renderBuffers().bufferSource().endBatch(bulletType);
        }
        if (!bulletTrail.getItem().isEmpty() && !bulletTrail.isTrailVisible()) {
            matrixStack.mulPose(Vector3f.YP
                    .rotationDegrees((bulletTrail.getAge() + partialTicks) * (float) 50));
            matrixStack.scale(0.25F, 0.25F, 0.25F);

            final int combinedLight =
                    LevelRenderer.getLightColor(entity.level, new BlockPos(entity.position()));
            final ItemStack stack = bulletTrail.getItem();
            RenderUtil.renderModel(stack, ItemTransforms.TransformType.NONE, matrixStack,
                    renderTypeBuffer, combinedLight, OverlayTexture.NO_OVERLAY, null, null);
        }

        matrixStack.popPose();
    }
}
