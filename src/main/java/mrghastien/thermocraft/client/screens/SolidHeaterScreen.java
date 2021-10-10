package mrghastien.thermocraft.client.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import mrghastien.thermocraft.client.screens.widgets.ThermoBar;
import mrghastien.thermocraft.client.screens.widgets.Widget;
import mrghastien.thermocraft.common.inventory.containers.SolidHeaterContainer;
import mrghastien.thermocraft.common.tileentities.SolidHeaterTile;
import mrghastien.thermocraft.util.MathUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class SolidHeaterScreen extends BaseContainerScreen<SolidHeaterContainer, SolidHeaterTile> {

    public SolidHeaterScreen(SolidHeaterContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        addWidget(new ThermoBar(tileEntity::getHeatHandler, 152, 8, 9, 70, 293, 750, Widget.Orientation.UP));
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);

        int c = tileEntity.getTotalBurnTime() == 0 ? 15 : MathUtils.scale(tileEntity.getTotalBurnTime() - tileEntity.getBurnTime(), tileEntity.getTotalBurnTime(), 15);
        this.blit(matrixStack, this.getGuiLeft() + 104, this.getGuiTop() + 34 + c, 176, c, 11 , 15 - c);
    }
}
