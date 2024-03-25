package com.tac.guns.item;

import com.tac.guns.item.attachment.impl.Barrel;

/**
 * A basic barrel attachment item implementation with color support
 *
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class PistolBarrelItem extends BarrelItem {

    public PistolBarrelItem(Barrel barrel, Properties properties) {
        super(barrel, properties);
    }

    public PistolBarrelItem(Barrel barrel, Properties properties, boolean colored) {
        super(barrel, properties, colored);
    }

    @Override
    public Type getType() {
        return Type.PISTOL_BARREL;
    }

}
