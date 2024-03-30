package com.tac.guns.client;

import java.util.Map;

import com.tac.guns.Reference;
import com.tac.guns.common.NetworkRigManager;
import com.tac.guns.weapon.customs.CustomRig;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class CustomRigManager {
    private static Map<ResourceLocation, CustomRig> customRigMap;

    public static boolean updateCustomRigs(final NetworkRigManager.IRigProvider provider) {
        CustomRigManager.customRigMap = provider.getCustomRigs();
        return true;
    }

    public static void fill(final NonNullList<ItemStack> items) {
        /*
         * if(customRigMap != null)
         * {
         * customRigMap.forEach((id, gun) ->
         * {
         * ItemStack stack = new ItemStack(ModItems.AK47.get());
         * stack.setDisplayName(new TranslatableComponent("item." + id.getNamespace() +
         * "." + id.getPath() + ".name"));
         * CompoundNBT tag = stack.getOrCreateTag();
         *//*
                    * tag.put("Model", gun.getModel().serializeNBT());
                    * tag.put("Gun", gun.getGun().serializeNBT());
                    * tag.putBoolean("Custom", true);
                    * tag.putInt("AmmoCount", gun.getGun().getReloads().getMaxAmmo());
                    * tag.putIntArray("supportedFireModes",
                    * gun.getGun().getGeneral().getRateSelector());
                    *//*
                               * items.add(stack);
                               * });
                               * }
                               */
    }

    @SubscribeEvent
    public static void onClientDisconnect(final ClientPlayerNetworkEvent.LoggedOutEvent event) {
        CustomRigManager.customRigMap = null;
    }
}
