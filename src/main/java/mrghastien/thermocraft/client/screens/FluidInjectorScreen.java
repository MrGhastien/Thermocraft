package mrghastien.thermocraft.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import mrghastien.thermocraft.client.screens.widgets.FluidTankWidget;
import mrghastien.thermocraft.common.blocks.machines.fluidinjector.FluidInjectorBlockEntity;
import mrghastien.thermocraft.common.inventory.menus.FluidInjectorContainer;
import mrghastien.thermocraft.util.MathUtils;
import mrghastien.thermocraft.util.RenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;

public class FluidInjectorScreen extends BaseContainerScreen<FluidInjectorContainer, FluidInjectorBlockEntity> {

    public FluidInjectorScreen(FluidInjectorContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        addWidget(new FluidTankWidget(tileEntity::getTank,
                8, 8, 16, 70, true));

        this.imageHeight = 175;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelX = (this.imageWidth - font.width(title)) / 2;
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
        FluidStack f = tileEntity.getTank().getFluid();
        int progress = tileEntity.getProgress();
        int maxProgress = tileEntity.getMaxProgress();
        int scaledProgress = maxProgress == 0 ? 0 : MathUtils.scale(progress, maxProgress, 22);
        if(!f.isEmpty()) {
            blit(matrixStack, leftPos + 8, topPos + 15, 176, 0, 16, 55);
            RenderUtils.fillFluid(matrixStack, leftPos + 28, topPos + 10, getBlitOffset(), 49, 20, f.getFluid());
            RenderUtils.fillFluid(matrixStack, leftPos + 28, topPos + 54, getBlitOffset(), 49, 20, f.getFluid());
            bindGuiTexture();
            blit(matrixStack, leftPos + 28, topPos + 10, 0, 175, 49, 20); //Top mask
            blit(matrixStack, leftPos + 28, topPos + 54, 49, 175, 49, 20); //Bottom mask

            blit(matrixStack, leftPos + 82, topPos + 35, 98, 175, scaledProgress, 16);
        }

    }
}
