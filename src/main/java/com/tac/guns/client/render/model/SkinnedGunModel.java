package com.tac.guns.client.render.model;

import static com.tac.guns.client.render.model.CommonComponents.GRIP_LIGHT;
import static com.tac.guns.client.render.model.CommonComponents.GRIP_TACTICAL;
import static com.tac.guns.client.render.model.CommonComponents.LASER_BASIC;
import static com.tac.guns.client.render.model.CommonComponents.LASER_BASIC_DEVICE;
import static com.tac.guns.client.render.model.CommonComponents.LASER_IR;
import static com.tac.guns.client.render.model.CommonComponents.LASER_IR_DEVICE;
import static com.tac.guns.client.render.model.CommonComponents.MAG_DRUM;
import static com.tac.guns.client.render.model.CommonComponents.MAG_EXTENDED;
import static com.tac.guns.client.render.model.CommonComponents.MAG_STANDARD;
import static com.tac.guns.client.render.model.CommonComponents.MUZZLE_BRAKE;
import static com.tac.guns.client.render.model.CommonComponents.MUZZLE_COMPENSATOR;
import static com.tac.guns.client.render.model.CommonComponents.MUZZLE_DEFAULT;
import static com.tac.guns.client.render.model.CommonComponents.MUZZLE_SILENCER;
import static com.tac.guns.client.render.model.CommonComponents.SIGHT;
import static com.tac.guns.client.render.model.CommonComponents.SIGHT_FOLDED;
import static com.tac.guns.client.render.model.CommonComponents.SIGHT_FOLDED_LIGHT;
import static com.tac.guns.client.render.model.CommonComponents.SIGHT_LIGHT;
import static com.tac.guns.client.render.model.CommonComponents.STOCK_DEFAULT;
import static com.tac.guns.client.render.model.CommonComponents.STOCK_HEAVY;
import static com.tac.guns.client.render.model.CommonComponents.STOCK_LIGHT;
import static com.tac.guns.client.render.model.CommonComponents.STOCK_TACTICAL;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.tac.guns.client.render.gunskin.GunSkin;
import com.tac.guns.client.render.gunskin.SkinManager;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.gun.GunItemHelper;
import com.tac.guns.weapon.Gun;
import com.tac.guns.weapon.attachment.IAttachmentItem;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Contains methods for rendering common components.
 */
public abstract class SkinnedGunModel implements IOverrideModel {
    @Deprecated
    protected Map<GunComponent, Vector3d> extraOffset = new HashMap<>();

    @Override
    public void render(float partialTicks, ItemTransforms.TransformType transformType,
            ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrixStack,
            MultiBufferSource buffer, int light, int overlay) {
        GunSkin skin = SkinManager.getSkin(stack);
        render(skin, partialTicks, transformType, stack, entity, matrixStack, buffer, light,
                overlay);
    }

    public abstract void render(GunSkin skin, float partialTicks,
            ItemTransforms.TransformType transformType, ItemStack stack, LivingEntity entity,
            PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay);

    protected BakedModel getComponentModel(GunSkin skin, GunComponent key) {
        if (skin == null)
            return null;
        CacheableModel cacheableModel = skin.getModel(key);
        if (cacheableModel == null) {
            GunSkin defaultSkin = SkinManager.getDefaultSkin(skin.gunItemRegistryName);
            if (defaultSkin != null)
                cacheableModel = defaultSkin.getModel(key);
        }
        if (cacheableModel == null)
            return null;
        return cacheableModel.getModel();
    }

    private void renderComponent(ItemStack stack, PoseStack matrices,
            MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin,
            GunComponent modelComponent) {
        BakedModel bakedModel = getComponentModel(skin, modelComponent);
        if (bakedModel == null)
            return;
        if (extraOffset.containsKey(modelComponent)) {
            Vector3d x = extraOffset.get(modelComponent);
            matrices.pushPose();
            matrices.translate(x.x, x.y, x.z);
            RenderUtil.renderModel(bakedModel, stack, matrices, renderBuffer, light, overlay);
            matrices.translate(-x.x, -x.y, -x.z);
            matrices.popPose();
        } else
            RenderUtil.renderModel(bakedModel, stack, matrices, renderBuffer, light, overlay);
    }

    private void renderLaserModuleComponent(ItemStack stack, PoseStack matrices,
            MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin,
            GunComponent modelComponent) {
        if (extraOffset.containsKey(modelComponent)) {
            Vector3d x = extraOffset.get(modelComponent);
            matrices.pushPose();
            matrices.translate(x.x, x.y, x.z);
            RenderUtil.renderLaserModuleModel(getComponentModel(skin, modelComponent), stack,
                    matrices, renderBuffer, light, overlay);
            matrices.translate(-x.x, -x.y, -x.z);
            matrices.popPose();
        } else
            RenderUtil.renderLaserModuleModel(getComponentModel(skin, modelComponent), stack,
                    matrices, renderBuffer, light, overlay);
    }

