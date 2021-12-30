package mrghastien.thermocraft.common.network.data;

import net.minecraft.inventory.container.Container;

public class ContainerDataHolder extends DefaultDataHolder {

    private final Container container;

    public ContainerDataHolder(Container container) {
        super();
        this.container = container;
    }

    @Override
    public DataHolderCategory getCategory() {
        return DataHolderCategory.CONTAINER;
    }

    @Override
    public <T> void addData(DataReference<T> data) throws IllegalArgumentException {
        if (data == null) throw new NullPointerException("Cannot register null data reference");
        if (syncedData.containsKey(data.getId()))
            throw new IllegalArgumentException("Duplicate data reference registered");
        if (data.getType().canCreateIntArray()) {
            container.addDataSlots(data.getType().toIntArray(data));
        } else {
            syncedData.put(data.getId(), data);
        }
        syncedData.put(data.getId(), data);
    }
}
