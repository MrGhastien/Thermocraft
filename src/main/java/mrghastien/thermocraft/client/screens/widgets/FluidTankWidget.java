package mrghastien.thermocraft.client.screens.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import mrghastien.thermocraft.util.Constants;
import mrghastien.thermocraft.util.MathUtils;
import mrghastien.thermocraft.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.function.Supplier;

public class FluidTankWidget extends Widget {

    final Supplier<IFluidTank> tankSupplier;
    final boolean drawLines;

    public FluidTankWidget(Supplier<IFluidTank> supplier, int posX, int posY, int width, int height, boolean drawLines) {
        super(posX, posY, width, height);
        this.tankSupplier = supplier;
        this.drawLines = drawLines;
    }


    @Override
    protected void renderBg(Screen screen, PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        IFluidTank tank = getTank();
        Minecraft mc = Minecraft.getInstance();
        FluidStack fluidStack = tank.getFluid();
        if (fluidStack.isEmpty()) return;
        int scaledHeight = MathUtils.scale(tank.getFluidAmount(), tank.getCapacity(), bounds.height);
        RenderUtils.fillFluid(stack, bounds.x, bounds.y + bounds.height - scaledHeight, screen.getBlitOffset(), bounds.width, scaledHeight, fluidStack.getFluid());
    }

    @Override
    protected void onHovered(Screen screen, PoseStack stack, int mouseX, int mouseY) {
        IFluidTank tank = getTank();
        Component name = tank.getFluid().getFluid().getAttributes().getDisplayName(tank.getFluid());
        setTooltips(String.format("%s : %d/%d %s", name.getString(), getTank().getFluidAmount(), getTank().getCapacity(), Constants.VOLUME_UNIT));
        super.onHovered(screen, stack, mouseX, mouseY);
    }

    private IFluidTank getTank() {
        return tankSupplier.get();
    }
}