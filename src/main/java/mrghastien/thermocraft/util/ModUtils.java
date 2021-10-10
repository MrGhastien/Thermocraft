package mrghastien.thermocraft.util;

import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Cable;
import mrghastien.thermocraft.common.tileentities.cables.HeatTransmitterTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModUtils {

    public static Cable getCable(BlockPos pos, World world) {
        TileEntity te = world.getBlockEntity(pos);
        if(te instanceof HeatTransmitterTile) {
            return ((HeatTransmitterTile<?>)te).getCable();
        }
        return null;
    }

}
