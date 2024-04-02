package com.tac.guns.item.wearable;

import java.util.Objects;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import com.tac.guns.Reference;
import com.tac.guns.common.NetworkRigManager;
import com.tac.guns.inventory.gear.armor.ArmorRigCapabilityProvider;
import com.tac.guns.inventory.gear.armor.ArmorRigContainerProvider;
import com.tac.guns.inventory.gear.armor.RigSlotsHandler;
import com.tac.guns.util.RigEnchantmentHelper;
import com.tac.guns.util.WearableHelper;
import com.tac.guns.weapon.Rig;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ArmorRigItem extends Item implements IArmoredRigItem {
    private int rows = 0;
    private float damageRate = 1.0F;

    public ArmorRigItem(final Properties properties) {
        super(properties);
    }

    private ArmorRigContainerProvider containerProvider;

    @Override
    public InteractionResultHolder<ItemStack> use(final Level world, final Player player,
            final InteractionHand hand) {
        if (player.getItemInHand(hand).getOrCreateTag().get("rig_rows") == null)
            player.getItemInHand(hand).getOrCreateTag().putInt("rig_rows",
                    this.rig.getGeneral().getInventoryRows());
        if (world.isClientSide)
            return super.use(world, player, hand);
        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        this.containerProvider = new ArmorRigContainerProvider(player.getItemInHand(hand));
        NetworkHooks.openGui((ServerPlayer) player, this.containerProvider);
        super.use(world, player, hand);
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack,
            @Nullable final CompoundTag nbt) {
        return new ArmorRigCapabilityProvider();
    }

    private final WeakHashMap<CompoundTag, Rig> modifiedRigCache = new WeakHashMap<>();

    private Rig rig = new Rig();

    public void setRig(final NetworkRigManager.Supplier supplier) {
        this.rig = supplier.getRig();
    }

    public Rig getRig() {
        return this.rig;
    }

    /*
     * @OnlyIn(Dist.CLIENT)
     * 
     * @Override
     * public void addInformation(ItemStack stack, @Nullable World worldIn,
     * List<ITextComponent> tooltip, ITooltipFlag flag) {
     * super.addInformation(stack, worldIn, tooltip, flag);
     * 
     * tooltip.add(new
     * TranslatableComponent("info.tac.current_armor_amount").append(new
     * TranslatableComponent(ItemStack.DECIMALFORMAT.format(WearableHelper.
     * GetCurrentDurability(stack))+"")).withStyle(ChatFormatting.BLUE));
     * int scancode =
     * GLFW.glfwGetKeyScancode(InputHandler.ARMOR_REPAIRING.getKeyCode());
     * if(GLFW.glfwGetKeyName(InputHandler.ARMOR_REPAIRING.getKeyCode(),scancode) !=
     * null)
     * tooltip.add((new
     * TranslatableComponent("info.tac.tac_armor_repair1").append(new
     * TranslatableComponent(GLFW.glfwGetKeyName(InputHandler.ARMOR_REPAIRING.
     * getKeyCode(), scancode)).withStyle(ChatFormatting.AQUA)).append(new
     * TranslatableComponent("info.tac.tac_armor_repair2"))).withStyle(
     * ChatFormatting.YELLOW));
     * }
     */

    @Override
    public boolean shouldOverrideMultiplayerNbt() {
        return true;
    }

    @Nullable
    @Override
    public CompoundTag getShareTag(final ItemStack stack) {
        stack.getOrCreateTag();
        final CompoundTag nbt = super.getShareTag(stack);
        if (stack.getItem() instanceof ArmorRigItem) {
            final RigSlotsHandler itemHandler = (RigSlotsHandler) stack
                    .getCapability(ArmorRigCapabilityProvider.capability).resolve().get();
            nbt.put("storage", itemHandler.serializeNBT());
        }

        return nbt;
    }

    @Override
    public boolean onEntitySwing(final ItemStack stack, final LivingEntity entity) {
        return true;
    }

    @Override
    public void fillItemCategory(final CreativeModeTab group, final NonNullList<ItemStack> stacks) {
        if (this.allowdedIn(group)) {
            final ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag();
            WearableHelper.FillDefaults(stack, this.rig);
            stacks.add(stack);
        }
    }

    @Override
    public int getDamage(final ItemStack stack) {
        return (int) WearableHelper.GetCurrentDurability(stack);
    }

    @Override
    public int getMaxDamage(final ItemStack stack) {
        return (int) RigEnchantmentHelper.getModifiedDurability(stack, this.getModifiedRig(stack));
    }

    @Override
    public boolean isBarVisible(final ItemStack p_150899_) {
        return true;
    }

    @Override
    public int getBarWidth(final ItemStack stack) {
        stack.getOrCreateTag();
        final Rig modifiedRig = this.getModifiedRig(stack);
        return (int) (13f * (WearableHelper.GetCurrentDurability(stack)
                / (float) RigEnchantmentHelper.getModifiedDurability(stack, modifiedRig)));
    }

    @Override
    public int getBarColor(final ItemStack p_150901_) {
        return Objects.requireNonNull(ChatFormatting.AQUA.getColor());
    }

    public Rig getModifiedRig(final ItemStack stack) {
        final CompoundTag tagCompound = stack.getTag();
        if (tagCompound != null && tagCompound.contains("Rig", Tag.TAG_COMPOUND)) {
            return this.modifiedRigCache.computeIfAbsent(tagCompound, item -> {
                if (tagCompound.getBoolean("Custom")) {
                    return Rig.create(tagCompound.getCompound("Rig"));
                } else {
                    final Rig gunCopy = this.rig.copy();
                    gunCopy.deserializeNBT(tagCompound.getCompound("Rig"));
                    return gunCopy;
                }
            });
        }
        return this.rig;
    }

    public ArmorRigItem setRigRows(final int rows) {
        this.rows = rows;
        return this;
    }

    public int getRigRows() {
        return this.rows;
    }

    public float getDamageRate() {
        return this.damageRate;
    }

    public ArmorRigItem setDamageRate(final float damageAttenuationRate) {
        this.damageRate = damageAttenuationRate;
        return this;
    }
}
