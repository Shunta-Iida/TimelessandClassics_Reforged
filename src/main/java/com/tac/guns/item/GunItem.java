package com.tac.guns.item;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import com.tac.guns.common.Gun;
import com.tac.guns.common.NetworkGunManager;
import com.tac.guns.init.ModItems;
import com.tac.guns.util.GunEnchantmentHelper;
import com.tac.guns.util.GunModifierHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.LootBonusEnchantment;
import net.minecraft.world.item.enchantment.QuickChargeEnchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class GunItem extends Item implements IColored {
    private final WeakHashMap<CompoundTag, Gun> modifiedGunCache = new WeakHashMap<>();

    private Gun gun = new Gun();

    public GunItem(final Item.Properties properties) {
        super(properties);
    }

    public void setGun(final NetworkGunManager.Supplier supplier) {
        this.gun = supplier.getGun();
    }

    public Gun getGun() {
        return this.gun;
    }

    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level worldIn,
            final List<Component> tooltip, final TooltipFlag flag) {
        final Gun modifiedGun = this.getModifiedGun(stack);

        final Item ammo = ForgeRegistries.ITEMS.getValue(modifiedGun.getProjectile().getItem());
        if (ammo != null) {
            tooltip.add(
                    new TranslatableComponent("info.tac.ammo_type",
                            new TranslatableComponent(ammo.getDescriptionId())
                                    .withStyle(ChatFormatting.WHITE))
                                            .withStyle(ChatFormatting.GRAY));
        }

        String additionalDamageText = "";
        final CompoundTag tagCompound = stack.getTag();
        if (tagCompound != null) {
            if (tagCompound.contains("AdditionalDamage", Tag.TAG_ANY_NUMERIC)) {
                float additionalDamage = tagCompound.getFloat("AdditionalDamage");
                additionalDamage += GunModifierHelper.getAdditionalDamage(stack);

                if (additionalDamage > 0) {
                    additionalDamageText = ChatFormatting.GREEN + " +"
                            + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalDamage);
                } else if (additionalDamage < 0) {
                    additionalDamageText = ChatFormatting.RED + " "
                            + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalDamage);
                }
            }
        }

        float damage = modifiedGun.getProjectile().getDamage();
        damage = GunModifierHelper.getModifiedProjectileDamage(stack, damage);
        damage = GunEnchantmentHelper.getAcceleratorDamage(stack, damage);
        tooltip.add(new TranslatableComponent("info.tac.damage", ChatFormatting.WHITE
                + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(damage) + additionalDamageText)
                        .withStyle(ChatFormatting.GRAY));

        if (tagCompound != null) {
            if (tagCompound.getBoolean("IgnoreAmmo")) {
                tooltip.add(new TranslatableComponent("info.tac.ignore_ammo")
                        .withStyle(ChatFormatting.AQUA));
            } else {
                final int ammoCount = tagCompound.getInt("AmmoCount");
                tooltip.add(new TranslatableComponent("info.tac.ammo",
                        ChatFormatting.WHITE.toString() + ammoCount + "/"
                                + GunModifierHelper.getAmmoCapacity(stack, modifiedGun))
                                        .withStyle(ChatFormatting.GRAY));
            }
        }

        tooltip.add(new TranslatableComponent("info.tac.attachment_help",
                new KeybindComponent("key.tac.attachments").getString().toUpperCase(Locale.ENGLISH))
                        .withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public boolean onEntitySwing(final ItemStack stack, final LivingEntity entity) {
        return true;
    }

    @Override
    public void fillItemCategory(final CreativeModeTab group, final NonNullList<ItemStack> stacks) {
        if (this.allowdedIn(group)) {
            final ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putInt("AmmoCount", this.gun.getReloads().getMaxAmmo());
            stacks.add(stack);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(final ItemStack oldStack, final ItemStack newStack,
            final boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public boolean isBarVisible(final ItemStack stack) {
        final CompoundTag tagCompound = stack.getOrCreateTag();
        final Gun modifiedGun = this.getModifiedGun(stack);
        return !tagCompound.getBoolean("IgnoreAmmo") && tagCompound
                .getInt("AmmoCount") != GunModifierHelper.getAmmoCapacity(stack, modifiedGun);
    }

    @Override
    public int getBarWidth(final ItemStack stack) {
        final CompoundTag tagCompound = stack.getOrCreateTag();
        final Gun modifiedGun = this.getModifiedGun(stack);
        return (int) (13.0 * (tagCompound.getInt("AmmoCount")
                / (double) GunModifierHelper.getAmmoCapacity(stack, modifiedGun)));
    }

    @Override
    public int getBarColor(final ItemStack p_150901_) {
        return Objects.requireNonNull(ChatFormatting.AQUA.getColor());
    }

    public Gun getModifiedGun(final ItemStack stack) {
        final CompoundTag tagCompound = stack.getTag();
        if (tagCompound != null && tagCompound.contains("Gun", Tag.TAG_COMPOUND)) {
            return this.modifiedGunCache.computeIfAbsent(tagCompound, item -> {
                if (tagCompound.getBoolean("Custom")) {
                    return Gun.create(tagCompound.getCompound("Gun"));
                } else {
                    final Gun gunCopy = this.gun.copy();
                    gunCopy.deserializeNBT(tagCompound.getCompound("Gun"));
                    return gunCopy;
                }
            });
        }
        return this.gun;
    }

    @Override
    public void inventoryTick(final ItemStack stack, final Level worldIn, final Entity entityIn,
            final int itemSlot, final boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        /*
         * if (isSelected && !worldIn.isClientSide)
         * {
         * if (entityIn instanceof Player)
         * {
         * Player playerEntity = (Player) entityIn;
         * if (!isSingleHanded(stack) && !DiscardOffhand.isSafeTime(playerEntity))
         * {
         * ItemStack offHand = playerEntity.getOffhandItem();
         * if (!(offHand.getItem() instanceof GunItem) && !offHand.isEmpty()) {
         * ItemEntity entity = playerEntity.drop(offHand, false);
         * playerEntity.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
         * if (entity != null)
         * {
         * entity.setNoPickUpDelay();
         * }
         * }
         * }
         * }
         * }
         */
    }

    public static boolean isSingleHanded(final ItemStack stack) {
        final Item item = stack.getItem();
        return item == ModItems.M1911.get() || item == ModItems.MICRO_UZI.get()
                || item == ModItems.CZ75.get() || item == ModItems.MK23.get();
    }


    // @Override
    // public boolean canApplyAtEnchantingTable(final ItemStack stack, final Enchantment enchantment) {
    //     if (enchantment.type == EnchantmentTypes.SEMI_AUTO_GUN) {
    //         final Gun modifiedGun = this.getModifiedGun(stack);
    //         return !modifiedGun.getGeneral().isAuto();
    //     }
    //     return super.canApplyAtEnchantingTable(stack, enchantment);
    // }

    // @Override
    // public boolean isEnchantable(final ItemStack stack) {
    //     return this.getItemStackLimit(stack) == 1;
    // }

    // @Override
    // public int getItemEnchantability() {
    //     return 5;
    // }

    @Override
    public boolean isBookEnchantable(final ItemStack stack, final ItemStack book) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(final ItemStack stack, final Enchantment enchantment) {

        // ドロップ増加
        if (enchantment instanceof LootBonusEnchantment
                && enchantment.category == EnchantmentCategory.WEAPON) {
            return true;
        }

        // 高速装填
        if (enchantment instanceof QuickChargeEnchantment) {
            return true;
        }

        return super.canApplyAtEnchantingTable(stack, enchantment);
    }
}
