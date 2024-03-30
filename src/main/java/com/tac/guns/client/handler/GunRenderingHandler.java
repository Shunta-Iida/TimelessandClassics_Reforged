package com.tac.guns.client.handler;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.tac.guns.Config;
import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.client.GunRenderType;
import com.tac.guns.client.event.PlayerModelEvent;
import com.tac.guns.client.event.RenderItemEvent;
import com.tac.guns.client.handler.command.GunEditor;
import com.tac.guns.client.render.IHeldAnimation;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PistalAnimationController;
import com.tac.guns.client.render.model.IOverrideModel;
import com.tac.guns.client.render.model.OverrideModelManager;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.network.ServerPlayHandler;
import com.tac.guns.common.tooling.CommandsHandler;
import com.tac.guns.event.GunFireEvent;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.attachment.ScopeItem;
import com.tac.guns.item.grenade.GrenadeItem;
import com.tac.guns.item.gun.GunItem;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessagePlayerShake;
import com.tac.guns.util.GunModifierHelper;
import com.tac.guns.util.IDLNBTUtil;
import com.tac.guns.util.OptifineHelper;
import com.tac.guns.util.math.MathUtil;
import com.tac.guns.util.math.OneDimensionalPerlinNoise;
import com.tac.guns.util.math.SecondOrderDynamics;
import com.tac.guns.weapon.Gun;
import com.tac.guns.weapon.attachment.IAttachment;
import com.tac.guns.weapon.attachment.IBarrel;
import com.tac.guns.weapon.attachment.impl.Barrel;
import com.tac.guns.weapon.attachment.impl.Scope;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.NetworkDirection;

public class GunRenderingHandler {
    private static GunRenderingHandler instance;
    private final SecondOrderDynamics recoilDynamics =
            new SecondOrderDynamics(0.5f, 0.6f, 2.65f, 0);
    private final SecondOrderDynamics swayYawDynamics =
            new SecondOrderDynamics(0.4f, 0.5f, 3.25f, 0);
    private final SecondOrderDynamics swayPitchDynamics =
            new SecondOrderDynamics(0.3f, 0.4f, 3.5f, 0);
    private final SecondOrderDynamics aimingDynamics =
            new SecondOrderDynamics(0.45f, 0.8f, 1.2f, 0);
    // Standard Sprint Dynamics
    private final SecondOrderDynamics sprintDynamics =
            new SecondOrderDynamics(0.22f, 0.7f, 0.6f, 0);
    private final SecondOrderDynamics bobbingDynamics =
            new SecondOrderDynamics(0.22f, 0.7f, 0.6f, 1);
    private final SecondOrderDynamics speedUpDynamics =
            new SecondOrderDynamics(0.22f, 0.7f, 0.6f, 0);
    private final SecondOrderDynamics delaySwayDynamics =
            new SecondOrderDynamics(0.75f, 0.9f, 1.4f, 0);
    private final SecondOrderDynamics sprintDynamicsZ =
            new SecondOrderDynamics(0.22f, 0.8f, 0.5f, 0);
    private final SecondOrderDynamics jumpingDynamics =
            new SecondOrderDynamics(0.28f, 1f, 0.65f, 0);
    // High Speed Sprint Dynamics
    private final SecondOrderDynamics sprintDynamicsHSS =
            new SecondOrderDynamics(0.3f, 0.6f, 0.6f, 0);
    /*
     * private final SecondOrderDynamics sprintDynamicsZHSS = new
     * SecondOrderDynamics(0.15f,0.7f,
     * -2.25f, new Vector3f(0,0,0));
     */
    private final SecondOrderDynamics sprintDynamicsZHSS =
            new SecondOrderDynamics(0.27f, 0.75f, 0.5f, 0);
    public final SecondOrderDynamics sprintDynamicsHSSLeftHand =
            new SecondOrderDynamics(0.38f, 1f, 0f, 0);

    public static GunRenderingHandler get() {
        if (GunRenderingHandler.instance == null) {
            GunRenderingHandler.instance = new GunRenderingHandler();
        }
        return GunRenderingHandler.instance;
    }

    public static final ResourceLocation MUZZLE_FLASH_TEXTURE =
            new ResourceLocation(Reference.MOD_ID, "textures/effect/muzzle_flash.png");
    public static final ResourceLocation MUZZLE_SMOKE_TEXTURE =
            new ResourceLocation(Reference.MOD_ID, "textures/effect/muzzle_smoke.png");

    private final Random random = new Random();
    private final Set<Integer> entityIdForMuzzleFlash = new HashSet<>();
    private final Set<Integer> entityIdForDrawnMuzzleFlash = new HashSet<>();
    private final Queue<ShellInAir> shells = new ArrayDeque<>();
    private final Map<Integer, Float> entityIdToRandomValue = new HashMap<>();

    public int sprintTransition;
    private int prevSprintTransition;
    private int sprintCooldown;
    private int restingTimer;
    private final int restingTimerUpper = 5;

    private float offhandTranslate;
    private float prevOffhandTranslate;

    public float muzzleExtraOnEnch = 0f;

    private Field equippedProgressMainHandField;
    private Field prevEquippedProgressMainHandField;

    private float startingDistance = 0f;
    private float speedUpDistanceFrom = 0f;

    public float speedUpDistance = 0.6f;

    public float speedUpProgress = 0f;

    private final float additionalSwayProgress = 0f;

    private GunRenderingHandler() {
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        this.updateSprinting();
        this.updateMuzzleFlash();
        this.updateShellCasing();
        this.updateOffhandTranslate();
        if (Minecraft.getInstance().player == null)
            return;
        if (Minecraft.getInstance().player.input.down) {
            if (this.backwardTicker < 8)
                this.backwardTicker++;
        } else {
            if (this.backwardTicker > 0)
                this.backwardTicker--;
        }
        if (CommandsHandler.get().getCatCurrentIndex() == 1
                && GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.flash) {
            final ItemStack stack = Minecraft.getInstance().player.getMainHandItem();
            if (!(stack.getItem() instanceof final GunItem gun))
                return;
            final Gun modifiedGun = gun.getModifiedGun(stack.getTag());
            if (modifiedGun.getDisplay().getFlash() != null
                    || GunEditor.get().getMode() == GunEditor.TaCWeaponDevModes.flash) {
                this.showMuzzleFlashForPlayer(Minecraft.getInstance().player.getId());
            }
        }
    }

    private long fireTime = System.currentTimeMillis();

    @SubscribeEvent
    public void onGunFired(final GunFireEvent event) {
        if (event.isClient())
            this.fireTime = System.currentTimeMillis();
    }

