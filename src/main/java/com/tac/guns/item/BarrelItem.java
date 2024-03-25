package com.tac.guns.item;

import com.tac.guns.item.attachment.IBarrel;
import com.tac.guns.item.attachment.impl.Barrel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * A basic barrel attachment item implementation with color support
 *
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class BarrelItem extends AttachmentItem<Barrel> implements IBarrel, IColored {
    private final boolean colored;

    public BarrelItem(Barrel barrel, Item.Properties properties) {
        super(barrel, properties);
        this.colored = true;
    }

    public BarrelItem(Barrel barrel, Item.Properties properties, boolean colored) {
        super(barrel, properties);
        this.colored = colored;
    }

    @Override
    public boolean canColor(ItemStack stack) {
        return this.colored;
    }
}
