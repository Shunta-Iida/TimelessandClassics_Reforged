package com.tac.guns.client.network;

import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.tac.guns.Config;
import com.tac.guns.client.BulletTrail;
import com.tac.guns.client.CustomGunManager;
import com.tac.guns.client.CustomRigManager;
import com.tac.guns.client.audio.GunShotSound;
import com.tac.guns.client.handler.BulletTrailRenderingHandler;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.handler.HUDRenderingHandler;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.AnimationSoundManager;
import com.tac.guns.client.render.animation.module.AnimationSoundMeta;
import com.tac.guns.common.NetworkGunManager;
import com.tac.guns.common.NetworkRigManager;
import com.tac.guns.init.ModParticleTypes;
import com.tac.guns.network.message.MessageBlood;
import com.tac.guns.network.message.MessageBulletTrail;
import com.tac.guns.network.message.MessageGunSound;
import com.tac.guns.network.message.MessageProjectileHitBlock;
import com.tac.guns.network.message.MessageProjectileHitEntity;
import com.tac.guns.network.message.MessageRemoveProjectile;
import com.tac.guns.network.message.MessageStunGrenade;
import com.tac.guns.network.message.MessageUpdateGuns;
import com.tac.guns.network.message.MessageUpdateRigs;
import com.tac.guns.particles.BulletHoleData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class ClientPlayHandler {
    public static void handleMessageGunSound(final MessageGunSound message, final Context context) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;

        if (message.showMuzzleFlash()) {
            GunRenderingHandler.get().showMuzzleFlashForPlayer(message.getShooterId());
        }

        // if (message.getShooterId() == mc.player.getId()) {
        //     Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(message.getId(),
        //             SoundSource.PLAYERS,
        //             (float) (message.getVolume() * Config.CLIENT.sounds.weaponsVolume.get()),
        //             message.getPitch(), false, 0, SoundInstance.Attenuation.NONE, 0, 0, 0, true));
        // } else {
        // Minecraft.getInstance().getSoundManager()
        //         .play(new GunShotSound(message.getId(), SoundSource.PLAYERS, message.getX(),
        //                 message.getY(), message.getZ(), message.getVolume(), message.getPitch(),
        //                 message.isReload()));
        // }

        // LogUtils.getLogger().debug("Handling gun sound message, playing sound");
        if (mc.level.getEntity(message.getShooterId()) instanceof Player player) {
            final EntityBoundSoundInstance sound = new GunShotSound(new SoundEvent(message.getId()),
                    SoundSource.PLAYERS, message.getVolume(), message.getPitch(), player, false);
            mc.getSoundManager().play(sound);
        }
    }

    public static void handleMessageAnimationSound(final UUID fromWho,
            final ResourceLocation animationResource, final ResourceLocation soundResource,
            final boolean play) {
        final Level world = Minecraft.getInstance().level;
        if (world == null)
            return;
        final Player player = world.getPlayerByUUID(fromWho);
        if (player == null)
            return;
        if (animationResource == null || soundResource == null)
            return;
        final AnimationMeta animationMeta = new AnimationMeta(animationResource);
        final AnimationSoundMeta soundMeta = new AnimationSoundMeta(soundResource);
        if (play)
            AnimationSoundManager.INSTANCE.playerSound(player, animationMeta, soundMeta);
        else
            AnimationSoundManager.INSTANCE.interruptSound(player, animationMeta);
    }

    public static void handleMessageBlood(final MessageBlood message) {
        if (!Config.CLIENT.particle.enableBlood.get()) {
            return;
        }
        final Level world = Minecraft.getInstance().level;
        if (world != null) {
            for (int i = 0; i < 10; i++) {
                world.addParticle(ModParticleTypes.BLOOD.get(), true, message.getX(),
                        message.getY(), message.getZ(), 0.5, 0, 0.5);
            }
        }
    }

    public static void handleMessageBulletTrail(final MessageBulletTrail message) {
        final Level world = Minecraft.getInstance().level;
        if (world != null) {

            final int[] entityIds = message.getEntityIds();
            final Vec3[] positions = message.getPositions();
            final Vec3[] motions = message.getMotions();
            final float[] shooterYaws = message.getShooterYaws();
            final float[] shooterPitch = message.getShooterPitches();
            final ItemStack item = message.getItem();
            final int trailColor = message.getTrailColor();
            final double trailLengthMultiplier = message.getTrailLengthMultiplier();
            final int life = message.getLife();
            final double gravity = message.getGravity();
            final int shooterId = message.getShooterId();
            for (int i = 0; i < message.getCount(); i++) {
                BulletTrailRenderingHandler.get()
                        .add(new BulletTrail(entityIds[i], positions[i], motions[i], shooterYaws[i],
                                shooterPitch[i], item, trailColor, trailLengthMultiplier, life,
                                gravity, shooterId, message.getCount()));
            }
        }
    }

    public static void handleExplosionStunGrenade(final MessageStunGrenade message) {
        final Minecraft mc = Minecraft.getInstance();
        final ParticleEngine particleManager = mc.particleEngine;
        final Level world = mc.level;
        final double x = message.getX();
        final double y = message.getY();
        final double z = message.getZ();

        /* Spawn lingering smoke particles */
        for (int i = 0; i < 30; i++) {
            ClientPlayHandler.spawnParticle(particleManager, ParticleTypes.CLOUD, x, y, z,
                    world.random, 0.2);
        }

        /* Spawn fast moving smoke/spark particles */
        for (int i = 0; i < 30; i++) {
            final Particle smoke = ClientPlayHandler.spawnParticle(particleManager,
                    ParticleTypes.SMOKE, x, y, z, world.random, 4.0);
            smoke.setLifetime((int) ((8 / (Math.random() * 0.1 + 0.4)) * 0.5));
            ClientPlayHandler.spawnParticle(particleManager, ParticleTypes.CRIT, x, y, z,
                    world.random, 4.0);
        }
    }

    private static Particle spawnParticle(final ParticleEngine manager, final ParticleOptions data,
            final double x, final double y, final double z, final Random rand,
            final double velocityMultiplier) {
        // if(GunMod.cabLoaded)
        // deleteBitOnHit();
        return manager.createParticle(data, x, y, z, (rand.nextDouble() - 0.5) * velocityMultiplier,
                (rand.nextDouble() - 0.5) * velocityMultiplier,
                (rand.nextDouble() - 0.5) * velocityMultiplier);
    }

    /*
     * private static boolean deleteBitOnHit(BlockPos blockPos, BlockState
     * blockState, double x, double y, double z)//(IParticleData data, double x,
     * double y, double z, Random rand, double velocityMultiplier)
     * {
     * Minecraft mc = Minecraft.getInstance();
     * ChiselAdaptingWorldMutator chiselAdaptingWorldMutator = new
     * ChiselAdaptingWorldMutator(mc.world, blockPos);
     * float bitSize =
     * ChiselsAndBitsAPI.getInstance().getStateEntrySize().getSizePerBit();
     * ChiselsAndBitsAPI.getInstance().getMutatorFactory().in(mc.world,
     * blockPos).overrideInAreaTarget(Blocks.AIR.getDefaultState(), new
     * Vector3d(bitSize*Math.abs(x),bitSize*Math.abs(y),bitSize*Math.abs(z)));
     * return true;
     * }
     */
    public static void handleProjectileHitBlock(final MessageProjectileHitBlock message) {
        final Minecraft mc = Minecraft.getInstance();
        final Level world = mc.level;
        if (world != null) {
            final BlockState state = world.getBlockState(message.getPos());
            final double holeX = message.getX() + 0.005 * message.getFace().getStepX();
            final double holeY = message.getY() + 0.005 * message.getFace().getStepY();
            final double holeZ = message.getZ() + 0.005 * message.getFace().getStepZ();
            final double distance = Math
                    .sqrt(mc.player.distanceToSqr(message.getX(), message.getY(), message.getZ()));
            world.addParticle(new BulletHoleData(message.getFace(), message.getPos()), false, holeX,
                    holeY, holeZ, 0, 0, 0);
            if (distance < 16.0) {
                for (int i = 0; i < 3; i++) {
                    final Vec3i normal = message.getFace().getNormal();
                    final Vec3 motion = new Vec3(normal.getX(), normal.getY(), normal.getZ());
                    motion.add(ClientPlayHandler.getRandomDir(world.random),
                            ClientPlayHandler.getRandomDir(world.random),
                            ClientPlayHandler.getRandomDir(world.random));
                    world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), false,
                            message.getX(), message.getY(), message.getZ(), 0, 0, 0);
                }
            }
            if (distance < 32.0) {
                world.playLocalSound(message.getX(), message.getY(), message.getZ(),
                        state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.75F, 0.5F,
                        false);
            }
        }
    }

    private static double getRandomDir(final Random random) {
        return -0.25 + random.nextDouble() * 0.5;
    }

    public static void handleProjectileHitEntity(final MessageProjectileHitEntity message) {
        final Minecraft mc = Minecraft.getInstance();
        final Level world = mc.level;
        if (world == null)
            return;

        HUDRenderingHandler.get().hitMarkerTracker = (int) HUDRenderingHandler.hitMarkerRatio;
        HUDRenderingHandler.get().hitMarkerHeadshot = message.isHeadshot();

        // Hit marker sound, after sound set HuD renderder hitmarker ticker to 3 fade in and out quick, use textured crosshair as a base
        final SoundEvent event = ClientPlayHandler.getHitSound(message.isCritical(),
                message.isHeadshot(), message.isPlayer());

        if (event == null)
            return;

        mc.getSoundManager().play(
                SimpleSoundInstance.forUI(event, 1.0F, 1.0F + world.random.nextFloat() * 0.2F));
    }

    @Nullable
    private static SoundEvent getHitSound(final boolean critical, final boolean headshot,
            final boolean player) {
        if (critical) {
            if (Config.CLIENT.sounds.playSoundWhenCritical.get()) {
                final SoundEvent event = ForgeRegistries.SOUND_EVENTS
                        .getValue(new ResourceLocation(Config.CLIENT.sounds.criticalSound.get()));
                return event != null ? event : SoundEvents.PLAYER_ATTACK_CRIT;
            }
        } else if (headshot) {
            if (Config.CLIENT.sounds.playSoundWhenHeadshot.get()) {
                // SoundEvent event =
                // ModSounds.HEADSHOT_EXTENDED_PLAYFUL.get();//ForgeRegistries.SOUND_EVENTS.getValue(new
                // ResourceLocation(Config.CLIENT.sounds.headshotSound.get()));
                final SoundEvent event = ForgeRegistries.SOUND_EVENTS
                        .getValue(new ResourceLocation(Config.CLIENT.sounds.headshotSound.get()));
                return event != null ? event : SoundEvents.PLAYER_ATTACK_KNOCKBACK;
            }
        } else if (player) {
            return SoundEvents.PLAYER_HURT;
        } else {
            return SoundEvents.PLAYER_ATTACK_WEAK; // Hitmarker
        }

        return null;
    }

    public static void handleRemoveProjectile(final MessageRemoveProjectile message) {
        BulletTrailRenderingHandler.get().remove(message.getEntityId());
    }

    /*
     * public static void handleDevelopingGuns(MessageUpdateGuns message)
     * {
     * NetworkGunManager.updateRegisteredGuns(message);
     * CustomGunManager.updateCustomGuns(message);
     * }
     */

    public static void handleUpdateGuns(final MessageUpdateGuns message) {
        NetworkGunManager.updateRegisteredGuns(message);
        CustomGunManager.updateCustomGuns(message);
    }

    public static void handleUpdateRigs(final MessageUpdateRigs message) {
        NetworkRigManager.updateRegisteredRigs(message);
        CustomRigManager.updateCustomRigs(message);
    }
}
