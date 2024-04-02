package com.tac.guns.client.screen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.render.model.OverrideModelManager;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.NetworkGunManager;
import com.tac.guns.common.WeaponType;
import com.tac.guns.common.container.WorkbenchContainer;
import com.tac.guns.crafting.WorkbenchRecipe;
import com.tac.guns.crafting.WorkbenchRecipes;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.IColored;
import com.tac.guns.item.ammo.IAmmo;
import com.tac.guns.item.attachment.ScopeItem;
import com.tac.guns.item.gun.GunItem;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessageCraft;
import com.tac.guns.tileentity.WorkbenchTileEntity;
import com.tac.guns.util.InventoryUtil;
import com.tac.guns.weapon.Gun;
import com.tac.guns.weapon.attachment.IAttachmentItem;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class WorkbenchScreen extends AbstractContainerScreen<WorkbenchContainer> {
    private static final ResourceLocation GUI_BASE =
            new ResourceLocation("tac:textures/gui/workbench.png");
    private static boolean showRemaining = false;

    private Tab currentTab;
    private final List<Tab> tabs = new ArrayList<>();
    private final List<MaterialItem> materials;
    private List<MaterialItem> filteredMaterials;
    private final Inventory playerInventory;
    private final WorkbenchTileEntity workbench;
    private Button btnCraft;
    private CheckBox checkBoxMaterials;
    private ItemStack displayStack;

    float posDelta = 0.1f;// works for gun models, + means higher

    public WorkbenchScreen(final WorkbenchContainer container, final Inventory playerInventory,
            final Component title) {
        super(container, playerInventory, title);
        this.playerInventory = playerInventory;
        this.workbench = container.getWorkbench();
        this.imageWidth = 256;
        this.imageHeight = 184;
        this.materials = new ArrayList<>();
        this.createTabs(WorkbenchRecipes.getAll(playerInventory.player.level));
        if (!this.tabs.isEmpty()) {
            this.imageHeight += 28;
        }
    }

    private void createTabs(final NonNullList<WorkbenchRecipe> recipes) {
        final List<WorkbenchRecipe> weapons_AR = new ArrayList<>();
        final List<WorkbenchRecipe> weapons_HMG = new ArrayList<>();
        final List<WorkbenchRecipe> weapons_PT = new ArrayList<>();
        final List<WorkbenchRecipe> weapons_SG = new ArrayList<>();
        final List<WorkbenchRecipe> weapons_SMG = new ArrayList<>();
        final List<WorkbenchRecipe> weapons_SR = new ArrayList<>();
        final List<WorkbenchRecipe> weapons = new ArrayList<>();
        final List<WorkbenchRecipe> attachments = new ArrayList<>();
        final List<WorkbenchRecipe> ammo = new ArrayList<>();
        final List<WorkbenchRecipe> misc = new ArrayList<>();

        for (final WorkbenchRecipe recipe : recipes) {
            final ItemStack output = recipe.getItem();
            if (output.getItem() instanceof GunItem) {
                final Gun gun = ((GunItem) output.getItem()).getGun();
                if (gun.getDisplay().getWeaponType() == WeaponType.AR)
                    weapons_AR.add(recipe);
                else if (gun.getDisplay().getWeaponType() == WeaponType.MG
                        || gun.getDisplay().getWeaponType() == WeaponType.RPG)
                    weapons_HMG.add(recipe);
                else if (gun.getDisplay().getWeaponType() == WeaponType.PT)
                    weapons_PT.add(recipe);
                else if (gun.getDisplay().getWeaponType() == WeaponType.SG)
                    weapons_SG.add(recipe);
                else if (gun.getDisplay().getWeaponType() == WeaponType.SMG)
                    weapons_SMG.add(recipe);
                else if (gun.getDisplay().getWeaponType() == WeaponType.SR)
                    weapons_SR.add(recipe);
                else
                    weapons.add(recipe);
            } else if (output.getItem() instanceof IAttachmentItem) {
                attachments.add(recipe);
            } else if (this.isAmmo(output)) {
                ammo.add(recipe);
            } else {
                misc.add(recipe);
            }
        }

        if (!weapons_AR.isEmpty()) {
            final ItemStack icon = new ItemStack(ModItems.AK47.get());
            icon.getOrCreateTag().putInt("AmmoCount",
                    ModItems.AK47.get().getGun().getReloads().getMaxAmmo());
            this.tabs.add(new Tab(icon, "weapons_ar", weapons_AR));
        }

        if (!weapons_HMG.isEmpty()) {
            final ItemStack icon = new ItemStack(ModItems.M60.get());
            icon.getOrCreateTag().putInt("AmmoCount",
                    ModItems.M60.get().getGun().getReloads().getMaxAmmo());
            this.tabs.add(new Tab(icon, "weapons_hmg", weapons_HMG));
        }

        if (!weapons_PT.isEmpty()) {
            final ItemStack icon = new ItemStack(ModItems.GLOCK_17.get());
            icon.getOrCreateTag().putInt("AmmoCount",
                    ((GunItem) ModItems.GLOCK_17.get()).getGun().getReloads().getMaxAmmo());
            this.tabs.add(new Tab(icon, "weapons_pt", weapons_PT));
        }

        if (!weapons_SG.isEmpty()) {
            final ItemStack icon = new ItemStack(ModItems.M870_CLASSIC.get());
            icon.getOrCreateTag().putInt("AmmoCount",
                    ((GunItem) ModItems.M870_CLASSIC.get()).getGun().getReloads().getMaxAmmo());
            this.tabs.add(new Tab(icon, "weapons_sg", weapons_SG));
        }

        if (!weapons_SMG.isEmpty()) {
            final ItemStack icon = new ItemStack(ModItems.VECTOR45.get());
            icon.getOrCreateTag().putInt("AmmoCount",
                    ModItems.VECTOR45.get().getGun().getReloads().getMaxAmmo());
            this.tabs.add(new Tab(icon, "weapons_smg", weapons_SMG));
        }

        if (!weapons_SR.isEmpty()) {
            final ItemStack icon = new ItemStack(ModItems.AI_AWP.get());
            icon.getOrCreateTag().putInt("AmmoCount",
                    ((GunItem) ModItems.AI_AWP.get()).getGun().getReloads().getMaxAmmo());
            this.tabs.add(new Tab(icon, "weapons_sr", weapons_SR));
        }

        if (!weapons.isEmpty()) {
            final ItemStack icon = new ItemStack(ModItems.HK416_A5.get());
            icon.getOrCreateTag().putInt("AmmoCount",
                    ((GunItem) ModItems.HK416_A5.get()).getGun().getReloads().getMaxAmmo());
            this.tabs.add(new Tab(icon, "weapons", weapons));
        }

        if (!attachments.isEmpty()) {
            this.tabs.add(new Tab(new ItemStack(ModItems.COYOTE_SIGHT.get()), "attachments",
                    attachments));
        }

        if (!ammo.isEmpty()) {
            this.tabs.add(new Tab(new ItemStack(ModItems.BULLET_30_WIN.get()), "ammo", ammo));
        }

        if (!misc.isEmpty()) {
            this.tabs.add(new Tab(new ItemStack(Items.BARRIER), "misc", misc));
        }

        if (!this.tabs.isEmpty()) {
            this.currentTab = this.tabs.get(0);
        }
    }

    private boolean isAmmo(final ItemStack stack) {
        if (stack.getItem() instanceof IAmmo) {
            return true;
        }
        final ResourceLocation id = stack.getItem().getRegistryName();
        Objects.requireNonNull(id);
        for (final GunItem gunItem : NetworkGunManager.getClientRegisteredGuns()) {
            if (id.equals(gunItem.getGun(stack.getTag()).getProjectile().getItem())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init() {
        super.init();
        if (!this.tabs.isEmpty()) {
            this.topPos += 28;
        }
        this.addRenderableWidget(new Button(this.leftPos + 9, this.topPos + 18, 15, 20,
                new TextComponent("<"), button -> {
                    final int index = this.currentTab.getCurrentIndex();
                    if (index - 1 < 0) {
                        this.loadItem(this.currentTab.getRecipes().size() - 1);
                    } else {
                        this.loadItem(index - 1);
                    }
                }));
        this.addRenderableWidget(new Button(this.leftPos + 153, this.topPos + 18, 15, 20,
                new TextComponent(">"), button -> {
                    final int index = this.currentTab.getCurrentIndex();
                    if (index + 1 >= this.currentTab.getRecipes().size()) {
                        this.loadItem(0);
                    } else {
                        this.loadItem(index + 1);
                    }
                }));
        this.btnCraft = this.addRenderableWidget(new Button(this.leftPos + 195, this.topPos + 16,
                74, 20, new TranslatableComponent("gui.tac.workbench.assemble"), button -> {
                    final int index = this.currentTab.getCurrentIndex();
                    final WorkbenchRecipe recipe = this.currentTab.getRecipes().get(index);
                    final ResourceLocation registryName = recipe.getId();
                    PacketHandler.getPlayChannel().sendToServer(
                            new MessageCraft(registryName, this.workbench.getBlockPos()));
                }));
        this.btnCraft.active = false;
        this.checkBoxMaterials = this.addRenderableWidget(new CheckBox(this.leftPos + 172,
                this.topPos + 51, new TranslatableComponent("gui.tac.workbench.show_remaining")));
        this.checkBoxMaterials.setToggled(WorkbenchScreen.showRemaining);
        this.loadItem(this.currentTab.getCurrentIndex());
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        for (final MaterialItem material : this.materials) {
            material.update();
        }

        boolean canCraft = true;
        for (final MaterialItem material : this.materials) {
            if (!material.isEnabled()) {
                canCraft = false;
                break;
            }
        }

        this.btnCraft.active = canCraft;
        this.updateColor();
    }

    private void updateColor() {
        if (this.currentTab != null) {
            final ItemStack item = this.displayStack;
            if (item.getItem() instanceof IColored && ((IColored) item.getItem()).canColor(item)) {
                final IColored colored = (IColored) item.getItem();
                if (!this.workbench.getItem(0).isEmpty()) {
                    final ItemStack dyeStack = this.workbench.getItem(0);
                    if (dyeStack.getItem() instanceof DyeItem) {
                        final DyeColor color = ((DyeItem) dyeStack.getItem()).getDyeColor();
                        final float[] components = color.getTextureDiffuseColors();
                        final int red = (int) (components[0] * 255F);
                        final int green = (int) (components[1] * 255F);
                        final int blue = (int) (components[2] * 255F);
                        colored.setColor(item,
                                ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF)));
                    } else {
                        colored.removeColor(item);
                    }
                } else {
                    colored.removeColor(item);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int mouseButton) {
        final boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);
        WorkbenchScreen.showRemaining = this.checkBoxMaterials.isToggled();

        for (int i = 0; i < this.tabs.size(); i++) {
            if (RenderUtil.isMouseWithin((int) mouseX, (int) mouseY, this.leftPos + 28 * i,
                    this.topPos - 28, 28, 28)) {
                this.currentTab = this.tabs.get(i);
                this.loadItem(this.currentTab.getCurrentIndex());
                this.minecraft.getSoundManager()
                        .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }

        return result;
    }

    private void loadItem(final int index) {
        final WorkbenchRecipe recipe = this.currentTab.getRecipes().get(index);
        this.displayStack = recipe.getItem().copy();
        this.updateColor();

        this.materials.clear();

        final List<Pair<Ingredient, Integer>> materials = recipe.getMaterials();
        if (materials != null) {
            for (final Pair<Ingredient, Integer> material : materials) {
                final MaterialItem item =
                        new MaterialItem(material.getFirst(), material.getSecond());
                item.update();
                this.materials.add(item);
            }

            this.currentTab.setCurrentIndex(index);
        }
    }

    @Override
    public void render(final PoseStack matrixStack, final int mouseX, final int mouseY,
            final float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);

        final int startX = this.leftPos;
        final int startY = this.topPos;

        for (int i = 0; i < this.tabs.size(); i++) {
            if (RenderUtil.isMouseWithin(mouseX, mouseY, startX + 28 * i, startY - 28, 28, 28)) {
                this.renderTooltip(matrixStack,
                        new TranslatableComponent(this.tabs.get(i).getTabKey()), mouseX, mouseY);
                return;
            }
        }

        if (this.filteredMaterials == null)
            return;
        for (int i = 0; i < this.filteredMaterials.size(); i++) {
            final int itemX = startX + 172;
            final int itemY = startY + i * 19 + 63;
            if (RenderUtil.isMouseWithin(mouseX, mouseY, itemX, itemY, 80, 19)) {
                final MaterialItem materialItem = this.filteredMaterials.get(i);
                if (!materialItem.getStack().isEmpty()) {
                    this.renderTooltip(matrixStack, materialItem.getStack(), mouseX, mouseY);
                    return;
                }
            }
        }

        if (RenderUtil.isMouseWithin(mouseX, mouseY, startX + 8, startY + 38, 160, 48)) {
            this.renderTooltip(matrixStack, this.displayStack, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(final PoseStack matrixStack, final int mouseX, final int mouseY) {
        final int offset = this.tabs.isEmpty() ? 0 : 28;
        this.font.draw(matrixStack, this.title, (float) this.titleLabelX,
                (float) this.titleLabelY - 28 + offset, 4210752);
    }

    @Override
    protected void renderBg(final PoseStack matrixStack, float partialTicks, final int mouseX,
            final int mouseY) {
        /* Fixes partial ticks to use percentage from 0 to 1 */
        partialTicks = Minecraft.getInstance().getFrameTime();

        final int startX = this.leftPos;
        final int startY = this.topPos;

        RenderSystem.enableBlend();

        /* Draw unselected tabs */
        for (int i = 0; i < this.tabs.size(); i++) {
            final Tab tab = this.tabs.get(i);
            if (tab != this.currentTab) {

                RenderSystem.setShaderTexture(0, WorkbenchScreen.GUI_BASE);
                this.blit(matrixStack, startX + 28 * i, startY - 28, 80, 184, 28, 32);
                Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(tab.getIcon(),
                        startX + 28 * i + 6, startY - 28 + 8);
                Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(this.font,
                        tab.getIcon(), startX + 28 * i + 6, startY - 28 + 8, null);
            }
        }

        RenderSystem.setShaderTexture(0, WorkbenchScreen.GUI_BASE);
        this.blit(matrixStack, startX, startY, 0, 0, 173, 184);
        GuiComponent.blit(matrixStack, startX + 173, startY, 78, 184, 173, 0, 1, 184, 256, 256);
        this.blit(matrixStack, startX + 251, startY, 174, 0, 24, 184);
        this.blit(matrixStack, startX + 172, startY + 16, 198, 0, 20, 20);

        /* Draw selected tab */
        if (this.currentTab != null) {
            final int i = this.tabs.indexOf(this.currentTab);
            final int u = i == 0 ? 80 : 108;
            RenderSystem.setShaderTexture(0, WorkbenchScreen.GUI_BASE);
            this.blit(matrixStack, startX + 28 * i, startY - 28, u, 214, 28, 32);
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(
                    this.currentTab.getIcon(), startX + 28 * i + 6, startY - 28 + 8);
            Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(this.font,
                    this.currentTab.getIcon(), startX + 28 * i + 6, startY - 28 + 8, null);
        }

        RenderSystem.setShaderTexture(0, WorkbenchScreen.GUI_BASE);

        if (this.workbench.getItem(0).isEmpty()) {
            this.blit(matrixStack, startX + 174, startY + 18, 165, 199, 16, 16);
        }

        final ItemStack currentItem = this.displayStack;
        if (currentItem == null)
            return;
        final StringBuilder builder = new StringBuilder(currentItem.getHoverName().getString());
        if (currentItem.getCount() > 1) {
            builder.append(ChatFormatting.GOLD);
            builder.append(ChatFormatting.BOLD);
            builder.append(" x ");
            builder.append(currentItem.getCount());
        }
        this.drawCenteredString(matrixStack, this.font, builder.toString(), startX + 88,
                startY + 22, Color.WHITE.getRGB());

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(startX + 8, startY + 17, 160, 70);

        final PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        {
            modelViewStack.translate(startX + 88, startY + 60, 100);
            modelViewStack.scale(50F, -50F, 50F);
            modelViewStack.mulPose(Vector3f.XP.rotationDegrees(5F));
            modelViewStack.mulPose(Vector3f.YP
                    .rotationDegrees(Minecraft.getInstance().player.tickCount + partialTicks));
            RenderSystem.applyModelViewMatrix();
            final MultiBufferSource.BufferSource buffer =
                    this.minecraft.renderBuffers().bufferSource();
            if (OverrideModelManager.hasModel(currentItem)
                    && currentItem.getItem() instanceof ScopeItem) {
                matrixStack.scale(2, 2, 2);
                GunRenderingHandler.get().renderScope(this.minecraft.player, currentItem,
                        ItemTransforms.TransformType.HEAD, matrixStack, buffer, 15728880, 0F); // GROUND, matrixStack,
                                                                                                                                                                         // buffer, 15728880, 0F);
                matrixStack.scale(0.5f, 0.5f, 0.5f);
            } else if (currentItem.getItem() instanceof GunItem) {
                matrixStack.translate(0, this.posDelta, 0);
                GunRenderingHandler.get().renderWeapon(this.minecraft.player, currentItem,
                        ItemTransforms.TransformType.FIXED, matrixStack, buffer, 15728880, 0F);
                // TransformType.GROUND with 2x size will block out the text above it.
            } else
                Minecraft.getInstance().getItemRenderer().render(currentItem,
                        ItemTransforms.TransformType.FIXED, false, matrixStack, buffer, 15728880,
                        OverlayTexture.NO_OVERLAY, RenderUtil.getModel(currentItem));
            buffer.endBatch();
        }
        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        this.filteredMaterials = this.getMaterials();
        for (int i = 0; i < this.filteredMaterials.size(); i++) {

            RenderSystem.setShaderTexture(0, WorkbenchScreen.GUI_BASE);

            final MaterialItem materialItem = this.filteredMaterials.get(i);
            ItemStack stack = materialItem.stack;
            if (!stack.isEmpty()) {
                Lighting.setupForFlatItems();
                if (materialItem.isEnabled()) {
                    this.blit(matrixStack, startX + 172, startY + i * 19 + 63, 0, 184, 80, 19);
                } else {
                    this.blit(matrixStack, startX + 172, startY + i * 19 + 63, 0, 222, 80, 19);
                }

                String name = stack.getHoverName().getString();
                if (this.font.width(name) > 55) {
                    name = this.font.plainSubstrByWidth(name, 50).trim() + "...";
                }
                this.font.draw(matrixStack, name, startX + 172 + 22, startY + i * 19 + 6 + 63,
                        Color.WHITE.getRGB());

                Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack,
                        startX + 172 + 2, startY + i * 19 + 1 + 63);

                if (this.checkBoxMaterials.isToggled()) {
                    final int count =
                            InventoryUtil.getItemStackAmount(Minecraft.getInstance().player, stack);
                    stack = stack.copy();
                    stack.setCount(stack.getCount() - count);
                }

                // GunRenderingHandler.get().renderWeapon(minecraft.player, stack,
                // ItemTransforms.TransformType.GROUND, matrixStack, buffer, )
                Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(this.font, stack,
                        startX + 172 + 2, startY + i * 19 + 1 + 63, null);
                // GunRenderingHandler.get().renderWeapon(this.minecraft.player,
                // this.minecraft.player.getHeldItemMainhand(),
                // ItemTransforms.TransformType.GROUND, matrixStack, buffer, 15728880, 0F); //
                // GROUND, matrixStack, buffer, 15728880, 0F);
            }
        }
    }

    private List<MaterialItem> getMaterials() {
        final List<MaterialItem> materials = NonNullList.withSize(6, new MaterialItem());
        final List<MaterialItem> filteredMaterials = this.materials.stream()
                .filter(materialItem -> this.checkBoxMaterials.isToggled()
                        ? !materialItem.isEnabled()
                        : !materialItem.stack.isEmpty())
                .collect(Collectors.toList());
        for (int i = 0; i < filteredMaterials.size() && i < materials.size(); i++) {
            materials.set(i, filteredMaterials.get(i));
        }
        return materials;
    }

    public List<Tab> getTabs() {
        return ImmutableList.copyOf(this.tabs);
    }

    public static class MaterialItem {
        public static final MaterialItem EMPTY = new MaterialItem();

        private boolean enabled = false;
        private final int count;
        private int tickcount;
        private int index;
        private ItemStack stack;
        private final Ingredient ingredient;
        private final ItemStack[] matchingstacks;

        private MaterialItem() {
            this.ingredient = null;
            this.matchingstacks = new ItemStack[] {ItemStack.EMPTY};
            this.stack = ItemStack.EMPTY;
            this.count = 0;
        }

        private MaterialItem(final Ingredient ingredient, final int count) {
            this.ingredient = ingredient;
            this.matchingstacks = ingredient.getItems();
            this.stack = this.matchingstacks[0];
            this.count = count;
        }

        public ItemStack getStack() {
            return this.stack;
        }

        public void update() {
            if (++this.tickcount % 20 == 0) {
                this.tickcount = 0;
                this.index += 1;
                if (this.index == this.matchingstacks.length) {
                    this.index = 0;
                }
                this.stack = this.matchingstacks[this.index];
            }
            if (this.ingredient != null) {
                this.stack.setCount(this.count);
                this.enabled = InventoryUtil.hasIngredient(Minecraft.getInstance().player,
                        new Pair<>(this.ingredient, this.count));
            }
        }

        public boolean isEnabled() {
            return this.stack.isEmpty() || this.enabled;
        }
    }

    private static class Tab {
        private final ItemStack icon;
        private final String id;
        private final List<WorkbenchRecipe> items;
        private int currentIndex;

        public Tab(final ItemStack icon, final String id, final List<WorkbenchRecipe> items) {
            this.icon = icon;
            this.id = id;
            this.items = items;
        }

        public ItemStack getIcon() {
            return this.icon;
        }

        public String getTabKey() {
            return "gui.tac.workbench.tab." + this.id;
        }

        public void setCurrentIndex(final int currentIndex) {
            this.currentIndex = currentIndex;
        }

        public int getCurrentIndex() {
            return this.currentIndex;
        }

        public List<WorkbenchRecipe> getRecipes() {
            return this.items;
        }
    }
}
