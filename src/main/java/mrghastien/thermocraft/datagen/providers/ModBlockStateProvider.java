package mrghastien.thermocraft.datagen.providers;

import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.common.registries.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ThermoCraft.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlock(ModBlocks.SOLID_HEATER.get(), state -> {
            boolean lit = state.getValue(BlockStateProperties.LIT);
            return models().cube(ModBlocks.SOLID_HEATER.getId().getPath() + (lit ? "_lit" : ""), mcLoc("block/furnace_top"),
                    modLoc("block/solid_heater_top"),
                    modLoc("block/solid_heater_front" + (lit ? "_lit" : "")),
                    modLoc("block/solid_heater_side"),
                    modLoc("block/solid_heater_side"),
                    modLoc("block/solid_heater_side"));
        },180);
        itemModels().withExistingParent("solid_heater", modLoc("block/" + ModBlocks.SOLID_HEATER.getId().getPath()));

//        horizontalBlock(ModBlocks.BOILER.get(), state -> {
//            boolean lit = state.getValue(BlockStateProperties.LIT);
//            return models().cube(ModBlocks.BOILER.getId().getPath() + (lit ? "_lit" : ""), mcLoc("block/furnace_top"),
//                    modLoc("block/boiler_top"),
//                    modLoc("block/boiler_front" + (lit ? "_lit" : "")),
//                    modLoc("block/boiler_side"),
//                    modLoc("block/boiler_side"),
//                    modLoc("block/boiler_side"));
//        },180);
//        itemModels().withExistingParent("boiler", ModBlocks.BOILER.getId());
    }

    @Override
    public void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        getVariantBuilder(block)
                .forAllStates(state -> {
                    Direction dir = state.getValue(BlockStateProperties.FACING);
                    return ConfiguredModel.builder()
                            .modelFile(modelFunc.apply(state))
                            .rotationX(dir == Direction.DOWN ? 90 : dir.getAxis().isHorizontal() ? 0 : -90)
                            .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + angleOffset) % 360)
                            .build();
                });
    }
}
