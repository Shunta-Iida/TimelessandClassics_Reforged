package com.tac.guns.weapon.customs;

import com.tac.guns.weapon.Rig;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class CustomRig implements INBTSerializable<CompoundTag> {
    public Rig rig;

    public Rig getRig() {
        return this.rig;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        compound.put("Rig", this.rig.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        this.rig = Rig.create(compound.getCompound("Rig"));
    }
}
