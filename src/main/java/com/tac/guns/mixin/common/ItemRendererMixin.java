package com.tac.guns.mixin.common;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.tac.guns.item.gun.GunItem;
import com.tac.guns.item.gun.GunItemHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Shadow
    private float blitOffset;

    @Shadow
    private void fillRect(final BufferBuilder buffer, final int x, final int y, final int width,
            final int height, final int red, final int green, final int blue, final int alpha) {
    }

    @Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At("HEAD"), cancellable = true)
    public void renderGuiItemDecorations(final Font font, final ItemStack stack, final int x,
            final int y, @Nullable final String customStr, final CallbackInfo ci) {

        ci.cancel();

        if (!stack.isEmpty()) {
            final PoseStack posestack = new PoseStack();
            if (stack.getCount() != 1 || customStr != null) {
                final String s = customStr == null ? String.valueOf(stack.getCount()) : customStr;
                final MultiBufferSource.BufferSource multibuffersource$buffersource =
                        MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                posestack.translate(0.0D, 0.0D, (double) (this.blitOffset + 200.0F));
                font.drawInBatch(s, (float) (x + 19 - 2 - font.width(s)), (float) (y + 6 + 3),
                        16777215, true, posestack.last().pose(), multibuffersource$buffersource,
                        false, 0, 15728880);
                multibuffersource$buffersource.endBatch();
            } else if (stack.isBarVisible() && !(stack.getItem() instanceof BundleItem)) {
                String text;
                if (stack.getItem() instanceof GunItem) {
                    text = GunItemHelper.of(stack).getAmmoForDisplay();
                } else {
                    text = stack.getDamageValue() + "/" + stack.getMaxDamage();
                }
                posestack.translate(0.0D, 0.0D, (double) (this.blitOffset + 200.0F));
                posestack.scale(0.5F, 0.5F, 1.0F);
                final MultiBufferSource.BufferSource multibuffersource$buffersource =
                        MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                font.drawInBatch("§l" + text,
                        (float) x * 2f + 24f - (float) font.width(text) / 2.0f
                                - (text.length() % 2 == 0 ? 0 : text.length() / 1.5f)
                                + Math.max(0, 8 - text.length() * 2),
                        (float) y * 2f + 24f, stack.getBarColor(), true, posestack.last().pose(),
                        multibuffersource$buffersource, false, 0, 15728880);
                multibuffersource$buffersource.endBatch();
            }

            final LocalPlayer localplayer = Minecraft.getInstance().player;
            final float f = localplayer == null ? 0.0F
                    : localplayer.getCooldowns().getCooldownPercent(stack.getItem(),
                            Minecraft.getInstance().getFrameTime());
            if (f > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                final Tesselator tesselator1 = Tesselator.getInstance();
                final BufferBuilder bufferbuilder1 = tesselator1.getBuilder();
                this.fillRect(bufferbuilder1, x, y + Mth.floor(16.0F * (1.0F - f)), 16,
                        Mth.ceil(16.0F * f), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

        }
    }
}
