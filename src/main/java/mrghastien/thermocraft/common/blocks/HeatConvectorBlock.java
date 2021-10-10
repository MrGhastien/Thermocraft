package mrghastien.thermocraft.common.blocks;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.tileentities.cables.HeatConvectorTile;
import mrghastien.thermocraft.common.tileentities.cables.HeatTransmitterTile;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class HeatConvectorBlock extends HeatTransmitterBlock {

    public HeatConvectorBlock() {
        super(Properties.of(Material.METAL).noOcclusion().strength(5f));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if(!world.isClientSide()) return ActionResultType.PASS;
        if(player.getItemInHand(hand).getItem() == Items.BOWL) {
            TileEntity te = world.getBlockEntity(pos);
            if(te instanceof HeatTransmitterTile<?>)
                player.sendMessage(new StringTextComponent("Cable connections : " + ((HeatTransmitterTile<?>)te).getCable().getCableConnections()), player.getUUID());
        }
        return ActionResultType.PASS;
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        ThermoCraft.LOGGER.debug("SetPlacedBy");
        if(itemStack.getItem() == asItem()) {
            CompoundNBT nbt = itemStack.getTag();
            Fluid fluid;
            if(nbt != null && nbt.contains("fluid")) {
                ResourceLocation fluidLocation = new ResourceLocation(nbt.getString("fluid"));
                fluid = ForgeRegistries.FLUIDS.getValue(fluidLocation);
            } else fluid = Fluids.EMPTY;

            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof HeatConvectorTile) {
                ((HeatConvectorTile) te).setFluid(fluid);
            }
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        ThermoCraft.LOGGER.debug("created TE");
        return new HeatConvectorTile();
    }

    @Override
    public HeatNetworkHandler.HeatNetworkType getNetworkType() {
        return HeatNetworkHandler.HeatNetworkType.CONVECTOR;
    }
}
