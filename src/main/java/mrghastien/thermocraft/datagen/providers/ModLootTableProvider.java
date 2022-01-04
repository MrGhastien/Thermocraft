package mrghastien.thermocraft.datagen.providers;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import mrghastien.thermocraft.common.ThermoCraft;
import mrghastien.thermocraft.datagen.providers.loottables.BlockLootTables;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ModLootTableProvider extends LootTableProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator generator;

    public ModLootTableProvider(DataGenerator pGenerator) {
        super(pGenerator);
        this.generator = pGenerator;
    }

    @Override
    public void run(@Nonnull HashCache cache) {
        Map<ResourceLocation, LootTable> tables = new HashMap<>();
        for (Pair<Supplier<SubProvider>, LootContextParamSet> pair : this.getLootTables()) {
            SubProvider provider = pair.getFirst().get();
            LootContextParamSet paramSet = pair.getSecond();
            provider.accept((id, lootTable) -> {
                ResourceLocation newId = new ResourceLocation(id.getNamespace(), provider.getSubFolder() + "/" + id.getPath());
                if (tables.put(newId, lootTable.setParamSet(paramSet).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + id);
                }
            });
        }
        Path outputFolder = this.generator.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                DataProvider.save(GSON, cache, LootTables.serialize(lootTable), path);
            } catch (IOException e) {
                LOGGER.error("Couldn't write loot table {}", path, e);
            }
        });
    }

    @Nonnull
    private List<Pair<Supplier<SubProvider>, LootContextParamSet>> getLootTables() {
        List<Pair<Supplier<SubProvider>, LootContextParamSet>> providers = new ArrayList<>();
        providers.add(Pair.of(BlockLootTables::new, LootContextParamSets.BLOCK));
        return providers;
    }

    @Nonnull
    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    public String getName() {
        return ThermoCraft.MODID + "Loot tables";
    }

    public static abstract class SubProvider implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {

        private final Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

        protected abstract void addTables();

        @Override
        public void accept(@Nonnull BiConsumer<ResourceLocation, LootTable.Builder> buildFunction) {
            addTables();
            tables.forEach(buildFunction);
        }

        protected void add(ResourceLocation id, LootTable.Builder table) {
            this.tables.put(id, table);
        }

        protected abstract String getSubFolder();

    }

}
