package com.tac.guns.item.misc;

import com.tac.guns.item.wearable.IArmorPlate;

import net.minecraft.world.item.Item;

/**
 * A basic item class that implements {@link IArmorPlate} to indicate this item
 * is a Plate
 *
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class ArmorPlateItem extends Item implements IArmorPlate {
    public ArmorPlateItem(final Properties properties) {
        super(properties);
    }
}
