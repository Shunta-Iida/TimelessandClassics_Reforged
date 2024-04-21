package com.tac.guns.item.gun;

import com.tac.guns.GunMod;
import com.tac.guns.interfaces.IGunModifier;
import com.tac.guns.util.Process;

import net.minecraft.world.item.Item;

public class PistolGunItem extends GunItem {
    public PistolGunItem(final Process<Properties> properties) {
        super(properties1 -> properties.process(new Properties().stacksTo(1).tab(GunMod.GENERAL)));
    }

    public PistolGunItem(final Process<Item.Properties> properties,
            final IGunModifier... modifiers) {
        super(properties1 -> properties
                .process(new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)), modifiers);
    }

    public PistolGunItem() {
        this(properties -> properties);
    }
}
