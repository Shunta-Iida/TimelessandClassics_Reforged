package com.tac.guns.item.attachment.impl;

import com.tac.guns.Reference;
import com.tac.guns.interfaces.IGunModifier;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * The base attachment object
 *
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public abstract class Attachment {
    private final IGunModifier[] modifiers;
    private List<Component> perks = null;

    Attachment(IGunModifier... modifiers) {
        this.modifiers = modifiers;
    }

    public IGunModifier[] getModifiers() {
        return this.modifiers;
    }

    public void setPerks(List<Component> perks) {
        if (this.perks == null) {
            this.perks = perks;
        }
    }

    public List<Component> getPerks() {
        return this.perks;
    }
}
