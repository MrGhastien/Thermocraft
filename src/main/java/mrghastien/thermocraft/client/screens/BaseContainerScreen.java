package mrghastien.thermocraft.client.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import mrghastien.thermocraft.client.screens.widgets.Widget;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.inventory.containers.BaseContainer;
import mrghastien.thermocraft.common.tileentities.BaseTile;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseContainerScreen<T extends BaseContainer, U extends TileEntity> extends ContainerScreen<T> {

    protected final ResourceLocation guiTexture;
    public final List<Widget> widgets = new ArrayList<>();
    protected final U tileEntity;

    public BaseContainerScreen(T container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        tileEntity = (U) container.tileEntity;
        guiTexture = new ResourceLocation(ThermoCraft.MODID, "textures/gui/" + tileEntity.getType().getRegistryName().getPath() + "_gui.png");
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        for (Widget widget : widgets) {
            widget.render(this, matrixStack, mouseX, mouseY, partialTicks);
        }
        renderTooltip(matrixStack, mouseX, mouseY);
    }

    public void addWidget(Widget widget) {
        widget.index = widgets.size();
        widget.getBounds().x += getGuiLeft();
        widget.getBounds().y += getGuiTop();
        widgets.add(widget);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bind(guiTexture);
        this.blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    protected void bindGuiTexture() {
        this.minecraft.getTextureManager().bind(guiTexture);
    }
}
