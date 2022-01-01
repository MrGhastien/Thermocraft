package mrghastien.thermocraft.datagen;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class CriterionHelper {

	public static InventoryChangeTrigger.TriggerInstance hasItem(ItemLike itemIn) {
        return hasItem(ItemPredicate.Builder.item().of(itemIn).build());
    }

	public static InventoryChangeTrigger.TriggerInstance hasItem(Tag<Item> tagIn) {
        return hasItem(ItemPredicate.Builder.item().of(tagIn).build());
    }

    public static InventoryChangeTrigger.TriggerInstance hasItem(ItemPredicate... predicates) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(predicates);
    }

    public static PlacedBlockTrigger.TriggerInstance placedBlock(Block block) {
	    return PlacedBlockTrigger.TriggerInstance.placedBlock(block);
    }
}