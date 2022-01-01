package mrghastien.thermocraft.datagen.builders.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ModRecipeBuilder<T extends ModRecipeBuilder<T>> {

	protected final List<ICondition> conditions = new ArrayList<>();
	protected final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
	protected final ResourceLocation serializerName;

	public ModRecipeBuilder(ResourceLocation serializerName) {
		this.serializerName = serializerName;
	}

    @SuppressWarnings("unchecked")
	public T unlockedBy(String name, CriterionTriggerInstance criterion) {
        advancementBuilder.addCriterion(name, criterion);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
	public T addCondition(ICondition condition) {
        conditions.add(condition);
        return (T) this;
    }
	
	protected abstract Result getResult(ResourceLocation id);
	
	public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		this.validate(id);
		this.advancementBuilder.parent(new ResourceLocation("recipes/root"))
				.rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
		consumer.accept(getResult(id));
	}
	
	protected void validate(ResourceLocation id) {
		if (this.advancementBuilder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id);
		}
	}
	
	protected abstract class Result implements FinishedRecipe {

		protected final ResourceLocation id;
		protected final ResourceLocation advancementId;
		
		public Result(ResourceLocation id) {
			this.id = id;
			this.advancementId = new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath());
		}
		
		@Override
		public JsonObject serializeRecipe() {
			JsonObject json = new JsonObject();
			json.addProperty("type", ForgeRegistries.RECIPE_SERIALIZERS.getKey(this.getType()).toString());
			if(!conditions.isEmpty()) {
				JsonArray array = new JsonArray();
				for (ICondition condition : conditions) {
					array.add(CraftingHelper.serialize(condition));
				}
				json.add("conditions", array);
			}
			this.serializeRecipeData(json);
			return json;
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return ForgeRegistries.RECIPE_SERIALIZERS.getValue(serializerName);
		}

		
		@Override
		public JsonObject serializeAdvancement() {
			return advancementBuilder.serializeToJson();
		}

		@Override
		
		public ResourceLocation getAdvancementId() {
			return advancementId;
		}
	}	
}
