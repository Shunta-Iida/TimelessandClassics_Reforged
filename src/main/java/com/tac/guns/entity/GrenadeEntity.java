package com.tac.guns.entity;

import com.tac.guns.item.gun.GunItem;
import com.tac.guns.weapon.Gun;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class GrenadeEntity extends ProjectileEntity {
    public GrenadeEntity(final EntityType<? extends ProjectileEntity> entityType,
            final Level world) {
        super(entityType, world);
    }

    public GrenadeEntity(final EntityType<? extends ProjectileEntity> entityType, final Level world,
            final LivingEntity shooter, final ItemStack weapon, final GunItem item,
            final Gun modifiedGun) {
        super(entityType, world, shooter, weapon, item, modifiedGun, 0, 0);
    }

    @Override
    protected void onHitEntity(final Entity entity, final Vec3 hitVec, final Vec3 startVec,
            final Vec3 endVec, final boolean headshot) {
        ProjectileEntity.createExplosion(this, this.getDamage() / 5F, true);
    }

    @Override
    protected void onHitBlock(final BlockState state, final BlockPos pos, final Direction face,
            final double x, final double y, final double z) {
        ProjectileEntity.createExplosion(this, this.getDamage() / 5F, true);
    }

    @Override
    public void onExpired() {
        ProjectileEntity.createExplosion(this, this.getDamage() / 5F, true);
    }
}
