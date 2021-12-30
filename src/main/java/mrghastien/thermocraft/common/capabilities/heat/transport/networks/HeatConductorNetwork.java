package mrghastien.thermocraft.common.capabilities.heat.transport.networks;

import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Cable;
import mrghastien.thermocraft.util.MathUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

public class HeatConductorNetwork extends HeatNetwork {

    HeatConductorNetwork(long id, World world) {
        super(id, world);
    }

    @Override
    public boolean contains(BlockPos pos) {
        return cables.containsKey(pos);
    }

    @Override
    protected void pushEnergyOut() {
        for (Map.Entry<BlockPos, TransferPoint> e : nodes.entrySet()) {
            TransferPoint point = e.getValue();
            if(point.getGlobalType().canExtract())
                e.getValue().pushEnergyOut(MathUtils.clampedMap(distanceToNearestAcceptor(e.getKey()), 100d, 1600d, 1d, 0.1d));
        }
    }

    @Override
    public HeatNetworkHandler.HeatNetworkType type() {
        return HeatNetworkHandler.HeatNetworkType.CONDUCTOR;
    }

    @Override
    public int size() {
        return cables.size();
    }

    @Override
    boolean canMerge(HeatNetwork other) {
        return other.type() == type();
    }

    @Override
    protected void refresh() {
        refreshMap.forEach(this::refreshTransferPoint);
        refreshMap.clear();
    }

    @Override
    public void requestRefresh(BlockPos pos, Cable cable) {
        if(!world.isClientSide() && contains(pos)) {
            needsRefresh = true;
            refreshMap.put(pos, cable);
        }
    }

    private int distanceToNearestAcceptor(BlockPos pos) {
        int nearest = Integer.MAX_VALUE;
        for (Map.Entry<BlockPos, TransferPoint> entry : nodes.entrySet()) {
            BlockPos p = entry.getKey();
            TransferPoint n = entry.getValue();
            if (p != pos && n.getGlobalType().canReceive()) {
                int dist = (int) p.distSqr(pos);
                if (dist < nearest)
                    nearest = dist;
            }
        }
        return nearest;
    }
}
