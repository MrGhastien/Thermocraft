package mrghastien.thermocraft.common.registries;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.blocks.machines.tartanicholder.TartanicHolderBlock;
import mrghastien.thermocraft.common.blocks.machines.boiler.BoilerBlock;
import mrghastien.thermocraft.common.blocks.machines.fluidinjector.FluidInjectorBlock;
import mrghastien.thermocraft.common.blocks.machines.solidheater.SolidHeaterBlock;
import mrghastien.thermocraft.common.blocks.machines.thermalcapacitor.ThermalCapacitorBlock;
import mrghastien.thermocraft.common.blocks.transmitters.conductor.HeatConductorBlock;
import mrghastien.thermocraft.common.blocks.transmitters.convector.HeatConvectorBlock;
import mrghastien.thermocraft.common.blocks.transmitters.convector.HeatConvectorPumpBlock;
import mrghastien.thermocraft.util.BlockRegistryObject;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ThermoCraft.MODID);

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> supplier) {
        return BLOCKS.register(name, supplier);
    }

    public static final BlockBehaviour.Properties ORE_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE).strength(3f);

    //Standard blocks
    public static final BlockRegistryObject<Block> CALORITE_ORE = BlockRegistryObject.register("calorite_ore", () -> new Block(ORE_PROPERTIES));
    public static final BlockRegistryObject<Block> TARTANE_ORE = BlockRegistryObject.register("tartane_ore", () -> new Block(ORE_PROPERTIES));

    //Machines
    public static final BlockRegistryObject<SolidHeaterBlock> SOLID_HEATER = BlockRegistryObject.register("solid_heater", SolidHeaterBlock::new);
    public static final BlockRegistryObject<BoilerBlock> BOILER = BlockRegistryObject.register("boiler", BoilerBlock::new);
    public static final BlockRegistryObject<ThermalCapacitorBlock> THERMAL_CAPACITOR = BlockRegistryObject.register("thermal_capacitor", ThermalCapacitorBlock::new);
    public static final BlockRegistryObject<FluidInjectorBlock> FLUID_INJECTOR = BlockRegistryObject.register("fluid_injector", FluidInjectorBlock::new);
    public static final BlockRegistryObject<TartanicHolderBlock> TARTANIC_HOLDER = BlockRegistryObject.register("tartanic_holder", TartanicHolderBlock::new);

    //Cables
    public static final BlockRegistryObject<HeatConductorBlock> HEAT_CONDUCTOR_BLOCK = BlockRegistryObject.register("heat_conductor", HeatConductorBlock::new);
    public static final RegistryObject<HeatConvectorBlock> HEAT_CONVECTOR_BLOCK = register("heat_convector", HeatConvectorBlock::new);

    public static final BlockRegistryObject<HeatConvectorPumpBlock> HEAT_CONVECTOR_PUMP = BlockRegistryObject.register("heat_convector_pump", HeatConvectorPumpBlock::new);

    //Conductor : Basic heat transmitter, (maybe) loses energy depending on the size of the network. Transfer rate is fixed. --> Useful for transfer across small distances (between machines).
    //Convector : Advanced heat transmitter, has no energy loss, requires a fluid to transfer energy. Transfer rate depends on the fluid.
    //Needs a controller / a pump in order to work properly. --> Useful for large distances (link: power plant -> base / machines)
    //The controller is used to insert fluid into the network, and can display information about it.
}
