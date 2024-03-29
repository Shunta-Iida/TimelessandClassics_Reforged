package com.tac.guns.common.container.slot;

import com.tac.guns.common.container.ColorBenchContainer;
import com.tac.guns.item.transition.GunItem;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class WeaponColorSegmentSlot extends Slot {
    private final ColorBenchContainer container;
    private final ItemStack weapon;
    private final Player player;

    // Segment handling with these slots will be based on index
    public WeaponColorSegmentSlot(final ColorBenchContainer container,
            final Container weaponInventory, final ItemStack weapon, final Player player,
            final int index, final int x, final int y) {
        super(weaponInventory, index, x, y);
        this.container = container;
        this.weapon = weapon;
        this.player = player;
    }

    @Override
    public boolean isActive() {
        if (this.weapon.getItem() instanceof GunItem)
            return true;
        else
            return false;
    }

    @Override
    public boolean mayPlace(final ItemStack stack) {
        if (this.weapon.getItem() instanceof GunItem)
            return stack.getItem() instanceof DyeItem;
        else
            return false;
    }

    /*
     * @Override
     * public void onSlotChanged()
     * {
     * if(this.container.get isLoaded())
     * {
     * this.player.world.playSound(null, this.player.getPosX(),
     * this.player.getPosY() + 1.0, this.player.getPosZ(),
     * ModSounds.UI_WEAPON_ATTACH.get(), SoundCategory.PLAYERS, 0.5F,
     * this.getHasStack() ? 1.0F : 0.75F);
     * }
     * }
     */
    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
