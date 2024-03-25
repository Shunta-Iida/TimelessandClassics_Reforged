package com.tac.guns.item;

import com.tac.guns.item.attachment.IUnderBarrel;
import com.tac.guns.item.attachment.impl.UnderBarrel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * A basic under barrel attachment item implementation with color support
 *
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class UnderBarrelItem extends AttachmentItem<UnderBarrel> implements IUnderBarrel, IColored {
    private final boolean colored;

    public UnderBarrelItem(UnderBarrel underBarrel, Properties properties) {
        super(underBarrel, properties);
        this.colored = true;
    }

    public UnderBarrelItem(UnderBarrel underBarrel, Properties properties, boolean colored) {
        super(underBarrel, properties);
        this.colored = colored;
    }

    @Override
    public boolean canColor(ItemStack stack) {
        return this.colored;
    }
}
