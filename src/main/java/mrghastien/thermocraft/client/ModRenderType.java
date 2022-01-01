package mrghastien.thermocraft.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class ModRenderType extends RenderType {

    public static final RenderType GLOW_TRANSPARENT = glowTransparent();

    public static RenderType glowTransparent() {
        return RenderType.create("glow_transparent", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, true, false,
        RenderType.CompositeState.builder()
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .setTextureState(RenderStateShard.NO_TEXTURE)
                .setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
                //.setShaderState(FLAT_SHADE)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                //.setOutputState(RenderState.WEATHER_TARGET)
                .setCullState(RenderStateShard.NO_CULL)
                .createCompositeState(false)

        );
    }

    //Useless, dummy
    public ModRenderType(String p_i225992_1_, VertexFormat p_i225992_2_, VertexFormat.Mode p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_, Runnable p_i225992_8_) {
        super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
    }
}
