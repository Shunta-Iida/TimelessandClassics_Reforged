package com.tac.guns.common;

import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.tac.guns.Reference;
import com.tac.guns.duck.PlayerWithSynData;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.inventory.gear.armor.ArmorRigCapabilityProvider;
import com.tac.guns.inventory.gear.armor.RigSlotsHandler;
import com.tac.guns.item.gun.GunItem;
import com.tac.guns.item.gun.GunItemHelper;
import com.tac.guns.item.wearable.ArmorRigItem;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessageGunSound;
import com.tac.guns.util.GunEnchantmentHelper;
import com.tac.guns.util.WearableHelper;
import com.tac.guns.weapon.Gun;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ReloadTracker {
    private static final Map<Player, ReloadTracker> RELOAD_TRACKER_MAP = new WeakHashMap<>();

    private final int startTick;
    private final int slot;
    private final ItemStack stack;
    private final Gun gun;

    private ReloadTracker(final Player player) {
        this.startTick = player.tickCount;
        this.slot = player.getInventory().selected;
        this.stack = player.getInventory().getSelected();
        this.gun = ((GunItem) this.stack.getItem()).getGun(this.stack.getTag());
    }

    /**
     * Tests if the current item the player is holding is the same as the one being
     * reloaded
     *
     * @param player the player to check
     * @return True if it's the same weapon and slot
     */
    private boolean isSameWeapon(final Player player) {
        return !this.stack.isEmpty() && player.getInventory().selected == this.slot
                && player.getInventory().getSelected() == this.stack;
    }

    /**
     * @return
     */
    private boolean isWeaponFull() {
        final CompoundTag tag = this.stack.getOrCreateTag();
        return tag.getInt("AmmoCount") >= GunItemHelper.of(this.stack).getAmmoCapacity();
    }

    /**
     * @return
     */
    private boolean isWeaponEmpty() {
        final CompoundTag tag = this.stack.getOrCreateTag();
        return tag.getInt("AmmoCount") == 0;
    }

    private boolean hasNoAmmo(final Player player) {
        return Gun.findAmmo(player, this.gun.getProjectile().getItem()).length == 0 ? true
                : Gun.findAmmo(player, this.gun.getProjectile().getItem())[0].isEmpty();
    }

    private boolean canReload(final Player player) {
        boolean reload;
        final Gun gun = ((GunItem) this.stack.getItem()).getGun();
        final ItemStack rig = WearableHelper.PlayerWornRig(player);
        // MessageRigInvToClient
        if (gun.getReloads().isMagFed()) {
            if (this.isWeaponEmpty()) {
                final int deltaTicks = player.tickCount - this.startTick;
                int interval = gun.getReloads().getReloadMagTimer()
                        + gun.getReloads().getAdditionalReloadEmptyMagTimer()
                        + this.gun.getReloads().getPreReloadPauseTicks();// GunEnchantmentHelper.getReloadInterval(this.stack);
                interval /= GunEnchantmentHelper.getReloadSpeed(this.stack);
                reload = deltaTicks > interval;
            } else {
                final int deltaTicks = player.tickCount - this.startTick;
                int interval = gun.getReloads().getReloadMagTimer()
                        + this.gun.getReloads().getPreReloadPauseTicks();// GunEnchantmentHelper.getReloadInterval(this.stack);
                interval /= GunEnchantmentHelper.getReloadSpeed(this.stack);
                reload = deltaTicks > interval;
            }
        } else {
            final int deltaTicks = player.tickCount - this.startTick;
            int interval = gun.getReloads().getinterReloadPauseTicks();
            interval /= GunEnchantmentHelper.getReloadSpeed(this.stack);
            reload = deltaTicks > 0 && deltaTicks % interval == 0;
        }
        return reload;
    }

    private void increaseAmmo(final Player player) {
        final ItemStack ammo = Gun.findAmmo(player, this.gun.getProjectile().getItem())[0];
        if (!ammo.isEmpty()) {
            final CompoundTag tag = this.stack.getTag();
            int amount = Math.min(ammo.getCount(), this.gun.getReloads().getReloadAmount());
            if (tag != null) {
                final int maxAmmo = GunItemHelper.of(this.stack).getAmmoCapacity();
                amount = Math.min(amount, maxAmmo - tag.getInt("AmmoCount"));
                tag.putInt("AmmoCount", tag.getInt("AmmoCount") + amount);
            }
            // ammo.shrink(amount);
            this.shrinkFromAmmoPool(new ItemStack[] {ammo}, player, amount);
        }

        final ResourceLocation reloadSound = this.gun.getSounds().getReload();
        if (reloadSound != null) {
            final MessageGunSound message = new MessageGunSound(reloadSound, SoundSource.PLAYERS,
                    (float) player.getX(), (float) player.getY() + 1.0F, (float) player.getZ(),
                    1.0F, 1.0F, player.getId(), false, true);
            PacketHandler.getPlayChannel().send(PacketDistributor.NEAR.with(
                    () -> new PacketDistributor.TargetPoint(player.getX(), (player.getY() + 1.0),
                            player.getZ(), 16.0, player.level.dimension())),
                    message);
        }
    }

    public static int calcMaxReserveAmmo(final ItemStack[] ammoStacks) {
        int result = 0;
        for (final ItemStack x : ammoStacks)
            result += x.getCount();
        return result;
    }

    private void shrinkFromAmmoPool(final ItemStack[] ammoStacks, final Player player,
            final int shrinkAmount) {
        int shrinkAmt = shrinkAmount;
        final ArrayList<ItemStack> stacks = new ArrayList<>();

        final ItemStack rig = WearableHelper.PlayerWornRig(player);
        if (!rig.isEmpty() && !player.isCreative() && rig.getItem() instanceof ArmorRigItem) {
            final RigSlotsHandler itemHandler = (RigSlotsHandler) rig
                    .getCapability(ArmorRigCapabilityProvider.capability).resolve().get();
            for (final ItemStack x : itemHandler.getStacks()) {
                if (Gun.isAmmo(x, this.gun.getProjectile().getItem()))
                    stacks.add(x);
            }
            boolean flag = false;
            for (final ItemStack x : stacks) {
                if (!x.isEmpty()) {
                    final int max = Math.min(shrinkAmt, x.getCount());
                    x.shrink(max);
                    shrinkAmt -= max;
                }
                if (shrinkAmt == 0) {
                    flag = true;
                    break;
                }
            }
            ((PlayerWithSynData) player).updateRig();
            if (flag)
                return;
        }

        for (final ItemStack x : ammoStacks) {
            if (!x.isEmpty()) {
                final int max = Math.min(shrinkAmt, x.getCount());
                x.shrink(max);
                shrinkAmt -= max;
            }
            if (shrinkAmt == 0)
                return;
        }
    }

    private void increaseMagAmmo(final Player player) {
        /*
         * ItemStack rig = WearableHelper.PlayerWornRig(player);
         * if(rig != null) {
         * RigSlotsHandler itemHandler = (RigSlotsHandler)
         * rig.getCapability(ArmorRigCapabilityProvider.capability).resolve().get();
         * for (ItemStack x : itemHandler.getStacks()) {
         * if(Gun.isAmmo(x, this.gun.getProjectile().getItem()))
         * ammoStacks.add(x);
         * }
         * }
         */
        final ItemStack[] ammoStacks = Gun.findAmmo(player, this.gun.getProjectile().getItem());
        final int stackItor = 0;
        // ItemStack ammo = Gun.findAmmo(player, this.gun.getProjectile().getItem());
        if (ammoStacks.length > 0) {
            final CompoundTag tag = this.stack.getTag();
            final int ammoAmount = Math.min(ReloadTracker.calcMaxReserveAmmo(ammoStacks),
                    GunItemHelper.of(this.stack).getAmmoCapacity());
            if (tag != null) {
                final int currentAmmo = tag.getInt("AmmoCount");
                final int maxAmmo = GunItemHelper.of(this.stack).getAmmoCapacity();
                final int amount = maxAmmo - currentAmmo; // amount < maxAmmo ? maxAmmo - amount :
                if (currentAmmo == 0 && !this.gun.getReloads().isOpenBolt()) {
                    if (ammoAmount < amount) {
                        tag.putInt("AmmoCount", currentAmmo + ammoAmount);
                        this.shrinkFromAmmoPool(ammoStacks, player, ammoAmount);
                    } else {
                        tag.putInt("AmmoCount", maxAmmo - 1);
                        this.shrinkFromAmmoPool(ammoStacks, player, amount - 1);
                    }
                } else {
                    if (ammoAmount < amount) {
                        tag.putInt("AmmoCount", currentAmmo + ammoAmount);
                        this.shrinkFromAmmoPool(ammoStacks, player, ammoAmount);
                    } else {
                        tag.putInt("AmmoCount", maxAmmo);
                        this.shrinkFromAmmoPool(ammoStacks, player, amount);// ammoStacks.shrink(amount);
                    }
                }
            }
        }

        final ResourceLocation reloadSound = this.gun.getSounds().getReload();
        if (reloadSound != null) {
            final MessageGunSound message = new MessageGunSound(reloadSound, SoundSource.PLAYERS,
                    (float) player.getX(), (float) player.getY() + 1.0F, (float) player.getZ(),
                    1.0F, 1.0F, player.getId(), false, true);
            PacketHandler.getPlayChannel().send(PacketDistributor.NEAR.with(
                    () -> new PacketDistributor.TargetPoint(player.getX(), (player.getY() + 1.0),
                            player.getZ(), 16.0, player.level.dimension())),
                    message);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !event.player.level.isClientSide) {
            final Player player = event.player;
            if (SyncedEntityData.instance().get(player, ModSyncedDataKeys.RELOADING)) {
                if (!ReloadTracker.RELOAD_TRACKER_MAP.containsKey(player)) {
                    if (!(player.getInventory().getSelected().getItem() instanceof GunItem)) {
                        SyncedEntityData.instance().set(player, ModSyncedDataKeys.RELOADING, false);
                        SyncedEntityData.instance().set(player, ModSyncedDataKeys.STOP_ANIMA,
                                false);
                        return;
                    }
                    ReloadTracker.RELOAD_TRACKER_MAP.put(player, new ReloadTracker(player));
                }
                final ReloadTracker tracker = ReloadTracker.RELOAD_TRACKER_MAP.get(player);
                if (!tracker.isSameWeapon(player) || tracker.isWeaponFull()
                        || tracker.hasNoAmmo(player)) {
                    ReloadTracker.RELOAD_TRACKER_MAP.remove(player);
                    SyncedEntityData.instance().set(player, ModSyncedDataKeys.RELOADING, false);
                    SyncedEntityData.instance().set(player, ModSyncedDataKeys.STOP_ANIMA, true);
                    return;
                }
                if (tracker.canReload(player)) {
                    final Player finalPlayer = player;
                    final Gun gun = tracker.gun;
                    if (gun.getReloads().isMagFed()) {
                        tracker.increaseMagAmmo(player);
                        ReloadTracker.RELOAD_TRACKER_MAP.remove(player);
                        SyncedEntityData.instance().set(player, ModSyncedDataKeys.RELOADING, false);
                        SyncedEntityData.instance().set(player, ModSyncedDataKeys.STOP_ANIMA,
                                false);
                        /*
                         * DelayedTask.runAfter(2, () ->
                         * {
                         * ResourceLocation cockSound = gun.getSounds().getCock();
                         * if (cockSound != null && finalPlayer.isAlive()) {
                         * MessageGunSound messageSound = new MessageGunSound(cockSound,
                         * SoundCategory.PLAYERS, (float) finalPlayer.getPosX(), (float)
                         * (finalPlayer.getPosY() + 1.0), (float) finalPlayer.getPosZ(), 1.0F, 1.0F,
                         * finalPlayer.getEntityId(), false, true);
                         * PacketHandler.getPlayChannel().send(PacketDistributor.NEAR.with(() -> new
                         * PacketDistributor.TargetPoint(finalPlayer.getPosX(), (finalPlayer.getPosY() +
                         * 1.0), finalPlayer.getPosZ(), 16.0, finalPlayer.world.getDimensionKey())),
                         * messageSound);
                         * }
                         * });
                         */
                    } else {
                        tracker.increaseAmmo(player);
                        if (tracker.isWeaponFull() || tracker.hasNoAmmo(player)) {
                            ReloadTracker.RELOAD_TRACKER_MAP.remove(player);
                            SyncedEntityData.instance().set(player, ModSyncedDataKeys.RELOADING,
                                    false);
                            SyncedEntityData.instance().set(player, ModSyncedDataKeys.STOP_ANIMA,
                                    false);
                            /*
                             * DelayedTask.runAfter(4, () ->
                             * {
                             * ResourceLocation cockSound = gun.getSounds().getCock();
                             * if (cockSound != null && finalPlayer.isAlive()) {
                             * MessageGunSound messageSound = new MessageGunSound(cockSound,
                             * SoundCategory.PLAYERS, (float) finalPlayer.getPosX(), (float)
                             * (finalPlayer.getPosY() + 1.0), (float) finalPlayer.getPosZ(), 1.0F, 1.0F,
                             * finalPlayer.getEntityId(), false, true);
                             * PacketHandler.getPlayChannel().send(PacketDistributor.NEAR.with(() -> new
                             * PacketDistributor.TargetPoint(finalPlayer.getPosX(), (finalPlayer.getPosY() +
                             * 1.0), finalPlayer.getPosZ(), 16.0, finalPlayer.world.getDimensionKey())),
                             * messageSound);
                             * }
                             * });
                             */
                        }
                    }
                }
            } else if (ReloadTracker.RELOAD_TRACKER_MAP.containsKey(player)) {
                ReloadTracker.RELOAD_TRACKER_MAP.remove(player);
            }
        }
    }

    public static boolean isPlayerReload(final Player player) {
        return ReloadTracker.RELOAD_TRACKER_MAP.containsKey(player);
    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerEvent.PlayerLoggedOutEvent event) {
        final MinecraftServer server = event.getPlayer().getServer();
        if (server != null) {
            server.execute(() -> ReloadTracker.RELOAD_TRACKER_MAP.remove(event.getPlayer()));
        }
    }
}
