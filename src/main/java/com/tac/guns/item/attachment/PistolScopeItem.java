package com.tac.guns.item.attachment;

import com.tac.guns.weapon.attachment.impl.Scope;

/**
 * A basic scope attachment item implementation with color support
 *
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class PistolScopeItem extends ScopeItem {
    public PistolScopeItem(final Scope scope, final Properties properties) {
        super(scope, properties);
    }

    @Override
    public Type getType() {
        return Type.PISTOL_SCOPE;
    }

}
