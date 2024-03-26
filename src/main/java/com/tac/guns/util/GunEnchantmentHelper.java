package com.tac.guns.util;

import java.util.HashMap;

import com.tac.guns.client.screen.UpgradeBenchScreen;
import com.tac.guns.init.ModEnchantments;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class GunEnchantmentHelper {
    // UNSAFE, ME NO LIKEY - CLUMSYALIEN
    public static HashMap<String, UpgradeBenchScreen.RequirementItem> upgradeableEnchs =
            new HashMap<String, UpgradeBenchScreen.RequirementItem>() {
                {
                    this.put("Over Pressured",
                            new UpgradeBenchScreen.RequirementItem(new int[] {2, 3, 3, 5},
                                    new int[] {5, 7, 10, 15}, ModEnchantments.ACCELERATOR.get()));
                    /*
                     * put("Over Capacity",
                     * new UpgradeBenchScreen.RequirementItem(new int[]{1,2,3}, new int[]{1,3,6},
                     * ModEnchantments.OVER_CAPACITY.get()));
                     */
                    this.put("Advanced Rifling",
                            new UpgradeBenchScreen.RequirementItem(new int[] {1, 2, 3},
                                    new int[] {4, 6, 8}, ModEnchantments.RIFLING.get()));
                    this.put("Buffered", new UpgradeBenchScreen.RequirementItem(new int[] {2, 4},
                            new int[] {5, 11}, ModEnchantments.BUFFERED.get()));
                    this.put("Puncturing",
                            new UpgradeBenchScreen.RequirementItem(new int[] {1, 2, 3, 4, 5, 6},
                                    new int[] {4, 6, 8, 11, 14, 17},
                                    ModEnchantments.PUNCTURING.get()));
                    this.put("Lightweight",
                            new UpgradeBenchScreen.RequirementItem(new int[] {1, 2, 3},
                                    new int[] {4, 6, 8}, ModEnchantments.LIGHTWEIGHT.get()));
                }
            };

    public static float getReloadSpeed(final ItemStack weapon) {
        float speed = 1.0F;
        final int level =
                EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, weapon);
        if (level > 0) {
            speed = (float) Math.pow(1.2, level);
        }

        return speed;
    }

    public static double getAimDownSightSpeed(final ItemStack weapon) {
        final int level = EnchantmentHelper
                .getItemEnchantmentLevel(ModEnchantments.LIGHTWEIGHT.get(), weapon);
        if (level > 0) {
            return Math.pow(1.4, level);
        }
        return 1;
    }

    public static float getSpreadModifier(final ItemStack weapon) {
        final int level =
                EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.RIFLING.get(), weapon);
        if (level > 0) {
            return (float) Math.pow(0.7, level);
        }
        return 1f;
    }

    public static float getWeightModifier(final ItemStack weapon) {
        final int level = EnchantmentHelper
                .getItemEnchantmentLevel(ModEnchantments.LIGHTWEIGHT.get(), weapon);
        if (level > 0) {
            return (float) (Math.pow(1.4, level) - 1f);
        }
        return 0f;
    }

    public static double getProjectileSpeedModifier(final ItemStack weapon) {
        final int level = EnchantmentHelper
                .getItemEnchantmentLevel(ModEnchantments.ACCELERATOR.get(), weapon);
        if (level > 0) {
            return 1.0 + 0.0333 * level;
        }
        return 1.0;
    }

    public static float getAcceleratorDamage(final ItemStack weapon, final float damage) {
        final int level = EnchantmentHelper
                .getItemEnchantmentLevel(ModEnchantments.ACCELERATOR.get(), weapon);
        if (level > 0) {
            return damage + damage * (0.05F * level);
        }
        return damage;
    }

    public static float getBufferedRecoil(final ItemStack weapon) {
        final int level =
                EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.BUFFERED.get(), weapon);
        if (level > 0) {
            return (1 - (0.15F * level));
        }
        return 1;
    }

    public static float getPuncturingChance(final ItemStack weapon) {
        final int level =
                EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.PUNCTURING.get(), weapon);
        return level * 0.05F;
    }
}
