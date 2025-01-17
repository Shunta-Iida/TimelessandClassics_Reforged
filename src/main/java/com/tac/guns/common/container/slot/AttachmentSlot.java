package com.tac.guns.common.container.slot;

import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.tac.guns.common.container.AttachmentContainer;
import com.tac.guns.init.ModSounds;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.IEasyColor;
import com.tac.guns.item.gun.GunItem;
import com.tac.guns.weapon.Gun;
import com.tac.guns.weapon.attachment.IAttachmentItem;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class AttachmentSlot extends Slot {
    private final AttachmentContainer container;
    private final ItemStack weapon;
    private IAttachmentItem.Type type;
    private final Player player;
    private IAttachmentItem.Type[] types;

    public AttachmentSlot(final AttachmentContainer container, final Container weaponInventory,
            final ItemStack weapon, final IAttachmentItem.Type type, final Player player,
            final int index, final int x, final int y) {
        super(weaponInventory, index, x, y);
        this.container = container;
        this.weapon = weapon;
        this.type = type;
        this.player = player;
    }

    public AttachmentSlot(final AttachmentContainer container, final Container weaponInventory,
            final ItemStack weapon, final IAttachmentItem.Type[] types, final Player player,
            final int index, final int x, final int y) {
        super(weaponInventory, index, x, y);
        this.container = container;
        this.weapon = weapon;
        this.types = types;
        this.player = player;
    }

    @Override
    public boolean isActive() {
        if ((this.type == IAttachmentItem.Type.EXTENDED_MAG && this.weapon.getOrCreateTag().getInt(
                "AmmoCount") > ((GunItem) this.weapon.getItem()).getGun().getReloads().getMaxAmmo())
                || SyncedEntityData.instance().get(this.player, ModSyncedDataKeys.RELOADING)) {
            return false;
        }
        if (this.player.getMainHandItem().getItem() instanceof IEasyColor) {
            return true;
        } else {
            final GunItem item = (GunItem) this.weapon.getItem();
            final Gun modifiedGun = item.getGun(this.weapon.getTag());
            if (modifiedGun.canAttachType(this.type))
                return true;
            else if (this.types != null) {
                for (final IAttachmentItem.Type x : this.types) {
                    if (modifiedGun.canAttachType(x))
                        return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean mayPlace(final ItemStack stack) {
        if ((this.type == IAttachmentItem.Type.EXTENDED_MAG && this.weapon.getOrCreateTag().getInt(
                "AmmoCount") > ((GunItem) this.weapon.getItem()).getGun().getReloads().getMaxAmmo())
                || SyncedEntityData.instance().get(this.player, ModSyncedDataKeys.RELOADING)) {
            return false;
        }
        if ((this.player.getMainHandItem().getItem() instanceof IEasyColor)
                && stack.getItem() instanceof DyeItem)
            return true;
        else {
            if (this.weapon.isEmpty() || !(this.weapon.getItem() instanceof GunItem))
                return false;
            final GunItem item = (GunItem) this.weapon.getItem();
            final Gun modifiedGun = item.getGun(this.weapon.getTag());
            if (stack.getItem() instanceof IAttachmentItem
                    && ((IAttachmentItem) stack.getItem()).getType() == this.type
                    && modifiedGun.canAttachType(this.type))
                return true;
            else if (this.types != null && stack.getItem() instanceof IAttachmentItem) {
                for (final IAttachmentItem.Type x : this.types) {
                    if (((IAttachmentItem) stack.getItem()).getType() == x)
                        return true;
                }
            }
            return false;
        }
    }

    @Override
    public void setChanged() {
        if (this.container.isLoaded()) {
            this.player.level.playSound(this.player, this.player, ModSounds.UI_WEAPON_ATTACH.get(),
                    SoundSource.PLAYERS, 0.5F, this.hasItem() ? 1.0F : 0.75F);
        }
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPickup(final Player player) {
        return true;
    }
}
