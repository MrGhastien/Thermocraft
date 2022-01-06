package mrghastien.thermocraft.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import mrghastien.thermocraft.api.capabilities.heat.IHeatHandler;
import mrghastien.thermocraft.client.screens.widgets.FluidTankWidget;
import mrghastien.thermocraft.client.screens.widgets.ThermoBar;
import mrghastien.thermocraft.client.screens.widgets.Widget;
import mrghastien.thermocraft.common.blocks.machines.boiler.BoilerBlockEntity;
import mrghastien.thermocraft.common.inventory.menus.BoilerMenu;
import mrghastien.thermocraft.util.MathUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class BoilerScreen extends BaseContainerScreen<BoilerMenu, BoilerBlockEntity> {
    public BoilerScreen(BoilerMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);

    }

    @Override
    protected void init() {
        super.init();
        addWidget(new FluidTankWidget(() -> tileEntity.getInputFluidHandler().getTank(0), 8, 8, 34, 70, true));
        addWidget(new FluidTankWidget(() -> tileEntity.getOutputFluidHandler().getTank(0), 134, 8, 34, 70, true));
        addWidget(new ThermoBar(tileEntity::getHeatHandler, 51, 69, 74, 9, 0, 750, Widget.Orientation.RIGHT));
        this.imageHeight = 175;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelX = (this.imageWidth - font.width(title)) / 2;
    }

    @Override
    protected void renderBg(@Nonnull PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(poseStack, partialTicks, mouseX, mouseY);

        IHeatHandler handler = tileEntity.getHeatHandler();
        int heatScaled = (int) MathUtils.clampedMap(handler.getTemperature(), 400, 600, 0, 18);
        blit(poseStack, leftPos + 57, topPos + 47 + 18 - heatScaled, 176, 18 - heatScaled, 8, heatScaled);
        blit(poseStack, leftPos + 84, topPos + 47 + 18 - heatScaled, 176, 18 - heatScaled, 8, heatScaled);
        blit(poseStack, leftPos + 111, topPos + 47 + 18 - heatScaled, 176, 18 - heatScaled, 8, heatScaled);
        if(tileEntity.isRunning())
            blit(poseStack, 45, 31, 0, 175, 86, 16);

        blit(poseStack, leftPos + 8, topPos + 8, 176, 18, 34, 70);
        blit(poseStack, leftPos + 134, topPos + 8, 176, 18, 34, 70);
        blit(poseStack, leftPos + 16, topPos + 50, 184, 0, 18, 18);
        blit(poseStack, leftPos + 142, topPos + 50, 184, 0, 18, 18);
    }
}
