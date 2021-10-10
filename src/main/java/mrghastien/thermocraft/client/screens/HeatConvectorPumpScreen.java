package mrghastien.thermocraft.client.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatConvectorNetwork;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.inventory.containers.ConvectorControllerContainer;
import mrghastien.thermocraft.common.tileentities.cables.HeatConvectorPumpTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class HeatConvectorPumpScreen extends BaseContainerScreen<ConvectorControllerContainer, HeatConvectorPumpTile> {

    public HeatConvectorPumpScreen(ConvectorControllerContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        HeatConvectorNetwork net = (HeatConvectorNetwork) HeatNetworkHandler.instance().getClient(tileEntity.getNetworkId());
        if(net == null) return;
        font.draw(matrixStack, "Fluid : " + net.getFluid().getAttributes().getDisplayName(null).getString(), 30, 12, 0x00FF00);
        font.draw(matrixStack, "Network Size : " + net.size(), 30, 32, 0x00FF00);
        font.draw(matrixStack, "Valid : " + net.canWork(), 30, 42, 0x00FF00);
    }
}
