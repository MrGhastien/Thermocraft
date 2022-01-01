package mrghastien.thermocraft.common.network.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockEntityDataHolder extends DefaultDataHolder {

    private final BlockEntity te;

    public BlockEntityDataHolder(BlockEntity te) {
        super();
        this.te = te;
    }

    @Override
    public <T> void addData(DataReference<T> data) {
        if (syncedData.containsKey(data.getId()))
            throw new IllegalArgumentException("Duplicate data reference registered");
        syncedData.put(data.getId(), data);
    }

    /**
     * This implementation automatically calls mark the bound tile entity as changed when the setter is called.
     */
    @Override
    public <T> void addData(DataType<T> type, ResourceLocation key, Supplier<T> getter, Consumer<T> setter) {
        addData(new DataReference<>(type, key, getter, v -> {
            setter.accept(v);
            te.setChanged();
        }));
    }

    @Override
    public DataHolderCategory getCategory() {
        return DataHolderCategory.TILE_ENTITY;
    }
}
