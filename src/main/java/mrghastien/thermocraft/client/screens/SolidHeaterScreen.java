package mrghastien.thermocraft.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import mrghastien.thermocraft.client.screens.widgets.ThermoBar;
import mrghastien.thermocraft.client.screens.widgets.Widget;
import mrghastien.thermocraft.common.blocks.machines.solidheater.SolidHeaterBlockEntity;
import mrghastien.thermocraft.common.inventory.menus.SolidHeaterMenu;
import mrghastien.thermocraft.util.MathUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class SolidHeaterScreen extends BaseContainerScreen<SolidHeaterMenu, SolidHeaterBlockEntity> {

    public SolidHeaterScreen(SolidHeaterMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        addWidget(new ThermoBar(tileEntity::getHeatHandler, 152, 8, 9, 70, 293, 750, Widget.Orientation.UP));
    }

    @Override
    protected void renderBg(@Nonnull PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(poseStack, partialTicks, mouseX, mouseY);

        int c = tileEntity.getTotalBurnTime() == 0 ? 15 : MathUtils.scale(tileEntity.getTotalBurnTime() - tileEntity.getBurnTime(), tileEntity.getTotalBurnTime(), 15);
        this.blit(poseStack, this.getGuiLeft() + 104, this.getGuiTop() + 34 + c, 176, c, 11 , 15 - c);
    }
}
