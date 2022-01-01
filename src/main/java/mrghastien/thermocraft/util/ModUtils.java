package mrghastien.thermocraft.util;

import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Cable;
import mrghastien.thermocraft.common.tileentities.cables.HeatTransmitterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ModUtils {

    public static Cable getCable(BlockPos pos, Level world) {
        BlockEntity te = world.getBlockEntity(pos);
        if(te instanceof HeatTransmitterTile) {
            return ((HeatTransmitterTile<?>)te).getCable();
        }
        return null;
    }

}
