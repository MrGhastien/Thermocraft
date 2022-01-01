package mrghastien.thermocraft.client.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mrghastien.thermocraft.client.ModRenderType;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.capabilities.heat.HeatHandler;
import mrghastien.thermocraft.common.tileentities.ThermalCapacitorTile;
import mrghastien.thermocraft.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class ThermalCapacitorRenderer implements BlockEntityRenderer<ThermalCapacitorTile> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ThermoCraft.MODID, "block/thermal_capacitor_core");

    public ThermalCapacitorRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(ThermalCapacitorTile tile, float partialTicks, PoseStack matrixStack, MultiBufferSource multiBufferSource, int lightLevel, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        TextureAtlasSprite sprite = mc.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(TEXTURE);
        VertexConsumer builder = multiBufferSource.getBuffer(ModRenderType.glowTransparent());
        //mc.getTextureManager().bind(PlayerContainer.BLOCK_ATLAS);

        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.scale(0.5f, 0.5f, 0.5f);

        cube(builder, matrixStack, 0, 0, 0, .41f, sprite, tile);

        matrixStack.popPose();
    }

    private void vertex(VertexConsumer builder, PoseStack stack, float x, float y, float z, float u, float v, ThermalCapacitorTile tile) {
        HeatHandler handler = tile.getHeatHandler();
        int scaledTemp = (int) MathUtils.clampedMap(handler.getTemperature(), 1000, 4000, 0, 765);
        int r = MathUtils.clamp(scaledTemp, 0, 255);
        int g = MathUtils.clamp(scaledTemp, 255, 510) - 255;
        int b = MathUtils.clamp(scaledTemp, 510, 765) - 510;
        builder.vertex(stack.last().pose(), x, y, z)
                .color(r, g, b, MathUtils.scale(scaledTemp, 765, 170)) //MAKE SURE TO WRITE 1.0f FOR FLOAT VERSION !
                //.uv(u, v) //Texture coords
                //.uv2(240, 240) //LightMap [0; 240]
                //.normal(1, 0, 0)
                .endVertex();
    }

    private void quad(VertexConsumer builder, PoseStack stack, float x1, float y1, float z1, float x2, float y2, float z2,
                      float x3, float y3, float z3, float x4, float y4, float z4, TextureAtlasSprite sprite, ThermalCapacitorTile tile) {
        vertex(builder, stack, x1, y1, z1, sprite.getU0(), sprite.getV0(), tile);
        vertex(builder, stack, x2, y2, z2, sprite.getU1(), sprite.getV0(), tile);
        vertex(builder, stack, x3, y3, z3, sprite.getU1(), sprite.getV1(), tile);
        vertex(builder, stack, x4, y4, z4, sprite.getU0(), sprite.getV1(), tile);
    }

    private void cube(VertexConsumer builder, PoseStack stack, float x, float y, float z, float scale, TextureAtlasSprite sprite, ThermalCapacitorTile tile) {
        float minX = x - scale, maxX = x + scale;
        float minY = y - scale, maxY = y + scale;
        float minZ = z - scale, maxZ = z + scale;

        quad(builder, stack, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ, minX, minY, minZ, sprite, tile); //FRONT
        quad(builder, stack, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ, sprite, tile); //BACK
        quad(builder, stack, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, minX, maxY, minZ, sprite, tile); //TOP
        quad(builder, stack, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ, sprite, tile); //BOTTOM
        quad(builder, stack, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ, minX, minY, minZ, sprite, tile); //LEFT
        quad(builder, stack, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ, sprite, tile); //RIGHT
    }
}
