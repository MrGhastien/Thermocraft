package mrghastien.thermocraft.client.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Cable;
import mrghastien.thermocraft.common.capabilities.heat.transport.cables.ConvectorCable;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatConvectorNetwork;
import mrghastien.thermocraft.common.tileentities.cables.HeatConvectorTile;
import mrghastien.thermocraft.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public class HeatConvectorRenderer implements BlockEntityRenderer<HeatConvectorTile> {

    private static final EnumSet<Direction> X_AXIS_DIRECTIONS = EnumSet.of(Direction.EAST, Direction.WEST);
    private static final EnumSet<Direction> Y_AXIS_DIRECTIONS = EnumSet.of(Direction.UP, Direction.DOWN);
    private static final EnumSet<Direction> Z_AXIS_DIRECTIONS = EnumSet.of(Direction.NORTH, Direction.SOUTH);

    public HeatConvectorRenderer(BlockEntityRendererProvider.Context ctx) {}



    @Override
    public void render(HeatConvectorTile tile, float partialTicks, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource multiBufferSource, int lightLevel, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        ConvectorCable cable = tile.getCable();
        HeatConvectorNetwork net = (HeatConvectorNetwork) cable.getNetwork();
        if(net == null || !net.isValid()) return;
        Fluid fluid = cable.getFluid();
        if(fluid == null || fluid == Fluids.EMPTY) return;

        //Don't use RenderType.translucent() because it prevents anything from rendering with fabulous graphics
        VertexConsumer builder = multiBufferSource.getBuffer(Sheets.translucentCullBlockSheet());
        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        TextureAtlasSprite sprite = mc.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluid.getAttributes().getStillTexture());
        //Vertex coordinates for the center part
        float offset = 0.24f;
        float verticalWidth = offset * 2f;
        float minX = -0.24f, minY = -0.24f, minZ = -0.24f;
        float tubeWidth = 0.48f, length = 1 - 0.24f;
        float height = 0.24f - minY;
        //Core
        EnumSet<Direction> cullFaces = EnumSet.copyOf(cable.getCableConnections());
        cullFaces.remove(Direction.UP);
        cullFaces.remove(Direction.DOWN);
        cuboid(builder, matrixStack, minX, minY, minZ, tubeWidth, height, tubeWidth, sprite, cullFaces);
        //Segments
        for (Direction dir : cable.getCableConnections()) {
            switch (dir) {
                case NORTH:
                    cuboid(builder, matrixStack, minX, minY, -1, tubeWidth, height, length, sprite, Z_AXIS_DIRECTIONS);
                    break;
                case SOUTH:
                    cuboid(builder, matrixStack, minX, minY, minZ + tubeWidth, tubeWidth, height, length, sprite, Z_AXIS_DIRECTIONS);
                    break;
                case EAST:
                    cuboid(builder, matrixStack, minX + tubeWidth, minY, minZ, length, height, tubeWidth, sprite, X_AXIS_DIRECTIONS);
                    break;
                case WEST:
                    cuboid(builder, matrixStack, -1, minY, minZ, length, height, tubeWidth, sprite, X_AXIS_DIRECTIONS);
                    break;
                case DOWN:
                    cuboid(builder, matrixStack, -offset, -1, -offset, verticalWidth, length, verticalWidth, sprite, Y_AXIS_DIRECTIONS);
                    break;
                case UP:
                    cuboid(builder, matrixStack, -offset, minY + tubeWidth, -offset, verticalWidth, length, verticalWidth, sprite, Y_AXIS_DIRECTIONS);
                    break;
            }
        }
        matrixStack.popPose();
    }

    private float getU(TextureAtlasSprite sprite, float value) {
        return sprite.getU(MathUtils.map(value, -1, 1, 0, 16));
    }

    private float getV(TextureAtlasSprite sprite, float value) {
        return sprite.getV(MathUtils.map(value, -1, 1, 0, 16));
    }

    private void vertex(VertexConsumer builder, PoseStack stack, float x, float y, float z, float u, float v) {
        PoseStack.Pose e = stack.last();
        builder.vertex(e.pose(), x, y, z)
                .color(1.0f, 1.0f, 1.0f, 1.0f) //MAKE SURE TO WRITE 1.0f FOR FLOAT VERSION !
                .uv(u, v) //Texture coords
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(0, 240) //LightMap [0; 240]
                .normal(e.normal(), 1, 0, 0)
                .endVertex();
    }

    private boolean isOnlyVertical(Cable c) {
        byte result = 0;
        for(Direction dir : c.getCableConnections()) {
            switch (dir) {
                case NORTH:
                case EAST:
                case WEST:
                case SOUTH:
                    return false;
                case UP:
                case DOWN:
                    result++;
            }
        }
        return result == 2;
    }


    private void quad(VertexConsumer builder, PoseStack stack, float x1, float y1, float z1, float x2, float y2, float z2,
                      float x3, float y3, float z3, float x4, float y4, float z4, float minU, float maxU, float minV, float maxV, boolean inverted) {
        if(inverted) {
            vertex(builder, stack, x4, y4, z4, minU, maxV);
            vertex(builder, stack, x3, y3, z3, maxU, maxV);
            vertex(builder, stack, x2, y2, z2, maxU, minV);
            vertex(builder, stack, x1, y1, z1, minU, minV);
        } else {
            vertex(builder, stack, x1, y1, z1, minU, minV);
            vertex(builder, stack, x2, y2, z2, maxU, minV);
            vertex(builder, stack, x3, y3, z3, maxU, maxV);
            vertex(builder, stack, x4, y4, z4, minU, maxV);
        }
    }

    private void quad(VertexConsumer builder, PoseStack stack, float x1, float y1, float z1, float x2, float y2, float z2,
                      float x3, float y3, float z3, float x4, float y4, float z4, TextureAtlasSprite sprite) {
        quad(builder, stack, x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), false);
    }

    private void cuboid(VertexConsumer builder, PoseStack stack, float x, float y, float z, float width, float height, float length, TextureAtlasSprite sprite, EnumSet<Direction> cullFaces) {
        float maxX = x + width, maxY = y + height, maxZ = z + length;
        if(!cullFaces.contains(Direction.DOWN)) quad(builder, stack, x, y, z, maxX, y, z, maxX, y, maxZ, x, y, maxZ, getU(sprite, x), getU(sprite, maxX), getV(sprite, z), getV(sprite, maxZ), false); //BOTTOM
        if(!cullFaces.contains(Direction.UP)) quad(builder, stack, x, maxY, z, maxX, maxY, z, maxX, maxY, maxZ, x, maxY, maxZ, getU(sprite, x), getU(sprite, maxX), getV(sprite, z), getV(sprite, maxZ), true); //TOP
        if(!cullFaces.contains(Direction.WEST)) quad(builder, stack, maxX, y, z, maxX, maxY, z, maxX, maxY, maxZ, maxX, y, maxZ, getU(sprite, y), getU(sprite, maxY), getV(sprite, z), getV(sprite, maxZ), false);
        if(!cullFaces.contains(Direction.EAST)) quad(builder, stack, x, y, z, x, maxY, z, x, maxY, maxZ, x, y, maxZ, getU(sprite, y), getU(sprite, maxY), getV(sprite, z), getV(sprite, maxZ), true);
        if(!cullFaces.contains(Direction.NORTH)) quad(builder, stack, x, y, z, maxX, y, z, maxX, maxY, z, x, maxY, z, getU(sprite, x), getU(sprite, maxX), getV(sprite, y), getV(sprite, maxY), true);
        if(!cullFaces.contains(Direction.SOUTH)) quad(builder, stack, x, y, maxZ, maxX, y, maxZ, maxX, maxY, maxZ, x, maxY, maxZ, getU(sprite, x), getU(sprite, maxX), getV(sprite, y), getV(sprite, maxY), false);
    }
}
