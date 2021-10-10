package mrghastien.thermocraft.common.capabilities.heat.transport.networks;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Cable;
import mrghastien.thermocraft.common.capabilities.heat.transport.cables.ConvectorCable;
import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Pump;
import mrghastien.thermocraft.common.network.NetworkDataType;
import mrghastien.thermocraft.common.network.NetworkHandler;
import mrghastien.thermocraft.util.MathUtils;
import mrghastien.thermocraft.util.Pair;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class HeatConvectorNetwork extends HeatNetwork {

    Pair<BlockPos, Pump> firstPump = null;
    Fluid fluid = Fluids.EMPTY;

    //Caches
    final Map<BlockPos, Pump> controllerCache;
    final Map<BlockPos, Cable> validCache;

    HeatConvectorNetwork(long id, World world) {
        super(id, world);
        this.validCache = new HashMap<>();
        controllerCache = new HashMap<>();
        NetworkHandler.getInstance(world).add(NetworkDataType.STRING, PacketDistributor.DIMENSION.with(world::dimension), this,
                fluid.getRegistryName()::toString,
                v -> setFluid(ForgeRegistries.FLUIDS.getValue(new ResourceLocation((String) v))));
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
        FluidAttributes attributes = fluid.getFluid().getAttributes();
        double coefficient = MathUtils.map(attributes.getDensity(), 500, 5000, 0.25, 1.5)
                * MathUtils.map(attributes.getViscosity(), 500, 5000, 1.2, 0.4);
        return super.getConductionCoefficient() * coefficient;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putString("fluid", fluid.getRegistryName().toString());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(nbt.getString("fluid")));
        if(f == null) ThermoCraft.LOGGER.warn("Couldn't deserialize heat network from nbt : no fluid specified");
        else this.fluid = f;
    }

}
