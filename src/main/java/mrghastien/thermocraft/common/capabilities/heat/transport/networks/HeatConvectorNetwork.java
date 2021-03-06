package mrghastien.thermocraft.common.capabilities.heat.transport.networks;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Cable;
import mrghastien.thermocraft.common.capabilities.heat.transport.cables.ConvectorCable;
import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Pump;
import mrghastien.thermocraft.common.network.data.DataType;
import mrghastien.thermocraft.util.MathUtils;
import mrghastien.thermocraft.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class HeatConvectorNetwork extends HeatNetwork {

    Pair<BlockPos, Pump> firstPump = null;
    Fluid fluid = Fluids.EMPTY;

    //Caches
    final Map<BlockPos, Pump> controllerCache;
    final Map<BlockPos, Cable> validCache;

    HeatConvectorNetwork(long id, Level world) {
        super(id, world);
        validCache = new HashMap<>();
        controllerCache = new HashMap<>();

        dataHolder.addData(DataType.STRING, "fluid_" + id,
                () -> Objects.requireNonNull(fluid.getRegistryName()).toString(),
                v -> setFluid(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(v))));
    }

    public void setFluid(Fluid fluid) {
        this.fluid = fluid;
    }

    public Fluid getFluid() {
        return fluid;
    }

    @Override
    public void addPosition(Cable cable) {
        boolean canAdd = false;
        if(cable instanceof ConvectorCable) {
            Fluid fluid = ((ConvectorCable) cable).getFluid();
            if(this.fluid == Fluids.EMPTY && fluid != null) {
                setFluid(fluid);
                canAdd = true;
            } else if(Objects.equals(this.fluid, fluid)) canAdd = true;

        } else if(cable instanceof Pump) canAdd = true;
        if(canAdd && cable.getType() == HeatNetworkHandler.HeatNetworkType.CONVECTOR) super.addPosition(cable);
    }

    public void checkFluidFlow() {
        if(firstPump == null || fluid.isSame(Fluids.EMPTY)) {
            canWork = false;
            return;
        }
        for(Cable c : cables.values()) {
            if(c.getCableConnections().size() == 1) {
                canWork = false;
                return;
            }
        }
        canWork = true;
    }

    @Override
    public boolean canMerge(HeatNetwork adjNet) {
        return adjNet.type() == HeatNetworkHandler.HeatNetworkType.CONVECTOR && fluid == ((HeatConvectorNetwork) adjNet).fluid;
    }

    @Override
    public Set<BlockPos> getCablePositions() {
        return cables.keySet();
    }

    @Override
    public boolean contains(BlockPos pos) {
        return cables.containsKey(pos);
    }

    @Override
    public boolean isEmpty() {
        return cables.isEmpty();
    }

    @Override
    public int size() {
        return cables.size();
    }

    @Override
    public void requestRefresh(BlockPos pos, Cable cable) {
        if(contains(pos)) {
            needsRefresh = true;
            refreshMap.put(pos, cables.get(pos));
        }
    }

    @Override
    protected void refresh() {
        controllerCache.clear();
        for (Map.Entry<BlockPos, Cable> entry : refreshMap.entrySet()) {
            Cable c = entry.getValue();
            BlockPos p = entry.getKey();
            if(c.isPump()) {
                controllerCache.put(p, (Pump) c);
                if(firstPump == null) firstPump = new Pair<>(p, (Pump) c);
            } else refreshTransferPoint(p, c);
        }
        refreshMap.clear();
        checkFluidFlow();
    }

    @Override
    protected void pushEnergyOut() {
        for (TransferPoint node : nodes.values()) node.pushEnergyOut(1d);
    }

    @Override
    public HeatNetworkHandler.HeatNetworkType type() {
        return HeatNetworkHandler.HeatNetworkType.CONVECTOR;
    }

    @Override
    public double getConductionCoefficient() {
        if(fluid == Fluids.EMPTY) return 0.0;
        FluidAttributes attributes = fluid.getAttributes();
        double coefficient = MathUtils.map(attributes.getDensity(), 500, 5000, 0.25, 1.5)
                * MathUtils.map(attributes.getViscosity(), 500, 5000, 1.2, 0.4);
        return super.getConductionCoefficient() * coefficient;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putString("fluid", fluid.getRegistryName().toString());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(nbt.getString("fluid")));
        if(f == null) ThermoCraft.LOGGER.warn("Couldn't deserialize heat network from nbt : no fluid specified");
        else this.fluid = f;
    }

}
