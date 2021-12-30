package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.common.network.INetworkBinding;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A data holder which cannot be modified.
 * <p>
 * The {@link IDataHolder#addData(DataReference) adding of data references} is not supported.
 *
 * @see DataReference
 */
public class ReadOnlyDataHolder implements IDataHolder {

    private final IDataHolder holder;

    /**
     * Creates a {@link ReadOnlyDataHolder} from the provided {@link IDataHolder}.
     * @param holder The encapsulated data holder.
     */
    public ReadOnlyDataHolder(IDataHolder holder) {
        if(holder instanceof ReadOnlyDataHolder) throw new IllegalArgumentException("Tried to create a ReadOnlyDataHolder based on a ReadOnlyDataHolder");
        this.holder = holder;
    }

    @Override
    public INetworkBinding getBinding() {
        return holder.getBinding();
    }

    @Override
    public DataHolderCategory getCategory() {
        return holder.getCategory();
    }

    @Override
    public DataReference<?> getData(ResourceLocation id) {
        return holder.getData(id);
    }

    @Override
    public void forEach(BiConsumer<ResourceLocation, DataReference<?>> action) {
        holder.forEach(action);
    }

    @Override
    public <T> void addData(DataReference<T> data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void addData(DataType<T> type, ResourceLocation key, Supplier<T> getter, Consumer<T> setter) {
        throw new UnsupportedOperationException();
    }
}
