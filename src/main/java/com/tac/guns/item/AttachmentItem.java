package com.tac.guns.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.tac.guns.interfaces.IGunModifier;
import com.tac.guns.item.attachment.IAttachment;
import com.tac.guns.item.attachment.impl.Attachment;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public abstract class AttachmentItem<T extends Attachment> extends Item implements IAttachment<T> {
    private final T attachment;

    public AttachmentItem(T attachment, Properties properties) {
        super(properties);
        this.attachment = attachment;
    }

    @Override
    public T getProperties() {
        return this.attachment;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.BINDING_CURSE
                || super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip,
            TooltipFlag flag) {

        // add the attachment type to the tooltip
        tooltip.add(new TranslatableComponent(this.getType().getTranslationKey()));

        if (stack.getItem() instanceof IAttachment<?>) {
            IAttachment<?> attachment = (IAttachment<?>) stack.getItem();
            List<Component> perks = attachment.getProperties().getPerks();

            // for classic attachments
            if (perks != null && perks.size() > 0) {
                tooltip.add(new TranslatableComponent("perk.tac.title")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
                tooltip.addAll(perks);
                return;
            }

            IGunModifier[] modifiers = attachment.getProperties().getModifiers();
            List<Component> positivePerks = new ArrayList<>();
            List<Component> negativePerks = new ArrayList<>();

            /* Test for fire sound volume */
            /*
             * float inputSound = 1.0F;
             * float outputSound = inputSound;
             * for (IGunModifier modifier : modifiers) {
             * outputSound = modifier.modifyFireSoundVolume(outputSound);
             * }
             * if (outputSound > inputSound) {
             * addPerk(negativePerks, "perk.tac.fire_volume.negative", new
             * TranslatableComponent("+" + String.valueOf((1.0F -
             * Math.round(outputSound)) * 100) +
             * "% Volume").withStyle(ChatFormatting.RED));
             * } else if (outputSound < inputSound) {
             * addPerk(negativePerks, "perk.tac.fire_volume.negative", new
             * TranslatableComponent("" + String.valueOf((1.0F -
             * Math.round(outputSound)) * 100) +
             * "% Volume").withStyle(ChatFormatting.GREEN));
             * //addPerk(positivePerks, "perk.tac.fire_volume.positive",
             * ChatFormatting.GREEN, "-" + String.valueOf((1.0F - outputSound) * 100) +
             * new TranslatableComponent("perk.tac.vol"));
             * }
             */

            /* Test for silenced */
            for (IGunModifier modifier : modifiers) {
                if (modifier.silencedFire()) {
                    addPerkP(positivePerks, "perk.tac.silenced.positive",
                            new TranslatableComponent("perk.tac.silencedv2")
                                    .withStyle(ChatFormatting.GREEN));
                    break;
                }
            }

            /* Test for sound radius */
            double inputRadius = 10.0;
            double outputRadius = inputRadius;
            for (IGunModifier modifier : modifiers) {
                outputRadius = modifier.modifyFireSoundRadius(outputRadius);
            }
            if (outputRadius > inputRadius) {
                addPerkN(negativePerks, "perk.tac.sound_radius.negative", new TranslatableComponent(
                        "perk.tac.sound_radiusv2", "+" + Math.round(outputRadius)));
            } else if (outputRadius < inputRadius) {
                addPerkP(positivePerks, "perk.tac.sound_radius.positive", new TranslatableComponent(
                        "perk.tac.sound_radiusv2", "-" + Math.round(outputRadius)));
            }

            /* Test for additional damage */
            float additionalDamage = 0.0F;
            for (IGunModifier modifier : modifiers) {
                additionalDamage += modifier.additionalDamage();
            }
            if (additionalDamage > 0.0F) {
                addPerkP(positivePerks, "perk.tac.additional_damage.positivev2",
                        ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalDamage / 2.0));
            } else if (additionalDamage < 0.0F) {
                addPerkN(negativePerks, "perk.tac.additional_damage.negativev2",
                        ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalDamage / 2.0));
            }

            /* Test for additional headshot damage */
            float additionalHeadshotDamage = 0.0F;
            for (IGunModifier modifier : modifiers) {
                additionalHeadshotDamage += modifier.additionalHeadshotDamage();
            }
            if (additionalHeadshotDamage > 0.0F) {
                addPerkP(positivePerks, "perk.tac.additional_damage.positiveh",
                        ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalHeadshotDamage / 2.0));
            } else if (additionalHeadshotDamage < 0.0F) {
                addPerkN(negativePerks, "perk.tac.additional_damage.negativeh",
                        ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalHeadshotDamage / 2.0));
            }

            /* Test for modified damage */
            float inputDamage = 10.0F;
            float outputDamage = inputDamage;
            for (IGunModifier modifier : modifiers) {
                outputDamage = modifier.modifyProjectileDamage(outputDamage);
            }
            if (outputDamage > inputDamage) {
                addPerkP(positivePerks, "perk.tac.modified_damage.positive",
                        new TranslatableComponent("perk.tac.modified_damage.positivev2",
                                outputDamage).withStyle(ChatFormatting.GREEN));
            } else if (outputDamage < inputDamage) {
                addPerkN(positivePerks, "perk.tac.modified_damage.negative",
                        new TranslatableComponent("perk.tac.modified_damage.negativev2",
                                outputDamage).withStyle(ChatFormatting.RED));
            }

            /* Test for modified damage */
            double inputSpeed = 10.0;
            double outputSpeed = inputSpeed;
            for (IGunModifier modifier : modifiers) {
                outputSpeed = modifier.modifyProjectileSpeed(outputSpeed);
            }
            if (outputSpeed > inputSpeed) {
                addPerkP(positivePerks, "perk.tac.projectile_speed.positive",
                        new TranslatableComponent("perk.tac.projectile_speed.positivev2",
                                Math.round((10.0F - outputSpeed) * 10) + "%"));
            } else if (outputSpeed < inputSpeed) {
                addPerkN(negativePerks, "perk.tac.projectile_speed.negative",
                        new TranslatableComponent("perk.tac.projectile_speed.negativev2",
                                Math.round((10.0F - outputSpeed) * 10) + "%"));
            }

            /* Test for modified projectile spread */
            float inputSpread = 10.0F;
            float outputSpread = inputSpread;
            for (IGunModifier modifier : modifiers) {
                outputSpread = modifier.modifyProjectileSpread(outputSpread);
            }
            if (outputSpread > inputSpread) {
                addPerkN(negativePerks, "perk.tac.projectile_spread.negative",
                        new TranslatableComponent("perk.tac.projectile_spread.negativev2",
                                Math.round((10.0F - outputSpread) * 10) + "%")
                                        .withStyle(ChatFormatting.RED));
            } else if (outputSpread < inputSpread) {
                addPerkP(positivePerks, "perk.tac.projectile_spread.positive",
                        new TranslatableComponent("perk.tac.projectile_spread.positivev2",
                                Math.round((10.0F - outputSpread) * 10) + "%")
                                        .withStyle(ChatFormatting.GREEN));
            }

            /* Test for modified projectile spread */
            float inputFirstSpread = 10.0F;
            float outputFirstSpread = inputFirstSpread;
            for (IGunModifier modifier : modifiers) {
                outputFirstSpread = modifier.modifyFirstShotSpread(outputFirstSpread);
            }
            if (outputFirstSpread > inputFirstSpread) {
                addPerkN(negativePerks, "perk.tac.projectile_spread_first.negativev2",
                        String.valueOf(Math.round((10.0F - outputFirstSpread) * 10f)) + "%");
            } else if (outputFirstSpread < inputFirstSpread) {
                addPerkP(positivePerks, "perk.tac.projectile_spread_first.positivev2",
                        "+" + String.valueOf(Math.round((10.0F - outputFirstSpread) * 10f)) + "%");
            }

            /* Test for modified projectile spread */
            float inputHipFireSpread = 10.0F;
            float outputHipFireSpread = inputHipFireSpread;
            for (IGunModifier modifier : modifiers) {
                outputHipFireSpread = modifier.modifyHipFireSpread(outputHipFireSpread);
            }
            if (outputHipFireSpread > inputHipFireSpread) {
                addPerkN(negativePerks, "perk.tac.projectile_spread_hip.negativev2",
                        String.valueOf(Math.round((10.0F - outputHipFireSpread) * 10f)) + "%");
            } else if (outputHipFireSpread < inputHipFireSpread) {
                addPerkP(positivePerks, "perk.tac.projectile_spread_hip.positivev2", "+"
                        + String.valueOf(Math.round((10.0F - outputHipFireSpread) * 10f)) + "%");
            }

            /* Test for modified projectile life */
            int inputLife = 100;
            int outputLife = inputLife;
            for (IGunModifier modifier : modifiers) {
                outputLife = modifier.modifyProjectileLife(outputLife);
            }
            if (outputLife > inputLife) {
                addPerkP(positivePerks, "perk.tac.projectile_life.positivev2",
                        String.valueOf(outputLife));
            } else if (outputLife < inputLife) {
                addPerkN(positivePerks, "perk.tac.projectile_life.negativev2",
                        String.valueOf(outputLife));
            }

            /* Test for modified recoil */
            float inputRecoil = 10.0F;
            float outputRecoil = inputRecoil;
            for (IGunModifier modifier : modifiers) {
                outputRecoil *= modifier.recoilModifier();
            }
            if (outputRecoil > inputRecoil) {
                addPerkN(negativePerks, "perk.tac.recoil.negativev2",
                        String.valueOf(Math.round((10.0F - outputRecoil) * -10f)) + "%");
            } else if (outputRecoil < inputRecoil) {
                addPerkP(positivePerks, "perk.tac.recoil.positivev2",
                        String.valueOf(Math.round((10.0F - outputRecoil) * -10f)) + "%");
            }

            float inputHRecoil = 10.0F;
            float outputHRecoil = inputHRecoil;
            for (IGunModifier modifier : modifiers) {
                outputHRecoil *= modifier.horizontalRecoilModifier();
            }
            if (outputHRecoil > inputHRecoil) {
                addPerkN(negativePerks, "perk.tac.recoilh.negativev2",
                        String.valueOf(Math.round((10.0F - outputHRecoil) * -10f)) + "%");
            } else if (outputHRecoil < inputHRecoil) {
                addPerkP(positivePerks, "perk.tac.recoilh.positivev2",
                        String.valueOf(Math.round((10.0F - outputHRecoil) * -10f)) + "%");
            }

            /* Test for aim down sight speed */
            double inputAdsSpeed = 10.0;
            double outputAdsSpeed = inputAdsSpeed;
            for (IGunModifier modifier : modifiers) {
                outputAdsSpeed = modifier.modifyAimDownSightSpeed(outputAdsSpeed);
            }
            if (outputAdsSpeed > inputAdsSpeed) {
                addPerkP(positivePerks, "perk.tac.ads_speed.positivev2",
                        String.valueOf(Math.round((10.0F - outputAdsSpeed) * 10f)) + "%");
            } else if (outputAdsSpeed < inputAdsSpeed) {
                addPerkN(negativePerks, "perk.tac.ads_speed.negativev2",
                        String.valueOf(Math.round((10.0F - outputAdsSpeed) * 10f)) + "%");
            }

            /* Test for fire rate */
            int inputRate = 10;
            int outputRate = inputRate;
            for (IGunModifier modifier : modifiers) {
                outputRate = modifier.modifyFireRate(outputRate);
            }
            if (outputRate > inputRate) {
                addPerkN(negativePerks, "perk.tac.rate.negative");
            } else if (outputRate < inputRate) {
                addPerkP(positivePerks, "perk.tac.rate.positive");
            }

            // merge perks
            positivePerks.addAll(negativePerks);
            attachment.getProperties().setPerks(positivePerks);
            if (positivePerks.size() > 0) {
                tooltip.addAll(positivePerks);
            }

        }
    }

    private static void addPerk(List<Component> components, String id, Object... params) {
        // ChatFormatting format, components.add(new
        // TranslatableComponent("perk.tac.entry.negative", new
        // TranslatableComponent(id, params).withStyle(format)));
        components.add(new TranslatableComponent("perk.tac.entry.negative",
                new TranslatableComponent(id, params).withStyle(ChatFormatting.AQUA)));
    }

    private static void addPerkP(List<Component> components, String id, Object... params) {
        // ChatFormatting format, components.add(new
        // TranslatableComponent("perk.tac.entry.negative", new
        // TranslatableComponent(id, params).withStyle(format)));
        components.add(new TranslatableComponent(id, params).withStyle(ChatFormatting.GREEN));
    }

    private static void addPerkN(List<Component> components, String id, Object... params) {
        // ChatFormatting format, components.add(new
        // TranslatableComponent("perk.tac.entry.negative", new
        // TranslatableComponent(id, params).withStyle(format)));
        components.add(new TranslatableComponent(id, params).withStyle(ChatFormatting.RED));
    }
}
