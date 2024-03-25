package com.tac.guns.item;

import com.tac.guns.item.attachment.IScope;
import com.tac.guns.item.attachment.impl.Scope;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * A basic scope attachment item implementation with color support
 *
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class ScopeItem extends AttachmentItem<Scope> implements IScope, IColored, IEasyColor {
    private final boolean colored;

    public ScopeItem(Scope scope, Item.Properties properties) {
        super(scope, properties);
        this.colored = true;
    }

    public ScopeItem(Scope scope, Item.Properties properties, boolean colored) {
        super(scope, properties);
        this.colored = colored;
    }

    @Override
    public boolean canColor(ItemStack stack) {
        return this.colored;
    }

}
