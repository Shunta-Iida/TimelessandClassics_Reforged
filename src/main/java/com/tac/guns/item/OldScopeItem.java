package com.tac.guns.item;

import com.tac.guns.item.attachment.impl.Scope;

/**
 * A basic scope attachment item implementation with color support
 *
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class OldScopeItem extends ScopeItem {
    public OldScopeItem(Scope scope, Properties properties) {
        super(scope, properties);
    }

    @Override
    public Type getType() {
        return Type.OLD_SCOPE;
    }

}
