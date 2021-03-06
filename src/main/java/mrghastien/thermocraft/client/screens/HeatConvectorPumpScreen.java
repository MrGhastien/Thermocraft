package mrghastien.thermocraft.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatConvectorNetwork;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.inventory.menus.ConvectorControllerMenu;
import mrghastien.thermocraft.common.blocks.transmitters.convector.HeatConvectorPumpBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class HeatConvectorPumpScreen extends BaseContainerScreen<ConvectorControllerMenu, HeatConvectorPumpBlockEntity> {

    public HeatConvectorPumpScreen(ConvectorControllerMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        HeatConvectorNetwork net = (HeatConvectorNetwork) HeatNetworkHandler.instance().getClient(tileEntity.getNetworkId());
        if(net == null) return;
        font.draw(matrixStack, "Fluid : " + net.getFluid().getAttributes().getDisplayName(null).getString(), 30, 12, 0x00FF00);
        font.draw(matrixStack, "Network Size : " + net.size(), 30, 32, 0x00FF00);
        font.draw(matrixStack, "Valid : " + net.canWork(), 30, 42, 0x00FF00);
    }
}
