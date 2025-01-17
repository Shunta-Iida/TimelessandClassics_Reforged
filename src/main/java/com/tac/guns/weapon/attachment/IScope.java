package com.tac.guns.weapon.attachment;

import com.tac.guns.weapon.attachment.impl.Scope;

/**
 * An interface to turn an any item into a scope attachment. This is useful if
 * your item extends a
 * custom item class otherwise {@link com.tac.guns.item.attachment.ScopeItem} can be used
 * instead of
 * this interface.
 * <p>
 * Author: Ocelot
 */
public interface IScope extends IAttachmentItem<Scope> {
    /**
     * @return The type of this attachment
     */
    @Override
    default Type getType() {
        return Type.SCOPE;
    }
}
