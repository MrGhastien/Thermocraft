package mrghastien.thermocraft.common.registries;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.blocks.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ThermoCraft.MODID);

    private static <T extends Block> RegistryObject<T> registerWithItem(String name, Supplier<T> supplier) {
        RegistryObject<T> ro = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, basicItem(ro));
        return ro;
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> supplier) {
        return BLOCKS.register(name, supplier);
    }

    private static Supplier<BlockItem> basicItem(final RegistryObject<? extends Block> block) {
        return () -> new BlockItem(block.get(), new Item.Properties().tab(ModItems.Tabs.MAIN));
    }

    //Standard blocks
    public static final RegistryObject<CaloriteOreBlock> CALORITE_ORE = registerWithItem("calorite_ore", CaloriteOreBlock::new);

    //Machines
    public static final RegistryObject<SolidHeaterBlock> SOLID_HEATER = registerWithItem("solid_heater", SolidHeaterBlock::new);
    public static final RegistryObject<BoilerBlock> BOILER = registerWithItem("boiler", BoilerBlock::new);
    public static final RegistryObject<ThermalCapacitorBlock> THERMAL_CAPACITOR = registerWithItem("thermal_capacitor", ThermalCapacitorBlock::new);
    public static final RegistryObject<FluidInjectorBlock> FLUID_INJECTOR = registerWithItem("fluid_injector", FluidInjectorBlock::new);

    //Cables
    public static final RegistryObject<HeatConductorBlock> HEAT_CONDUCTOR_BLOCK = registerWithItem("heat_conductor", HeatConductorBlock::new);
    public static final RegistryObject<HeatConvectorBlock> HEAT_CONVECTOR_BLOCK = register("heat_convector", HeatConvectorBlock::new);

    public static final RegistryObject<HeatConvectorPumpBlock> HEAT_CONVECTOR_PUMP = registerWithItem("heat_convector_pump", HeatConvectorPumpBlock::new);

    //Conductor : Basic heat transmitter, (maybe) loses energy depending on the size of the network. Transfer rate is fixed. --> Useful for transfer across small distances (between machines).
    //Convector : Advanced heat transmitter, has no energy loss, requires a fluid to transfer energy. Transfer rate depends on the fluid.
    //Needs a controller / a pump in order to work properly. --> Useful for large distances (link: power plant -> base / machines)
    //The controller is used to insert fluid into the network, and can display information about it.
}
