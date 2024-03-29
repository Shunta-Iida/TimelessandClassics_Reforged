package com.tac.guns.client.audio;

import com.tac.guns.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class GunShotSound extends EntityBoundSoundInstance {
    public GunShotSound(SoundEvent event, SoundSource source, float volume, float pitch,
            Entity entity, boolean reload) {
        super(event, source, volume, pitch, entity);
        this.attenuation = Attenuation.NONE;

        final LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            final float distance =
                    reload ? 16.0F : Config.SERVER.gunShotMaxDistance.get().floatValue();
            float multiplexer = 1.0F
                    - Math.min(1.0F, (float) Math.sqrt(player.distanceToSqr(x, y, z)) / distance);
            this.volume *= multiplexer;
            this.volume *= this.volume; // Ease the volume instead of linear
            this.pitch *= multiplexer;
            this.pitch -= this.pitch * 0.1f; // Ease the pitch instead of linear
            this.pitch = Math.max(0.3f, this.pitch); // Cap the pitch
        }
    }
}
