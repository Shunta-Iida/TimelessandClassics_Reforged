package com.tac.guns.client.handler;

import com.tac.guns.client.Keys;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessageFireMode;

import net.minecraft.client.Minecraft;

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

            if (Minecraft.getInstance().player != null)
                PacketHandler.getPlayChannel().sendToServer(new MessageFireMode());
        });
    }
}
