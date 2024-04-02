package com.tac.guns.item.gun;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.tac.guns.interfaces.IGunModifier;
import com.tac.guns.inventory.gear.armor.ArmorRigCapabilityProvider;
import com.tac.guns.inventory.gear.armor.RigSlotsHandler;
import com.tac.guns.item.ItemAttributeValues;
import com.tac.guns.item.ItemAttributeValues.AmmoInspectType;
import com.tac.guns.item.ItemAttributes;
import com.tac.guns.item.ammo.AmmoItem;
import com.tac.guns.item.attachment.ScopeItem;
import com.tac.guns.util.GunModifierHelper;
import com.tac.guns.util.WearableHelper;
import com.tac.guns.weapon.Gun;
import com.tac.guns.weapon.attachment.IAttachmentItem;
import com.tac.guns.weapon.attachment.impl.Scope;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class GunItemHelper {

    private final ItemStack gunItemStack;
    private final GunItem gunItem;

    private GunItemHelper(final ItemStack gunItemStack) {
        if (!(gunItemStack.getItem() instanceof final GunItem gunItem)) {
            throw new IllegalArgumentException("ItemStack must be a GunItem");
        }
        this.gunItemStack = gunItemStack;
        this.gunItem = gunItem;
    }

    public static GunItemHelper of(final ItemStack gunItemStack) {
        return new GunItemHelper(gunItemStack);
    }

    public ItemStack[] getAttachmentItemStacks() {
        final CompoundTag tag = this.gunItemStack.getTag();
        if (tag == null || !tag.contains(ItemAttributes.Gun.ATTACHMENTS)) {
            return new ItemStack[0];
        }
        final CompoundTag attachmentsTag = tag.getCompound(ItemAttributes.Gun.ATTACHMENTS);
        final ArrayList<ItemStack> list = new ArrayList<>();

        for (final String attachmentKey : attachmentsTag.getAllKeys()) {
            final CompoundTag attachmentTag = attachmentsTag.getCompound(attachmentKey);
            final ItemStack attachmentItemStack = ItemStack.of(attachmentTag);
            if (attachmentItemStack.isEmpty()) {
                continue;
            }
            list.add(attachmentItemStack);
        }

        return list.toArray(new ItemStack[list.size()]);
    }

    public ItemStack[] findAmmo(final Player player) {
        final AmmoItem ammoItem = this.gunItem.getAmmoItem();
        if (!player.isAlive())
            return new ItemStack[0];
        final ArrayList<ItemStack> stacks = new ArrayList<>();

        if (player.isCreative()) {
            return new ItemStack[] {new ItemStack(ammoItem, Integer.MAX_VALUE)};
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            final ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ammoItem)) {
                stacks.add(stack);
            }
        }

        final ItemStack wornRig = WearableHelper.PlayerWornRig(player);

        if (!wornRig.isEmpty()) {
            final RigSlotsHandler itemHandler = (RigSlotsHandler) wornRig
                    .getCapability(ArmorRigCapabilityProvider.capability).resolve().get();
            final List<ItemStack> list = itemHandler.getStacks();
            for (final ItemStack stack : list) {
                if (stack.is(ammoItem)) {
                    stacks.add(stack);
                }
            }
        }

        return stacks.toArray(new ItemStack[stacks.size()]);
    }


    @Nullable
    public Scope getScope() {
        final CompoundTag tag = this.gunItemStack.getTag();
        if (tag == null || !tag.contains(ItemAttributes.Gun.ATTACHMENTS)) {
            return null;
        }
        final CompoundTag attachmentsTag = tag.getCompound(ItemAttributes.Gun.ATTACHMENTS);
        for (final String attachmentKey : attachmentsTag.getAllKeys()) {
            final CompoundTag attachmentTag = attachmentsTag.getCompound(attachmentKey);
            final ItemStack attachmentItemStack = ItemStack.of(attachmentTag);
            if (attachmentItemStack.isEmpty()) {
                continue;
            }
            if (!(attachmentItemStack.getItem() instanceof final ScopeItem scopeItem)) {
                continue;
            }
            return scopeItem.getAttachment();
        }
        return null;
    }

    public ItemStack getAttachment(final IAttachmentItem.Type type) {
        final CompoundTag compound = this.gunItemStack.getTag();
        if (compound != null
                && compound.contains(ItemAttributes.Gun.ATTACHMENTS, Tag.TAG_COMPOUND)) {
            final CompoundTag attachment = compound.getCompound("Attachments");
            if (attachment.contains(type.getTagKey(), Tag.TAG_COMPOUND)) {
                return ItemStack.of(attachment.getCompound(type.getTagKey()));
            } else if (type == IAttachmentItem.Type.SCOPE && (attachment
                    .contains(IAttachmentItem.Type.PISTOL_SCOPE.getTagKey(), Tag.TAG_COMPOUND)))
                return ItemStack
                        .of(attachment.getCompound(IAttachmentItem.Type.PISTOL_SCOPE.getTagKey()));
            else if (type == IAttachmentItem.Type.SIDE_RAIL && (attachment
                    .contains(IAttachmentItem.Type.IR_DEVICE.getTagKey(), Tag.TAG_COMPOUND)))
                return ItemStack
                        .of(attachment.getCompound(IAttachmentItem.Type.IR_DEVICE.getTagKey()));

        }
        return ItemStack.EMPTY;
    }

    public String getAmmoForDisplay() {
        switch (ItemAttributeValues.AmmoInspectType
                .fromInt(this.gunItemStack.getTag().getInt(ItemAttributes.Gun.AMMO_INSPECT_TYPE))) {
            case UNKNOWN:
                return "?";
            case VISUAL:
                return ItemAttributeValues.VisualAmmoInspectValues.getShortLabelFromPercentage(
                        (float) this.getAmmoInGun() / (float) this.getMagAmmoCapacity());
            case CORRECT:
                return this.getAmmoInGun() + "/" + this.getMagAmmoCapacity();
            default:
                return "";
        }
    }

    /**
     * @param weapon
     * @param modifiedGun
     * @return チャンバーの分を含めたマガジンの最大弾数
     */
    public int getAmmoCapacity() {
        final Gun modifiedGun = this.gunItem.getGun(this.gunItemStack.getTag());
        int capacity = modifiedGun.getReloads().isOpenBolt() ? modifiedGun.getReloads().getMaxAmmo()
                : modifiedGun.getReloads().getMaxAmmo() + 1;
        final int level = this.getAmmoCapacityWeight();
        if (level > -1 && level < modifiedGun.getReloads().getMaxAdditionalAmmoPerOC().length) {
            capacity += modifiedGun.getReloads().getMaxAdditionalAmmoPerOC()[level];
        } else if (level > -1) {
            capacity += (capacity / 2) * level - 3;
        }
        return capacity;
    }

    public int getMagAmmoCapacity() {
        final Gun modifiedGun = this.gunItem.getGun(this.gunItemStack.getTag());
        int capacity = modifiedGun.getReloads().getMaxAmmo();
        final int level = this.getAmmoCapacityWeight();
        if (level > -1 && level < modifiedGun.getReloads().getMaxAdditionalAmmoPerOC().length) {
            capacity += modifiedGun.getReloads().getMaxAdditionalAmmoPerOC()[level];
        } else if (level > -1) {
            capacity += (capacity / 2) * level - 3;
        }
        return capacity;
    }

    public int getAmmoInGun() {
        return this.gunItemStack.getTag().getInt(ItemAttributes.Gun.AMMO_COUNT);
    }

    public int getAmmoInMag() {
        return this.gunItem.getGun(this.gunItemStack.getTag()).getReloads().isOpenBolt()
                ? this.getAmmoInGun()
                : Math.max(0, this.getAmmoInGun() - 1);
    }

    public void decreaseAmmo(final int ammount) {
        final CompoundTag tag = this.gunItemStack.getOrCreateTag();
        tag.putInt(ItemAttributes.Gun.AMMO_COUNT, this.getAmmoInGun() - ammount);
    }

    public int getAmmoCapacityWeight() {
        int modifierWeight = -1;
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers = GunModifierHelper.getModifiers(this.gunItemStack,
                    IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                modifierWeight = Math.max(modifier.additionalAmmunition(), modifierWeight);
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(this.gunItemStack);
        for (final IGunModifier modifier : modifiers) {
            modifierWeight = Math.max(modifier.additionalAmmunition(), modifierWeight);
        }
        return modifierWeight;
    }

    public ItemAttributeValues.AmmoInspectType getAmmoInspectType() {
        return ItemAttributeValues.AmmoInspectType
                .fromInt(this.gunItemStack.getTag().getInt(ItemAttributes.Gun.AMMO_INSPECT_TYPE));
    }

    public void setGunInspectData(final AmmoInspectType inspectType) {
        final CompoundTag tag = this.gunItemStack.getOrCreateTag();
        tag.putInt(ItemAttributes.Gun.AMMO_INSPECT_TYPE, inspectType.toInt());
    }

    public AmmoItem getAmmoItem() {
        return this.gunItem.getAmmoItem();
    }

}
