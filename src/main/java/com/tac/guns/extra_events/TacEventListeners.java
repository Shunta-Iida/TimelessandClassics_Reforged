package com.tac.guns.extra_events;

import org.apache.logging.log4j.Level;

import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.event.GunFireEvent;
import com.tac.guns.event.LevelUpEvent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: ClumsyAlien
 */

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TacEventListeners {

    /*
     * A bit decent bit of extra code will be locked in external methods such as
     * this, separating some of the standard and advanced
     * Functions, especially in order to keep it all clean and allow easy
     * backtracking, however both functions may receive changes
     * For now as much of the work I can do will be kept externally such as with
     * fire selection, and burst fire.
     * (In short this serves as a temporary test bed to keep development on new
     * functions on course)
     */

    private static boolean checked = true;
    private static boolean confirmed = false;
    private static VersionChecker.CheckResult status;

    @SubscribeEvent
    public static void InformPlayerOfUpdate(final EntityJoinWorldEvent e) {
        try {
            if (!(e.getEntity() instanceof Player))
                return;

            if (TacEventListeners.checked) {
                if (GunMod.modInfo != null) {
                    TacEventListeners.status = VersionChecker.getResult(GunMod.modInfo);
                    TacEventListeners.checked = false;
                }
            }
            if (!TacEventListeners.confirmed) {
                if (TacEventListeners.status.status() == VersionChecker.Status.OUTDATED
                        || TacEventListeners.status
                                .status() == VersionChecker.Status.BETA_OUTDATED) {
                    ((Player) e.getEntity()).displayClientMessage(new TranslatableComponent(
                            "updateCheck.tac", TacEventListeners.status.target(),
                            TacEventListeners.status.url()), false);
                    TacEventListeners.confirmed = true;
                }
            }
        } catch (final Exception ev) {
            GunMod.LOGGER.log(Level.ERROR, ev.getMessage());
            return;
        }
        GunMod.LOGGER.log(Level.INFO, TacEventListeners.status.status());
    }

    @SubscribeEvent
    public void onPartialLevel(final LevelUpEvent.Post event) {
        final Player player = event.getPlayer();
        event.getPlayer().getCommandSenderWorld().playSound(player, player.blockPosition(),
                ForgeRegistries.SOUND_EVENTS
                        .getValue(new ResourceLocation("entity.experience_orb.pickup")),
                SoundSource.PLAYERS, 4.0F, 1.0F);
    }

    // TODO: remaster method to play empty fire sound on most-all guns
    /* BTW this was by bomb787 as a Timeless Contributor */
    @SubscribeEvent
    public static void postShoot(final GunFireEvent.Post event) {
        final Player player = event.getPlayer();
        final ItemStack heldItem = player.getMainHandItem();
        final CompoundTag tag = heldItem.getTag();
        if (tag != null) {
            if (tag.getInt("AmmoCount") == 1) {
                // only play in client side
                // final EntityBoundSoundInstance sound = new EntityBoundSoundInstance(
                //         ModSounds.M1_PING.get(), SoundSource.PLAYERS, 0.25F, 0.9F, player);
                // Minecraft.getInstance().getSoundManager().play(sound);

                // event.getPlayer().getCommandSenderWorld().playSound(player, player.blockPosition(),
                //         ModSounds.M1_PING.get()/* .GARAND_PING.get() */, SoundSource.MASTER, 1.0F,
                //         1.0F);
            }
        }
    }

    /*
     * @SubscribeEvent(priority = EventPriority.HIGHEST)
     * public static void handleDeathWithArmor(LivingDeathEvent event)
     * {
     * if(event.getEntity() instanceof PlayerEntity)
     * {
     * PlayerEntity player = (PlayerEntity) event.getEntityLiving();
     * if(WearableHelper.PlayerWornRig(player) != null)
     * {
     * GearSlotsHandler ammoItemHandler = (GearSlotsHandler)
     * player.getCapability(ITEM_HANDLER_CAPABILITY).resolve().get();
     * Block.spawnAsEntity(player.world, player.getPosition(),
     * (ammoItemHandler.getStackInSlot(0)));
     * Block.spawnAsEntity(player.world, player.getPosition(),
     * (ammoItemHandler.getStackInSlot(1)));
     * }
     * }
     * // TODO: Continue for dropping armor on a bot's death
     * }
     */

}
