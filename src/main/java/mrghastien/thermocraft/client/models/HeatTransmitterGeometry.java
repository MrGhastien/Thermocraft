package mrghastien.thermocraft.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import mrghastien.thermocraft.util.Constants;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.SimpleModelState;
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
    //IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, ModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation
//IModelConfiguration iModelConfiguration, ModelBakery modelBakery, Function<Material, TextureAtlasSprite> function, ModelState modelState, ItemOverrides itemOverrides, ResourceLocation resourceLocation
    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides itemOverrides, ResourceLocation modelLocation) {
        ImmutableMap.Builder<Direction, BakedModel> transferModels = new ImmutableMap.Builder<>();
        ImmutableMap.Builder<Direction, BakedModel> neutralModels = new ImmutableMap.Builder<>();
        for(Direction dir : Constants.DIRECTIONS) {
            Transformation transformationMatrix = modelState.getRotation();
            ModelState transform = new SimpleModelState(new Transformation(
                    transformationMatrix.getTranslation(),
                    getRotation(dir),
                    transformationMatrix.getScale(),
                    transformationMatrix.getRightRotation()
            ));
            transferModels.put(dir, transfer.bake(owner, bakery, spriteGetter, transform, itemOverrides, modelLocation));
            neutralModels.put(dir, neutral.bake(owner, bakery, spriteGetter, transform, itemOverrides, modelLocation));
        }
        return new HeatTransmitterBakedModel(center.bake(owner, bakery, spriteGetter, modelState, itemOverrides, modelLocation), transferModels.build(), neutralModels.build(), spriteGetter.apply(owner.resolveTexture(textureMap.get("particle"))));
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        ImmutableList.Builder<Material> listBuilder = ImmutableList.builder();
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
