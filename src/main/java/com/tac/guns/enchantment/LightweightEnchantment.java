package com.tac.guns.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class LightweightEnchantment extends GunEnchantment {
    public LightweightEnchantment() {
        super(Rarity.RARE, EnchantmentTypes.GUN, new EquipmentSlot[] {EquipmentSlot.MAINHAND},
                Type.WEAPON);
    }

    @Override
    public int getMinCost(final int level) {
        return 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMaxCost(final int level) {
        return this.getMinCost(level) + 20;
    }
}
