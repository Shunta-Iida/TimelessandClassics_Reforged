package com.tac.guns.item.grenade;

import com.tac.guns.entity.ThrowableGrenadeEntity;
import com.tac.guns.entity.ThrowableStunGrenadeEntity;
import com.tac.guns.init.ModSounds;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class StunGrenadeItem extends GrenadeItem {
    public StunGrenadeItem(final Item.Properties properties, final int maxCookTime,
            final float speed) {
        super(properties, maxCookTime, 1, speed);
    }

    @Override
    public ThrowableGrenadeEntity create(final Level world, final LivingEntity entity,
            final int timeLeft) {
        return new ThrowableStunGrenadeEntity(world, entity, 20 * 2);
    }

    @Override
    public boolean canCook() {
        return false;
    }

    @Override
    protected void onThrown(final Level world, final ThrowableGrenadeEntity entity) {
        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                ModSounds.ITEM_GRENADE_PIN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
