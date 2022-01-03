package mrghastien.thermocraft.common.capabilities.tartanicflux;

import mrghastien.thermocraft.common.network.data.DataType;
import mrghastien.thermocraft.util.math.FixedPointNumber;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

public interface ITartanicFluxHandler {

    FixedPointNumber getFlux();

    /**
     * @return The maximum amount of flux stored by this handler.
     */
    FixedPointNumber getCapacity();

    /**
     * When the flux amount is above this capacity, the flux will start to leak in the environment.
     * The input rate is also significantly reduced.
     * If the current handler cannot leak, the value can be ignored.
     * @apiNote This capacity is not necessarily the same as the standard capacity.
     * @return The leaking capacity
     * @see #getCapacity()
     */
    FixedPointNumber getLeakageCapacity();

    default void transferFlux(long flux) {
        transferFlux(FixedPointNumber.valueOf(flux));
    }

    void transferFlux(FixedPointNumber flux);

    boolean canReceive();

    boolean canExtract();

    boolean canLeak();
}
