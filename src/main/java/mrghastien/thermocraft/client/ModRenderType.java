package mrghastien.thermocraft.client;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

public class ModRenderType extends RenderType {

    public static final RenderType GLOW_TRANSPARENT = glowTransparent();

    public static RenderType glowTransparent() {
        return RenderType.create("glow_transparent", DefaultVertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, true, false,
        RenderType.State.builder()
                .setWriteMaskState(RenderState.COLOR_WRITE)
                .setTextureState(RenderState.NO_TEXTURE)
                .setLayeringState(RenderState.POLYGON_OFFSET_LAYERING)
                .setShadeModelState(FLAT_SHADE)
                .setTransparencyState(RenderState.TRANSLUCENT_TRANSPARENCY)
                //.setOutputState(RenderState.WEATHER_TARGET)
                .setCullState(RenderState.NO_CULL)
                .createCompositeState(false)

        );
    }

    //Useless, dummy
    public ModRenderType(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_, Runnable p_i225992_8_) {
        super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
    }
}
