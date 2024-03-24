package com.tac.guns.mixin.enchantment;

import org.spongepowered.asm.mixin.Mixin;

import com.tac.guns.item.GunItem;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.LootBonusEnchantment;

@Mixin(LootBonusEnchantment.class)
public abstract class LootingMixin extends Enchantment {

    protected LootingMixin(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
        super(rarity, category, slots);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        if (stack.getItem() instanceof GunItem) {
            return true;
        }
        return super.canEnchant(stack);
    }

}
