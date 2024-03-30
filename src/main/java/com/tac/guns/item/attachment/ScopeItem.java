package com.tac.guns.item.attachment;

import com.tac.guns.item.IColored;
import com.tac.guns.item.IEasyColor;
import com.tac.guns.weapon.attachment.IScope;
import com.tac.guns.weapon.attachment.impl.Scope;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * A basic scope attachment item implementation with color support
 *
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class ScopeItem extends AttachmentItem<Scope> implements IScope, IColored, IEasyColor {
    private final boolean colored;

    public ScopeItem(final Scope scope, final Item.Properties properties) {
        super(scope, properties);
        this.colored = true;
    }

    public ScopeItem(final Scope scope, final Item.Properties properties, final boolean colored) {
        super(scope, properties);
        this.colored = colored;
    }

    @Override
    public boolean canColor(final ItemStack stack) {
        return this.colored;
    }

}
