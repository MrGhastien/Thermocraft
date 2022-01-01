package mrghastien.thermocraft.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;

import javax.annotation.Nonnull;
import java.awt.*;

public final class RenderUtils {

	public static int color(int r, int g, int b, int a) {
		return new Color(r, g, b, a).getRGB();
	}

	public static void fillGradient(int xPos, int yPos, int width, int height, int offset, Color start, Color end,
			GradientMode mode) {
		int lastXPos = xPos + width;
		int lastYPos = yPos + height;
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		//RenderSystem.disableAlphaTest();
		RenderSystem.defaultBlendFunc();
		//RenderSystem.shadeModel(GL11.GL_SMOOTH);
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		switch(mode) {
			case VERTICAL:
				bufferbuilder.vertex(lastXPos, yPos, offset).color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha()).endVertex();
				bufferbuilder.vertex(xPos, yPos, offset).color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha()).endVertex();
				bufferbuilder.vertex(xPos, lastYPos, offset).color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha()).endVertex();
				bufferbuilder.vertex(lastXPos, lastYPos, offset).color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha()).endVertex();
			break;
			
			case HORIZONTAL:
				bufferbuilder.vertex(lastXPos, yPos, offset).color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha()).endVertex();
				bufferbuilder.vertex(xPos, yPos, offset).color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha()).endVertex();
				bufferbuilder.vertex(xPos, lastYPos, offset).color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha()).endVertex();
				bufferbuilder.vertex(lastXPos, lastYPos, offset).color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha()).endVertex();
				break;
		}
		tessellator.end();
		//RenderSystem.shadeModel(GL11.GL_FLAT);
		RenderSystem.disableBlend();
		//RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}
	
	public static void fillGradientLerpColor(int xPos, int yPos, int width, int height, int maxWidth, int maxHeight, int offset, Color start, Color end,
			GradientMode mode) {
		float lerpHeight = MathUtils.inverseLerp(0, maxHeight, height);
		fillGradient(xPos, yPos, width, height, offset, lerpColor(start, end, lerpHeight), end, mode);
		
	}

	public static void fillFluid(PoseStack stack, int x, int y, int offset, int width, int height, @Nonnull Fluid f) {
		Minecraft mc = Minecraft.getInstance();
		FluidAttributes attributes = f.getAttributes();
		ResourceLocation id = attributes.getStillTexture();
		TextureAtlasSprite sprite = mc.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(id);
		RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
		Color color = new Color(attributes.getColor());
		RenderSystem.setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
		int heightIterations = (height) / sprite.getHeight(); //The amount of full height quads
		int widthIterations = (width) / sprite.getWidth(); //The amount of full width quads
		int heightRemainder = (height) % sprite.getHeight(); //The height of the last quad
		int widthRemainder = (width) % sprite.getWidth(); //The width of the last quad
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		for(int i = 0; i < heightIterations; i++) {
			for(int j = 0; j < widthIterations; j++) {
				//Bottom left full quads (if large enough)
				renderFluidQuad(stack, sprite, x + j * sprite.getWidth(), y + height - (i + 1) * sprite.getHeight(), offset, width, height);
			}
			//Right quads
			renderFluidQuad(stack, sprite, x + widthIterations * sprite.getWidth(), y + height - (i + 1) * sprite.getHeight(), offset, widthRemainder, sprite.getHeight());
		}
		for(int i = 0; i < widthIterations; i++) {
			//Top quads
			renderFluidQuad(stack, sprite, x + i * sprite.getWidth(), y, offset, sprite.getWidth(), heightRemainder);
		}
		//Top Right quad
		renderFluidQuad(stack, sprite, x + widthIterations * sprite.getWidth(), y, offset, widthRemainder, heightRemainder);
		bufferbuilder.end();
		//RenderSystem.enableAlphaTest();
		bufferbuilder.end();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

	public static void renderFluidQuad(PoseStack stack, TextureAtlasSprite sprite, int x, int y, int z, int width, int height) {
		int spriteWidth = MathUtils.clamp(sprite.getWidth(), 0, width);
		int spriteHeight = MathUtils.clamp(sprite.getHeight(), 0, height);
		float u = MathUtils.clamp(MathUtils.inverseLerp(0, sprite.getWidth(), width) * 16, 0, 16);
		float v = MathUtils.clamp(MathUtils.inverseLerp(sprite.getHeight(), 0, height) * 16, 0, 16);

		Matrix4f matrix = stack.last().pose();
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		bufferbuilder.vertex(matrix, (float)x, (float)(y + spriteHeight), (float)z).uv(sprite.getU0(), sprite.getV1()).endVertex();
		bufferbuilder.vertex(matrix, (float)(x + spriteWidth), (float)(y + spriteHeight), (float)z).uv(sprite.getU(u), sprite.getV1()).endVertex();
		bufferbuilder.vertex(matrix, (float)(x + spriteWidth), (float)y, (float)z).uv(sprite.getU(u), sprite.getV(v)).endVertex();
		bufferbuilder.vertex(matrix, (float)x, (float)y, (float)z).uv(sprite.getU0(), sprite.getV(v)).endVertex();
	}
	
	public static Color lerpColor(Color start, Color end, float t) {
		int lerpRed = (int) Mth.clampedLerp(end.getRed(), start.getRed(), t);
		int lerpGreen = (int) Mth.clampedLerp(end.getGreen(), start.getGreen(), t);
		int lerpBlue = (int) Mth.clampedLerp(end.getBlue(), start.getBlue(), t);
		int lerpAlpha = (int) Mth.clampedLerp(end.getAlpha(), start.getAlpha(), t);
		return new Color(lerpRed, lerpGreen, lerpBlue, lerpAlpha);
	}

	public enum GradientMode {
		VERTICAL, HORIZONTAL
	}

}
