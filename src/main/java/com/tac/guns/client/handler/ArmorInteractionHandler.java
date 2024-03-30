package com.tac.guns.client.handler;

import org.apache.logging.log4j.Level;

import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.tac.guns.GunMod;
import com.tac.guns.client.Keys;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.wearable.ArmorRigItem;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessageArmorRepair;
import com.tac.guns.util.WearableHelper;
import com.tac.guns.weapon.Rig;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class ArmorInteractionHandler {
    private static ArmorInteractionHandler instance;

    public static ArmorInteractionHandler get() {
        if (ArmorInteractionHandler.instance == null) {
            ArmorInteractionHandler.instance = new ArmorInteractionHandler();
        }
        return ArmorInteractionHandler.instance;
    }

    private static final double MAX_AIM_PROGRESS = 4;
    // TODO: Only commented, since we may need to track players per client for
    // future third person animation ... private final Map<PlayerEntity, AimTracker>
    // aimingMap = new WeakHashMap<>();
    private double normalisedRepairProgress;
    private int totalPlatesToRepair;
    private boolean repairing = false;

    public boolean getRepairing() {
        return this.repairing;
    }

    private int repairTime = -1;
    private int prevRepairTime = 0;

    private ArmorInteractionHandler() {
        Keys.ARMOR_REPAIRING.addPressCallback(() -> {
            if (!Keys.noConflict(Keys.ARMOR_REPAIRING))
                return;
            this.initializeRepairing(false);
        });
    }

    // Made public so interrupts can simply reset the armor repairing process
    // true = armor will attempt to repair again, false = armor has been reset.
    public void resetRepairProgress(final boolean isAnotherPlateRepairing) {
        if (isAnotherPlateRepairing) {
            if (this.initializeRepairing(true))
                return;
        } else
            this.totalPlatesToRepair = 0;
        this.repairing = false;
        this.repairTime = 0;
        this.prevRepairTime = 0;
    }

    private boolean initializeRepairing(final boolean isAnotherPlateRepairing) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            final ItemStack rigStack = WearableHelper.PlayerWornRig(mc.player);
            if (!rigStack.isEmpty() && !WearableHelper.isFullDurability(rigStack)) {
                final Rig rig = ((ArmorRigItem) rigStack.getItem()).getRig();
                if (rig.getRepair().isQuickRepairable()) {
                    final Item repairItem =
                            ForgeRegistries.ITEMS.getValue(rig.getRepair().getItem());
                    if (repairItem == null) {
                        GunMod.LOGGER.log(Level.ERROR,
                                rig.getRepair().getItem() + " | Is not a real / registered item.");
                        return false;
                    }
                    int loc = -1;
                    for (int i = 0; i < mc.player.getInventory().getContainerSize(); ++i) {
                        final ItemStack stack = mc.player.getInventory().getItem(i);
                        if (!stack.isEmpty() && stack.getItem().getRegistryName()
                                .equals(rig.getRepair().getItem())) {
                            loc = i;
                        }
                    }
                    if (loc > -1) {
                        this.repairing = true;
                        this.repairTime = rig.getRepair().getTicksToRepair();
                        if (!isAnotherPlateRepairing) {
                            final float rawPlates = (rig.getRepair().getDurability()
                                    - WearableHelper.GetCurrentDurability(rigStack))
                                    / (rig.getRepair().getDurability()
                                            * rig.getRepair().getQuickRepairability());
                            this.totalPlatesToRepair =
                                    rawPlates > (int) rawPlates ? (int) (rawPlates + 1)
                                            : (int) rawPlates;
                            if (this.totalPlatesToRepair > mc.player.getInventory().getItem(loc)
                                    .getCount())
                                this.totalPlatesToRepair =
                                        mc.player.getInventory().getItem(loc).getCount();
                        }
                        SyncedEntityData.instance().set(mc.player, ModSyncedDataKeys.QREPAIRING,
                                true);
                    } else {
                        GunMod.LOGGER.log(Level.WARN, rig.getRepair().getItem()
                                + " | Is not found in local player inventory.");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public float getRepairProgress(final Player player) {
        if (WearableHelper.PlayerWornRig(player).isEmpty())
            return 0;
        return this.repairTime > 0 ? ((float) this.repairTime)
                / (float) ((ArmorRigItem) WearableHelper.PlayerWornRig(player).getItem()).getRig()
                        .getRepair().getTicksToRepair()
                : 1F;
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START)
            return;
        final Player player = Minecraft.getInstance().player;
        if (player == null)
            return;
        if (WearableHelper.PlayerWornRig(player).isEmpty()) {
            this.repairing = false;
            return;
        }

        if (this.repairing) {
            if (this.repairTime == 0) {
                this.totalPlatesToRepair--;
                final boolean canRepairAgain = this.totalPlatesToRepair > 0;
                this.resetRepairProgress(canRepairAgain);
                PacketHandler.getPlayChannel().sendToServer(new MessageArmorRepair());

            } else {
                this.prevRepairTime = this.repairTime;
                this.repairTime--;
            }
            GunMod.LOGGER.log(Level.WARN, this.repairTime + " | " + this.totalPlatesToRepair);
        }

    }
}
