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
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

//An ingredient is designed to hold only one fluid or all the fluids of a tag.
//If we want a recipe to have multiple possibilities of ingredients which aren't in the same tag,
//Then it's more convenient to create multiple recipes
public class FluidIngredient extends Ingredient {
    public static final FluidIngredient EMPTY = new FluidIngredient();

    private final IFluidList[] elements;
    private FluidStack[] fluidCache;

    private FluidIngredient() {
        this(Stream.empty());
    }

    private FluidIngredient(Stream<? extends IFluidList> fluids) {
        super(Stream.empty());
        this.elements = fluids.toArray(IFluidList[]::new);
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
        for (IFluidList element : elements) {
            cache.addAll(element.getFluids());
        }
        fluidCache = cache.toArray(new FluidStack[0]);
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson() {
        if(this.elements.length == 1)
            return elements[0].toJson();

        JsonArray array = new JsonArray();

        for(IFluidList list : elements) {
            array.add(list.toJson());
        }
        return array;
    }

    public static FluidIngredient from(Stream<? extends IFluidList> stream) {
        FluidIngredient ing = new FluidIngredient(stream);
        return ing.elements.length == 0 ? FluidIngredient.EMPTY : ing;
    }

    public static FluidIngredient of(FluidStack... fluids) {
        return fluids.length == 0 ? EMPTY : new FluidIngredient(Arrays.stream(fluids).map(SingleFluidList::new));
    }

    public static FluidIngredient of(Tag<Fluid> tag, int count) {
        return new FluidIngredient(Stream.of(new TagFluidList(tag, count)));
    }

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

    public static IFluidList listFromJson(JsonObject json) {
        if(json.has("fluid") && json.has("tag"))
            throw new JsonParseException("An ingredient entry is either a tag or a fluid, not both");

        if (json.has("fluid")) {
            ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "fluid"));
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
            if(fluid == null) throw new JsonSyntaxException("Unknown fluid '" + id + "'");

            int amount = GsonHelper.getAsInt(json, "amount");
            return new SingleFluidList(new FluidStack(fluid, amount));

        } else if(json.has("tag")) {
            ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
            Tag<Fluid> tag = FluidTags.getAllTags().getTag(id);
            if(tag == null) throw new JsonSyntaxException("Unknown fluid tag '" + id + "'");

            return new TagFluidList(tag, GsonHelper.getAsInt(json, "amount", 100));
        }
        throw new JsonParseException("An ingredient entry needs either a tag or a fluid");
    }

    public interface IFluidList {
        Collection<FluidStack> getFluids();

        JsonObject toJson();
    }

    public static class SingleFluidList implements IFluidList {

        final FluidStack fluid;

        public SingleFluidList(FluidStack fluid) {
            this.fluid = fluid;
        }

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

    public static class TagFluidList implements IFluidList {

        final Tag<Fluid> fluidTag;
        final int count;

        public TagFluidList(Tag<Fluid> tag, int count) {
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

        @Override
        public FluidIngredient parse(FriendlyByteBuf buffer) {
            int length = buffer.readVarInt();
            return new FluidIngredient(Stream.generate(() -> new SingleFluidList(buffer.readFluidStack())).limit(length));
        }

        @Override
        public FluidIngredient parse(JsonObject json) {
            if(json.has("fluid") && json.has("tag"))
                throw new JsonParseException("An ingredient entry is either a tag or a fluid, not both");

            if (json.has("fluid")) {
                ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "fluid"));
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
                if(fluid == null) throw new JsonSyntaxException("Unknown fluid '" + id + "'");

                int amount = GsonHelper.getAsInt(json, "amount");
                return FluidIngredient.from(Stream.of(new SingleFluidList(new FluidStack(fluid, amount))));

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
