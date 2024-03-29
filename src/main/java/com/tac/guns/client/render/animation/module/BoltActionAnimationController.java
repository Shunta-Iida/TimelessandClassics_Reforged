package com.tac.guns.client.render.animation.module;

import com.tac.guns.common.Gun;
import com.tac.guns.item.transition.GunItem;

import net.minecraft.world.item.Item;

public abstract class BoltActionAnimationController extends GunAnimationController {
    @Override
    protected AnimationSoundMeta getSoundFromLabel(Item item, AnimationLabel label) {
        if (item instanceof GunItem) {
            GunItem gunItem = (GunItem) item;
            Gun.Sounds sounds = gunItem.getGun().getSounds();
            if (label == AnimationLabel.PULL_BOLT) {
                return new AnimationSoundMeta(sounds.getPullBolt());
            }
            return super.getSoundFromLabel(item, label);
        }
        return null;
    }
}
