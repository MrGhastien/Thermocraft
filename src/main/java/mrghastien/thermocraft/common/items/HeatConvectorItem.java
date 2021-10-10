package mrghastien.thermocraft.common.items;

import mrghastien.thermocraft.common.registries.ModBlocks;
import mrghastien.thermocraft.common.registries.ModItems;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class HeatConvectorItem extends BlockItem {

    public HeatConvectorItem() {
        super(ModBlocks.HEAT_CONVECTOR_BLOCK.get(), new Item.Properties().tab(ModItems.Tabs.MAIN));
    }

    @Nonnull
    @Override
    public ITextComponent getName(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getTag();
        IFormattableTextComponent name = (IFormattableTextComponent) super.getName(itemStack);
        if(nbt == null || !nbt.contains("fluid") || nbt.getString("fluid").equals(Fluids.EMPTY.getRegistryName().getPath()))
            return name.append(" (Empty)");
        else {
            Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(nbt.getString("fluid")));
            return name.append(" (").append(new TranslationTextComponent(f.getAttributes().getTranslationKey())).append(")");
        }
    }
}
