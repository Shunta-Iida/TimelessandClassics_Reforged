package com.tac.guns.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.inventory.gear.armor.IRigContainer;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class AmmoScreen<T extends AbstractContainerMenu & IRigContainer>
        extends AbstractContainerScreen<T> {
    private static final ResourceLocation CHEST_GUI_TEXTURE =
            new ResourceLocation("textures/gui/container/generic_54.png");
    private final int rows;

    public AmmoScreen(final T container, final Inventory playerInventory, final Component title) {
        super(container, playerInventory, title);
        this.passEvents = false;
        final int i = 222;
        final int j = 114;
        this.rows = container.getNumRows();
        this.imageHeight = 114 + this.rows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(final PoseStack matrixStack, final int mouseX, final int mouseY,
            final float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(final PoseStack matrixStack, final float partialTicks, final int x,
            final int y) {
        RenderSystem.setShaderTexture(0, AmmoScreen.CHEST_GUI_TEXTURE);
        final int i = (this.width - this.imageWidth) / 2;
        final int j = (this.height - this.imageHeight) / 2;

        // Draw for ammo pack, current issue is the number of slots not being drawn
        // correctly, we can't cut this off either due to the background, get design
        // team to create alternative off generic_54.png baseline
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, (this.rows) * 18 + 17);
        this.blit(matrixStack, i, j + (this.rows) * 18 + 17, 0, 126, this.imageWidth, 96);
    }
}
