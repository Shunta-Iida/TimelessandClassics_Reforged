package com.tac.guns.common;

import java.util.Map;
import java.util.WeakHashMap;

import com.tac.guns.Reference;
import com.tac.guns.event.GunFireEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ShootTracker {
    private boolean isShooting = false;
    private boolean isTicked = false;
    private static final Map<Player, ShootTracker> SHOOT_TRACKER_MAP = new WeakHashMap<>();

    public static ShootTracker getShootTracker(final Player player) {
        return ShootTracker.SHOOT_TRACKER_MAP.computeIfAbsent(player,
                player1 -> new ShootTracker());
    }

    @SubscribeEvent
    public static void onGunFire(final GunFireEvent.Post event) {
        if (event.isClient())
            return;
        final ShootTracker tracker = ShootTracker.getShootTracker(event.getPlayer());
        tracker.isShooting = true;
        tracker.isTicked = false;
    }

    @SubscribeEvent
    public static void onServerTick(final TickEvent.ServerTickEvent event) {
        ShootTracker.SHOOT_TRACKER_MAP.values().forEach(tracker -> {
            if (tracker.isTicked)
                tracker.isShooting = false;
            else
                tracker.isTicked = true;
        });
    }

    public boolean isShooting() {
        return this.isShooting;
    }
}
