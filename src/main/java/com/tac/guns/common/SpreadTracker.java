package com.tac.guns.common;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.commons.lang3.tuple.Pair;

import com.tac.guns.Reference;
import com.tac.guns.item.transition.GunItem;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class SpreadTracker {
    private static final Map<Player, SpreadTracker> TRACKER_MAP = new WeakHashMap<>();

    private final Map<GunItem, Pair<MutableLong, MutableInt>> SPREAD_TRACKER_MAP = new HashMap<>();

    public void update(final Player player, final GunItem item) {
        final Pair<MutableLong, MutableInt> entry = this.SPREAD_TRACKER_MAP.computeIfAbsent(item,
                gun -> Pair.of(new MutableLong(-1), new MutableInt()));
        final MutableLong lastFire = entry.getLeft();
        final Gun gun = item.getGun();
        if (lastFire.getValue() != -1) {
            final MutableInt spreadCount = entry.getRight();
            final long deltaTime = System.currentTimeMillis() - lastFire.getValue();
            if (deltaTime < gun.getGeneral().getMsToAccuracyReset()) {
                if (spreadCount.getValue() < gun.getGeneral().getProjCountAccuracy()) {
                    spreadCount.increment();

                    /* Increases the spread count quicker if the player is not aiming down sight *//*
                                                                                                    * if(spreadCount.
                                                                                                    * getValue() <
                                                                                                    * gun.getGeneral().
                                                                                                    * getProjCountAccuracy
                                                                                                    * () &&
                                                                                                    * !SyncedPlayerData.
                                                                                                    * instance().get(
                                                                                                    * player,
                                                                                                    * ModSyncedDataKeys.
                                                                                                    * AIMING))
                                                                                                    * {
                                                                                                    * spreadCount.
                                                                                                    * increment();
                                                                                                    * }
                                                                                                    */
                }
            } else {
                spreadCount.setValue(0);
            }
        }
        lastFire.setValue(System.currentTimeMillis());
    }

    public float getSpread(final GunItem item) {
        final Pair<MutableLong, MutableInt> entry = this.SPREAD_TRACKER_MAP.get(item);
        final Gun gun = item.getGun();
        if (entry != null) {
            if (entry.getRight().getValue() == 1)
                return 0f; // Will apply first shot accuracy if 0
            return (float) entry.getRight().getValue()
                    / (float) gun.getGeneral().getProjCountAccuracy();
        }
        return 0f;
    }

    public static SpreadTracker get(final Player player) {
        return SpreadTracker.TRACKER_MAP.computeIfAbsent(player, player1 -> new SpreadTracker());
    }

    @SubscribeEvent
    public static void onPlayerDisconnect(final PlayerEvent.PlayerLoggedOutEvent event) {
        final MinecraftServer server = event.getPlayer().getServer();
        if (server != null) {
            server.execute(() -> SpreadTracker.TRACKER_MAP.remove(event.getPlayer()));
        }
    }
}
