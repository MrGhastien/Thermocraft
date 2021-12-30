package mrghastien.thermocraft.common.capabilities;

import mrghastien.thermocraft.api.heat.IHeatHandler;
import mrghastien.thermocraft.common.capabilities.heat.HeatHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class Capabilities {

    @CapabilityInject(IHeatHandler.class)
    public static Capability<IHeatHandler> HEAT_HANDLER_CAPABILITY = null;

    public static void registerAll() {
        CapabilityManager.INSTANCE.register(IHeatHandler.class, new NBTSerializableStorage<>(), () -> new HeatHandler(1000, 40, 10, () -> {}));
    }

    //Default implementation for INBTSerializable instances
    static class NBTSerializableStorage<T extends INBTSerializable<CompoundNBT>> implements Capability.IStorage<T> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
            if(nbt instanceof CompoundNBT) instance.deserializeNBT((CompoundNBT) nbt);
            else throw new IllegalArgumentException("Cannot read an NBT which is not a Compound NBT !");
        }
    }

}
