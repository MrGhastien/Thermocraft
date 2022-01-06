package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.common.network.CompositeDataNetworkBinding;
import mrghastien.thermocraft.common.network.INetworkBinding;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;

public abstract class DefaultDataHolder implements IDataHolder {

    protected final Map<ResourceLocation, DataReference<?>> syncedData = new HashMap<>();

    private Set<DataReference<?>> changedSet;

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
    public boolean hasChanged() {
        return !getChangedReferences().isEmpty();
    }

    @Override
    public Set<DataReference<?>> getChangedReferences() {
        if(this.changedSet == null)
            this.changedSet = new ChangedMap();
        return changedSet;
    }

    @Override
    public DataReference<?> getData(ResourceLocation id) {
        return syncedData.get(id);
    }

    @Override
    public void forEach(BiConsumer<ResourceLocation, DataReference<?>> action) {
        syncedData.forEach(action);
    }

    final class ChangedMap extends AbstractSet<DataReference<?>> {

        @Nonnull
        @Override
        public Iterator<DataReference<?>> iterator() {
            return new ChangedIterator();
        }

        @Override
        public int size() {
            int size = 0;
            for(DataReference<?> ref : syncedData.values())
                if (ref.hasChanged()) size++;
            return size;
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Cannot remove references from changed set");
        }

        @Override
        public boolean contains(Object o) {
            if(o instanceof DataReference<?> ref)
                return ref.hasChanged();
            return false;
        }
    }

    final class ChangedIterator implements Iterator<DataReference<?>> {

        Iterator<DataReference<?>> completeIterator = syncedData.values().iterator();

        DataReference<?> next;
        DataReference<?> current;

        @Override
        public boolean hasNext() {
            return next != null;
        }

        private DataReference<?> nextRef() {
            DataReference<?> ref = null;
            while(completeIterator.hasNext() && !(ref = completeIterator.next()).hasChanged());
            return ref;
        }

        @Override
        public DataReference<?> next() {
            current = next;
            next = nextRef();
            return current;
        }
    }

}
