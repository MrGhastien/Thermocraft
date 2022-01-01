package mrghastien.thermocraft.util;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.registries.ModBlocks;
import mrghastien.thermocraft.common.registries.ModFluids;
import mrghastien.thermocraft.common.registries.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fmllegacy.RegistryObject;

import java.util.function.UnaryOperator;

public class FluidRegistryObject {

    private final RegistryObject<ForgeFlowingFluid.Source> source;
    private final RegistryObject<ForgeFlowingFluid.Flowing> flowing;
    private final RegistryObject<LiquidBlock> block;
    private final RegistryObject<BucketItem> bucket;

    private final ResourceLocation id;

    private FluidRegistryObject(String name, UnaryOperator<FluidAttributes.Builder> attributes, UnaryOperator<ForgeFlowingFluid.Properties> propertiesOperator) {

        FluidAttributes.Builder attributesBuilder = attributes.apply(FluidAttributes.builder(ThermoCraft.modLoc("block/" + name + "_still"), ThermoCraft.modLoc("block/" + name + "_flowing")));
        ForgeFlowingFluid.Properties properties = propertiesOperator.apply(new ForgeFlowingFluid.Properties(this::getSource, this::getFlowing, attributesBuilder).bucket(this::getBucket).block(this::getBlock));

        this.source = ModFluids.FLUIDS.register(name, () -> new ForgeFlowingFluid.Source(properties));
        this.flowing = ModFluids.FLUIDS.register("flowing_" + name, () -> new ForgeFlowingFluid.Flowing(properties));
        this.block = ModBlocks.BLOCKS.register(name, () -> new LiquidBlock(this::getSource, BlockBehaviour.Properties.of(Material.WATER, MaterialColor.COLOR_PURPLE).noCollission().strength(100.0F).noDrops()));
        this.bucket = ModItems.ITEMS.register(name + "_bucket", () -> new BucketItem(this::getSource, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(ModItems.Tabs.MAIN)));
        this.id = ThermoCraft.modLoc(name);
    }

    public static FluidRegistryObject of(String name, UnaryOperator<FluidAttributes.Builder> attributes, UnaryOperator<ForgeFlowingFluid.Properties> propertiesOperator) {
        return new FluidRegistryObject(name, attributes, propertiesOperator);
    }

    public ForgeFlowingFluid.Source getSource() {
        return source.get();
    }

    public ForgeFlowingFluid.Flowing getFlowing() {
        return flowing.get();
    }

    public LiquidBlock getBlock() {
        return block.get();
    }

    public BucketItem getBucket() {
        return bucket.get();
    }

    public ResourceLocation getId() {
        return id;
    }

    public ResourceLocation getFlowingId() {
        return ThermoCraft.modLoc("flowing_" + id.getPath());
    }
}
