package mrghastien.thermocraft.util;

import mrghastien.thermocraft.common.capabilities.heat.transport.cables.Cable;
import mrghastien.thermocraft.common.blocks.transmitters.HeatTransmitterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ModUtils {

    public static Cable getCable(BlockPos pos, Level world) {
        BlockEntity te = world.getBlockEntity(pos);
        if(te instanceof HeatTransmitterBlockEntity) {
            return ((HeatTransmitterBlockEntity<?>)te).getCable();
        }
        return null;
    }

}
