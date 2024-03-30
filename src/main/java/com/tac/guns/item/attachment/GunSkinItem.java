package com.tac.guns.item.attachment;

import com.tac.guns.item.IColored;
import com.tac.guns.weapon.attachment.IGunSkin;
import com.tac.guns.weapon.attachment.impl.GunSkin;

import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class GunSkinItem extends AttachmentItem<GunSkin> implements IGunSkin, IColored {
    public static final String CUSTOM_MODIFIER = "CustomModifier";

    public GunSkinItem(final GunSkin gunSkin, final Properties properties) {
        super(gunSkin, properties);
    }

    public static boolean hasCustomModifier(final ItemStack stack) {
        return stack != null && stack.getTag() != null
                && stack.getTag().contains(GunSkinItem.CUSTOM_MODIFIER, Tag.TAG_STRING);
    }

    public static void setCustomModifier(final ItemStack stack, final ResourceLocation location) {
        if (stack != null && location != null) {
            stack.getOrCreateTag().putString(GunSkinItem.CUSTOM_MODIFIER, location.toString());
        }
    }

    @Override
    public boolean canColor(final ItemStack stack) {
        return false;
    }
}
