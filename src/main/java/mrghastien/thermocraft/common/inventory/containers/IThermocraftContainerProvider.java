package mrghastien.thermocraft.common.inventory.containers;

import net.minecraft.inventory.container.INamedContainerProvider;

public interface IThermocraftContainerProvider extends INamedContainerProvider {

    default void registerContainerUpdatedData(BaseContainer c) {}
}
