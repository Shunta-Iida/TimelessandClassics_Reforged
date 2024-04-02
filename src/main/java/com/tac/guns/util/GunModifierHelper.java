package com.tac.guns.util;

import com.tac.guns.interfaces.IGunModifier;
import com.tac.guns.item.gun.GunItem;
import com.tac.guns.weapon.Gun;
import com.tac.guns.weapon.attachment.IAttachmentItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class GunModifierHelper {
    private static final IGunModifier[] EMPTY = {};

    public static IGunModifier[] getModifiers(final ItemStack weapon,
            final IAttachmentItem.Type type) {
        final ItemStack stack = Gun.getAttachment(type, weapon);
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof IAttachmentItem) {
                final IAttachmentItem attachment = (IAttachmentItem) stack.getItem();
                return attachment.getAttachment().getModifiers();
            }
        }
        return GunModifierHelper.EMPTY;
    }

    public static IGunModifier[] getModifiers(final ItemStack weapon) {
        if (!weapon.isEmpty()) {
            if (weapon.getItem() instanceof GunItem) {
                final GunItem gunItem = (GunItem) weapon.getItem();
                return gunItem.getModifiers();
            }
        }
        return GunModifierHelper.EMPTY;
    }

    public static int getModifiedProjectileLife(final ItemStack weapon, int life) {
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                life = modifier.modifyProjectileLife(life);
            }
        }

        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            life = modifier.modifyProjectileLife(life);
        }
        return life;
    }

    public static double getModifiedProjectileGravity(final ItemStack weapon, double gravity) {
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                gravity = modifier.modifyProjectileGravity(gravity);
            }
        }

        final IGunModifier[] modifiersD = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiersD) {
            gravity = modifier.modifyProjectileGravity(gravity);
        }

        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                gravity += modifier.additionalProjectileGravity();
            }
        }

        final IGunModifier[] modifierD = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifierD) {
            gravity += modifier.additionalProjectileGravity();
        }
        return gravity;
    }

    public static float getModifiedSpread(final ItemStack weapon, float spread) {
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                spread = modifier.modifyProjectileSpread(spread);
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            spread = modifier.modifyProjectileSpread(spread);
        }
        return spread;
    }

    public static float getModifiedFirstShotSpread(final ItemStack weapon, float spread) {
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                spread = modifier.modifyFirstShotSpread(spread);
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            spread = modifier.modifyFirstShotSpread(spread);
        }
        return spread;
    }

    public static float getModifiedHipFireSpread(final ItemStack weapon, float spread) {
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                spread = modifier.modifyHipFireSpread(spread);
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            spread = modifier.modifyHipFireSpread(spread);
        }
        return spread;
    }

    public static double getModifiedProjectileSpeed(final ItemStack weapon, double speed) {
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                speed = modifier.modifyProjectileSpeed(speed);
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            speed = modifier.modifyProjectileSpeed(speed);
        }
        return speed;
    }

    public static float getFireSoundVolume(final ItemStack weapon) {
        float volume = 1.0F;
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                volume = modifier.modifyFireSoundVolume(volume);
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            volume = modifier.modifyFireSoundVolume(volume);
        }
        return Mth.clamp(volume, 0.0F, 16.0F);
    }

    public static double getMuzzleFlashSize(final ItemStack weapon, double size) {
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                size = modifier.modifyMuzzleFlashSize(size);
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            size = modifier.modifyMuzzleFlashSize(size);
        }
        return size;
    }

    public static float getKickReduction(final ItemStack weapon) {
        float kickReduction = 1.0F;
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                kickReduction *= Mth.clamp(modifier.kickModifier(), 0.0F, 1.0F);
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            kickReduction *= Mth.clamp(modifier.kickModifier(), 0.0F, 1.0F);
        }
        return 1.0F - kickReduction;
    }

    public static float getRecoilSmootheningTime(final ItemStack weapon) {
        float recoilTime = 1;
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                recoilTime *= Mth.clamp(modifier.modifyRecoilSmoothening(), 1.0F, 2.0F);
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            recoilTime *= Mth.clamp(modifier.modifyRecoilSmoothening(), 1.0F, 2.0F);
        }
        return recoilTime;
    }

    public static float getRecoilModifier(final ItemStack weapon) {
        float recoilReduction = 1.0F;
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                recoilReduction *= Mth.clamp(modifier.recoilModifier(), 0.0F, 1.0F);
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            recoilReduction *= Mth.clamp(modifier.recoilModifier(), 0.0F, 1.0F);
        }
        return 1.0F - recoilReduction;
    }

    public static float getHorizontalRecoilModifier(final ItemStack weapon) {
        float reduction = 1.0F;
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                reduction *= Mth.clamp(modifier.horizontalRecoilModifier(), 0.0F, 1.0F);
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            reduction *= Mth.clamp(modifier.horizontalRecoilModifier(), 0.0F, 1.0F);
        }
        return 1.0F - reduction;
    }

    public static boolean isSilencedFire(final ItemStack weapon) {
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                if (modifier.silencedFire()) {
                    return true;
                }
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            if (modifier.silencedFire()) {
                return true;
            }
        }
        return false;
    }


    public static ResourceLocation getFireSound(final ItemStack weapon) {
        if (!(weapon.getItem() instanceof GunItem)) {
            return null;
        }
        return GunModifierHelper.isSilencedFire(weapon)
                ? ((GunItem) weapon.getItem()).getGun(weapon.getTag()).getSounds().getSilencedFire()
                : ((GunItem) weapon.getItem()).getGun(weapon.getTag()).getSounds().getFire();
    }

    public static double getModifiedFireSoundRadius(final ItemStack weapon, final double radius) {
        double minRadius = radius;
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                final double newRadius = modifier.modifyFireSoundRadius(radius);
                if (newRadius < minRadius) {
                    minRadius = newRadius;
                }
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            final double newRadius = modifier.modifyFireSoundRadius(radius);
            if (newRadius < minRadius) {
                minRadius = newRadius;
            }
        }
        return Mth.clamp(minRadius, 0.0, Double.MAX_VALUE);
    }

    public static float getAdditionalDamage(final ItemStack weapon) {
        float additionalDamage = 0.0F;
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                additionalDamage += modifier.additionalDamage();
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            additionalDamage += modifier.additionalDamage();
        }
        return additionalDamage;
    }

    public static float getAdditionalHeadshotDamage(final ItemStack weapon) {
        float additionalDamage = 0.0F;
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                additionalDamage += modifier.additionalHeadshotDamage();
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            additionalDamage += modifier.additionalHeadshotDamage();
        }
        return additionalDamage;
    }

    public static float getModifiedProjectileDamage(final ItemStack weapon, final float damage) {
        float finalDamage = damage;
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                finalDamage = modifier.modifyProjectileDamage(finalDamage);
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            finalDamage = modifier.modifyProjectileDamage(finalDamage);
        }
        return finalDamage;
    }

    public static float getModifiedDamage(final ItemStack weapon, final Gun modifiedGun,
            final float damage) {
        float finalDamage = damage;
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                finalDamage = modifier.modifyProjectileDamage(finalDamage);
            }
        }
        final IGunModifier[] modifiersD1 = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiersD1) {
            finalDamage = modifier.modifyProjectileDamage(finalDamage);
        }
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                finalDamage += modifier.additionalDamage();
            }
        }
        final IGunModifier[] modifiersD2 = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiersD2) {
            finalDamage += modifier.additionalDamage();
        }
        return finalDamage;
    }

    public static double getModifiedAimDownSightSpeed(final ItemStack weapon, double speed) {
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                speed = modifier.modifyAimDownSightSpeed(speed);
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            speed = modifier.modifyAimDownSightSpeed(speed);
        }
        return Mth.clamp(speed, 0.01, Double.MAX_VALUE);
    }

    public static int getModifiedRate(final ItemStack weapon, int rate) {
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                rate = modifier.modifyFireRate(rate);
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            rate = modifier.modifyFireRate(rate);
        }
        return Mth.clamp(rate, 0, Integer.MAX_VALUE);
    }

    public static float getCriticalChance(final ItemStack weapon) {
        float chance = 0F;

        // for attachment types
        final IAttachmentItem.Type[] attachmentTypes = IAttachmentItem.Type.values();
        for (int i = 0; i < attachmentTypes.length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, attachmentTypes[i]);
            for (final IGunModifier modifier : modifiers) {
                chance += modifier.criticalChance();
            }
        }

        // for classic guns
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            chance += modifier.criticalChance();
        }

        // for enchantments
        chance += GunEnchantmentHelper.getPuncturingChance(weapon);

        return Mth.clamp(chance, 0F, 1F);
    }

    public static float getAdditionalWeaponWeight(final ItemStack weapon) {
        float additionalWeight = 0.0F;
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                additionalWeight += modifier.additionalWeaponWeight();
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            additionalWeight += modifier.additionalWeaponWeight();
        }
        return additionalWeight;
    }

    public static float getModifierOfWeaponWeight(final ItemStack weapon) {
        float modifierWeight = 0.0F;
        for (int i = 0; i < IAttachmentItem.Type.values().length; i++) {
            final IGunModifier[] modifiers =
                    GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.values()[i]);
            for (final IGunModifier modifier : modifiers) {
                modifierWeight += modifier.modifyWeaponWeight();
            }
        }
        final IGunModifier[] modifiers = GunModifierHelper.getModifiers(weapon);
        for (final IGunModifier modifier : modifiers) {
            modifierWeight += modifier.modifyWeaponWeight();
        }
        return modifierWeight;
    }

    public static String getAdditionalSkin(final ItemStack weapon) {
        final IGunModifier[] skin =
                GunModifierHelper.getModifiers(weapon, IAttachmentItem.Type.GUN_SKIN);

        if (skin.length > 0) {
            return skin[0].additionalSkin();
        }
        return "NONE";
    }
}
