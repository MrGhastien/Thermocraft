package mrghastien.thermocraft.client.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class HeatTransmitterLoader implements IModelLoader<HeatTransmitterGeometry> {
    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {

    }

    @Nonnull
    @Override
    public HeatTransmitterGeometry read(@Nonnull JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        validate(modelContents);
        Map<String, String> textureMap = new HashMap<>();
        if(modelContents.has("textures")) {
            JsonObject textures = GsonHelper.getAsJsonObject(modelContents, "textures");
            for(Map.Entry<String, JsonElement> entry : textures.entrySet()) {
                textureMap.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
        ModelLoaderRegistry.VanillaProxy center = null;
        ModelLoaderRegistry.VanillaProxy transfer = null;
        ModelLoaderRegistry.VanillaProxy neutral = null;
        ResourceLocation parentLocation = null;
        if(modelContents.has("parent")) {
            parentLocation = getParent(modelContents);
        } else {
            center = (ModelLoaderRegistry.VanillaProxy) ModelLoaderRegistry.getModel(new ResourceLocation("elements"), deserializationContext, GsonHelper.getAsJsonObject(modelContents, "center"));
            transfer = (ModelLoaderRegistry.VanillaProxy) ModelLoaderRegistry.getModel(new ResourceLocation("elements"), deserializationContext, GsonHelper.getAsJsonObject(modelContents, "transfer_connection"));
            neutral = (ModelLoaderRegistry.VanillaProxy) ModelLoaderRegistry.getModel(new ResourceLocation("elements"), deserializationContext, GsonHelper.getAsJsonObject(modelContents, "neutral_connection"));
        }

        return new HeatTransmitterGeometry(center, transfer, neutral, textureMap, parentLocation);
    }

    private void validate(JsonObject modelContents) {
        boolean hasAnySegmentModels = modelContents.has("center") || modelContents.has("transfer_connection") || modelContents.has("neutral_connection");
        boolean hasParent = modelContents.has("parent");
        if(hasAnySegmentModels && hasParent)
            throw new JsonSyntaxException("Model cannot define segment models and have a parent at the same time !");
        else if(!hasAnySegmentModels && !hasParent)
            throw new JsonSyntaxException("Model must define segment models or a parent !");
        else if(!(modelContents.has("center") && modelContents.has("transfer_connection") && modelContents.has("neutral_connection")))
            throw new JsonSyntaxException("Model must have a center, transfer_connection, and neutral_connection models !");
    }

    private ResourceLocation getParent(JsonObject modelContents) {
        return new ResourceLocation(GsonHelper.getAsString(modelContents, "parent"));
    }
}
