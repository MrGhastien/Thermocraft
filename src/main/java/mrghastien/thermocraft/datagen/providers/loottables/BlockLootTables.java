package mrghastien.thermocraft.datagen.providers.loottables;

import mrghastien.thermocraft.common.registries.ModBlocks;
import mrghastien.thermocraft.common.registries.ModItems;
import mrghastien.thermocraft.datagen.providers.ModLootTableProvider;
import mrghastien.thermocraft.util.BlockRegistryObject;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class BlockLootTables extends ModLootTableProvider.SubProvider {

    private static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));

    private final Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

    protected void addTables() {
        add(ModBlocks.CALORITE_ORE, silkTouchDropSelf(ModBlocks.CALORITE_ORE, ModItems.RAW_CALORITE, 1, 3));
    }

    private LootTable.Builder silkTouchDropSelf(BlockRegistryObject<?> block, RegistryObject<Item> item, int min, int max) {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(block.getItem())
                                        .when(HAS_SILK_TOUCH)
                                        .otherwise(LootItem.lootTableItem(item.get())
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                                                .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 1))
                                        )
                                )
                );
    }


    private void add(@Nonnull BlockRegistryObject<?> block, @Nonnull LootTable.Builder lootTable) {
        add(block.getId(), lootTable);
    }

    @Override
    protected String getSubFolder() {
        return "blocks";
    }
}
