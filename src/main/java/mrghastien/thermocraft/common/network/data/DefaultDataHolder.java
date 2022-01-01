package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.common.network.CompositeDataNetworkBinding;
import mrghastien.thermocraft.common.network.INetworkBinding;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class DefaultDataHolder implements IDataHolder {

    protected final Map<ResourceLocation, DataReference<?>> syncedData = new HashMap<>();

    private final INetworkBinding binding;

    protected DefaultDataHolder() {
        this.binding = new CompositeDataNetworkBinding(this);
    }

    protected DefaultDataHolder(INetworkBinding binding) {
        this.binding = binding;
    }

    public static DefaultDataHolder container(AbstractContainerMenu container) {
        return new ContainerDataHolder(container);
    }

    @Override
    public INetworkBinding getBinding() {
        return binding;
    }

    @Override
    public DataReference<?> getData(ResourceLocation id) {
        return syncedData.get(id);
    }

    @Override
    public void forEach(BiConsumer<ResourceLocation, DataReference<?>> action) {
        syncedData.forEach(action);
    }

}
