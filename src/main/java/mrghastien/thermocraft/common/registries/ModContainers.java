package mrghastien.thermocraft.common.registries;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.inventory.containers.*;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {

    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, ThermoCraft.MODID);

    public static <T extends Container> RegistryObject<ContainerType<T>> register(String name, IContainerFactory<T> factory) {
        return CONTAINERS.register(name, () -> IForgeContainerType.create(factory));
    }

    public static final RegistryObject<ContainerType<SolidHeaterContainer>> SOLID_HEATER = register("solid_heater", SolidHeaterContainer::createClient);
    public static final RegistryObject<ContainerType<BoilerContainer>> BOILER = register("boiler", BoilerContainer::createClient);
    public static final RegistryObject<ContainerType<ThermalCapacitorContainer>> THERMAL_CAPACITOR = register("thermal_capacitor", ThermalCapacitorContainer::createClient);
    public static final RegistryObject<ContainerType<FluidInjectorContainer>> FLUID_INJECTOR = register("fluid_injector", FluidInjectorContainer::createClient);

    public static final RegistryObject<ContainerType<ConvectorControllerContainer>> HEAT_CONVECTOR_PUMP = register("heat_convector_pump", ConvectorControllerContainer::createClient);
}
