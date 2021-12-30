package mrghastien.thermocraft.client.screens.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import mrghastien.thermocraft.api.heat.IHeatHandler;
import mrghastien.thermocraft.util.Constants;
import mrghastien.thermocraft.util.RenderUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.function.Supplier;

public class ThermoBar extends Widget {

    final Supplier<IHeatHandler> handlerSupplier;
    final Orientation orientation;
    final int min;
    final int max;
    public ThermoBar(Supplier<IHeatHandler> handlerSupplier, int x, int y, int width, int height, int min, int max, Orientation orientation) {
        super(x, y, width, height);
        this.orientation = orientation;
        this.handlerSupplier = handlerSupplier;
        this.min = min;
        this.max = max;
    }

    @Override
    protected void renderBg(Screen screen, MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        int x = bounds.x;
        int y = bounds.y;
        int width = bounds.width;
        int height = bounds.height;

        int blitOffset = screen.getBlitOffset();
        Color cold = new Color(255, 0, 0);
        Color hot = new Color (255, 255, 0);
        int[] rotated = orientation.rotate(x, y, width, height, (int)getHandler().getTemperature(), min, max);
        RenderUtils.fillGradientLerpColor(rotated[0], rotated[1], rotated[2], rotated[3], width, height, blitOffset, hot, cold, orientation.isVertical() ? RenderUtils.GradientMode.VERTICAL : RenderUtils.GradientMode.HORIZONTAL);
    }

    public IHeatHandler getHandler() {
        return handlerSupplier.get();
    }

    @Override
    protected void onHovered(Screen screen, MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        IHeatHandler handler = getHandler();
        setTooltips(String.format("Heat : %3.2f " + Constants.TEMPERATURE_UNIT, handler.getTemperature()),
                TextFormatting.DARK_GRAY + "Energy : " + handler.getInternalEnergy().longValue() + " " + Constants.ENERGY_UNIT,
                String.format("%sDissipation : %.2f %s/t", TextFormatting.DARK_GRAY, Math.max(0, -handler.getDissipation()), Constants.ENERGY_UNIT));
        super.onHovered(screen, stack, mouseX, mouseY, partialTicks);
    }
}
