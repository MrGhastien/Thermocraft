package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.network.INetworkBinding;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Associates data references to an id.
 * <p> Provides a network binding to allow sending values over the network, as well as a way
 * to track any changes made to data held by this holder.
 * @see DataReference
 */
public interface IDataHolder {

    /**
     * Gives a binding allowing for encoding and decoding of all the data held by this holder
     * @return The holder's binding
     */
    INetworkBinding getBinding();

    /**
     * The type of holder is a way to register data only for a specific synchronization context.
     *
     * @return The type of this holder.
     */
    DataHolderCategory getCategory();

    /** Gets the data interface with the provided identifier
     *
     * @param id The actual identifier
     * @return The actual data, or null if none was found.
     */
    DataReference<?> getData(ResourceLocation id);

    /**Applies the provided function to each data interface
     *
     * @param action The actual function
     */
    void forEach(BiConsumer<ResourceLocation, DataReference<?>> action);

    /**
     * Registers an existing {@link DataReference data reference} into this holder.
     * @param data The actual data
     * @param <T> The type of data
     *
     * @throws UnsupportedOperationException if this operation is not supported by the implementation.
     */
    <T> void addData(DataReference<T> data) throws IllegalArgumentException;

    /**
     * Checks if this holder contains at least one changed data reference.
     * @implSpec This method should update changed references.
     * @return True if any data reference noticed a value change, false otherwise.
     */
    boolean hasChanged();

    /**
     * The result set is containing all data references for which {@link DataReference#hasChanged()} returns true.
     * @implSpec This method should NOT update the references. <p>The return set should act as a read-only set.
     * @return A set containing all data references that notice a value change.
     */
    Set<DataReference<?>> getChangedReferences();

    /**
     * Creates a {@link DataReference data reference} and registers it into this holder.
     * @param type The type of the data to be synchronized
     * @param key The identifier of the data
     * @param getter Function returning the value of the underlying variable
     * @param setter Function changing the variable
     * @param <T> The type of data
     *
     * @throws UnsupportedOperationException if this operation is not supported by the implementation.
     */
    default <T> void addData(DataType<T> type, ResourceLocation key, Supplier<T> getter, Consumer<T> setter) {
        addData(new DataReference<>(type, key, getter, setter));
    }

    default <T> void addData(DataType<T> type, String key, Supplier<T> getter, Consumer<T> setter) {
        addData(type, ThermoCraft.modLoc(key), getter, setter);
    }

    enum DataHolderCategory {
        CONTAINER,
        TILE_ENTITY,
        HEAT_NETWORK,
        OTHER
    }
}
