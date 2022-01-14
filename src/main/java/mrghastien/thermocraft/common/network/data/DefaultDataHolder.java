package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.common.network.ChangedDataNetworkBinding;
import mrghastien.thermocraft.common.network.INetworkBinding;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;

public abstract class DefaultDataHolder implements IDataHolder {

    protected final Map<ResourceLocation, DataReference<?>> syncedData = new HashMap<>();

    private ChangedSet changedSet;

    private final INetworkBinding binding;

    protected DefaultDataHolder() {
        this.binding = new ChangedDataNetworkBinding(this);
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
        ChangedSet set = ((ChangedSet)getChangedReferences());
        set.refresh();
        return !set.isEmpty();
    }

    @Override
    public Set<DataReference<?>> getChangedReferences() {
        if(this.changedSet == null)
            this.changedSet = new ChangedSet();
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

    final class ChangedSet extends AbstractSet<DataReference<?>> {

        private final Set<DataReference<?>> refs;

        public ChangedSet() {
            refs = new HashSet<>();
        }

        @Nonnull
        @Override
        public Iterator<DataReference<?>> iterator() {
            return refs.iterator();
        }

        private void refresh() {
            refs.clear();
            for(DataReference<?> ref : syncedData.values())
                if (ref.hasChanged()) refs.add(ref);
        }

        @Override
        public int size() {
            return refs.size();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Cannot remove references from changed set");
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("Cannot remove references from changed set");
        }

        @Override
        public boolean contains(Object o) {
            return refs.contains(o);
        }
    }

}
