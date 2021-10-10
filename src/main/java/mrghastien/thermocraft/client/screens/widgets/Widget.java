package mrghastien.thermocraft.client.screens.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import mrghastien.thermocraft.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Widget {
	
	protected final Rectangle bounds;
	protected final Minecraft mc;
		
	private boolean isHovered = false;
	private String name;
	protected ArrayList<ITextComponent> tooltips;
	public int index = - 1;

	public Widget(int posX, int posY, int width, int height) {
		this.bounds = new Rectangle(posX, posY, width, height);
		this.mc = Minecraft.getInstance();
	}
	
	public void render(Screen screen, MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
		renderBg(screen, stack, mouseX, mouseY, partialTicks);
		renderFg(screen, stack, mouseX, mouseY, partialTicks);
		isHovered = isHovered(mouseX, mouseY);
		if(isHovered) onHovered(screen, stack, mouseX, mouseY, partialTicks);
	}

	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX > bounds.x && mouseX < bounds.width + bounds.x && mouseY > bounds.y && mouseY < bounds.height + bounds.y;
	}
	
	protected void onHovered(Screen screen, MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
		screen.renderComponentTooltip(stack, getTooltips(), mouseX, mouseY);
	}

	protected void onClicked(Screen screen, MatrixStack stack, int mouseX, int mouseY, float partialTicks) {}

	protected void onMouseReleased(Screen screen, MatrixStack stack, int mouseX, int mouseY, float partialTicks) {}

	protected abstract void renderBg(Screen screen, MatrixStack stack, int mouseX, int mouseY, float partialTicks);

	protected void renderFg(Screen screen, MatrixStack stack, int mouseX, int mouseY, float partialTicks) {

	}
	//onHovered, onClicked, onMouseReleased, renderBG, renderFG + getters

	public String getName() {
		return name;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public boolean isHovered() {
		return isHovered;
	}

	public List<ITextComponent> getTooltips() {
		return tooltips;
	}

	public void addTooltip(String tooltip) {
		this.tooltips.add(new StringTextComponent(tooltip));
	}

	public void addTooltip(ITextComponent tooltip) {
		this.tooltips.add(tooltip);
	}

	public void setTooltips(String... tooltips) {
		this.tooltips = new ArrayList<>(tooltips.length);
		for (String tooltip : tooltips) {
			this.tooltips.add(new StringTextComponent(tooltip));
		}
	}

	public enum Orientation {
		UP(0, 1, 0, 1), DOWN(0, 0, 0, 1), LEFT(1, 0, 1, 0), RIGHT(0, 0, 1, 0);

		final int xc;
		final int yc;
		final int wc;
		final int hc;

		Orientation(int xCoeff, int yCoeff, int wCoeff, int hCoeff) {
			this.xc = xCoeff;
			this.yc = yCoeff;
			this.wc = wCoeff;
			this.hc = hCoeff;
		}
		public int[] rotate(int x, int y, int width, int height, int val, int minVal, int maxVal) {
			int rescaledVal = (int) MathUtils.clampedMap(val, minVal, maxVal, 0, this.wc == 0 ? width : height); //rescaled value (ex energy) to stay between zero and width or height.
			int[] outputs = new int[4];
			outputs[0] = x + this.xc * (width - rescaledVal);
			outputs[1] = y + this.yc * (height - rescaledVal);
			outputs[2] = (1 - this.wc) * width + this.wc * rescaledVal;
			outputs[3] = (1 - this.yc) * height + this.hc * rescaledVal;
			return outputs;
		}

		public boolean isVertical() {
			return this.hc == 1;
		}

		public boolean isHorizontal() {
			return this.wc == 1;
		}
	}
}
