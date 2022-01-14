package mrghastien.thermocraft.common.crafting;

import com.google.gson.*;
import mrghastien.thermocraft.common.ThermoCraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

//An ingredient is designed to hold only one fluid or all the fluids of a tag.
//If we want a recipe to have multiple possibilities of ingredients which aren't in the same tag,
//Then it's more convenient to create multiple recipes
public class FluidIngredient extends Ingredient {
    public static final FluidIngredient EMPTY = new FluidIngredient();

    private final FluidValue[] elements;
    private FluidStack[] fluidCache;

    private FluidIngredient() {
        this(Stream.empty());
    }

    private FluidIngredient(Stream<? extends FluidValue> fluids) {
        super(Stream.empty());
        this.elements = fluids.toArray(FluidValue[]::new);
    }

    public boolean test(FluidStack fluid) {
        if(fluid == null || fluid.isEmpty()) return false;
        this.updateCache();
        for(FluidStack stack : fluidCache) {
            if (fluid.containsFluid(stack))
                return true;
        }
        return false;

    }

    public FluidStack[] getFluids() {
        updateCache();
        return fluidCache;
    }

    private void updateCache() {
        if(fluidCache != null) return;
        List<FluidStack> cache = new ArrayList<>();
        for (FluidValue element : elements) {
            cache.addAll(element.getFluids());
        }
        fluidCache = cache.toArray(new FluidStack[0]);
    }

    @Nonnull
    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Nonnull
    @Override
    public JsonElement toJson() {
        if(this.elements.length == 1)
            return elements[0].toJson();

        JsonArray array = new JsonArray();

        for(FluidValue list : elements) {
            array.add(list.toJson());
        }
        return array;
    }

    @Override
    public boolean isEmpty() {
        return elements.length == 0;
    }

    public static FluidIngredient from(Stream<? extends FluidValue> stream) {
        FluidIngredient ing = new FluidIngredient(stream);
        return ing.elements.length == 0 ? FluidIngredient.EMPTY : ing;
    }

    public static FluidIngredient of(FluidStack... fluids) {
        return fluids.length == 0 ? EMPTY : new FluidIngredient(Arrays.stream(fluids).map(SingleFluidValue::new));
    }

    public static FluidIngredient of(Tag<Fluid> tag, int count) {
        return new FluidIngredient(Stream.of(new TagFluidValue(tag, count)));
    }

    @Nonnull
    public static FluidIngredient fromJson(JsonElement json) {
        if(json == null || json.isJsonNull()) throw new JsonSyntaxException("Fluid ingredient cannot be null");
        if(json.isJsonObject()) return from(Stream.of(listFromJson(json.getAsJsonObject())));
        if(json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            if(array.size() == 0) throw new JsonSyntaxException("Fluid array cannot be empty, at least one must be defined");
            return from(StreamSupport.stream(array.spliterator(), false)
                    .map(element -> listFromJson(GsonHelper.convertToJsonObject(element, "fluid"))));
            //The 2nd param is only used for the exception message if failed to convert
        }
        throw new JsonSyntaxException("Expected item to be object or array of objects");
    }

    public static FluidValue listFromJson(JsonObject json) {
        if(json.has("fluid") && json.has("tag"))
            throw new JsonParseException("An ingredient entry is either a tag or a fluid, not both");

        if (json.has("fluid")) {
            ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "fluid"));
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
            if(fluid == null) throw new JsonSyntaxException("Unknown fluid '" + id + "'");

