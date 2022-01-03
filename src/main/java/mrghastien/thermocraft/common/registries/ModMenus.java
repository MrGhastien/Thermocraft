package mrghastien.thermocraft.common.registries;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.inventory.menus.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, ThermoCraft.MODID);

    public static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static final RegistryObject<MenuType<SolidHeaterMenu>> SOLID_HEATER = register("solid_heater", SolidHeaterMenu::createClient);
    public static final RegistryObject<MenuType<BoilerMenu>> BOILER = register("boiler", BoilerMenu::createClient);
    public static final RegistryObject<MenuType<ThermalCapacitorMenu>> THERMAL_CAPACITOR = register("thermal_capacitor", ThermalCapacitorMenu::createClient);
    public static final RegistryObject<MenuType<FluidInjectorMenu>> FLUID_INJECTOR = register("fluid_injector", FluidInjectorMenu::createClient);

    public static final RegistryObject<MenuType<ConvectorControllerMenu>> HEAT_CONVECTOR_PUMP = register("heat_convector_pump", ConvectorControllerMenu::createClient);
}
