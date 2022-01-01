package mrghastien.thermocraft.client.models;

import com.google.common.collect.ImmutableMap;
import mrghastien.thermocraft.api.heat.TransferType;
import mrghastien.thermocraft.common.blocks.transmitters.HeatTransmitterTile;
import mrghastien.thermocraft.util.Constants;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class HeatTransmitterBakedModel implements IDynamicBakedModel {

    private final BakedModel bakedCenter;
    private final ImmutableMap<Direction, BakedModel> transferModels;
    private final ImmutableMap<Direction, BakedModel> neutralModels;
    private final TextureAtlasSprite particle;

    public HeatTransmitterBakedModel(BakedModel bakedCenter, ImmutableMap<Direction, BakedModel> transferModels, ImmutableMap<Direction, BakedModel> neutralModels, TextureAtlasSprite particle) {
        this.bakedCenter = bakedCenter;
        this.transferModels = transferModels;
        this.neutralModels = neutralModels;
        this.particle = particle;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>();
        RenderType layer = MinecraftForgeClient.getRenderLayer();
        if(state == null || layer == null) return quads;

        quads.addAll(bakedCenter.getQuads(state, side, rand, extraData));
        for(Direction dir : Constants.DIRECTIONS) {
            TransferType transferType = extraData.getData(HeatTransmitterTile.PROPERTY_MAP.get(dir));
            if(transferType == null) transferType = TransferType.NONE;
            if(transferType.canTransfer()) quads.addAll(transferModels.get(dir).getQuads(state, side, rand, extraData));
            else if(transferType == TransferType.NEUTRAL) quads.addAll(neutralModels.get(dir).getQuads(state, side, rand, extraData));
        }
        return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particle;
    }

    @Override
    public ItemOverrides getOverrides() {
        return null;
    }


}
