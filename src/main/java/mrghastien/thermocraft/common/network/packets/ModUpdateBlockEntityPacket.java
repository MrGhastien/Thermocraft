package mrghastien.thermocraft.common.network.packets;

import io.netty.buffer.Unpooled;
import mrghastien.thermocraft.common.blocks.MachineBlockEntity;
import mrghastien.thermocraft.common.network.INetworkBinding;
import mrghastien.thermocraft.common.network.data.IDataHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class ModUpdateBlockEntityPacket {

    private final BlockPos pos;
    private final FriendlyByteBuf buf;

    public ModUpdateBlockEntityPacket(BlockPos pos, INetworkBinding binding) {
        this.pos = pos;
        this.buf = new FriendlyByteBuf(Unpooled.buffer());
        binding.encode(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeBytes(this.buf);
    }

    public ModUpdateBlockEntityPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.buf = new FriendlyByteBuf(buf.copy());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;
            if(level != null && level.isLoaded(pos)) {
                BlockEntity be = level.getBlockEntity(pos);
                if(be instanceof MachineBlockEntity cast) {
                    IDataHolder dataHolder = cast.getDataHolder();
                    if(dataHolder != null)
                        dataHolder.getBinding().decode(buf);
                }
            }

        });
        ctx.get().setPacketHandled(true);
    }

}
