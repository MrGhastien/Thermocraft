package mrghastien.thermocraft.common.network.data;

import net.minecraft.world.inventory.AbstractContainerMenu;

public class ContainerDataHolder extends DefaultDataHolder {

    private final AbstractContainerMenu container;

    public ContainerDataHolder(AbstractContainerMenu container) {
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
