package com.tac.guns.item.grenade;

import com.tac.guns.entity.ThrowableGrenadeEntity;
import com.tac.guns.entity.specifics.BaseballGrenadeEntity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class BaseballGrenadeItem extends GrenadeItem {
    private final float power;

    public BaseballGrenadeItem(final Properties properties, final int maxCookTime,
            final float power, final float speed) {
        super(properties, maxCookTime, power, speed);
        this.power = power;
    }

    @Override
    public ThrowableGrenadeEntity create(final Level world, final LivingEntity entity,
            final int timeLeft) {
        return new BaseballGrenadeEntity(world, entity, timeLeft, this.power); // Current ThrowableGrenadeEntity is
                                                                               // perfect for impact 1/31/2022
    }

    @Override
    public boolean canCook() {
        return true;
    }

    @Override
    protected void onThrown(final Level world, final ThrowableGrenadeEntity entity) {
    }
}
