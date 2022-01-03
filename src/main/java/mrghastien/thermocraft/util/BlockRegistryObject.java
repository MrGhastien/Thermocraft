package mrghastien.thermocraft.util;

import mrghastien.thermocraft.common.registries.ModBlocks;
import mrghastien.thermocraft.common.registries.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockRegistryObject<T extends Block> {

    private final RegistryObject<T> blockRegistryObject;
    private final RegistryObject<Item> itemRegistryObject;

    public BlockRegistryObject(RegistryObject<T> blockRegistryObject, RegistryObject<Item> itemRegistryObject) {
        this.blockRegistryObject = blockRegistryObject;
        this.itemRegistryObject = itemRegistryObject;
    }

    public static <T extends Block> BlockRegistryObject<T> register(String name, Supplier<T> blockSupplier) {
        RegistryObject<T> blockRegistryObject = ModBlocks.BLOCKS.register(name, blockSupplier);
        RegistryObject<Item> itemRegistryObject = ModItems.ITEMS.register(name, () -> new BlockItem(blockRegistryObject.get(), new Item.Properties().tab(ModItems.Tabs.MAIN)));
        return new BlockRegistryObject<>(blockRegistryObject, itemRegistryObject);
    }

    public T getBlock() {
        return blockRegistryObject.get();
    }

    public Item getItem() {
        return itemRegistryObject.get();
    }

    public ResourceLocation getId() {
        return blockRegistryObject.getId();
    }
}
