package com.tac.guns.item.attachment;

import com.tac.guns.weapon.attachment.impl.Barrel;

/**
 * A basic barrel attachment item implementation with color support
 *
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class PistolBarrelItem extends BarrelItem {

    public PistolBarrelItem(final Barrel barrel, final Properties properties) {
        super(barrel, properties);
    }

    public PistolBarrelItem(final Barrel barrel, final Properties properties,
            final boolean colored) {
        super(barrel, properties, colored);
    }

    @Override
    public Type getType() {
        return Type.PISTOL_BARREL;
    }

}
