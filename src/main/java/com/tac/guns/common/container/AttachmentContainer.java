package com.tac.guns.common.container;

import java.util.ArrayDeque;

import com.tac.guns.common.container.slot.AttachmentSlot;
import com.tac.guns.init.ModContainers;
import com.tac.guns.item.IEasyColor;
import com.tac.guns.item.attachment.AttachmentItem;
import com.tac.guns.item.attachment.PistolBarrelItem;
import com.tac.guns.item.attachment.PistolScopeItem;
import com.tac.guns.item.attachment.ScopeItem;
import com.tac.guns.item.attachment.SideRailItem;
import com.tac.guns.item.gun.GunItem;
import com.tac.guns.item.gun.PistolGunItem;
import com.tac.guns.weapon.Gun;
import com.tac.guns.weapon.attachment.IAttachmentItem;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class AttachmentContainer extends AbstractContainerMenu {
    private ItemStack weapon;
    private Container playerInventory;
    private final Container weaponInventory =
            new SimpleContainer(IAttachmentItem.Type.values().length) {
                @Override
                public void setChanged() {
                    super.setChanged();
                    AttachmentContainer.this.slotsChanged(this);
                }
            };

    private boolean loaded = false;

    // Range of attachments to scroll through

    public AttachmentContainer(final int windowId, final Inventory playerInventory,
            final ItemStack stack) // reads from attachments inv
    {
        this(windowId, playerInventory);
        // Add each attachment into stack, then pop each one
        final ArrayDeque<ItemStack> attachments = new ArrayDeque<>();
        if (this.weapon.getItem() instanceof IEasyColor) {
            // Traverse range of values, replace specific attachments per instanceof used
            // such as TimelessGunItem vs GunItem
            for (int i = IAttachmentItem.easyColorStart; i <= IAttachmentItem.easyColorEnd; i++)
                attachments.push(Gun.getAttachment(IAttachmentItem.Type.values()[i], stack));
            int i = 0;
            while (attachments.peekLast() != null)
                this.weaponInventory.setItem(i++, attachments.pollLast()); // set 0, add 1 to I, move ahead

        } else if (this.weapon.getItem() instanceof PistolGunItem) {
            for (int i = IAttachmentItem.standardAttStart; i <= IAttachmentItem.standardAttEnd; i++)
                if (i == IAttachmentItem.Type.SCOPE.getId())
                    attachments.push(Gun.getAttachment(IAttachmentItem.Type.PISTOL_SCOPE, stack));
                else if (i == IAttachmentItem.Type.BARREL.getId())
                    attachments.push(Gun.getAttachment(IAttachmentItem.Type.PISTOL_BARREL, stack));
                else
                    attachments.push(Gun.getAttachment(IAttachmentItem.Type.values()[i], stack));
            int i = 0;
            while (attachments.peekLast() != null)
                this.weaponInventory.setItem(i++, attachments.pollLast());
        } else if (this.weapon.getItem() instanceof GunItem) {
            for (int i = IAttachmentItem.standardAttStart; i <= IAttachmentItem.standardAttEnd; i++)
                attachments.push(Gun.getAttachment(IAttachmentItem.Type.values()[i], stack));
            int i = 0;
            while (attachments.peekLast() != null)
                this.weaponInventory.setItem(i++, attachments.pollLast());
        }
        this.loaded = true;
    }

    public AttachmentContainer(final int windowId, final Inventory playerInventory) {
        super(ModContainers.ATTACHMENTS.get(), windowId);
        this.weapon = playerInventory.getSelected();
        this.playerInventory = playerInventory;

        if (this.weapon.getItem() instanceof IEasyColor) {
            for (int i =
                    0; i <= (IAttachmentItem.easyColorEnd - IAttachmentItem.easyColorStart); i++) {
                if (i == 0) {
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon,
                            IAttachmentItem.Type.SCOPE_RETICLE_COLOR, playerInventory.player, i, 70,
                            32 + (2) * 18) {
                        @Override
                        public boolean mayPickup(final Player playerIn) {
                            return true;
                        }
                    });
                }
                if (i == 1) {
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon,
                            IAttachmentItem.Type.SCOPE_BODY_COLOR, playerInventory.player, i, 40,
                            -1 + (1) * 18) {
                        @Override
                        public boolean mayPickup(final Player playerIn) {
                            return true;
                        }
                    });
                }
                if (i == 2) {
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon,
                            IAttachmentItem.Type.SCOPE_GLASS_COLOR, playerInventory.player, i, 10,
                            50 + (1) * 18) {
                        @Override
                        public boolean mayPickup(final Player playerIn) {
                            return true;
                        }
                    });
                }
            }
        } else if (this.weapon.getItem() instanceof PistolGunItem) {
            for (int i = IAttachmentItem.standardAttStart; i <= IAttachmentItem.standardAttEnd; i++)
                if (i == IAttachmentItem.Type.SCOPE.getId())
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon,
                            IAttachmentItem.Type.PISTOL_SCOPE, playerInventory.player, 0, 5,
                            17 + 0 * 18));
                else if (i == IAttachmentItem.Type.BARREL.getId())
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon,
                            IAttachmentItem.Type.PISTOL_BARREL, playerInventory.player, 1, 5,
                            17 + 1 * 18));
                else if (i > 3)
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon,
                            IAttachmentItem.Type.values()[i], playerInventory.player, i, 155,
                            17 + (i - 4) * 18));
                else
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon,
                            IAttachmentItem.Type.values()[i], playerInventory.player, i, 5,
                            17 + i * 18));

        } else if (this.weapon.getItem() instanceof GunItem) {
            for (int i = IAttachmentItem.standardAttStart; i <= IAttachmentItem.standardAttEnd; i++)
                if (i == IAttachmentItem.Type.SCOPE.getId() && ((GunItem) this.weapon.getItem())
                        .getGun().canAttachType(IAttachmentItem.Type.PISTOL_SCOPE))
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon,
                            new IAttachmentItem.Type[] {IAttachmentItem.Type.values()[i],
                                    IAttachmentItem.Type.PISTOL_SCOPE},
                            playerInventory.player, 0, 5, 17 + 0 * 18));
                else if (IAttachmentItem.Type.values()[i] == IAttachmentItem.Type.SIDE_RAIL
                        && ((GunItem) this.weapon.getItem()).getGun()
                                .canAttachType(IAttachmentItem.Type.IR_DEVICE))
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon,
                            new IAttachmentItem.Type[] {IAttachmentItem.Type.values()[i],
                                    IAttachmentItem.Type.IR_DEVICE},
                            playerInventory.player, i, 155, 17 + (i - 4) * 18));
                else if (i > 3) {
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon,
                            IAttachmentItem.Type.values()[i], playerInventory.player, i, 155,
                            17 + (i - 4) * 18));
                } else
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon,
                            IAttachmentItem.Type.values()[i], playerInventory.player, i, 5,
                            17 + i * 18));
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 102 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            if (i == playerInventory.selected) {
                this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 160) {
                    @Override
                    public boolean mayPickup(final Player playerIn) {
                        return true;
                    }
                });
            } else {
                this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 160) {
                    @Override
                    public boolean mayPickup(final Player playerIn) {
                        return true;
                    }
                });;
            }
        }
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    @Override
    public boolean stillValid(final Player playerIn) {
        return true;
    }

    @Override
    public void slotsChanged(final Container inventoryIn) {
        final CompoundTag attachments = new CompoundTag();

        if (this.weapon.getItem() instanceof IEasyColor) {
            for (int i = 0; i < this.getWeaponInventory().getContainerSize(); i++) {
                final ItemStack attachment = this.getSlot(i).getItem();
                if (attachment.getItem() instanceof DyeItem) {
                    if (i == 0)
                        attachments.put(IAttachmentItem.Type.SCOPE_RETICLE_COLOR.getTagKey(),
                                attachment.save(new CompoundTag()));
                    if (i == 1)
                        attachments.put(IAttachmentItem.Type.SCOPE_BODY_COLOR.getTagKey(),
                                attachment.save(new CompoundTag()));
                    if (i == 2)
                        attachments.put(IAttachmentItem.Type.SCOPE_GLASS_COLOR.getTagKey(),
                                attachment.save(new CompoundTag()));
                }
            }
        } else if (this.weapon.getItem() instanceof PistolGunItem) {
            for (int i =
                    IAttachmentItem.standardAttStart; i <= IAttachmentItem.standardAttEnd; i++) {
                if (i == 0) {
                    final ItemStack attachment = this.getSlot(i).getItem();
                    if (attachment.getItem() instanceof PistolScopeItem) {
                        attachments.put(
                                ((PistolScopeItem) attachment.getItem()).getType().getTagKey(),
                                attachment.save(new CompoundTag()));
                    }
                } else if (i == 1) {
                    final ItemStack attachment = this.getSlot(i).getItem();
                    if (attachment.getItem() instanceof PistolBarrelItem) {
                        attachments.put(
                                ((PistolBarrelItem) attachment.getItem()).getType().getTagKey(),
                                attachment.save(new CompoundTag()));
                    }
                } else {
                    final ItemStack attachment = this.getSlot(i).getItem();
                    if (attachment.getItem() instanceof AttachmentItem<?>) {
                        attachments.put(
                                ((AttachmentItem<?>) attachment.getItem()).getType().getTagKey(),
                                attachment.save(new CompoundTag()));
                    }
                }
            }
        } else if (this.weapon.getItem() instanceof GunItem) {
            for (int i =
                    IAttachmentItem.standardAttStart; i <= IAttachmentItem.standardAttEnd; i++) {
                final ItemStack attachment = this.getSlot(i).getItem();
                if (attachment.getItem() instanceof AttachmentItem<?>)
                    attachments.put(
                            ((AttachmentItem<?>) attachment.getItem()).getType().getTagKey(),
                            attachment.save(new CompoundTag()));
            }
        }

        final CompoundTag tag = this.weapon.getOrCreateTag();
        tag.put("Attachments", attachments);
        super.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(final Player playerIn, final int index) {
        ItemStack copyStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);

        if (this.weapon.getItem() instanceof ScopeItem
                || this.weapon.getItem() instanceof SideRailItem) {
            if (slot != null && slot.hasItem()) {
                final ItemStack slotStack = slot.getItem();
                copyStack = slotStack.copy();

                if (index == 0) {
                    if (!this.moveItemStackTo(slotStack, 0, 36, true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (slotStack.getItem() instanceof DyeItem) {
                        if (!this.moveItemStackTo(slotStack, 0, 3, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 28) {
                        if (!this.moveItemStackTo(slotStack, 28, 36, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index <= 36 && !this.moveItemStackTo(slotStack, 0, 28, false)) {
                        return ItemStack.EMPTY;
                    }
                }

                if (slotStack.isEmpty()) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }

                if (slotStack.getCount() == copyStack.getCount()) {
                    return ItemStack.EMPTY;
                }

                slot.onTake(playerIn, slotStack);
            }
        } else {
            if (slot != null && slot.hasItem()) {
                final ItemStack slotStack = slot.getItem();
                copyStack = slotStack.copy();
                if (index < this.weaponInventory.getContainerSize()) {
                    if (!this.moveItemStackTo(slotStack, this.weaponInventory.getContainerSize(),
                            this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(slotStack, 0,
                        this.weaponInventory.getContainerSize(), false)) {
                    return ItemStack.EMPTY;
                }
                if (slotStack.isEmpty()) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }
            }
        }

        return copyStack;
    }

    public Container getPlayerInventory() {
        return this.playerInventory;
    }

    public Container getWeaponInventory() {
        return this.weaponInventory;
    }
}
