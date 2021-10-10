package mrghastien.thermocraft.datagen;

import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.PlacedBlockTrigger;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.Tag;

public class CriterionHelper {

	public static InventoryChangeTrigger.Instance hasItem(net.minecraft.util.IItemProvider itemIn) {
        return hasItem(ItemPredicate.Builder.item().of(itemIn).build());
    }

	public static InventoryChangeTrigger.Instance hasItem(Tag<Item> tagIn) {
        return hasItem(ItemPredicate.Builder.item().of(tagIn).build());
    }

    public static InventoryChangeTrigger.Instance hasItem(ItemPredicate... predicates) {
        return InventoryChangeTrigger.Instance.hasItems(predicates);
    }

    public static PlacedBlockTrigger.Instance placedBlock(Block block) {
	    return PlacedBlockTrigger.Instance.placedBlock(block);
    }
}