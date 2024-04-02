package com.tac.guns.weapon.attachment;

import com.tac.guns.weapon.attachment.impl.Barrel;

/**
 * An interface to turn an any item into a barrel attachment. This is useful if
 * your item extends a
 * custom item class otherwise {@link com.tac.guns.item.attachment.BarrelItem} can be used
 * instead of
 * this interface.
 * <p>
 * Author: Ocelot, MrCrayfish
 */
public interface IBarrel extends IAttachmentItem<Barrel> {
    /**
     * @return The type of this attachment
     */
    @Override
    default Type getType() {
        return Type.BARREL;
    }
}
