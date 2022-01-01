package mrghastien.thermocraft.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import mrghastien.thermocraft.api.heat.IHeatHandler;
import mrghastien.thermocraft.client.screens.widgets.FluidTankWidget;
import mrghastien.thermocraft.client.screens.widgets.ThermoBar;
import mrghastien.thermocraft.client.screens.widgets.Widget;
import mrghastien.thermocraft.common.inventory.menus.BoilerContainer;
import mrghastien.thermocraft.common.blocks.machines.boiler.BoilerBlockEntity;
import mrghastien.thermocraft.util.MathUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BoilerScreen extends BaseContainerScreen<BoilerContainer, BoilerBlockEntity> {
    public BoilerScreen(BoilerContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);

    }

    @Override
    protected void init() {
        super.init();
        addWidget(new FluidTankWidget(() -> tileEntity.getInputHandler().getTank(0), 8, 8, 16, 70, true));
        addWidget(new FluidTankWidget(() -> tileEntity.getOutputHandler().getTank(0), 152, 8, 16, 70, true));
        addWidget(new ThermoBar(tileEntity::getHeatHandler, 44, 69, 88, 9, 293, 750, Widget.Orientation.RIGHT));
        this.imageHeight = 175;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelX = (this.imageWidth - font.width(title)) / 2;
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
        blit(matrixStack, leftPos + 8, topPos + 8, 176, 18, 16, 70);
        blit(matrixStack, leftPos + 152, topPos + 8, 176, 18, 16, 70);
        IHeatHandler handler = tileEntity.getHeatHandler();
        int heatScaled = (int) MathUtils.clampedMap(handler.getTemperature(), 400, 600, 0, 18);
        blit(matrixStack, leftPos + 57, topPos + 47 + 18 - heatScaled, 176, 18 - heatScaled, 8, heatScaled);
        blit(matrixStack, leftPos + 84, topPos + 47 + 18 - heatScaled, 176, 18 - heatScaled, 8, heatScaled);
        blit(matrixStack, leftPos + 111, topPos + 47 + 18 - heatScaled, 176, 18 - heatScaled, 8, heatScaled);
    }
}
