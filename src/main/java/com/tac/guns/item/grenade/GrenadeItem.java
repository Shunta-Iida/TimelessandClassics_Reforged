package com.tac.guns.item.grenade;

import com.tac.guns.entity.ThrowableGrenadeEntity;
import com.tac.guns.init.ModSounds;
import com.tac.guns.item.ammo.AmmoItem;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class GrenadeItem extends AmmoItem {
    protected int maxCookTime;
    private final float power;
    private final float speed;

    public GrenadeItem(final Item.Properties properties, final int maxCookTime, final float power,
            final float speed) {
        super(properties);
        this.maxCookTime = maxCookTime;
        this.power = power;
        this.speed = speed;
    }

    @Override
    public UseAnim getUseAnimation(final ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(final ItemStack stack) {
        return this.maxCookTime;
    }

    @Override
    public void onUsingTick(final ItemStack stack, final LivingEntity player, final int count) {
        if (!this.canCook())
            return;

        final int duration = this.getUseDuration(stack) - count;
        if (duration == 5)
            player.level.playLocalSound(player.getX(), player.getY(), player.getZ(),
                    ModSounds.ITEM_GRENADE_PIN.get(), SoundSource.PLAYERS, 1.0F, 1.0F, false);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level worldIn, final Player playerIn,
            final InteractionHand handIn) {
        final ItemStack stack = playerIn.getItemInHand(handIn);
        playerIn.startUsingItem(handIn);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(final ItemStack stack, final Level worldIn,
            final LivingEntity entityLiving) {
        if (this.canCook() && !worldIn.isClientSide()) {
            if (!(entityLiving instanceof Player) || !((Player) entityLiving).isCreative())
                stack.shrink(1);
            final ThrowableGrenadeEntity grenade = this.create(worldIn, entityLiving, 0);
            grenade.onDeath();
        }
        return stack;
    }

    @Override
    public void releaseUsing(final ItemStack stack, final Level worldIn,
            final LivingEntity entityLiving, final int timeLeft) {
        if (!worldIn.isClientSide()) {
            final int duration = this.getUseDuration(stack) - timeLeft;
            if (duration >= 5) {
                if (!(entityLiving instanceof Player) || !((Player) entityLiving).isCreative())
                    stack.shrink(1);
                final ThrowableGrenadeEntity grenade =
                        this.create(worldIn, entityLiving, this.maxCookTime - duration);
                grenade.shootFromRotation(entityLiving, entityLiving.getXRot(),
                        entityLiving.getYRot(), 0.0F, Math.min(1.0F, duration / 20F) * this.speed,
                        1.5F);
                worldIn.addFreshEntity(grenade);
                this.onThrown(worldIn, grenade);
            }
        }
    }

    public ThrowableGrenadeEntity create(final Level world, final LivingEntity entity,
            final int timeLeft) {
        return null;
    }

    /* return new ThrowableGrenadeEntity(world, entity, timeLeft, this.power); */

    public boolean canCook() {
        return true;
    }

    protected void onThrown(final Level world, final ThrowableGrenadeEntity entity) {
    }
}
