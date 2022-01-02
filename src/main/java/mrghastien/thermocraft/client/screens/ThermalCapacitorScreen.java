package mrghastien.thermocraft.client.screens;

import mrghastien.thermocraft.client.screens.widgets.ThermoBar;
import mrghastien.thermocraft.client.screens.widgets.Widget;
import mrghastien.thermocraft.common.blocks.machines.thermalcapacitor.ThermalCapacitorBlockEntity;
import mrghastien.thermocraft.common.inventory.menus.ThermalCapacitorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ThermalCapacitorScreen extends BaseContainerScreen<ThermalCapacitorMenu, ThermalCapacitorBlockEntity> {

    public ThermalCapacitorScreen(ThermalCapacitorMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        addWidget(new ThermoBar(tileEntity::getHeatHandler, 62, 9, 52, 69, 293, 1000, Widget.Orientation.UP));
        this.imageHeight = 175;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelX = (this.imageWidth - font.width(title)) / 2;
    }
}
