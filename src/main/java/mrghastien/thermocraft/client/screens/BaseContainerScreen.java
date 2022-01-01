package mrghastien.thermocraft.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mrghastien.thermocraft.client.screens.widgets.Widget;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.inventory.menus.BaseContainer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseContainerScreen<T extends BaseContainer, U extends BlockEntity> extends AbstractContainerScreen<T> {

    protected final ResourceLocation guiTexture;
    public final List<Widget> widgets = new ArrayList<>();
    protected final U tileEntity;

    public BaseContainerScreen(T container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        tileEntity = (U) container.tileEntity;
        guiTexture = new ResourceLocation(ThermoCraft.MODID, "textures/gui/" + tileEntity.getType().getRegistryName().getPath() + "_gui.png");
    }

    @Override
    public void render(PoseStack transformation, int mouseX, int mouseY, float partialTicks) {
        renderBackground(transformation);
        super.render(transformation, mouseX, mouseY, partialTicks);
        for (Widget widget : widgets) {
            widget.render(this, transformation, mouseX, mouseY, partialTicks);
        }
        renderTooltip(transformation, mouseX, mouseY);
    }

    public void addWidget(Widget widget) {
        widget.index = widgets.size();
        widget.getBounds().x += getGuiLeft();
        widget.getBounds().y += getGuiTop();
        widgets.add(widget);
    }

    @Override
    protected void renderBg(PoseStack Transformation, float partialTicks, int mouseX, int mouseY) {
        bindGuiTexture();
        this.blit(Transformation, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    protected void bindGuiTexture() {
        RenderSystem.setShaderTexture(0, guiTexture);
    }
}
