package com.tac.guns.weapon.attachment;

import com.tac.guns.weapon.attachment.impl.IrDevice;

/**
 * An interface to turn an any item into a under barrel attachment. This is
 * useful if your item
 * extends a custom item class otherwise
 * {@link com.tac.guns.item.attachment.UnderBarrelItem} can be
 * used instead of this interface.
 * <p>
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public interface IirDevice extends IAttachmentItem<IrDevice> {
    /**
     * @return The type of this attachment
     */
    @Override
    default Type getType() {
        return Type.IR_DEVICE;
    }
}
