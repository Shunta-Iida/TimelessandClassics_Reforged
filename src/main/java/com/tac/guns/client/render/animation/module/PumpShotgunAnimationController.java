package com.tac.guns.client.render.animation.module;

import com.tac.guns.item.gun.GunItem;
import com.tac.guns.weapon.Gun;

import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class PumpShotgunAnimationController extends GunAnimationController {

    public void setEmpty(final boolean empty) {
        this.empty = empty;
    }

    private boolean empty = false;

    @Override
    protected AnimationSoundMeta getSoundFromLabel(final Item item, final AnimationLabel label) {
        if (item instanceof final GunItem gunItem) {
            final Gun.Sounds sounds = gunItem.getGun().getSounds();
            switch (label) {
                case PUMP:
                    return new AnimationSoundMeta(sounds.getPump());
                case RELOAD_INTRO:
                    return new AnimationSoundMeta(sounds.getReloadIntro());
                case RELOAD_LOOP:
                    return new AnimationSoundMeta(sounds.getReloadLoop());
                case RELOAD_NORMAL_END:
                    return new AnimationSoundMeta(sounds.getReloadEnd());
                case RELOAD_EMPTY_END:
                    return new AnimationSoundMeta(sounds.getReloadEndEmpty());
                default:
                    return super.getSoundFromLabel(item, label);
            }
        }
        return null;
    }

    @Override
    public void runAnimation(final AnimationLabel label) {
        switch (label) {
            case RELOAD_EMPTY:
            case RELOAD_NORMAL:
                super.runAnimation(AnimationLabel.RELOAD_INTRO, () -> {
                    if (!this.isAnimationRunning())
                        super.runAnimation(AnimationLabel.RELOAD_LOOP);
                });
            default:
                super.runAnimation(label);
        }
    }

    @Override
    public void runAnimation(final AnimationLabel label, final Runnable callback) {
        switch (label) {
            case RELOAD_EMPTY:
            case RELOAD_NORMAL:
                super.runAnimation(AnimationLabel.RELOAD_INTRO, () -> {
                    if (!this.isAnimationRunning())
                        super.runAnimation(AnimationLabel.RELOAD_LOOP, callback);
                });
            default:
                super.runAnimation(label, callback);
        }
    }

    @Override
    public void runAnimation(final AnimationLabel label, final float speed) {
        switch (label) {
            case RELOAD_EMPTY:
            case RELOAD_NORMAL:
                this.runAnimation(AnimationLabel.RELOAD_INTRO, () -> {
                    if (this.isAnimationRunning())
                        super.stopAnimation();
                    this.runAnimation(AnimationLabel.RELOAD_LOOP, speed);
                }, speed);
            default:
                super.runAnimation(label, speed);
        }
    }

    @Override
    public void runAnimation(final AnimationLabel label, final Runnable callback,
            final float speed) {
        switch (label) {
            case RELOAD_EMPTY:
            case RELOAD_NORMAL:
                this.runAnimation(AnimationLabel.RELOAD_INTRO, () -> {
                    if (this.isAnimationRunning())
                        super.stopAnimation();
                    this.runAnimation(AnimationLabel.RELOAD_LOOP, callback, speed);
                }, speed);
            default:
                super.runAnimation(label, callback, speed);
        }
    }

    public boolean isEmpty() {
        return this.empty;
    }
}
