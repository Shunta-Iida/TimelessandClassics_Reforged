package com.tac.guns.weapon.attachment;

import com.tac.guns.weapon.attachment.impl.GunSkin;

public interface IGunSkin extends IAttachment<GunSkin> {
    @Override
    default Type getType() {
        return Type.GUN_SKIN;
    }
}
