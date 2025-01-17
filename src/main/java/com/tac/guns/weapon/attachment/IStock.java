package com.tac.guns.weapon.attachment;

import com.tac.guns.weapon.attachment.impl.Stock;

/**
 * An interface to turn an any item into a stock attachment. This is useful if
 * your item extends a
 * custom item class otherwise {@link com.tac.guns.item.attachment.StockItem} can be used
 * instead of
 * this interface.
 * <p>
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public interface IStock extends IAttachmentItem<Stock> {
    /**
     * @return The type of this attachment
     */
    @Override
    default Type getType() {
        return Type.STOCK;
    }
}
