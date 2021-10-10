package mrghastien.thermocraft.common.registries;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.items.HeatConvectorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ThermoCraft.MODID);

    public static final RegistryObject<HeatConvectorItem> HEAT_CONVECTOR = ITEMS.register("heat_convector", HeatConvectorItem::new);

    public static class Tabs {
        public static final ItemGroup MAIN = new ItemGroup(ThermoCraft.MODID) {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Items.BARREL);
            }
        };
    }

}
