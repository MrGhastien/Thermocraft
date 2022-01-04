package mrghastien.thermocraft.common.registries;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.items.HeatConvectorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ThermoCraft.MODID);

    public static final RegistryObject<HeatConvectorItem> HEAT_CONVECTOR = ITEMS.register("heat_convector", HeatConvectorItem::new);

    public static final Item.Properties DEFAULT_ITEM_PROPERTIES = new Item.Properties().tab(Tabs.MAIN);

    public static final RegistryObject<Item> RAW_CALORITE = ITEMS.register("raw_calorite", () -> new Item(DEFAULT_ITEM_PROPERTIES));
    public static final RegistryObject<Item> POLISHED_CALORITE_CRYSTAL = ITEMS.register("polished_calorite_crystal", () -> new Item(DEFAULT_ITEM_PROPERTIES));

    public static class Tabs {
        public static final CreativeModeTab MAIN = new CreativeModeTab(ThermoCraft.MODID) {
            @Nonnull
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Items.BARREL);
            }
        };
    }

}
