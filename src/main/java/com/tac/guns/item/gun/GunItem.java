package com.tac.guns.item.gun;

import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import com.tac.guns.Config;
import com.tac.guns.GunMod;
import com.tac.guns.client.Keys;
import com.tac.guns.common.NetworkGunManager;
import com.tac.guns.common.network.ServerPlayHandler;
import com.tac.guns.init.ModItems;
import com.tac.guns.interfaces.IGunModifier;
import com.tac.guns.item.IColored;
import com.tac.guns.item.ItemAttributeValues;
import com.tac.guns.item.ItemAttributes;
import com.tac.guns.item.ammo.AmmoItem;
import com.tac.guns.util.GunEnchantmentHelper;
import com.tac.guns.util.GunModifierHelper;
import com.tac.guns.util.Process;
import com.tac.guns.weapon.Gun;
import com.tac.guns.weapon.attachment.IAttachmentItem;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
    private final IGunModifier[] modifiers;
    private Boolean integratedOptic = false;
    private Gun gun = new Gun();
    private AmmoItem ammoItem;
    private final WeakHashMap<CompoundTag, Gun> modifiedGunCache = new WeakHashMap<>();


    public GunItem(final Process<Item.Properties> properties, final IGunModifier... modifiers) {
        this(properties, false, modifiers);
    }

    public GunItem(final Process<Item.Properties> properties, final Boolean integratedOptic,
            final IGunModifier... modifiers) {
        super(properties.process(new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));
        this.modifiers = modifiers;
        this.integratedOptic = integratedOptic;
    }

    public GunItem() {
        this(properties -> properties);
    }

    public void setGun(final NetworkGunManager.Supplier supplier) {
        this.gun = supplier.getGun();
        final Item ammoItem = ForgeRegistries.ITEMS.getValue(this.gun.getProjectile().getItem());
        if (!(ammoItem instanceof AmmoItem)) {
            throw new IllegalArgumentException("Gun projectile item must be an AmmoItem");
        }
        this.ammoItem = (AmmoItem) ammoItem;
    }

    public Gun getGun() {
        return this.getGun(null, false);
    }

    public Gun getGun(final CompoundTag tagCompound) {
        return this.getGun(tagCompound, true);
    }

    public Gun getGun(final CompoundTag tagCompound, final boolean modified) {

        if (!modified)
            return this.gun;

        if (tagCompound != null && tagCompound.contains(ItemAttributes.Gun.GUN, Tag.TAG_COMPOUND)) {
            return this.modifiedGunCache.computeIfAbsent(tagCompound, item -> {
                if (tagCompound.getBoolean("Custom")) {
                    return Gun.create(tagCompound.getCompound(ItemAttributes.Gun.GUN));
                } else {
                    final Gun gunCopy = this.gun.copy();
                    gunCopy.deserializeNBT(tagCompound.getCompound(ItemAttributes.Gun.GUN));
                    return gunCopy;
                }
            });
        }

        return this.gun;
    }

    public Boolean isIntegratedOptic() {
        return this.integratedOptic;
    }

    // For Iris (Oculus) Mod flashLight shader integration
    public int getLightEmission(final Player player, final ItemStack stack) {
        if (stack.getItem() == ModItems.MP7.get())
            return 0;
        return 15;
    }

    @Override
    public boolean onEntitySwing(final ItemStack stack, final LivingEntity entity) {
        return true;
    }

    @Override
    public void fillItemCategory(final CreativeModeTab group, final NonNullList<ItemStack> stacks) {
        if (this.allowdedIn(group)) {
            final ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putInt(ItemAttributes.Gun.AMMO_COUNT,
                    this.gun.getReloads().getMaxAmmo());
            stack.getTag().putInt(ItemAttributes.Gun.AMMO_INSPECT_TYPE,
                    ItemAttributeValues.AmmoInspectType.NONE.toInt());
            stacks.add(stack);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(final ItemStack oldStack, final ItemStack newStack,
            final boolean slotChanged) {
        return slotChanged;
    }


    public AmmoItem getAmmoItem() {
        if (this.ammoItem == null) {
            throw new IllegalStateException("Ammo item has not been set for gun item");
        }
        return this.ammoItem;
    }

    public void setAmmoItem(final AmmoItem ammoItem) {
        this.ammoItem = ammoItem;
    }

    public static boolean isSingleHanded(final ItemStack stack) {
        final Item item = stack.getItem();
        return item == ModItems.M1911.get() || item == ModItems.MICRO_UZI.get()
                || item == ModItems.CZ75.get() || item == ModItems.MK23.get();
    }

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

    public IGunModifier[] getModifiers() {
        return this.modifiers;
    }

    @Override
    public int getBarColor(final ItemStack p_150901_) {
        return Objects.requireNonNull(ChatFormatting.GOLD.getColor());
    }

    @Override
    public boolean isBarVisible(final ItemStack stack) {
        if (Config.CLIENT.display.weaponAmmoBar.get()) {
            return true;
        } else
            return false;
    }

    @Override
    public int getBarWidth(final ItemStack stack) {
        if (stack.getTag() != null) {
            final CompoundTag tagCompound = stack.getTag();
            return Math.round(13.0F * (float) tagCompound.getInt(ItemAttributes.Gun.AMMO_COUNT)
                    / (float) GunItemHelper.of(stack).getAmmoCapacity());
        }
        return 0;
    }

    @Override
    public boolean isFoil(final ItemStack gunItem) {
        return false;
    }

    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level worldIn,
            final List<Component> tooltip, final TooltipFlag flag) {
        final Gun modifiedGun = this.getGun(stack.getTag());
        final Item ammo = ForgeRegistries.ITEMS.getValue(modifiedGun.getProjectile().getItem());
        if (ammo != null) {
            tooltip.add(
                    (new TranslatableComponent("info.tac.ammo_type",
                            new TranslatableComponent(ammo.getDescriptionId())
                                    .withStyle(ChatFormatting.GOLD))
                                            .withStyle(ChatFormatting.DARK_GRAY)));
        }

        String additionalDamageText = "";
        final CompoundTag tagCompound = stack.getTag();
        float additionalDamage;
        if (tagCompound != null && tagCompound.contains(ItemAttributes.Gun.ADDITIONAL_DAMAGE,
                Tag.TAG_ANY_NUMERIC)) {
            additionalDamage = tagCompound.getFloat(ItemAttributes.Gun.ADDITIONAL_DAMAGE);
            additionalDamage += GunModifierHelper.getAdditionalDamage(stack);
            if (additionalDamage > 0.0F) {
                additionalDamageText = ChatFormatting.GREEN + " +"
                        + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalDamage);
            } else if (additionalDamage < 0.0F) {
                additionalDamageText = ChatFormatting.RED + " "
                        + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalDamage);
            }
        }

        additionalDamage = modifiedGun.getProjectile().getDamage();
        additionalDamage = GunModifierHelper.getModifiedProjectileDamage(stack, additionalDamage);
        additionalDamage = GunEnchantmentHelper.getAcceleratorDamage(stack, additionalDamage);
        tooltip.add((new TranslatableComponent("info.tac.damage",
                ChatFormatting.GOLD + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalDamage)
                        + additionalDamageText)).withStyle(ChatFormatting.DARK_GRAY));
        if (tagCompound != null) {
            if (tagCompound.getBoolean(ItemAttributes.Gun.IGNORE_AMMO)) {
                tooltip.add((new TranslatableComponent("info.tac.ignore_ammo"))
                        .withStyle(ChatFormatting.AQUA));
            } else {
                final int ammoCount = tagCompound.getInt(ItemAttributes.Gun.AMMO_COUNT);
                tooltip.add((new TranslatableComponent("info.tac.ammo",
                        ChatFormatting.GOLD.toString() + ammoCount + "/"
                                + GunItemHelper.of(stack).getAmmoCapacity()))
                                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        final boolean isShift = Keys.MORE_INFO_HOLD.isDown();
        if (!isShift) {
            // String text = "SHIFT";
            // if(!InputHandler.MORE_INFO_HOLD.keyCode().equals(GLFW.GLFW_KEY_LEFT_SHIFT))
            tooltip.add((new TranslatableComponent("info.tac.more_info_gunitem",
                    Keys.MORE_INFO_HOLD.getTranslatedKeyMessage()))
                            .withStyle(ChatFormatting.YELLOW));
        }
        if (isShift) {
            final GunItem gun = (GunItem) stack.getItem();
            if (tagCompound != null) {
                final double armorPen =
                        gun.getGun().getProjectile().getGunArmorIgnore() >= 0 ? Math.min(
                                (Config.COMMON.gameplay.percentDamageIgnoresStandardArmor.get()
                                        * gun.getGun().getProjectile().getGunArmorIgnore() * 100),
                                100F) : 0F;
                tooltip.add((new TranslatableComponent("info.tac.armorPen",
                        new TranslatableComponent(String.format("%.1f", armorPen) + "%")
                                .withStyle(ChatFormatting.RED))
                                        .withStyle(ChatFormatting.DARK_AQUA)));

                final int headDamgeModifier = Config.COMMON.gameplay.headShotDamageMultiplier.get()
                        * gun.getGun().getProjectile().getGunHeadDamage() >= 0
                                ? (int) (Config.COMMON.gameplay.headShotDamageMultiplier.get()
                                        * gun.getGun().getProjectile().getGunHeadDamage() * 100)
                                : 0;
                tooltip.add((new TranslatableComponent("info.tac.headDamageModifier",
                        new TranslatableComponent(String.format("%d", headDamgeModifier) + "%")
                                .withStyle(ChatFormatting.RED))
                                        .withStyle(ChatFormatting.DARK_AQUA)));

                float speed = ServerPlayHandler.calceldGunWeightSpeed(gun.getGun(), stack);
                speed = Math.max(Math.min(speed, 0.1F), 0.075F);
                if (speed > 0.094f)
                    tooltip.add((new TranslatableComponent("info.tac.lightWeightGun",
                            new TranslatableComponent(-((int) ((0.1 - speed) * 1000)) + "%")
                                    .withStyle(ChatFormatting.RED))
                                            .withStyle(ChatFormatting.DARK_AQUA)));
                else if (speed < 0.095 && speed > 0.0875)
                    tooltip.add((new TranslatableComponent("info.tac.standardWeightGun",
                            new TranslatableComponent(-((int) ((0.1 - speed) * 1000)) + "%")
                                    .withStyle(ChatFormatting.RED))
                                            .withStyle(ChatFormatting.DARK_GREEN)));
                else
                    tooltip.add((new TranslatableComponent("info.tac.heavyWeightGun",
                            new TranslatableComponent(-((int) ((0.1 - speed) * 1000)) + "%")
                                    .withStyle(ChatFormatting.RED))
                                            .withStyle(ChatFormatting.DARK_RED)));

                final float percentageToNextLevel =
                        (tagCompound.getFloat(ItemAttributes.Gun.LEVEL_DMG) * 100) / (modifiedGun
                                .getGeneral().getLevelReq()
                                * (((tagCompound.getInt(ItemAttributes.Gun.LEVEL)) * 3.0f)));
                tooltip.add((new TranslatableComponent("info.tac.current_level").append(
                        new TranslatableComponent(" " + tagCompound.getInt(ItemAttributes.Gun.LEVEL)
                                + " : " + String.format("%.2f", percentageToNextLevel) + "%")))
                                        .withStyle(ChatFormatting.GRAY)
                                        .withStyle(ChatFormatting.BOLD));
            }

            tooltip.add((new TranslatableComponent("info.tac.attachment_help",
                    Keys.ATTACHMENTS.getTranslatedKeyMessage())).withStyle(ChatFormatting.YELLOW));
            if (gun.getGun().canAttachType(IAttachmentItem.Type.PISTOL_SCOPE))
                tooltip.add((new TranslatableComponent("info.tac.pistolScope",
                        new TranslatableComponent("slot.tac.attachment.pistol_scope")
                                .withStyle(ChatFormatting.BOLD))
                                        .withStyle(ChatFormatting.LIGHT_PURPLE)));
            if (gun.getGun().canAttachType(IAttachmentItem.Type.IR_DEVICE)) {
                tooltip.add(
                        (new TranslatableComponent("info.tac.irLaserEquip",
                                new TranslatableComponent("slot.tac.attachment.ir_device")
                                        .withStyle(ChatFormatting.BOLD))
                                                .withStyle(ChatFormatting.AQUA)));
            }
            if (gun.getGun().canAttachType(IAttachmentItem.Type.PISTOL_BARREL)) {
                tooltip.add((new TranslatableComponent("info.tac.pistolBarrel",
                        new TranslatableComponent("slot.tac.attachment.pistol_barrel")
                                .withStyle(ChatFormatting.BOLD))
                                        .withStyle(ChatFormatting.LIGHT_PURPLE)));
            }

        }
    }
}