            int amount = GsonHelper.getAsInt(json, "amount");
            return new SingleFluidValue(new FluidStack(fluid, amount));

        } else if(json.has("tag")) {
            ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
            Tag<Fluid> tag = FluidTags.getAllTags().getTag(id);
            if(tag == null) throw new JsonSyntaxException("Unknown fluid tag '" + id + "'");

            return new TagFluidValue(tag, GsonHelper.getAsInt(json, "amount", 100));
        }
        throw new JsonParseException("An ingredient entry needs either a tag or a fluid");
    }

    public static FluidStack getFluidStack(JsonObject json, boolean readNBT, boolean disallowsAirInRecipe) {
        String fluidName = json.get("fluid").getAsString();
        ResourceLocation fluidLocation = new ResourceLocation(fluidName);
        if(!ForgeRegistries.FLUIDS.containsKey(fluidLocation))
            throw new JsonSyntaxException("Unknown fluid '" + fluidName + "'");

        Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidLocation);

        if(fluid == Fluids.EMPTY && disallowsAirInRecipe)
            throw new JsonSyntaxException("Invalid fluid '" + fluidName + "'");

        //TODO: Make proper nbt deserialization

        assert fluid != null;
        return new FluidStack(fluid, GsonHelper.getAsInt(json, "amount", 1));
    }

    public interface FluidValue {
        Collection<FluidStack> getFluids();

        JsonObject toJson();
    }

    public record SingleFluidValue(FluidStack fluid) implements FluidValue {

        @Override
        public Collection<FluidStack> getFluids() {
            return Collections.singletonList(fluid);
        }

        @Override
        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", FluidIngredient.Serializer.ID.toString());
            json.addProperty("fluid", fluid.getFluid().getRegistryName().toString());
            //json.addProperty("NBT", fluid.getTag().toString());
            json.addProperty("amount", fluid.getAmount());
            return json;
        }
    }

    public static class TagFluidValue implements FluidValue {

        final Tag<Fluid> fluidTag;
        final int count;

        public TagFluidValue(Tag<Fluid> tag, int count) {
            this.fluidTag = tag;
            this.count = count;
        }

        @Override
        public Collection<FluidStack> getFluids() {
            Stream<FluidStack> stream = fluidTag.getValues().stream().map(f -> new FluidStack(f, count));
            return stream.collect(Collectors.toList());
        }

        @Override
        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", FluidIngredient.Serializer.ID.toString());
            json.addProperty("tag", FluidTags.getAllTags().getId(this.fluidTag).toString());
            json.addProperty("amount", count);
            return json;
        }
    }

    public static class Serializer implements IIngredientSerializer<FluidIngredient> {

        public static final ResourceLocation ID = new ResourceLocation(ThermoCraft.MODID, "fluid");
        public static final Serializer INSTANCE = new Serializer();

        @Nonnull
        @Override
        public FluidIngredient parse(FriendlyByteBuf buffer) {
            int length = buffer.readVarInt();
            return new FluidIngredient(Stream.generate(() -> new SingleFluidValue(buffer.readFluidStack())).limit(length));
        }

        @Nonnull
        @Override
        public FluidIngredient parse(JsonObject json) {
            if(json.has("fluid") && json.has("tag"))
                throw new JsonParseException("An ingredient entry is either a tag or a fluid, not both");

            if (json.has("fluid")) {
                ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "fluid"));
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
                if(fluid == null) throw new JsonSyntaxException("Unknown fluid '" + id + "'");

                int amount = GsonHelper.getAsInt(json, "amount");
                return FluidIngredient.from(Stream.of(new SingleFluidValue(new FluidStack(fluid, amount))));

            } else if(json.has("tag")) {
                ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
                Tag<Fluid> tag = FluidTags.getAllTags().getTag(id);
                if(tag == null) throw new JsonSyntaxException("Unknown fluid tag '" + id + "'");

                return FluidIngredient.of(tag, GsonHelper.getAsInt(json, "amount", 1000));
            }
            throw new JsonParseException("An ingredient entry needs either a tag or a fluid");
        }

        @Override
        public void write(FriendlyByteBuf buffer, FluidIngredient ingredient) {
            FluidStack[] fluids = ingredient.getFluids();
            buffer.writeVarInt(fluids.length);
            for(FluidStack stack : fluids)
                buffer.writeFluidStack(stack);
        }
    }
}