    @SubscribeEvent
    public void onCameraSetup(final EntityViewRenderEvent.CameraSetup event) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null)
            return;
        if (!(mc.player.getMainHandItem().getItem() instanceof GunItem)
                || mc.player.getMainHandItem().getTag() == null)
            return;
        if ((Config.COMMON.gameplay.forceCameraShakeOnFire.get()
                || Config.CLIENT.display.cameraShakeOnFire.get())
                && IDLNBTUtil.getInt(mc.player.getMainHandItem(), "CurrentFireMode") != 0) {
            final float cameraShakeDuration = 0.06f; // TODO: Force to be adjusted per shot later in 0.3.4-0.3.5, customizable
            // per gun
            final long alphaTime = System.currentTimeMillis() - this.fireTime;
            final float progress = (alphaTime < cameraShakeDuration * 1000
                    ? 1 - alphaTime / (cameraShakeDuration * 1000f)
                    : 0);
            // apply camera shake when firing.
            final float alpha = (progress * (Math.random() - 0.5 < 0 ? -1 : 1) * 0.9f);
            event.setPitch(event.getPitch() - Math.abs(alpha));
            event.setRoll(event.getRoll() + alpha * 0.5f);
        }
    }

    @SubscribeEvent
    public void onFovModifying(final EntityViewRenderEvent.FieldOfView event) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isAlive() || mc.player.isSpectator())
            return;
        if (!(mc.player.getMainHandItem().getItem() instanceof GunItem)
                || mc.player.getMainHandItem().getTag() == null)
            return;

        if ((Config.COMMON.gameplay.forceCameraShakeOnFire.get()
                || Config.CLIENT.display.cameraShakeOnFire.get())
                && mc.player.getMainHandItem().getTag().getInt("CurrentFireMode") != 0) {
            final float cameraShakeDuration = 0.06f * (AimingHandler.get().isAiming() ? 1.5f : 1f);
            final long alphaTime = System.currentTimeMillis() - this.fireTime;
            final float progress = (alphaTime < cameraShakeDuration * 1000
                    ? 1 - alphaTime / (cameraShakeDuration * 1000f)
                    : 0);
            event.setFOV(event.getFOV() + progress * 0.5f);
        }
    }

    private void updateSprinting() {
        this.prevSprintTransition = this.sprintTransition;

        final Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && (mc.player.isSprinting() && !mc.player.isCrouching())
                && !SyncedEntityData.instance().get(mc.player, ModSyncedDataKeys.SHOOTING)
                && !SyncedEntityData.instance().get(mc.player, ModSyncedDataKeys.RELOADING)
                && !AimingHandler.get().isAiming() && this.sprintCooldown == 0) {
            if (Minecraft.getInstance().player != null) {
                final ItemStack heldItem = Minecraft.getInstance().player.getMainHandItem();
                if (heldItem.getItem() instanceof GunItem) {
                    final GunItem modifiedGun = (GunItem) heldItem.getItem();
                    final GunAnimationController controller =
                            GunAnimationController.fromItem(modifiedGun);
                    if (this.sprintTransition < 5) {
                        if (controller == null || (modifiedGun.getGun().getGeneral().getGripType()
                                .getHeldAnimation().canApplySprintingAnimation()
                                && !controller.isAnimationRunning())) {
                            this.sprintTransition++;
                        }
                    }
                    if (controller != null && controller.isAnimationRunning()) {
                        if (this.sprintTransition > 0) {
                            this.sprintTransition--;
                        }
                    }
                }
            }
        } else if (this.sprintTransition > 0) {
            this.sprintTransition = 0;
        }

        if (this.sprintCooldown > 0) {
            this.sprintCooldown--;
        }
        if (this.sprintTransition == 0) {
            if (this.restingTimer < this.restingTimerUpper) {
                this.restingTimer++;
            }
        } else {
            this.restingTimer = 0;
        }
    }

    private void updateMuzzleFlash() {
        this.entityIdForMuzzleFlash.removeAll(this.entityIdForDrawnMuzzleFlash);
        this.entityIdToRandomValue.keySet().removeAll(this.entityIdForDrawnMuzzleFlash);
        this.entityIdForDrawnMuzzleFlash.clear();
        this.entityIdForDrawnMuzzleFlash.addAll(this.entityIdForMuzzleFlash);
    }

    private void updateShellCasing() {
        while (this.shells.peek() != null && this.shells.peek().livingTick <= 0)
            this.shells.poll();
        for (final ShellInAir shell : this.shells) {
            shell.livingTick--;
            shell.velocity.add(
                    new Vector3f(-0.02f * shell.velocity.x(), -0.25f, -0.02f * shell.velocity.y())); // simulating
                                                                                                                        // gravity
            shell.preDisplacement.setX(shell.displacement.x());
            shell.preDisplacement.setY(shell.displacement.y());
            shell.preDisplacement.setZ(shell.displacement.z());
            shell.preRotation.setX(shell.rotation.x());
            shell.preRotation.setY(shell.rotation.y());
            shell.preRotation.setZ(shell.rotation.z());
            shell.displacement.add(shell.velocity);
            shell.rotation.add(shell.angularVelocity);
        }
    }

    // TODO: Review for mistracking
    private void updateOffhandTranslate() {
        this.prevOffhandTranslate = this.offhandTranslate;
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;

        boolean down = false;
        final ItemStack heldItem = mc.player.getMainHandItem();
        if (heldItem.getItem() instanceof GunItem) {
            final Gun modifiedGun =
                    ((GunItem) heldItem.getItem()).getModifiedGun(heldItem.getTag());
            down = !modifiedGun.getGeneral().getGripType().getHeldAnimation()
                    .canRenderOffhandItem();
        }

        final float direction = down ? -0.3F : 0.3F;
        this.offhandTranslate = Mth.clamp(this.offhandTranslate + direction, 0.0F, 1.0F);
    }

    @SubscribeEvent
    public void onGunFire(final GunFireEvent.Post event) {
        if (!event.isClient())
            return;
        if (Minecraft.getInstance().player == null)
            return;
        final Item item = event.getStack().getItem();
        this.sprintTransition = 0;
        this.speedUpDistanceFrom = Minecraft.getInstance().player.walkDist;
        this.sprintCooldown = 8;

        final ItemStack heldItem = event.getStack();
        final GunItem gunItem = (GunItem) heldItem.getItem();
        final Gun modifiedGun = gunItem.getModifiedGun(heldItem.getTag());
        if (modifiedGun.getDisplay().getFlash() != null) {
            this.showMuzzleFlashForPlayer(Minecraft.getInstance().player.getId());
        }
        final Gun.ShellCasing shellCasing = modifiedGun.getDisplay().getShellCasing();
        if (shellCasing != null) {
            final float card = 1f - this.random.nextFloat() * 2f;
            final float vard = 1.2f - this.random.nextFloat() * 0.4f;
            this.shells.add(new ShellInAir(
                    new Vector3f((float) shellCasing.getXOffset(), (float) shellCasing.getYOffset(),
                            (float) shellCasing.getZOffset()),
                    new Vector3f(shellCasing.getVelocityX() + shellCasing.getRVelocityX() * card,
                            shellCasing.getVelocityY() + shellCasing.getRVelocityY() * card,
                            shellCasing.getVelocityZ() + shellCasing.getRVelocityZ() * card),
                    new Vector3f(vard * shellCasing.getAVelocityX(),
                            vard * shellCasing.getAVelocityY(), vard * shellCasing.getAVelocityZ()),
                    modifiedGun.getDisplay().getShellCasing().getTickLife()));
        }
    }

    @SubscribeEvent
    public void onGunFirePre(final GunFireEvent.Pre event) {
        if (this.sprintTransition == 0) {
            if (this.restingTimer < this.restingTimerUpper) {
                event.setCanceled(true);
            }
        } else {
            event.setCanceled(true);
        }
    }

    public void showMuzzleFlashForPlayer(final int entityId) {
        this.entityIdForMuzzleFlash.add(entityId);
        this.entityIdToRandomValue.put(entityId, this.random.nextFloat());
    }

    /*
     * @SubscribeEvent
     * public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) // TEST
     * FROM CGM
     * {
     *//*
                                         * if(!Config.CLIENT.experimental.immersiveCamera.get())
                                         * return;
                                         *//*
                                                                             * 
                                                                             * Minecraft mc = Minecraft.getInstance();
                                                                             * if(mc.player == null)
                                                                             * return;
                                                                             * 
                                                                             * ItemStack heldItem = mc.player.getHeldItemMainhand();
                                                                             * float targetAngle = heldItem.getItem() instanceof GunItem ?
                                                                             * mc.player.movementInput.moveStrafe * 1F: 0F;
                                                                             * this.immersiveRoll = MathHelper.approach(this.immersiveRoll, targetAngle,
                                                                             * 0.4F);
                                                                             * event.setRoll(-this.immersiveRoll);
                                                                             * }
                                                                             */

    public float immersiveWeaponRoll;

    public float walkingDistance1;
    public float walkingDistance = 0.0f; // Abandoned. It is always 0.0f.
    public float walkingCrouch;
    public float walkingCameraYaw;
    public float zoomProgressInv;

    public double xOffset = 0.0;
    public double yOffset = 0.0;
    public double zOffset = 0.0;

    public double opticMovement;
    public double slideKeep;

    public float translateX = 0f;
    public float translateY = 0f;
    public float translateZ = 0f;

    private double fix = 0;
    /*
     * @SubscribeEvent(priority = EventPriority.LOWEST)
     * public void onRenderPreOverlay(RenderHandEvent event)
     * {
     * if(event.getItemStack().getItem() instanceof GunItem)
     * {
     * ShadersFramebufferMixin.tac_getMappedShaderImage2();
     * }
     * }
     */

    @SubscribeEvent
    public void onRenderOverlay(final RenderHandEvent event) {
        final GunAnimationController controller =
                GunAnimationController.fromItem(event.getItemStack().getItem());
        final boolean isAnimated = controller != null;
        final PoseStack matrixStack = event.getPoseStack();

        final boolean right = Minecraft.getInstance().options.mainHand == HumanoidArm.RIGHT
                ? event.getHand() == InteractionHand.MAIN_HAND
                : event.getHand() == InteractionHand.OFF_HAND;
        final ItemStack heldItem = event.getItemStack();

        if (!(heldItem.getItem() instanceof GunItem)) {
            return;
        }

        /* Cancel it because we are doing our own custom render */
        event.setCanceled(true);

        ItemStack overrideModel = ItemStack.EMPTY;
        if (heldItem.getTag() != null) {
            if (heldItem.getTag().contains("Model", Tag.TAG_COMPOUND)) {
                overrideModel = ItemStack.of(heldItem.getTag().getCompound("Model"));
            }
        }
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        final GunItem gunItem = (GunItem) heldItem.getItem();
        if (mc.options.bobView && mc.getCameraEntity() instanceof Player) {
            final Player playerentity = (Player) mc.getCameraEntity();
            final float deltaDistanceWalked = playerentity.walkDist - playerentity.walkDistO;
            float distanceWalked =
                    -(playerentity.walkDist + deltaDistanceWalked * event.getPartialTicks());
            final float cameraYaw =
                    Mth.lerp(event.getPartialTicks(), playerentity.oBob, playerentity.bob);

            /*
             * Reverses the original bobbing rotations and translations so it can be
             * controlled
             */
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(
                    -(Math.abs(Mth.cos(distanceWalked * (float) Math.PI - 0.2F) * cameraYaw)
                            * 5.0F)));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(
                    -(Mth.sin(distanceWalked * (float) Math.PI) * cameraYaw * 3.0F)));
            matrixStack.translate(-(Mth.sin(distanceWalked * (float) Math.PI) * cameraYaw * 0.5F),
                    -(-Math.abs(Mth.cos(distanceWalked * (float) Math.PI) * cameraYaw)), 0.0D);

            // Walking bobbing
            boolean aimed = false;
            /* The new controlled bobbing */
            if (AimingHandler.get().isAiming())
                aimed = true;

            // double invertZoomProgress = aimed ? 0.0575 : 0.468; //0.135 :
            // 0.44;//0.94;//aimed ? 1.0 - AimingHandler.get().getNormalisedRepairProgress()
            // : ;
            final double invertZoomProgress =
                    aimed ? (Gun.getScope(heldItem) != null ? 0.0575 : 0.0725) : 0.468;
            final float crouch = mc.player.isCrouching() ? 148f : 1f;

            if (playerentity.walkDist == playerentity.walkDistO && !playerentity.isShiftKeyDown())
                this.startingDistance = playerentity.walkDist;
            if (!mc.player.input.hasForwardImpulse()) {
                this.speedUpDistanceFrom = playerentity.walkDist;
                this.speedUpProgress -= (new Date().getTime() - this.prevTime) / 150f;
                if (this.speedUpProgress < 0)
                    this.speedUpProgress = 0;
            } else {
                this.speedUpProgress =
                        (-distanceWalked - this.speedUpDistanceFrom < this.speedUpDistance
                                ? (-distanceWalked - this.speedUpDistanceFrom)
                                        / this.speedUpDistance
                                : 1);
            }
            distanceWalked = distanceWalked + this.startingDistance;
            this.walkingDistance1 = distanceWalked;
            this.walkingCrouch = crouch;
            this.walkingCameraYaw = cameraYaw;
            this.zoomProgressInv = (float) invertZoomProgress;

            // matrixStack.translate((double)
            // (Math.asin(-MathHelper.sin(distanceWalked*crouch * (float) Math.PI) *
            // cameraYaw * 0.5F)) * invertZoomProgress, ((double)
            // (Math.asin((-Math.abs(-MathHelper.cos(distanceWalked*crouch * (float)
            // Math.PI) * cameraYaw))) * invertZoomProgress)) * 1.140, 0.0D);// * 1.140,
            // 0.0D);
            this.applyBobbingTransforms(matrixStack, false);
            this.applyJumpingTransforms(matrixStack, event.getPartialTicks());
            // TODO: Implement config switch, it's not a required mechanic. it's just fun
            this.applyNoiseMovementTransform(matrixStack);

            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(
                    (Mth.sin(distanceWalked * crouch * (float) Math.PI) * cameraYaw * 3.0F)
                            * (float) invertZoomProgress));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(
                    (Math.abs(Mth.cos(distanceWalked * crouch * (float) Math.PI - 0.2F) * cameraYaw)
                            * 5.0F) * (float) invertZoomProgress));

            // Weapon movement clanting
            final float rollingForceCrouch = mc.player.isCrouching() ? 4f : 1f;
            final float rollingForceAim = AimingHandler.get().isAiming() ? 0.425f : 1f;
            /*
             * Pretty much from CGM, was going to build something very similar for 0.3,
             * movement update comes early I guess,
             * all credit to Mr.Crayfish who developed this weapon roll code for CGM,
             * all I added was scaling for other game actions and adjusted rolling values
             */
            final float targetAngle = heldItem.getItem() instanceof GunItem
                    ? mc.player.input.leftImpulse * (6.65F * rollingForceCrouch * rollingForceAim)
                    : 0F;
            this.immersiveWeaponRoll = Mth.approach(this.immersiveWeaponRoll, targetAngle, 0.335F);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(this.immersiveWeaponRoll));
        }

        if (event.getHand() == InteractionHand.OFF_HAND) {
            if (heldItem.getItem() instanceof GunItem) {
                event.setCanceled(true);
                return;
            }

            final float offhand = 1.0F - Mth.lerp(event.getPartialTicks(),
                    this.prevOffhandTranslate, this.offhandTranslate);
            matrixStack.translate(0, offhand * -0.6F, 0);

            final Player player = Minecraft.getInstance().player;
            if (player != null && player.getMainHandItem().getItem() instanceof GunItem) {
                final Gun modifiedGun = ((GunItem) player.getMainHandItem().getItem())
                        .getModifiedGun(player.getMainHandItem().getTag());
                if (!modifiedGun.getGeneral().getGripType().getHeldAnimation()
                        .canRenderOffhandItem()) {
                    return;
                }
            }

            /* Makes the off hand item move out of view */
            matrixStack.translate(0, -2 * AimingHandler.get().getNormalisedAdsProgress(), 0);
        }

        final LocalPlayer entity = mc.player;
        final BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(
                overrideModel.isEmpty() ? heldItem : overrideModel, entity.level, entity,
                entity == null ? 0 : entity.getId());
        final float scaleX = model.getTransforms().firstPersonRightHand.scale.x();
        final float scaleY = model.getTransforms().firstPersonRightHand.scale.y();
        final float scaleZ = model.getTransforms().firstPersonRightHand.scale.z();
        this.translateX = model.getTransforms().firstPersonRightHand.translation.x();
        this.translateY = model.getTransforms().firstPersonRightHand.translation.y();
        this.translateZ = model.getTransforms().firstPersonRightHand.translation.z();

        matrixStack.pushPose();
        final Gun modifiedGun = gunItem.getModifiedGun(heldItem.getTag());

        if (modifiedGun.canAimDownSight()) {
            if (event.getHand() == InteractionHand.MAIN_HAND) {
                this.xOffset = 0.0;
                this.yOffset = 0.0;
                this.zOffset = 0.0;
                final Scope scope = Gun.getScope(heldItem);
                final boolean isScopeOffsetType =
                        Config.CLIENT.display.gameplayEnchancedScopeOffset.get();
                final boolean isScopeRenderType = Config.CLIENT.display.scopeDoubleRender.get();
                /*
                 * Creates the required offsets to position the scope into the middle of the
                 * screen.
                 */
                if (modifiedGun.canAttachType(IAttachment.Type.SCOPE) && scope != null) {
                    double viewFinderOffset = isScopeOffsetType || OptifineHelper.isShadersEnabled()
                            ? scope.getViewFinderOffsetSpecial()
                            : scope.getViewFinderOffset();
                    if (scope.getAdditionalZoom().getZoomMultiple() > 1)
                        viewFinderOffset = isScopeRenderType
                                ? (isScopeOffsetType || OptifineHelper.isShadersEnabled()
                                        ? scope.getViewFinderOffsetSpecial()
                                        : scope.getViewFinderOffset())
                                : (isScopeOffsetType || OptifineHelper.isShadersEnabled()
                                        ? scope.getViewFinderOffsetSpecialDR()
                                        : scope.getViewFinderOffsetDR()); // switches between either, but either must be
                                                                                                                                                                                                                                                                                                                                              // populated

                    // if (OptifineHelper.isShadersEnabled()) viewFinderOffset *= 0.735;
                    // if (isScopeRenderType) viewFinderOffset *= 0.735;
                    try {
                        final Gun.ScaledPositioned scaledPos =
                                modifiedGun.getModules().getAttachments().getScope();
                        this.xOffset = -this.translateX
                                + (modifiedGun.getModules().getZoom().getXOffset() * 0.0625)
                                + -scaledPos.getXOffset() * 0.0625 * scaleX;
                        this.yOffset = -this.translateY
                                + (8 - scaledPos.getYOffset()) * 0.0625 * scaleY
                                - scope.getCenterOffset() * scaleY * 0.0625 * scaledPos.getScale();
                        this.zOffset = Config.CLIENT.display.sight1xRealisticPosition.get()
                                && scope.getAdditionalZoom().getZoomMultiple() == 1
                                        ? -this.translateZ
                                                + modifiedGun.getModules().getZoom().getZOffset()
                                                        * 0.0625 * scaleZ
                                        : -this.translateZ
                                                - scaledPos.getZOffset() * 0.0625 * scaleZ + 0.72
                                                - viewFinderOffset * scaleZ * scaledPos.getScale();
                    } catch (final NullPointerException e) {
                        GunMod.LOGGER.info("GunRenderingHandler NPE @509");
                    }

                    if (Objects.equals(scope.getTagName(), "qmk152"))
                        this.fix = -0.05;
                    else if (Objects.equals(scope.getTagName(), "elcan14x"))
                        this.fix = -0.06;
                    else if (Objects.equals(scope.getTagName(), "acog4x"))
                        this.fix = -0.03;
                    else if (Objects.equals(scope.getTagName(), "vlpvo6"))
                        this.fix = -0.05;
                    else if (Objects.equals(scope.getTagName(), "gener8x"))
                        this.fix = -0.06;
                    else if (Objects.equals(scope.getTagName(), "aimpoint2")
                            || Objects.equals(scope.getTagName(), "eotechn")
                            || Objects.equals(scope.getTagName(), "vortex1")
                            || Objects.equals(scope.getTagName(), "eotechshort"))
                        this.fix = -0.02;
                    else
                        this.fix = 0;
                } else if (modifiedGun.canAttachType(IAttachment.Type.PISTOL_SCOPE)
                        && scope != null) {
                    double viewFinderOffset = isScopeOffsetType || isScopeRenderType
                            ? scope.getViewFinderOffsetSpecial()
                            : scope.getViewFinderOffset(); // switches between either, but either must be populated
                    if (OptifineHelper.isShadersEnabled())
                        viewFinderOffset *= 0.735;
                    final Gun.ScaledPositioned scaledPos =
                            modifiedGun.getModules().getAttachments().getPistolScope();

                    this.xOffset = -this.translateX
                            + (modifiedGun.getModules().getZoom().getXOffset() * 0.0625)
                            + -scaledPos.getXOffset() * 0.0625 * scaleX;
                    this.yOffset = -this.translateY + (8 - scaledPos.getYOffset()) * 0.0625 * scaleY
                            - scope.getCenterOffset() * scaleY * 0.0625 * scaledPos.getScale();
                    this.zOffset = Config.CLIENT.display.sight1xRealisticPosition.get()
                            && scope.getAdditionalZoom().getZoomMultiple() == 1
                                    ? -this.translateZ
                                            + modifiedGun.getModules().getZoom().getZOffset()
                                                    * 0.0625 * scaleZ
                                    : -this.translateZ - scaledPos.getZOffset() * 0.0625 * scaleZ
                                            + 0.72
                                            - viewFinderOffset * scaleZ * scaledPos.getScale();

                    this.fix = 0;
                } else if (modifiedGun.getModules().getZoom() != null) {
                    this.xOffset = -this.translateX
                            + modifiedGun.getModules().getZoom().getXOffset() * 0.0625 * scaleX;
                    this.yOffset = -this.translateY
                            + (8 - modifiedGun.getModules().getZoom().getYOffset() - 0.2) * 0.0625
                                    * scaleY;
                    this.zOffset = -this.translateZ
                            + modifiedGun.getModules().getZoom().getZOffset() * 0.0625 * scaleZ;

                    this.fix = 0;
                }

                /*
                 * Controls the direction of the following translations, changes depending on
                 * the main hand.
                 */
                final float side = right ? 1.0F : -1.0F;
                // double transition = 1.0 - Math.pow(1.0 -
                // AimingHandler.get().getNormalisedRepairProgress(), 2);

                final double transition =
                        (float) AimingHandler.get().getLerpAdsProgress(event.getPartialTicks());

                final float result = this.aimingDynamics.update(0.05f, (float) transition);
                /* Reverses the original first person translations */
                // matrixStack.translate(-0.56 * side * transition, 0.52 * transition, 0);
                matrixStack.translate(
                        this.xOffset * side * result - 0.56 * side * result, this.yOffset * result
                                + 0.52 * result + 0.033 - Math.abs(0.5 - result) * 0.066 + this.fix,
                        this.zOffset * result);
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) (5 * (1 - transition))));
                /*
                 * Reverses the first person translations of the item in order to position it in
                 * the center of the screen
                 */
                // matrixStack.translate(xOffset * side * transition, yOffset * transition,
                // zOffset * transition);
                matrixStack.translate(0, (0.015 - this.fix) * transition, 0);

                if (Config.COMMON.gameplay.realisticAimedBreathing.get()) {
                    /* Apply scope jitter */
                    double scopeJitterOffset = 0.8;
                    if (scope == null)
                        scopeJitterOffset *=
                                modifiedGun.getModules().getZoom().getStabilityOffset();
                    else
                        scopeJitterOffset *= scope.getStabilityOffset();
                    if (entity.isCrouching())
                        scopeJitterOffset *= 0.30;
                    if (entity.isSprinting() && !entity.isCrouching())
                        scopeJitterOffset *= 4;
                    if (entity.getDeltaMovement().x() != 0.0 || entity.getDeltaMovement().y() != 0.0
                            || entity.getDeltaMovement().z() != 0.0)
                        scopeJitterOffset *= 6.5;

                    final double yOffsetRatio = ScopeJitterHandler.getInstance().getYOffsetRatio()
                            * (0.0125 * 0.75 * scopeJitterOffset);
                    final double xOffsetRatio = ScopeJitterHandler.getInstance().getXOffsetRatio()
                            * (0.0085 * 0.875 * scopeJitterOffset);
                    Objects.requireNonNull(Minecraft.getInstance().player)
                            .setXRot((float) (Objects.requireNonNull(Minecraft.getInstance().player)
                                    .getXRot() + yOffsetRatio));
                    Objects.requireNonNull(Minecraft.getInstance().player)
                            .setXRot((float) (Objects.requireNonNull(Minecraft.getInstance().player)
                                    .getYRot() + xOffsetRatio));
                }
            }
        }

        // Set Delayed Sway config options

        GunRenderingHandler.maxRotationDegree =
                Config.CLIENT.display.weaponDelayedSwayMaximum.get().floatValue(); // 3.95f or 4.5f
        GunRenderingHandler.delayedSwayMultiplier =
                Config.CLIENT.display.weaponDelayedSwayMultiplier.get().floatValue(); // Lower = a
        // more
        // delayed
        // sway
        GunRenderingHandler.YDIR =
                Config.CLIENT.display.weaponDelayedSwayDirection.get() ? Vector3f.YN : Vector3f.YP;

        this.applyDelayedSwayTransforms(matrixStack, entity, event.getPartialTicks());
        final float equipProgress = this.getEquipProgress(event.getPartialTicks());
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(equipProgress * -50F));

        final HumanoidArm hand = right ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
        Objects.requireNonNull(entity);
        int blockLight = entity.isOnFire() ? 14
                : entity.level.getBrightness(LightLayer.BLOCK,
                        new BlockPos(entity.getEyePosition(event.getPartialTicks())));
        if (blockLight > 14)
            blockLight = 14;
        final int packedLight =
                LightTexture.pack(blockLight, entity.level.getBrightness(LightLayer.SKY,
                        new BlockPos(entity.getEyePosition(event.getPartialTicks()))));

        /*
         * Renders the reload arm. Will only render if actually reloading. This is
         * applied before
         * any recoil or reload rotations as the animations would be borked if applied
         * after.
         */
        // this.renderReloadArm(matrixStack, event.getBuffers(), event.getLight(),
        // modifiedGun, heldItem, hand);

        /* Translate the item position based on the hand side */
        final int offset = right ? 1 : -1;
        matrixStack.translate(0.56 * offset, -0.52, -0.72);

        this.applySprintingTransforms(heldItem, hand, matrixStack, event.getPartialTicks());
        /* Applies recoil and reload rotations */
        this.applyRecoilTransforms(matrixStack, heldItem, modifiedGun);
        if (!isAnimated)
            this.applyReloadTransforms(matrixStack, hand, event.getPartialTicks(), heldItem);

        /* Renders the first persons arms from the grip type of the weapon */
        matrixStack.pushPose();
        final IHeldAnimation pose = modifiedGun.getGeneral().getGripType().getHeldAnimation();
        if (pose != null) {
            if (!isAnimated)
                matrixStack.translate(-0.56, 0.52, 0.72);
            if (!isAnimated)
                pose.renderFirstPersonArms(Minecraft.getInstance().player, hand, heldItem,
                        matrixStack, event.getMultiBufferSource(), event.getPackedLight(),
                        event.getPartialTicks());
        }
        matrixStack.popPose();

        /* Renders the weapon */
        final ItemTransforms.TransformType transformType =
                right ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                        : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
        this.renderWeapon(Minecraft.getInstance().player, heldItem, transformType,
                event.getPoseStack(), event.getMultiBufferSource(), packedLight,
                event.getPartialTicks());
        matrixStack.popPose();
    }

    private static float maxRotationDegree; // 3.95f or 4.5f
    private static float delayedSwayMultiplier; // Lower = a more delayed sway
    private static Vector3f YDIR;

    private void applyDelayedSwayTransforms(final PoseStack stack, final LocalPlayer player,
            final float partialTicks) {
        this.applyDelayedSwayTransforms(stack, player, partialTicks, 1f);
    }

    private boolean checkIsLongRangeScope(final ItemStack itemStack) {
        final Gun gun = ((GunItem) itemStack.getItem()).getModifiedGun(itemStack.getTag());
        final IAttachment.Type type = IAttachment.Type.SCOPE;
        if (gun.canAttachType(type)) {
            final ItemStack attachmentStack = Gun.getAttachment(type, itemStack);
            if (!attachmentStack.isEmpty()) {

            }
        }
        return false;
    }

    public void applyDelayedSwayTransforms(final PoseStack stack, final LocalPlayer player,
            final float partialTicks, final float percentage) {
        if (Config.CLIENT.display.weaponDelayedSway.get())
            if (player != null) {
                final float f4 = Mth.lerp(partialTicks, player.yBobO, player.yBob);
                float degree =
                        this.delaySwayDynamics.update(0, (player.getViewYRot(partialTicks) - f4)
                                * GunRenderingHandler.delayedSwayMultiplier);
                if (Math.abs(degree) > GunRenderingHandler.maxRotationDegree)
                    degree = degree / Math.abs(degree) * GunRenderingHandler.maxRotationDegree;
                degree *= 1 / Math.pow(MathUtil.fovToMagnification(this.currentHandLayerFov,
                        this.originHandLayerFov), 2);
                degree *= percentage;

                if ((Config.CLIENT.display.weaponDelayedSwayYNOptical.get()
                        && Gun.getScope(player.getMainHandItem()) != null)
                        || GunRenderingHandler.YDIR.equals(Vector3f.YN)) {
                    stack.translate(this.translateX, this.translateY, this.translateZ);
                    stack.mulPose(GunRenderingHandler.YDIR.rotationDegrees(degree));
                    stack.mulPose(Vector3f.ZP.rotationDegrees(degree * 1.5f
                            * (float) (1f - AimingHandler.get().getNormalisedAdsProgress())));
                    stack.translate(-this.translateX, -this.translateY, -this.translateZ);
                } else {
                    stack.translate(-this.translateX, -this.translateY, -this.translateZ);
                    stack.mulPose(GunRenderingHandler.YDIR.rotationDegrees(degree));
                    stack.mulPose(Vector3f.ZP.rotationDegrees(degree * 1.5f
                            * (float) (1f - AimingHandler.get().getNormalisedAdsProgress())));
                    stack.translate(this.translateX, this.translateY, this.translateZ);
                }
            }
    }

    // Sprinting Offset Transition, the same transition aggregate used for all
    // running anims,
    // made public for adjusting hands within animator instances
    public float sOT = 0.0f;
    public float wSpeed = 0.0f;

    private void applySprintingTransforms(final ItemStack gun, final HumanoidArm hand,
            final PoseStack matrixStack, final float partialTicks) {
        final GunItem modifiedGun = (GunItem) gun.getItem();
        final GunAnimationController controller = GunAnimationController.fromItem(gun.getItem());
        final float draw = (controller == null
                || !controller.isAnimationRunning(GunAnimationController.AnimationLabel.DRAW) ? 1
                        : 0);
        final float leftHanded = hand == HumanoidArm.LEFT ? -1 : 1;
        this.sOT = (this.prevSprintTransition
                + (this.sprintTransition - this.prevSprintTransition) * partialTicks) / 5F;
        // TODO: Speed of the held weapon, make a static method? it's not that useful
        // but will be cleaner
        this.wSpeed = ServerPlayHandler.calceldGunWeightSpeed(modifiedGun.getGun(), gun);
        // Light weight animation, used for SMGS and light rifles like the hk416
        if (this.wSpeed > 0.094f) {
            // Translation
            final float result = this.sprintDynamicsHSS.update(0.05f, this.sOT) * draw;

            // Rotating to the left a bit
            final float result2 = this.sprintDynamicsZHSS.update(0.05f, this.sOT) * draw;

            matrixStack.translate(0.215 * leftHanded * result, 0.07f * result,
                    -30F * leftHanded * result / 170);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(60f * result2));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-25f * result2));
        }
        // Default
        else {
            // transition = (float) Math.sin((transition * Math.PI) / 2);
            final float result = this.sprintDynamics.update(0.05f, this.sOT) * draw;
            final float result2 = this.sprintDynamicsZ.update(0.05f, this.sOT) * draw;
            // matrixStack.translate(-0.25 * leftHanded * transition, -0.1 * transition, 0);
            matrixStack.translate(-0.25 * leftHanded * result,
                    -0.1 * result - 0.1 + Math.abs(0.5 - result) * 0.2, 0);
            // matrixStack.rotate(Vector3f.YP.rotationDegrees(45F * leftHanded *
            // transition));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(28F * leftHanded * result));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(15F * result2));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(20f * result2));
        }
    }

    private void applyReloadTransforms(final PoseStack matrixStack, final HumanoidArm hand,
            final float partialTicks, final ItemStack modifiedGun) {
        /*
         * float reloadProgress = ReloadHandler.get().getRepairProgress(partialTicks,
         * stack);
         * matrixStack.translate(0, 0.35 * reloadProgress, 0);
         * matrixStack.translate(0, 0, -0.1 * reloadProgress);
         * matrixStack.rotate(Vector3f.XP.rotationDegrees(45F * reloadProgress));
         */

        final float reloadProgress =
                ReloadHandler.get().getReloadProgress(partialTicks, modifiedGun);

        if (reloadProgress > 0) {
            final float leftHanded = hand == HumanoidArm.LEFT ? -1 : 1;

            matrixStack.translate(-0.25 * leftHanded, -0.1, 0);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(45F * leftHanded));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(-25F));
        }
    }

    public float kickReduction = 0;
    public float recoilReduction = 0;
    public double kick = 0;
    public float recoilLift = 0;
    public float newSwayYawAmount = 0;
    public float weaponsHorizontalAngle = 0;
    public float newSwayPitch = 0;

    public float newSwayYaw = 0;

    public float newSwayYawPitch = 0;
    public float newSwayYawYaw = 0;

    private void applyRecoilTransforms(final PoseStack matrixStack, final ItemStack item,
            final Gun gun) {
        final Minecraft mc = Minecraft.getInstance();
        double kickReduce = 1;
        double recoilNormal = RecoilHandler.get().getGunRecoilNormal();
        if (Gun.hasAttachmentEquipped(item, gun, IAttachment.Type.SCOPE)
                || Gun.hasAttachmentEquipped(item, gun, IAttachment.Type.PISTOL_SCOPE)) {
            recoilNormal -= recoilNormal * (0.25 * AimingHandler.get().getNormalisedAdsProgress());
            kickReduce = gun.getModules().getZoom().getFovModifier();
            if (kickReduce > 1)
                kickReduce = 1;
            if (kickReduce < 0)
                kickReduce = 0;
        }
        this.kickReduction = 1.0F - GunModifierHelper.getKickReduction(item);
        this.recoilReduction = 1.0F - GunModifierHelper.getRecoilModifier(item);
        this.kick = gun.getGeneral().getRecoilKick() * 0.0625 * recoilNormal
                * RecoilHandler.get().getAdsRecoilReduction(gun);
        // this.recoilLift = ((float) (gun.getGeneral().getRecoilAngle() * recoilNormal)
        // * (float) RecoilHandler.get().getAdsRecoilReduction(gun));
        this.recoilLift = (float) ((float) (RecoilHandler.get().getGunRecoilAngle() * recoilNormal)
                * (float) RecoilHandler.get().getAdsRecoilReduction(gun) * kickReduce);
        this.newSwayYawAmount =
                ((float) (2F + 1F * (1.0 - AimingHandler.get().getNormalisedAdsProgress())));// * 1.5f;
        this.newSwayYawPitch = ((float) ((RecoilHandler.get().lastRandPitch * this.newSwayYawAmount
                - this.newSwayYawAmount / 2F) * recoilNormal)) / 2;
        this.newSwayYawYaw = ((float) ((RecoilHandler.get().lastRandYaw * this.newSwayYawAmount
                - this.newSwayYawAmount / 2F) * recoilNormal)) / 2;
        float kickTiming = 0.11f;
        if (IDLNBTUtil.getInt(item, "CurrentFireMode") == 1) {
            this.newSwayYawAmount *= 0.5;
            this.recoilLift *= 0.925;
            kickTiming += 0.06f; // Soften the kick a little, helps with tracking and feel
        }
        if (mc.player != null && mc.player.isCrouching()) {
            this.recoilLift *= 0.875;
        }
        this.weaponsHorizontalAngle =
                ((float) (RecoilHandler.get().getGunHorizontalRecoilAngle() * recoilNormal)
                        * (float) RecoilHandler.get().getAdsRecoilReduction(gun)) * 1.25f;
        float newKick =
                this.recoilDynamics.update(kickTiming, (float) this.kick * this.kickReduction);

        // reduce for scope sight
        final double magnification =
                MathUtil.fovToMagnification(this.currentHandLayerFov, this.originHandLayerFov);
        newKick *= 1 / Math.pow(magnification, 0.3);

        matrixStack.translate(0, 0, newKick);
        matrixStack.translate(0, 0.05 * newKick, 0.35 * newKick);

        // TODO: have T/Time updatable per gun, weapons like the pistols, especially the
        // deagle benifits from forcing accurate shots and awaiting front sight reset,
        // unlike the m4 which should have little effect
        this.newSwayYaw = this.swayYawDynamics.update(0.12f,
                this.newSwayYawYaw * this.recoilReduction * this.weaponsHorizontalAngle);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(this.newSwayPitch * 0.2875f));
        this.newSwayPitch = this.swayPitchDynamics.update(0.21f,
                this.newSwayYawPitch * this.recoilReduction * this.recoilLift);
        matrixStack.mulPose(Vector3f.ZN.rotationDegrees(this.newSwayPitch * 0.215f));

        // matrixStack.rotate(Vector3f.ZP.rotationDegrees(newSwayYaw *
        // recoilReduction)); // seems to be interesting to increase the force of

        if (gun.getGeneral().getWeaponRecoilOffset() != 0)
            matrixStack
                    .mulPose(Vector3f.XP.rotationDegrees(this.recoilLift * this.recoilReduction));
        matrixStack.translate(0, -0.05 * newKick, -0.35 * newKick);
    }

    private int backwardTicker = 0;

    public void applyBobbingTransforms(final PoseStack matrixStack, final boolean convert) {
        matrixStack.translate(0, 0, 0.25);
        final float amplifier = this.bobbingDynamics.update(0.05f,
                (float) ((this.sprintTransition / 2f + 1) * (1 - AimingHandler.get()
                        .getNormalisedAdsProgress()
                        * 0.75) /** (RecoilHandler.get().getRecoilProgress() == 0 ? 1 : 0)) */
                ));
        final float speedUp = this.speedUpDynamics.update(0.05f, this.speedUpProgress
                * (1 - this.sOT) * (float) (1 - AimingHandler.get().getNormalisedAdsProgress()));
        final float delta =
                -Mth.sin(this.walkingDistance1 * (float) Math.PI) * this.walkingCameraYaw * 0.5f
                        * (convert ? -0.5f : 1) * amplifier * (8 - this.backwardTicker) / 8;
        final float delta2 = -Mth.sin(this.walkingDistance1 * (float) Math.PI * 2f)
                * this.walkingCameraYaw * 0.5f * (convert ? -0.35f : 1) * amplifier
                * (12 - this.backwardTicker) / 12;
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(
                35f * delta * (float) (1 - AimingHandler.get().getNormalisedAdsProgress())));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-3f * speedUp));
        matrixStack.translate(0, 0, -0.25 + 0.07 * speedUp);
        matrixStack.translate(0.45 * delta, 0.25 * delta2, 0);
        if (this.wSpeed > 0.094f)
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(delta * 5f * this.sprintTransition));
        else
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(delta * 5f));
    }

    public void applyBobbingTransforms(final PoseStack matrixStack, final boolean convert,
            float effectMultiplier) {
        if (effectMultiplier == 0)
            effectMultiplier = 1f;
        matrixStack.translate(0, 0, 0.25);
        final float amplifier = this.bobbingDynamics.update(0.05f,
                (float) ((this.sprintTransition / 2f + 1)
                        * (1 - AimingHandler.get().getNormalisedAdsProgress() * 0.75)
                        * (RecoilHandler.get().getRecoilProgress() == 0 ? 1 : 0)));
        final float speedUp = this.speedUpDynamics.update(0.05f, this.speedUpProgress
                * (1 - this.sOT) * (float) (1 - AimingHandler.get().getNormalisedAdsProgress()));
        final float delta =
                -Mth.sin(this.walkingDistance1 * (float) Math.PI) * this.walkingCameraYaw * 0.5f
                        * (convert ? -0.5f : 1) * amplifier * (8 - this.backwardTicker) / 8;
        final float delta2 = -Mth.sin(this.walkingDistance1 * (float) Math.PI * 2f)
                * this.walkingCameraYaw * 0.5f * (convert ? -0.35f : 1) * amplifier
                * (12 - this.backwardTicker) / 12;
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(35f * effectMultiplier * delta
                * (float) (1 - AimingHandler.get().getNormalisedAdsProgress())));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-3f * effectMultiplier * speedUp));
        matrixStack.translate(0, 0, -0.25 + 0.07 * effectMultiplier * speedUp);
        matrixStack.translate(0.45 * effectMultiplier * delta, 0.25 * effectMultiplier * delta2, 0);
        if (this.wSpeed > 0.094f)
            matrixStack.mulPose(Vector3f.XP
                    .rotationDegrees(delta * effectMultiplier * 5f * this.sprintTransition));
        else
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(delta * effectMultiplier * 5f));
    }

    private float velocity = 0;
    private float acceleration = 0;
    private float stepLength = 0;
    private long prevTime = new Date().getTime();

    public void applyJumpingTransforms(final PoseStack matrixStack, final float partialTicks) {
        if (Minecraft.getInstance().player == null)
            return;
        final double posY = Mth.lerp(partialTicks, Minecraft.getInstance().player.yOld,
                Minecraft.getInstance().player.getY());
        final float newVelocity =
                (float) (posY - Minecraft.getInstance().player.yOld) / partialTicks;
        final float newAcceleration = newVelocity - this.velocity;
        final Date date = new Date();
        if (Math.abs(this.acceleration) < Math.abs(newAcceleration)
                && Math.abs(newAcceleration) > 0.05) {
            this.acceleration = newAcceleration;
            this.stepLength = this.acceleration / 250f;
        }
        final long partialTime = date.getTime() - this.prevTime;
        if (this.acceleration > 0) {
            this.acceleration -= partialTime * this.stepLength;
            if (this.acceleration < 0)
                this.acceleration = 0;
        }
        if (this.acceleration < 0) {
            this.acceleration -= partialTime * this.stepLength;
            if (this.acceleration > 0)
                this.acceleration = 0;
        }
        final float maxMotion = 0.265f;
        float transition = -this.jumpingDynamics.update(0.05f,
                (Math.abs(this.acceleration) < maxMotion ? (this.acceleration / maxMotion) * 0.15f
                        : Math.abs(this.acceleration) / this.acceleration * 0.15f)
                        * (this.sprintTransition / 3f + 1)
                        * (1f - 0.7f * (float) AimingHandler.get().getNormalisedAdsProgress()));
        if (transition > 0)
            transition *= 0.8f;
        matrixStack.translate(0, transition, 0);
        this.velocity = newVelocity;
        this.prevTime = date.getTime();
    }

    public void applyJumpingTransforms(final PoseStack matrixStack, final float partialTicks,
            final float reverser) {
        if (Minecraft.getInstance().player == null)
            return;
        final double posY = Mth.lerp(partialTicks, Minecraft.getInstance().player.yOld,
                Minecraft.getInstance().player.getY());
        final float newVelocity =
                (float) (posY - Minecraft.getInstance().player.yOld) / partialTicks;
        final float newAcceleration = newVelocity - this.velocity;
        final Date date = new Date();
        if (Math.abs(this.acceleration) < Math.abs(newAcceleration)
                && Math.abs(newAcceleration) > 0.05) {
            this.acceleration = newAcceleration;
            this.stepLength = this.acceleration / 250f;
        }
        final long partialTime = date.getTime() - this.prevTime;
        if (this.acceleration > 0) {
            this.acceleration -= partialTime * this.stepLength;
            if (this.acceleration < 0)
                this.acceleration = 0;
        }
        if (this.acceleration < 0) {
            this.acceleration -= partialTime * this.stepLength;
            if (this.acceleration > 0)
                this.acceleration = 0;
        }
        final float maxMotion = 0.265f;
        float transition = -this.jumpingDynamics.update(0.05f,
                (Math.abs(this.acceleration) < maxMotion ? (this.acceleration / maxMotion) * 0.15f
                        : Math.abs(this.acceleration) / this.acceleration * 0.15f)
                        * (this.sprintTransition / 3f + 1)
                        * (1f - 0.7f * (float) AimingHandler.get().getNormalisedAdsProgress()));
        if (transition > 0)
            transition *= 0.8f;
        if (reverser != 0)
            transition *= reverser;
        matrixStack.translate(0, transition, 0);
        this.velocity = newVelocity;
        this.prevTime = date.getTime();
    }

    // TODO: Update noises for breathing animation per new weapon held, aka give
    // weapons customization of their breathing, pistols for example should be
    // realatively unstable, along with lighter weapons
    private final OneDimensionalPerlinNoise noiseX =
            new OneDimensionalPerlinNoise(-0.0025f, 0.0025f, 1900);
    private final OneDimensionalPerlinNoise noiseY =
            new OneDimensionalPerlinNoise(-0.003f, 0.003f, 1900);
    {
        this.noiseY.setReverse(true);
    }

    private final OneDimensionalPerlinNoise aimed_noiseX =
            new OneDimensionalPerlinNoise(-0.000075f, 0.000075f, 1750);
    private final OneDimensionalPerlinNoise aimed_noiseY =
            new OneDimensionalPerlinNoise(-0.00165f, 0.00165f, 1750);
    {
        this.noiseY.setReverse(true);
    }

    private final OneDimensionalPerlinNoise additionNoiseY =
            new OneDimensionalPerlinNoise(-0.00095f, 0.00095f, 1900);

    private final OneDimensionalPerlinNoise noiseRotationY =
            new OneDimensionalPerlinNoise(-0.675f, 0.675f, 1900);
    private final OneDimensionalPerlinNoise aimed_noiseRotationY =
            new OneDimensionalPerlinNoise(-0.675f, 0.675f, 1900);

    public void applyNoiseMovementTransform(final PoseStack matrixStack) {
        // matrixStack.translate(noiseX.getValue()* (1 -
        // AimingHandler.get().getNormalisedRepairProgress()), (noiseY.getValue() +
        // additionNoiseY.getValue()) * (1 -
        // AimingHandler.get().getNormalisedRepairProgress()), 0);
        if (AimingHandler.get().getNormalisedAdsProgress() == 1) {
            matrixStack.translate(this.aimed_noiseX.getValue() / 2,
                    this.aimed_noiseY.getValue() + this.additionNoiseY.getValue(), 0);
            matrixStack.mulPose(
                    Vector3f.YP.rotationDegrees(this.aimed_noiseRotationY.getValue() * 0.405f));
            matrixStack.mulPose(Vector3f.ZP
                    .rotationDegrees((float) (this.aimed_noiseRotationY.getValue() * 0.925)));
        } else {
            matrixStack.translate(this.noiseX.getValue(),
                    this.noiseY.getValue() + this.additionNoiseY.getValue(), 0);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(this.noiseRotationY.getValue()));
        }

    }

    public void applyNoiseMovementTransform(final PoseStack matrixStack, final float reverser) {
        // matrixStack.translate(noiseX.getValue()* (1 -
        // AimingHandler.get().getNormalisedRepairProgress()), (noiseY.getValue() +
        // additionNoiseY.getValue()) * (1 -
        // AimingHandler.get().getNormalisedRepairProgress()), 0);
        if (AimingHandler.get().getNormalisedAdsProgress() == 1) {
            matrixStack.translate(this.aimed_noiseX.getValue() * (reverser * 2),
                    (this.aimed_noiseY.getValue() + this.additionNoiseY.getValue()) * reverser, 0);
            matrixStack.mulPose(
                    Vector3f.YP.rotationDegrees(this.aimed_noiseRotationY.getValue() * reverser));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(
                    (float) (this.aimed_noiseRotationY.getValue() * 0.85 * reverser)));
        } else {
            matrixStack.translate(this.noiseX.getValue() * (-reverser),
                    (this.noiseY.getValue() + this.additionNoiseY.getValue()) * reverser, 0);
            matrixStack.mulPose(
                    Vector3f.YP.rotationDegrees(this.noiseRotationY.getValue() * reverser));
        }

    }

    // Author:
    // https://github.com/Charles445/DamageTilt/blob/1.16/src/main/java/com/charles445/damagetilt/MessageUpdateAttackYaw.java,
    // continued by Timeless devs
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onKnockback(final LivingKnockBackEvent event) {
        if (event.getEntityLiving() instanceof Player) {
            final Player player = (Player) event.getEntityLiving();
            if (player.level.isClientSide)
                return;
            if (!(player.getMainHandItem().getItem() instanceof GunItem)
                    && !Config.CLIENT.display.cameraShakeOptionGlobal.get())
                return;

            // Server Side
            if (player instanceof final ServerPlayer serverPlayer) {
                if (serverPlayer.connection == null)
                    return;
                if (serverPlayer.connection.getConnection() == null)
                    return;
                PacketHandler.getPlayChannel().sendTo(new MessagePlayerShake(player),
                        serverPlayer.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }

    @SubscribeEvent
    public void onTick(final TickEvent.RenderTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START))
            return;

        final Minecraft mc = Minecraft.getInstance();
        if (!mc.isWindowActive())
            return;

        final Player player = mc.player;
        if (player == null)
            return;

        if (Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON)
            return;

        final ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (heldItem.isEmpty())
            return;

        if (player.isUsingItem() && player.getUsedItemHand() == InteractionHand.MAIN_HAND
                && heldItem.getItem() instanceof GrenadeItem) {
            if (!((GrenadeItem) heldItem.getItem()).canCook())
                return;

            final int duration = player.getTicksUsingItem();
            if (duration >= 10) {
                final float cookTime = 1.0F - ((float) (duration - 10)
                        / (float) (player.getUseItem().getUseDuration() - 10));
                if (cookTime > 0.0F) {
                    final double scale = 3;
                    final Window window = mc.getWindow();
                    final int i = (int) ((window.getGuiScaledHeight() / 2 - 7 - 60) / scale);
                    final int j =
                            (int) Math.ceil((window.getGuiScaledWidth() / 2 - 8 * scale) / scale);

                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);

                    final PoseStack stack = RenderSystem.getModelViewStack();
                    stack.pushPose();
                    {
                        stack.scale((float) scale, (float) scale, (float) scale);
                        RenderSystem.applyModelViewMatrix();
                        final int progress = (int) Math.ceil((cookTime) * 17.0F) - 1;
                        final PoseStack matrixStack = new PoseStack();
                        Screen.blit(matrixStack, j, i, 36, 94, 16, 4, 256, 256);
                        Screen.blit(matrixStack, j, i, 52, 94, progress, 4, 256, 256);
                    }
                    stack.popPose();
                    RenderSystem.applyModelViewMatrix();

                    RenderSystem.disableBlend();
                }
            }
            return;
        }
    }

    @SubscribeEvent
    public void onRenderHeldItem(final RenderItemEvent.Held.Pre event) {
        final InteractionHand hand = Minecraft.getInstance().options.mainHand == HumanoidArm.RIGHT
                ? event.getHandSide() == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND
                        : InteractionHand.OFF_HAND
                : event.getHandSide() == HumanoidArm.LEFT ? InteractionHand.MAIN_HAND
                        : InteractionHand.OFF_HAND;
        final LivingEntity entity = event.getEntity();
        final ItemStack heldItem = entity.getItemInHand(hand);

        if (hand == InteractionHand.OFF_HAND) {
            if (heldItem.getItem() instanceof GunItem) {
                event.setCanceled(true);
                return;
            }

            if (entity.getMainHandItem().getItem() instanceof GunItem) {
                final Gun modifiedGun = ((GunItem) entity.getMainHandItem().getItem())
                        .getModifiedGun(entity.getMainHandItem().getTag());
                if (!modifiedGun.getGeneral().getGripType().getHeldAnimation()
                        .canRenderOffhandItem()) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        if (heldItem.getItem() instanceof GunItem) {
            event.setCanceled(true);

            if (heldItem.getTag() != null) {
                final CompoundTag compound = heldItem.getTag();
                if (compound.contains("Scale", Tag.TAG_FLOAT)) {
                    final float scale = compound.getFloat("Scale");
                    event.getPoseStack().scale(scale, scale, scale);
                }
            }

            final Gun gun = ((GunItem) heldItem.getItem()).getModifiedGun(heldItem.getTag());
            if (entity instanceof Player) {
                gun.getGeneral().getGripType().getHeldAnimation().applyHeldItemTransforms(
                        (Player) entity, hand,
                        AimingHandler.get().getAimProgress((Player) entity,
                                event.getPartialTicks()),
                        event.getPoseStack(), event.getBufferSource());
            }
            this.renderWeapon(entity, heldItem, event.getTransformType(), event.getPoseStack(),
                    event.getBufferSource(), event.getLight(), event.getPartialTicks());
        }
    }

    @SubscribeEvent
    public void onSetupAngles(final PlayerModelEvent.SetupAngles.Post event) {
        // Dirty hack to reject first person arms
        if (event.getAgeInTicks() == 0F) {
            event.getModelPlayer().rightArm.xRot = 0;
            event.getModelPlayer().rightArm.yRot = 0;
            event.getModelPlayer().rightArm.zRot = 0;
            event.getModelPlayer().leftArm.xRot = 0;
            event.getModelPlayer().leftArm.yRot = 0;
            event.getModelPlayer().leftArm.zRot = 0;
            return;
        }

        final Player player = event.getPlayer();
        final ItemStack heldItem = player.getMainHandItem();
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof GunItem) {
            final PlayerModel<?> model = event.getModelPlayer();
            final Gun gun = ((GunItem) heldItem.getItem()).getModifiedGun(heldItem.getTag());
            gun.getGeneral().getGripType().getHeldAnimation().applyPlayerModelRotation(player,
                    model, InteractionHand.MAIN_HAND, AimingHandler.get().getAimProgress(
                            (Player) event.getEntity(), Minecraft.getInstance().getFrameTime()));
            GunRenderingHandler.copyModelAngles(model.rightArm, model.rightSleeve);
            GunRenderingHandler.copyModelAngles(model.leftArm, model.leftSleeve);
        }
    }

    private static void copyModelAngles(final ModelPart source, final ModelPart dest) {
        dest.xRot = source.xRot;
        dest.yRot = source.yRot;
        dest.zRot = source.zRot;
    }

    @SubscribeEvent
    public void onRenderPlayer(final RenderPlayerEvent.Pre event) {
        final Player player = event.getPlayer();
        final ItemStack heldItem = player.getMainHandItem();
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof GunItem) {
            final Gun gun = ((GunItem) heldItem.getItem()).getModifiedGun(heldItem.getTag());
            gun.getGeneral().getGripType().getHeldAnimation().applyPlayerPreRender(player,
                    InteractionHand.MAIN_HAND,
                    AimingHandler.get().getAimProgress((Player) event.getEntity(),
                            event.getPartialTick()),
                    event.getPoseStack(), event.getMultiBufferSource());
        }
    }

    @SubscribeEvent
    public void onModelRender(final PlayerModelEvent.Render.Pre event) {
        final Player player = event.getPlayer();
        final ItemStack offHandStack = player.getOffhandItem();
        if (offHandStack.getItem() instanceof GunItem) {
            switch (player.getMainArm().getOpposite()) {
                case LEFT:
                    event.getModelPlayer().leftArmPose = HumanoidModel.ArmPose.EMPTY;
                    break;
                case RIGHT:
                    event.getModelPlayer().rightArmPose = HumanoidModel.ArmPose.EMPTY;
                    break;
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(final PlayerModelEvent.Render.Post event) {
        final PoseStack matrixStack = event.getPoseStack();
        final Player player = event.getPlayer();
        final ItemStack heldItem = player.getOffhandItem();
        // First Person Check for rendering offhand gun
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson())
            return;

        if (!heldItem.isEmpty() && heldItem.getItem() instanceof GunItem) {
            matrixStack.pushPose();
            final Gun gun = ((GunItem) heldItem.getItem()).getModifiedGun(heldItem.getTag());
            if (gun.getGeneral().getGripType().getHeldAnimation().applyOffhandTransforms(player,
                    event.getModelPlayer(), heldItem, matrixStack, event.getDeltaTicks())) {
                final MultiBufferSource buffer =
                        Minecraft.getInstance().renderBuffers().bufferSource();
                this.renderWeapon(player, heldItem, ItemTransforms.TransformType.FIXED, matrixStack,
                        buffer, event.getLight(), event.getDeltaTicks());
            }
            matrixStack.popPose();
        }
    }

    @SubscribeEvent
    public void onRenderEntityItem(final RenderItemEvent.Entity.Pre event) {
        final Minecraft mc = Minecraft.getInstance();
        if (!event.getTransformType().equals(ItemTransforms.TransformType.GUI)) {
            event.setCanceled(this.renderWeapon(mc.player, event.getItem(),
                    event.getTransformType(), event.getPoseStack(), event.getBufferSource(),
                    event.getLight(), event.getPartialTicks()));
        }
    }

    @SubscribeEvent
    public void onRenderEntityItem(final RenderItemEvent.Gui.Pre event) {

        // MK47AnimationController x = MK47AnimationController.getInstance();
        // PlayerHandAnimation.render(x,event.getTransformType(),event.getPoseStack(),event.getRenderTypeBuffer(),event.getLight());
        if (!Config.CLIENT.quality.reducedQualityHotBar.get()/*
                                                              * && event.getTransformType().equals(ItemTransforms.
                                                              * TransformType.GUI)
                                                              */) {
            final Minecraft mc = Minecraft.getInstance();
            event.setCanceled(this.renderWeapon(mc.player, event.getItem(),
                    event.getTransformType(), event.getPoseStack(), event.getBufferSource(),
                    event.getLight(), event.getPartialTicks()));
        }
        // TODO: Enable some form of either player hand anim preloading, or on game load
        // segment for the held gun, since it seems 90%+ cases don't miss loading hand
        // animations
    }

    @SubscribeEvent
    public void onRenderItemFrame(final RenderItemEvent.ItemFrame.Pre event) {
        final Minecraft mc = Minecraft.getInstance();
        event.setCanceled(this.renderWeapon(mc.player, event.getItem(), event.getTransformType(),
                event.getPoseStack(), event.getBufferSource(), event.getLight(),
                event.getPartialTicks()));
    }

    public float aimingHandLayerFov = 6.41236f;
    public float originHandLayerFov = 70;
    public float currentHandLayerFov = 70;

    public boolean renderWeapon(final LivingEntity entity, final ItemStack stack,
            final ItemTransforms.TransformType transformType, final PoseStack matrixStack,
            final MultiBufferSource renderTypeBuffer, final int light, final float partialTicks) {
        if (stack.getItem() instanceof GunItem) {
            matrixStack.pushPose();

            ItemStack model = ItemStack.EMPTY;
            if (stack.getTag() != null) {
                if (stack.getTag().contains("Model", Tag.TAG_COMPOUND)) {
                    model = ItemStack.of(stack.getTag().getCompound("Model"));
                }
            }

            RenderUtil.applyTransformType(model.isEmpty() ? stack : model, matrixStack,
                    transformType, entity);

            if (ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND.equals(transformType)) {
                final Gun gun = ((GunItem) stack.getItem()).getModifiedGun(stack.getTag());
                final IAttachment.Type type = IAttachment.Type.SCOPE;
                if (gun.canAttachType(type)) {
                    final Scope scope = Gun.getScope(stack);
                    if (scope != null) {
                        final ItemStack attachmentStack = Gun.getAttachment(type, stack);
                        if (!attachmentStack.isEmpty()) {
                            final Gun.ScaledPositioned positioned = gun.getAttachmentPosition(type);
                            if (positioned != null) {
                                final double transition =
                                        AimingHandler.get().getLerpAdsProgress(partialTicks);
                                final double displayX = positioned.getXOffset() * 0.0625;
                                final double displayY = positioned.getYOffset() * 0.0625;
                                final double displayZ = positioned.getZOffset() * 0.0625;
                                this.currentHandLayerFov = Mth.lerp((float) transition,
                                        this.originHandLayerFov,
                                        scope.isNeedSqueeze() ? this.aimingHandLayerFov : 55f);
                                final float zScale = (float) Math
                                        .tan(this.currentHandLayerFov / 180 * Math.PI / 2)
                                        / (float) Math
                                                .tan(this.originHandLayerFov / 180 * Math.PI / 2);
                                matrixStack.translate(displayX, displayY, displayZ);
                                matrixStack.translate(0, -0.5, 0);
                                matrixStack.scale(1f, 1f, zScale);
                                matrixStack.translate(0, 0.5, 0);
                                matrixStack.translate(-displayX, -displayY, -displayZ);
                                matrixStack.translate(0, 0,
                                        (float) transition
                                                * scope.getAdditionalZoom().getZoomZTransition()
                                                * 0.0625 / zScale);
                            }
                        }
                    }
                }
            }
            this.renderGun(entity, transformType, model.isEmpty() ? stack : model, matrixStack,
                    renderTypeBuffer, light, partialTicks);
            this.renderAttachments(entity, transformType, stack, matrixStack, renderTypeBuffer,
                    light, partialTicks);
            this.renderMuzzleFlash(entity, matrixStack, renderTypeBuffer, stack, transformType);
            this.renderShellCasing(entity, matrixStack, stack, transformType, renderTypeBuffer,
                    light, partialTicks);
            matrixStack.popPose();
            return true;
        }
        return false;
    }

    public boolean renderScope(final LivingEntity entity, final ItemStack stack,
            final ItemTransforms.TransformType transformType, final PoseStack matrixStack,
            final MultiBufferSource renderTypeBuffer, final int light, final float partialTicks) {
        if (stack.getItem() instanceof ScopeItem) {
            matrixStack.pushPose();

            final ItemStack model = ItemStack.EMPTY;
            RenderUtil.applyTransformType(model.isEmpty() ? stack : model, matrixStack,
                    transformType, entity);
            this.renderGun(entity, transformType, model.isEmpty() ? stack : model, matrixStack,
                    renderTypeBuffer, light, partialTicks);// matrixStack, renderTypeBuffer, light, partialTicks);
            matrixStack.popPose();
            return true;
        }
        return false;
    }

    private void renderGun(final LivingEntity entity,
            final ItemTransforms.TransformType transformType, final ItemStack stack,
            final PoseStack matrixStack, final MultiBufferSource renderTypeBuffer, final int light,
            final float partialTicks) {
        if (OverrideModelManager.hasModel(stack)) {
            final IOverrideModel model = OverrideModelManager.getModel(stack);
            if (model != null) {

                // TODO: Only when needed
                if (OverrideModelManager.hasModel(stack)
                        && transformType.equals(ItemTransforms.TransformType.GUI)
                        && !Config.CLIENT.quality.reducedQualityHotBar.get()) {
                    matrixStack.pushPose();
                    matrixStack.mulPose(Vector3f.XP.rotationDegrees(25.0F));
                    matrixStack.mulPose(Vector3f.YP.rotationDegrees(-145.0F));
                    matrixStack.scale(0.55f, 0.55f, 0.55f);
                }
                model.render(partialTicks, transformType, stack, ItemStack.EMPTY, entity,
                        matrixStack, renderTypeBuffer, light, OverlayTexture.NO_OVERLAY);
            }
        } else {
            RenderUtil.renderModel(stack, matrixStack, renderTypeBuffer, light,
                    OverlayTexture.NO_OVERLAY, entity);
        }
        if (OverrideModelManager.hasModel(stack)
                && transformType.equals(ItemTransforms.TransformType.GUI)
                && !Config.CLIENT.quality.reducedQualityHotBar.get())
            matrixStack.popPose();
    }

    /*
     * private void renderColoredModel(LivingEntity entity,
     * ItemTransforms.TransformType transformType, IBakedModel model, MatrixStack
     * matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, float
     * partialTicks)
     * {
     * //if(stack.getItem() instanceof ITimelessAnimated)
     * RenderUtil.renderModel(stack, matrixStack, renderTypeBuffer, light,
     * OverlayTexture.NO_OVERLAY, entity);
     * IOverrideModel model = ModelOverrides.getModel(stack);
     * if (model != null) {
     * model.render(partialTicks, transformType, stack, ItemStack.EMPTY, entity,
     * matrixStack, renderTypeBuffer, light, OverlayTexture.NO_OVERLAY);
     * }
     * }
     */
    private void renderAttachments(final LivingEntity entity,
            final ItemTransforms.TransformType transformType, final ItemStack stack,
            final PoseStack matrixStack, final MultiBufferSource renderTypeBuffer, final int light,
            final float partialTicks) {
        if (stack.getItem() instanceof GunItem) {
            final Gun gun = ((GunItem) stack.getItem()).getModifiedGun(stack.getTag());
            final CompoundTag gunTag = stack.getOrCreateTag();
            final CompoundTag attachments = gunTag.getCompound("Attachments");
            for (final String tagKey : attachments.getAllKeys()) {
                final IAttachment.Type type = IAttachment.Type.byTagKey(tagKey);
                if (gun.canAttachType(type)) {
                    final ItemStack attachmentStack = Gun.getAttachment(type, stack);
                    if (!attachmentStack.isEmpty()) {
                        final Gun.ScaledPositioned positioned = gun.getAttachmentPosition(type);
                        if (positioned != null) {
                            final double displayX = positioned.getXOffset() * 0.0625;
                            final double displayY = positioned.getYOffset() * 0.0625;
                            final double displayZ = positioned.getZOffset() * 0.0625;
                            matrixStack.pushPose();
                            final GunAnimationController controller =
                                    GunAnimationController.fromItem(stack.getItem());
                            if (controller != null) {
                                if (type != null) {
                                    if (controller instanceof final PistalAnimationController pcontroller
                                            && gun.getModules().getAttachments()
                                                    .getPistolScope() != null
                                            && gun.getModules().getAttachments().getPistolScope()
                                                    .getDoOnSlideMovement()) {
                                        controller.applyTransform(stack,
                                                pcontroller.getSlideNodeIndex(), transformType,
                                                entity, matrixStack);
                                    } else
                                        controller.applyAttachmentsTransform(stack, transformType,
                                                entity, matrixStack);
                                } else
                                    controller.applyAttachmentsTransform(stack, transformType,
                                            entity, matrixStack);
                            }
                            matrixStack.translate(displayX, displayY, displayZ);
                            matrixStack.translate(0, -0.5, 0);
                            matrixStack.scale((float) positioned.getScale(),
                                    (float) positioned.getScale(), (float) positioned.getScale());

                            final IOverrideModel model =
                                    OverrideModelManager.getModel(attachmentStack);
                            if (model != null) {
                                model.render(partialTicks, transformType, attachmentStack, stack,
                                        entity, matrixStack, renderTypeBuffer, light,
                                        OverlayTexture.NO_OVERLAY);
                            } else {
                                RenderUtil.renderModel(attachmentStack, stack, matrixStack,
                                        renderTypeBuffer, light, OverlayTexture.NO_OVERLAY);
                            }
                            matrixStack.popPose();
                        }
                    }
                }
            }
        }
    }

    private void renderMuzzleFlash(final LivingEntity entity, final PoseStack matrixStack,
            final MultiBufferSource buffer, final ItemStack weapon,
            final ItemTransforms.TransformType transformType) {
        final Gun modifiedGun = ((GunItem) weapon.getItem()).getModifiedGun(weapon.getTag());
        if (modifiedGun.getDisplay().getFlash() == null) {
            return;
        }
        // if (modifiedGun.canAttachType(IAttachment.Type.BARREL) &&
        // GunModifierHelper.isSilencedFire(weapon)) return;

        if (transformType == ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                || transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND
                || transformType == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                || transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND) {
            if (this.entityIdForMuzzleFlash.contains(entity.getId())) {
                final float randomValue = this.entityIdToRandomValue.get(entity.getId());
                this.drawMuzzleFlash(weapon, modifiedGun, randomValue, randomValue >= 0.5F,
                        matrixStack, buffer);
            }
        }
    }

    public double displayX = 0;
    public double displayY = 0;
    public double displayZ = 0;
    public double sizeZ = 0;

    public double adjustedTrailZ = 0;

    private void drawMuzzleFlash(final ItemStack weapon, final Gun modifiedGun, final float random,
            final boolean flip, final PoseStack matrixStack, final MultiBufferSource buffer) {
        matrixStack.pushPose();

        final Gun.Positioned muzzleFlash = modifiedGun.getDisplay().getFlash();
        if (muzzleFlash == null)
            return;

        final double displayX = muzzleFlash.getXOffset() * 0.0625;
        final double displayY = muzzleFlash.getYOffset() * 0.0625;
        final double displayZ = (muzzleFlash.getZOffset() + this.muzzleExtraOnEnch) * 0.0625;

        if (GunRenderingHandler.get().muzzleExtraOnEnch != 0)
            this.muzzleExtraOnEnch = 0;

        matrixStack.translate(displayX, displayY, displayZ);
        matrixStack.translate(0, -0.5, 0);

        final ItemStack barrelStack = Gun.getAttachment(IAttachment.Type.BARREL, weapon);
        if (!barrelStack.isEmpty() && barrelStack.getItem() instanceof IBarrel) {
            final Barrel barrel = ((IBarrel) barrelStack.getItem()).getProperties();
            final Gun.ScaledPositioned positioned =
                    modifiedGun.getModules().getAttachments().getBarrel();
            if (positioned != null) {
                matrixStack.translate(0, 0, -barrel.getLength() * 0.0625 * positioned.getScale());
            }
        }

        matrixStack.scale(0.5F, 0.5F, 0.0F);

        final double partialSize = modifiedGun.getDisplay().getFlash().getSize() / 5.0;
        float size = (float) (modifiedGun.getDisplay().getFlash().getSize() - partialSize
                + partialSize * random);
        size = (float) GunModifierHelper.getMuzzleFlashSize(weapon, size);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(360F * random));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(flip ? 180F : 0F));
        matrixStack.translate(-size / 2, -size / 2, 0);

        final Matrix4f matrix = matrixStack.last().pose();
        final VertexConsumer builder = buffer.getBuffer(GunRenderType.getMuzzleFlash());
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableScissor();
        final Matrix3f normal = matrixStack.last().normal();
        builder.vertex(matrix, 0, 0, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 1.0F)
                .overlayCoords(0).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix, size, 0, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0, 1.0F)
                .overlayCoords(0).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix, size, size, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0, 0)
                .overlayCoords(0).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix, 0, size, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 0)
                .overlayCoords(0).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();

        /*
         * float smokeSize = (float) modifiedGun.getDisplay().getFlash().getSmokeSize();
         * builder = buffer.getBuffer(GunRenderType.getMuzzleSmoke());
         * matrixStack.translate(size / 2,size / 2,0);
         * matrixStack.translate(-smokeSize / 2, -smokeSize / 2, 0);
         * builder.pos(matrix, 0, 0, 0).color(1.0F, 1.0F, 1.0F, 1.0F).tex(1.0F,
         * 1.0F).lightmap(15728880).endVertex();
         * builder.pos(matrix, smokeSize, 0, 0).color(1.0F, 1.0F, 1.0F, 1.0F).tex(0,
         * 1.0F).lightmap(15728880).endVertex();
         * builder.pos(matrix, smokeSize, smokeSize, 0).color(1.0F, 1.0F, 1.0F,
         * 1.0F).tex(0, 0).lightmap(15728880).endVertex();
         * builder.pos(matrix, 0, smokeSize, 0).color(1.0F, 1.0F, 1.0F, 1.0F).tex(1.0F,
         * 0).lightmap(15728880).endVertex();
         */
        matrixStack.popPose();
    }

    private void renderShellCasing(final LivingEntity entity, final PoseStack matrixStack,
            final ItemStack weapon, final ItemTransforms.TransformType transformType,
            final MultiBufferSource renderTypeBuffer, final int light, final float partialTicks) {
        final Gun modifiedGun = ((GunItem) weapon.getItem()).getModifiedGun(weapon.getTag());
        if (modifiedGun.getDisplay().getShellCasing() == null) {
            return;
        }
        final ResourceLocation modelResource =
                modifiedGun.getDisplay().getShellCasing().getCasingModel();
        if (modelResource == null)
            return;
        final BakedModel caseModel =
                Minecraft.getInstance().getModelManager().getModel(modelResource);
        if (caseModel == Minecraft.getInstance().getModelManager().getMissingModel())
            return;

        if (transformType == ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                || transformType == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
            for (final ShellInAir shell : this.shells) {
                matrixStack.pushPose();

                final Vector3f pos = shell.origin.copy();
                final Vector3f dis1 = shell.preDisplacement.copy();
                final Vector3f dis2 = shell.displacement.copy();
                dis1.mul(1 - partialTicks);
                dis2.mul(partialTicks);
                pos.add(dis1);
                pos.add(dis2);

                final Vector3f rot = new Vector3f(0f, 0f, 0f);
                final Vector3f rot1 = shell.preRotation.copy();
                final Vector3f rot2 = shell.rotation.copy();
                rot1.mul(1 - partialTicks);
                rot2.mul(partialTicks);
                rot.add(rot1);
                rot.add(rot2);

                final float displayXv = pos.x() * 0.0625f;
                final float displayYv = pos.y() * 0.0625f;
                final float displayZv = pos.z() * 0.0625f;
                final float scale = (float) modifiedGun.getDisplay().getShellCasing().getScale();

                matrixStack.translate(displayXv, displayYv, displayZv);
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(rot.x()));
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(rot.y()));
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(rot.z()));
                matrixStack.scale(scale, scale, scale);

                RenderUtil.renderModel(caseModel, weapon, matrixStack, renderTypeBuffer, light,
                        OverlayTexture.NO_OVERLAY);

                matrixStack.popPose();
            }
        }
    }

    /**
     * A temporary hack to get the equip progress until Forge fixes the issue.
     *
     * @return
     */
    private float getEquipProgress(final float partialTicks) {
        if (this.equippedProgressMainHandField == null) {
            this.equippedProgressMainHandField =
                    ObfuscationReflectionHelper.findField(ItemInHandRenderer.class, "f_109302_");
            this.equippedProgressMainHandField.setAccessible(true);
        }
        if (this.prevEquippedProgressMainHandField == null) {
            this.prevEquippedProgressMainHandField =
                    ObfuscationReflectionHelper.findField(ItemInHandRenderer.class, "f_109303_");
            this.prevEquippedProgressMainHandField.setAccessible(true);
        }
        final ItemInHandRenderer firstPersonRenderer =
                Minecraft.getInstance().getItemInHandRenderer();
        try {
            final float equippedProgressMainHand =
                    (float) this.equippedProgressMainHandField.get(firstPersonRenderer);
            final float prevEquippedProgressMainHand =
                    (float) this.prevEquippedProgressMainHandField.get(firstPersonRenderer);
            return 1.0F - Mth.lerp(partialTicks, prevEquippedProgressMainHand,
                    equippedProgressMainHand);
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0.0F;
    }

    public static class ShellInAir {
        public int livingTick;
        public Vector3f preDisplacement = new Vector3f(0f, 0f, 0f);
        public Vector3f displacement = new Vector3f(0f, 0f, 0f);
        public Vector3f preRotation = new Vector3f(0f, 0f, 0f);
        public Vector3f rotation = new Vector3f(0f, 0f, 0f);
        public Vector3f origin;
        public Vector3f velocity;
        public Vector3f angularVelocity;

        public ShellInAir(@Nonnull final Vector3f origin, @Nonnull final Vector3f velocity,
                @Nonnull final Vector3f angularVelocity, final int life) {
            this.origin = origin.copy();
            this.velocity = velocity.copy();
            this.angularVelocity = angularVelocity.copy();
            this.livingTick = life;
        }
    }
}
