package com.tac.guns.item.attachment;

import com.tac.guns.item.IColored;
import com.tac.guns.item.IEasyColor;
import com.tac.guns.weapon.attachment.ISideRail;
import com.tac.guns.weapon.attachment.impl.SideRail;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * A basic under barrel attachment item implementation with color support
 *
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class SideRailItem extends AttachmentItem<SideRail>
        implements ISideRail, IColored, IEasyColor {
    private final boolean colored;

    public SideRailItem(final SideRail sideRail, final Properties properties) {
        super(sideRail, properties);
        this.colored = true;
    }

    public SideRailItem(final SideRail sideRail, final Properties properties,
            final boolean colored) {
        super(sideRail, properties);
        this.colored = colored;
    }

    @Override
    public boolean canColor(final ItemStack stack) {
        return this.colored;
    }
}
