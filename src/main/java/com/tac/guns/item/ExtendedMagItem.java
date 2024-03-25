package com.tac.guns.item;

import com.tac.guns.item.attachment.IExtendedMag;
import com.tac.guns.item.attachment.impl.ExtendedMag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * A basic under barrel attachment item implementation with color support
 *
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class ExtendedMagItem extends AttachmentItem<ExtendedMag> implements IExtendedMag, IColored {
    private final boolean colored;

    public ExtendedMagItem(ExtendedMag extendedMag, Properties properties) {
        super(extendedMag, properties);
        this.colored = true;
    }

    public ExtendedMagItem(ExtendedMag extendedMag, Properties properties, boolean colored) {
        super(extendedMag, properties);
        this.colored = colored;
    }

    @Override
    public boolean canColor(ItemStack stack) {
        return this.colored;
    }
}
