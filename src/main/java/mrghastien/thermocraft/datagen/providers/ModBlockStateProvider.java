package mrghastien.thermocraft.datagen.providers;

import mrghastien.thermocraft.common.ThermoCraft;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.function.Function;

import static mrghastien.thermocraft.common.registries.ModBlocks.*;

public class ModBlockStateProvider extends BlockStateProvider {

    private final ExistingFileHelper existingFileHelper;

    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ThermoCraft.MODID, exFileHelper);
        existingFileHelper = exFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlock(SOLID_HEATER.getBlock(), state -> {
            boolean lit = state.getValue(BlockStateProperties.LIT);
            return models().cube(SOLID_HEATER.getId().getPath() + (lit ? "_lit" : ""),
                    mcLoc("block/furnace_top"),
                    modLoc("block/solid_heater_top"),
                    modLoc("block/solid_heater_front" + (lit ? "_lit" : "")),
                    modLoc("block/solid_heater_side"),
                    modLoc("block/solid_heater_side"),
                    modLoc("block/solid_heater_side"));
        },180);
        itemModels().withExistingParent(SOLID_HEATER.getId().getPath(), modLoc("block/" + SOLID_HEATER.getId().getPath()));

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

        basicBlock(CALORITE_ORE.getBlock());
        //Do not generate a model for the boiler, as it is made manually
        simpleBlock(BOILER.getBlock(), models().getExistingFile(modLoc("block/" + BOILER.getId().getPath())));
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

    private void basicBlock(Block block) {
        ModelFile modelFile = cubeAll(block);
        simpleBlock(block, modelFile);
        simpleBlockItem(block, modelFile);
    }

    @Nonnull
    @Override
    public String getName() {
        return ThermoCraft.MODID + " BlockStates";
    }
}
