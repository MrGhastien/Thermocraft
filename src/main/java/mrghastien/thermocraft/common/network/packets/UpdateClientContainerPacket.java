package mrghastien.thermocraft.common.network.packets;

import io.netty.buffer.Unpooled;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.inventory.containers.BaseContainer;
import mrghastien.thermocraft.common.network.INetworkBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


/** Packet responsible for carrying data from the server to the client, and tell the client-side container
 * there is an update
 */
public class UpdateClientContainerPacket {

    private final PacketBuffer buf;

    public UpdateClientContainerPacket(INetworkBinding binding) {
        this.buf = new PacketBuffer(Unpooled.buffer());
        binding.encode(buf);
    }

    public void encode(PacketBuffer buf) {
        buf.writeBytes(this.buf);
    }

    public UpdateClientContainerPacket(PacketBuffer buf) {
        this.buf = new PacketBuffer(buf.copy());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            ClientPlayerEntity player = mc.player;
            if(player == null) {
                ThermoCraft.LOGGER.warn("Could not synchronize menu value, as the player no longer exists");
                return;
            }
            Container menu = mc.player.containerMenu;
            if(!(menu instanceof BaseContainer)) {
                ThermoCraft.LOGGER.debug("Could not synchronize menu value, as it is invalid");
                return;
            }
            BaseContainer baseContainer = (BaseContainer) menu;
            baseContainer.getDataHolder().getBinding().decode(buf);

        });
        ctx.get().setPacketHandled(true);
    }

}
