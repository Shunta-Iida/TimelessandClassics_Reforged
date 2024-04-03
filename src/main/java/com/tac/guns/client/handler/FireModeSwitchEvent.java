package com.tac.guns.client.handler;

import com.tac.guns.client.Keys;
import com.tac.guns.item.gun.GunItem;
import com.tac.guns.item.gun.GunItemHelper;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessageFireMode;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author: ClumsyAlien
 */
public class FireModeSwitchEvent {
    private static FireModeSwitchEvent instance;

    // TODO: remove this class maybe? Its function has been replaced by callback
    @Deprecated
    public static FireModeSwitchEvent get() {
        if (FireModeSwitchEvent.instance == null) {
            FireModeSwitchEvent.instance = new FireModeSwitchEvent();
        }
        return FireModeSwitchEvent.instance;
    }

    private FireModeSwitchEvent() {
        Keys.FIRE_SELECT.addPressCallback(() -> {
            if (!Keys.noConflict(Keys.FIRE_SELECT))
                return;

            final Player player = Minecraft.getInstance().player;
            if (player != null) {
                final ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof GunItem) {
                    HUDRenderingHandler.onChangeFireMode(GunItemHelper.of(stack).getNextFireMode());
                }
                PacketHandler.getPlayChannel().sendToServer(new MessageFireMode());
            }
        });
    }
}
