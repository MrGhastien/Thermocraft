package mrghastien.thermocraft.client.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mrghastien.thermocraft.common.blocks.machines.boiler.BoilerBlockEntity;
import mrghastien.thermocraft.common.capabilities.fluid.ModFluidHandler;
import mrghastien.thermocraft.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class BoilerRenderer implements BlockEntityRenderer<BoilerBlockEntity> {

    public BoilerRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(@Nonnull BoilerBlockEntity blockEntity, float pPartialTick, @Nonnull PoseStack pPoseStack, @Nonnull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        ModFluidHandler fluidHandler = blockEntity.getInputFluidHandler();
        FluidStack contained = blockEntity.getInputFluidHandler().getFluidInTank(0);
        if(contained.isEmpty()) return;

        VertexConsumer builder = pBufferSource.getBuffer(Sheets.translucentCullBlockSheet());
        pPoseStack.pushPose();
        pPoseStack.scale(1f/16f, 1f/16f, 1f/16f);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(contained.getFluid().getAttributes().getStillTexture());
        int code = contained.getFluid().getAttributes().getColor();

        float y = MathUtils.map((float) contained.getAmount(), 0f, (float) fluidHandler.getTankCapacity(0), 5f, 16f);
        quad(builder, pPoseStack, 2, y, 2, 2, y, 14, 14, y, 14, 14, y, 2, ((code >> 16) & 0xff) / 255.0f, ((code >> 8) & 0xff) / 255.0f, (code & 0xff) / 255.0f, ((code >>24) & 0xff) / 255.0f, pPackedLight, pPackedOverlay, sprite);
        pPoseStack.popPose();
    }

    private void vertex(VertexConsumer builder, PoseStack stack, float x, float y, float z, float u, float v, float r, float g, float b, float a, int light, int overlay) {
        builder.vertex(stack.last().pose(), x, y, z)
                .color(r, g, b, a) //MAKE SURE TO WRITE 1.0f FOR FLOAT VERSION !
                .uv(u, v) //Texture coords
                .overlayCoords(0)
                .uv2(light) //LightMap [0; 240]
                .normal(0, 1, 0)
                .endVertex();
    }

    private void quad(VertexConsumer builder, PoseStack stack, float x1, float y1, float z1, float x2, float y2, float z2,
                      float x3, float y3, float z3, float x4, float y4, float z4, float r, float g, float b, float a, int light, int overlay, TextureAtlasSprite sprite) {
        vertex(builder, stack, x1, y1, z1, sprite.getU0(), sprite.getV0(), r, g, b, a, light, overlay);
        vertex(builder, stack, x2, y2, z2, sprite.getU1(), sprite.getV0(), r, g, b, a, light, overlay);
        vertex(builder, stack, x3, y3, z3, sprite.getU1(), sprite.getV1(), r, g, b, a, light, overlay);
        vertex(builder, stack, x4, y4, z4, sprite.getU0(), sprite.getV1(), r, g, b, a, light, overlay);

    }
}
