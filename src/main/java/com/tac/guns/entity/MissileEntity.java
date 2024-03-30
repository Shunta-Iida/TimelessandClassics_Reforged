package com.tac.guns.entity;

import com.tac.guns.Config;
import com.tac.guns.item.gun.GunItem;
import com.tac.guns.weapon.Gun;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
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
public class MissileEntity extends ProjectileEntity {
    private float power;

    public MissileEntity(final EntityType<? extends ProjectileEntity> entityType,
            final Level worldIn) {
        super(entityType, worldIn);
    }

    public MissileEntity(final EntityType<? extends ProjectileEntity> entityType,
            final Level worldIn, final LivingEntity shooter, final ItemStack weapon,
            final GunItem item, final Gun modifiedGun, final float power) {
        super(entityType, worldIn, shooter, weapon, item, modifiedGun, 0, 0);
        this.power = power;
    }

    @Override
    protected void onProjectileTick() {
        if (this.level.isClientSide) {
            for (int i = 5; i > 0; i--) {
                this.level.addParticle(ParticleTypes.CLOUD, true,
                        this.getX() - (this.getDeltaMovement().x() / i),
                        this.getY() - (this.getDeltaMovement().y() / i),
                        this.getZ() - (this.getDeltaMovement().z() / i), 0, 0, 0);
            }
            if (this.level.random.nextInt(2) == 0) {
                this.level.addParticle(ParticleTypes.SMOKE, true, this.getX(), this.getY(),
                        this.getZ(), 0, 0, 0);
                this.level.addParticle(ParticleTypes.FLAME, true, this.getX(), this.getY(),
                        this.getZ(), 0, 0, 0);
            }
        }
    }

    @Override
    protected void onHitEntity(final Entity entity, final Vec3 hitVec, final Vec3 startVec,
            final Vec3 endVec, final boolean headshot) {
        ProjectileEntity.createExplosion(this,
                this.power * Config.COMMON.missiles.explosionRadius.get().floatValue(), true);
    }

    @Override
    protected void onHitBlock(final BlockState state, final BlockPos pos, final Direction face,
            final double x, final double y, final double z) {
        ProjectileEntity.createExplosion(this,
                this.power * Config.COMMON.missiles.explosionRadius.get().floatValue(), true);
        this.life = 0;
    }

    @Override
    public void onExpired() {
        ProjectileEntity.createExplosion(this,
                this.power * Config.COMMON.missiles.explosionRadius.get().floatValue(), true);
    }
}
