package com.tac.guns.item.ammo;

import com.tac.guns.GunMod;
import com.tac.guns.util.Process;

public class TimelessRFAmmoItem extends AmmoItem {
    public TimelessRFAmmoItem() {
        this(properties -> properties);
    }

    public TimelessRFAmmoItem(Process<Properties> properties) {
        super(properties.process(new Properties().stacksTo(30).tab(GunMod.AMMO)));
    }
}
