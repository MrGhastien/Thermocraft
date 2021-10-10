package mrghastien.thermocraft.client.models;

import com.google.common.collect.ImmutableMap;
import mrghastien.thermocraft.api.heat.TransferType;
import mrghastien.thermocraft.common.tileentities.cables.HeatTransmitterTile;
import mrghastien.thermocraft.util.Constants;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HeatTransmitterBakedModel implements IDynamicBakedModel {

    private final IBakedModel bakedCenter;
    private final ImmutableMap<Direction, IBakedModel> transferModels;
    private final ImmutableMap<Direction, IBakedModel> neutralModels;
    private final TextureAtlasSprite particle;

    public HeatTransmitterBakedModel(IBakedModel bakedCenter, ImmutableMap<Direction, IBakedModel> transferModels, ImmutableMap<Direction, IBakedModel> neutralModels, TextureAtlasSprite particle) {
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
    public ItemOverrideList getOverrides() {
        return null;
    }
}
