package com.tac.guns.item.attachment;

import com.tac.guns.item.IColored;
import com.tac.guns.weapon.attachment.IStock;
import com.tac.guns.weapon.attachment.impl.Stock;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * A basic stock attachment item implementation with color support
 *
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class StockItem extends AttachmentItem<Stock> implements IStock, IColored {
    private final boolean colored;

    public StockItem(final Stock stock, final Properties properties) {
        super(stock, properties);
        this.colored = true;
    }

    public StockItem(final Stock stock, final Properties properties, final boolean colored) {
        super(stock, properties);
        this.colored = colored;
    }

    @Override
    public boolean canColor(final ItemStack stack) {
        return this.colored;
    }
}
