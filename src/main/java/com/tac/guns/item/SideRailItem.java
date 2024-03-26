package com.tac.guns.item;

import com.tac.guns.item.attachment.ISideRail;
import com.tac.guns.item.attachment.impl.SideRail;
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

    public SideRailItem(SideRail sideRail, Properties properties) {
        super(sideRail, properties);
        this.colored = true;
    }

    public SideRailItem(SideRail sideRail, Properties properties, boolean colored) {
        super(sideRail, properties);
        this.colored = colored;
    }

    @Override
    public boolean canColor(ItemStack stack) {
        return this.colored;
    }
}
