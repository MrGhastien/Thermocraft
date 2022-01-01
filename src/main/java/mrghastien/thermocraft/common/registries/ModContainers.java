package mrghastien.thermocraft.common.registries;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.inventory.containers.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeContainerType;;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.fmllegacy.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, ThermoCraft.MODID);

    public static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, IContainerFactory<T> factory) {
        return CONTAINERS.register(name, () -> IForgeContainerType.create(factory));
    }

    public static final RegistryObject<MenuType<SolidHeaterContainer>> SOLID_HEATER = register("solid_heater", SolidHeaterContainer::createClient);
    public static final RegistryObject<MenuType<BoilerContainer>> BOILER = register("boiler", BoilerContainer::createClient);
    public static final RegistryObject<MenuType<ThermalCapacitorContainer>> THERMAL_CAPACITOR = register("thermal_capacitor", ThermalCapacitorContainer::createClient);
    public static final RegistryObject<MenuType<FluidInjectorContainer>> FLUID_INJECTOR = register("fluid_injector", FluidInjectorContainer::createClient);

    public static final RegistryObject<MenuType<ConvectorControllerContainer>> HEAT_CONVECTOR_PUMP = register("heat_convector_pump", ConvectorControllerContainer::createClient);
}
