package mrghastien.thermocraft.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import mrghastien.thermocraft.common.ThermoCraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Stream;

/**
 * Ingredient taking into account the number of items.
 */
public class StackIngredient extends Ingredient {

    public static final StackIngredient EMPTY = new StackIngredient(Stream.empty());

    private int count = 0;

    private StackIngredient(Stream<? extends Value> itemLists) {
        super(itemLists);
    }

    @Override
    public boolean test(ItemStack stack) {
        if(stack == null || stack.isEmpty()) {
            return super.test(stack);
        } else {
            for(ItemStack itemstack : this.getItems()) {
                if (stack.sameItem(itemstack) && stack.getCount() >= getCount() && (!itemstack.hasTag() || itemstack.getTag().equals(stack.getTag()))) {
                    return true;
                }
            }
            return false;
        }
    }

    @Nonnull
    public static StackIngredient of(ItemStack... stacks) {
        return fromItemListStream(Arrays.stream(stacks).map(ItemStackList::new));
    }

    public static StackIngredient of(Tag<Item> tag, int count) {
        return fromItemListStream(Stream.of(new TagStackList(tag, count)));
    }

    public static StackIngredient fromItemListStream(Stream<? extends Ingredient.Value> stream) {
        StackIngredient ingredient = new StackIngredient(stream);
        return ingredient.isEmpty() ? StackIngredient.EMPTY : ingredient;
    }

    private static Value deserializeItemStackList(JsonObject json) {
        if (json.has("item") && json.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");

        } else if (json.has("item")) { //Parsing item
            ResourceLocation itemName = new ResourceLocation(GsonHelper.getAsString(json, "item"));
            if(ForgeRegistries.ITEMS.containsKey(itemName)) {
                Item item = ForgeRegistries.ITEMS.getValue(itemName);
                int count = GsonHelper.getAsInt(json, "count", 1);
                return new ItemStackList(new ItemStack(item, count));
            } else
                throw new JsonSyntaxException("Unknown item '" + itemName + "'");

        } else if (json.has("tag")) { //Parsing tag
            ResourceLocation tagName = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
            Tag<Item> tag = ItemTags.getAllTags().getTag(tagName);
            if (tag == null) {
                throw new JsonSyntaxException("Unknown item tag '" + tagName + "'");
            } else {
                int count = GsonHelper.getAsInt(json, "count", 1);
                return new TagStackList(tag, count);
            }

        } else {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
        }
    }


    public static NonNullList<StackIngredient> readIngredients(JsonArray array) {
        NonNullList<StackIngredient> list = NonNullList.withSize(array.size(), StackIngredient.EMPTY);

        for (int i = 0; i < array.size(); ++i) {
            JsonObject obj = array.get(i).getAsJsonObject();
            StackIngredient ingredient = (StackIngredient) CraftingHelper.getIngredient(obj);
            if (!ingredient.isEmpty()) {
                list.set(i, ingredient);
            }
        }

        return list;
    }

    public int getCount() {
        return count == 0 ? count = getItems()[0].getCount() : count;
    }

    @Nonnull
    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class ItemStackList implements Value {

        private final ItemStack stack;

        public ItemStackList(ItemStack stack) {
            this.stack = stack;
        }

        @Nonnull
        @Override
        public Collection<ItemStack> getItems() {
            return Collections.singletonList(stack);
        }

        @Nonnull
        @Override
        public JsonObject serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("type", Serializer.ID.toString());
            json.addProperty("item", stack.getItem().getRegistryName().toString());
            json.addProperty("count", stack.getCount());
            return json;
        }
    }

    public static class TagStackList implements Value {

        private final Tag<Item> tag;
        private final int count;

        public TagStackList(Tag<Item> tag, int count) {
            this.tag = tag;
            this.count = count;
        }

        @Nonnull
        @Override
        public Collection<ItemStack> getItems() {
            List<ItemStack> list = new ArrayList<>();
            for(Item item : this.tag.getValues()) {
                list.add(new ItemStack(item, count));
            }

            if(list.size() == 0 && !ForgeConfig.SERVER.treatEmptyTagsAsAir.get()) {
                list.add(new ItemStack(Blocks.BARRIER)
                        .setHoverName(new TextComponent(
                                "Empty Tag: " + ItemTags.getAllTags().getId(this.tag).toString())));
            }
            return list;
        }

        @Nonnull
        @Override
        public JsonObject serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("type", Serializer.ID.toString());
            json.addProperty("tag", ItemTags.getAllTags().getId(this.tag).toString());
            json.addProperty("count", count);
            return json;
        }

    }

    public static class Serializer implements IIngredientSerializer<StackIngredient> {
        public static final Serializer INSTANCE  = new Serializer();
        public static final ResourceLocation ID = ThermoCraft.modLoc("stacked_item");

        @Nonnull
        @Override
        public StackIngredient parse(@Nonnull FriendlyByteBuf buffer) {
            return fromItemListStream(Stream.generate(() -> new ItemStackList(buffer.readItem())));
        }

        @Nonnull
        @Override
        public StackIngredient parse(@Nonnull JsonObject json) {
            return fromItemListStream(Stream.of(deserializeItemStackList(json)));
        }

        @Override
        public void write(FriendlyByteBuf buffer, StackIngredient ingredient) {
            ItemStack[] items = ingredient.getItems();
            buffer.writeVarInt(items.length);

            for (ItemStack stack : items)
                buffer.writeItem(stack);
        }
    }
}