    public void renderStockWithDefault(ItemStack stack, PoseStack matrices,
            MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachmentItem.Type.STOCK, stack).getItem() == ModItems.LIGHT_STOCK
                .orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, STOCK_LIGHT);
        } else if (Gun.getAttachment(IAttachmentItem.Type.STOCK, stack)
                .getItem() == ModItems.TACTICAL_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, STOCK_TACTICAL);
        } else if (Gun.getAttachment(IAttachmentItem.Type.STOCK, stack)
                .getItem() == ModItems.WEIGHTED_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, STOCK_HEAVY);
        } else {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, STOCK_DEFAULT);
        }
    }

    public void renderStock(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer,
            int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachmentItem.Type.STOCK, stack).getItem() == ModItems.LIGHT_STOCK
                .orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, STOCK_LIGHT);
        } else if (Gun.getAttachment(IAttachmentItem.Type.STOCK, stack)
                .getItem() == ModItems.TACTICAL_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, STOCK_TACTICAL);
        } else if (Gun.getAttachment(IAttachmentItem.Type.STOCK, stack)
                .getItem() == ModItems.WEIGHTED_STOCK.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, STOCK_HEAVY);
        }
    }

    public void renderBarrelWithDefault(ItemStack stack, PoseStack matrices,
            MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachmentItem.Type.BARREL, stack).getItem() == ModItems.SILENCER
                .orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MUZZLE_SILENCER);
        } else if (Gun.getAttachment(IAttachmentItem.Type.BARREL, stack)
                .getItem() == ModItems.MUZZLE_COMPENSATOR.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin,
                    MUZZLE_COMPENSATOR);
        } else if (Gun.getAttachment(IAttachmentItem.Type.BARREL, stack)
                .getItem() == ModItems.MUZZLE_BRAKE.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MUZZLE_BRAKE);
        } else {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MUZZLE_DEFAULT);
        }
    }

    public void renderBarrel(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer,
            int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachmentItem.Type.BARREL, stack).getItem() == ModItems.SILENCER
                .orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MUZZLE_SILENCER);
        } else if (Gun.getAttachment(IAttachmentItem.Type.BARREL, stack)
                .getItem() == ModItems.MUZZLE_COMPENSATOR.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin,
                    MUZZLE_COMPENSATOR);
        } else if (Gun.getAttachment(IAttachmentItem.Type.BARREL, stack)
                .getItem() == ModItems.MUZZLE_BRAKE.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MUZZLE_BRAKE);
        }
    }

    public void renderGrip(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer,
            int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachmentItem.Type.UNDER_BARREL, stack)
                .getItem() == ModItems.SPECIALISED_GRIP.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, GRIP_TACTICAL);
        } else if (Gun.getAttachment(IAttachmentItem.Type.UNDER_BARREL, stack)
                .getItem() == ModItems.LIGHT_GRIP.orElse(ItemStack.EMPTY.getItem())) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, GRIP_LIGHT);
        }
    }

    public void renderSight(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer,
            int light, int overlay, GunSkin skin) {
        if (Gun.getScope(stack) != null) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, SIGHT_FOLDED);
            renderComponent(stack, matrices, renderBuffer, 15728880, overlay, skin,
                    SIGHT_FOLDED_LIGHT);
        } else {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, SIGHT);
            renderComponent(stack, matrices, renderBuffer, 15728880, overlay, skin, SIGHT_LIGHT);
        }
    }

    public void renderMag(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer,
            int light, int overlay, GunSkin skin) {
        if (GunItemHelper.of(stack).getAmmoCapacityWeight() > -1) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MAG_EXTENDED);
        } else {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MAG_STANDARD);
        }
    }

    public void renderDrumMag(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer,
            int light, int overlay, GunSkin skin) {
        if (GunItemHelper.of(stack).getAmmoCapacityWeight() > -1) {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MAG_DRUM);
        } else {
            renderComponent(stack, matrices, renderBuffer, light, overlay, skin, MAG_STANDARD);
        }
    }

    public void renderLaserDevice(ItemStack stack, PoseStack matrices,
            MultiBufferSource renderBuffer, int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachmentItem.Type.SIDE_RAIL, stack)
                .getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
            renderLaserModuleComponent(Gun.getAttachment(IAttachmentItem.Type.SIDE_RAIL, stack),
                    matrices, renderBuffer, light, overlay, skin, LASER_BASIC_DEVICE);
        } else if (Gun.getAttachment(IAttachmentItem.Type.SIDE_RAIL, stack)
                .getItem() != ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem())
                || Gun.getAttachment(IAttachmentItem.Type.IR_DEVICE, stack)
                        .getItem() == ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem())) {
            renderLaserModuleComponent(Gun.getAttachment(IAttachmentItem.Type.IR_DEVICE, stack),
                    matrices, renderBuffer, light, overlay, skin, LASER_IR_DEVICE);
        }
    }

    public void renderLaser(ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer,
            int light, int overlay, GunSkin skin) {
        if (Gun.getAttachment(IAttachmentItem.Type.SIDE_RAIL, stack)
                .getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
            renderLaserModuleComponent(Gun.getAttachment(IAttachmentItem.Type.SIDE_RAIL, stack),
                    matrices, renderBuffer, 15728880, overlay, skin, LASER_BASIC);
        } else if (Gun.getAttachment(IAttachmentItem.Type.SIDE_RAIL, stack)
                .getItem() != ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem())
                || Gun.getAttachment(IAttachmentItem.Type.IR_DEVICE, stack)
                        .getItem() == ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem())) {
            renderLaserModuleComponent(Gun.getAttachment(IAttachmentItem.Type.IR_DEVICE, stack),
                    matrices, renderBuffer, 15728880, overlay, skin, LASER_IR);
        }
    }
}
