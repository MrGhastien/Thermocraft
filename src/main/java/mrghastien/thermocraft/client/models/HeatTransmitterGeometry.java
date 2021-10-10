package mrghastien.thermocraft.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import mrghastien.thermocraft.util.Constants;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class HeatTransmitterGeometry implements IModelGeometry<HeatTransmitterGeometry> {

    private final ModelLoaderRegistry.VanillaProxy center;
    private final ModelLoaderRegistry.VanillaProxy transfer;
    private final ModelLoaderRegistry.VanillaProxy neutral;
    private final Map<String, String> textureMap;
    private final ResourceLocation parentLocation;
    @Nullable private HeatTransmitterGeometry parent;

    public HeatTransmitterGeometry(@Nullable ModelLoaderRegistry.VanillaProxy center, @Nullable ModelLoaderRegistry.VanillaProxy transfer, @Nullable ModelLoaderRegistry.VanillaProxy neutral, Map<String, String> textureMap, @Nullable ResourceLocation parentLocation) {
        this.center = center;
        this.transfer = transfer;
        this.neutral = neutral;
        this.textureMap = textureMap;
        this.parentLocation = parentLocation;
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
        ImmutableMap.Builder<Direction, IBakedModel> transferModels = new ImmutableMap.Builder<>();
        ImmutableMap.Builder<Direction, IBakedModel> neutralModels = new ImmutableMap.Builder<>();
        for(Direction dir : Constants.DIRECTIONS) {
            TransformationMatrix transformationMatrix = modelTransform.getRotation();
            IModelTransform transform = new SimpleModelTransform(new TransformationMatrix(
                    transformationMatrix.getTranslation(),
                    getRotation(dir),
                    transformationMatrix.getScale(),
                    transformationMatrix.getRightRot()
            ));
            transferModels.put(dir, transfer.bake(owner, bakery, spriteGetter, transform, overrides, modelLocation));
            neutralModels.put(dir, neutral.bake(owner, bakery, spriteGetter, transform, overrides, modelLocation));
        }
        return new HeatTransmitterBakedModel(center.bake(owner, bakery, spriteGetter, modelTransform, overrides, modelLocation), transferModels.build(), neutralModels.build(), spriteGetter.apply(owner.resolveTexture(textureMap.get("particle"))));
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        ImmutableList.Builder<RenderMaterial> listBuilder = ImmutableList.builder();
        listBuilder.addAll(center.getTextures(owner, modelGetter, missingTextureErrors));
        listBuilder.addAll(transfer.getTextures(owner, modelGetter, missingTextureErrors));
        listBuilder.addAll(neutral.getTextures(owner, modelGetter, missingTextureErrors));
        return listBuilder.build();
    }

    private Quaternion getRotation(Direction dir) {
        switch(dir) {
            case UP:
                return Vector3f.XP.rotationDegrees(-90.0F);
            case DOWN:
                return Vector3f.XP.rotationDegrees(90.0F);
            case SOUTH:
                return Quaternion.ONE.copy();
            case NORTH:
                return Vector3f.YP.rotationDegrees(180.0f);
            case EAST:
                return Vector3f.YN.rotationDegrees(-90.0f);
            case WEST:
                return Vector3f.YN.rotationDegrees(90.0f);

        }
        return null;
    }

}
