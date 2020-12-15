package tk.valoeghese.climatic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;
import tk.valoeghese.climatic.api.Climate;
import tk.valoeghese.climatic.api.ClimateBiomes;
//import tk.valoeghese.climatic.compat.TerrestriaCompat;
//import tk.valoeghese.climatic.compat.TraverseCompat;
import tk.valoeghese.climatic.config.ClimaticConfig;
import tk.valoeghese.climatic.impl.ClimaticWorldTypeHolder;
import tk.valoeghese.climatic.impl.type.ClimaticChunkGenerator;
import tk.valoeghese.climatic.impl.type.ClimaticChunkGeneratorType;

public class ClimaticWorldType implements ModInitializer {
	
	public static ChunkGeneratorType<OverworldChunkGeneratorConfig, ClimaticChunkGenerator> CHUNKGEN_TYPE = new ClimaticChunkGeneratorType().getChunkGeneratorType(OverworldChunkGeneratorConfig::new);

	public static ClimaticConfig config = null; // if it cannot load the config we will get an NPE

	public static final Logger log = LogManager.getLogger("ClimaticWorldType");
	
	// load and initialise me on client pls
	static LevelGeneratorType loadMeOnClientPls;
	
	@Override
	public void onInitialize() {
		initConfig();
		
		loadMeOnClientPls = ClimaticWorldTypeHolder.WORLDTYPE;

		Registry.register(Registry.CHUNK_GENERATOR_TYPE, from("climatic"), CHUNKGEN_TYPE);

		boolean traverseLoaded = FabricLoader.getInstance().isModLoaded("traverse");
		boolean terrestriaLoaded = FabricLoader.getInstance().isModLoaded("terrestria");

		addDefaultBiomes(traverseLoaded, terrestriaLoaded);

//		if (traverseLoaded) {
//			TraverseCompat.load();
//		}
//		if (terrestriaLoaded) {
//			TerrestriaCompat.load();
//		}
	}

	private void initConfig() {
		Gson gson = new Gson();
		
		// first make sure config folder exists
		new File("./config/").mkdir();
		
		File loc = new File("./config/climaticworldtype.json");
		try {
			if (loc.createNewFile()) {
				log.info("Creating config for climatic world type");

				try (FileWriter writer = new FileWriter(loc)) {
					writer.write("{\n" + 
							"  \"humidity_zone_size\": 11.0,\n" + 
							"  \"temperature_zone_size\": 10.0,\n" + 
							"  \n" + 
							"  \"biome_size\": 4,\n" + 
							"  \"river_size\": 5,\n" + 
							"  \"large_shores\": true\n" + 
							"}");
				} catch (FileNotFoundException e) {
					throw new RuntimeException("Unhandled FileNotFoundException in generating config!");
				}
			}
			
			log.info("Loading climatic world type config file");
			try (FileReader reader = new FileReader(loc)) {
				config = gson.fromJson(reader, ClimaticConfig.class);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Unhandled FileNotFoundException in reading config!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unhandled IOException in config handling!");
		}
	}

	// 10 average weight; 5 average "uncommon" weight
	private void addDefaultBiomes(boolean traverseLoaded, boolean terrestriaLoaded) {
		// Boreal
		ClimateBiomes.addBiome(Climate.BOREAL, BiomeKeys.TAIGA, 7); // 7 + 2 + 1 = 10
		ClimateBiomes.addBiome(Climate.BOREAL, BiomeKeys.GIANT_TREE_TAIGA, 2);
		ClimateBiomes.addBiome(Climate.BOREAL, BiomeKeys.GIANT_SPRUCE_TAIGA, 1);

		// Cool Temperate
		ClimateBiomes.addBiome(Climate.COOL_TEMPERATE, BiomeKeys.BIRCH_FOREST, 7); // 7 + 3 = 10
		ClimateBiomes.addBiome(Climate.COOL_TEMPERATE, BiomeKeys.TALL_BIRCH_FOREST, 3);

		// Humid Subtropical
		ClimateBiomes.addBiome(Climate.HUMID_SUBTROPICAL, BiomeKeys.DARK_FOREST, 10);
		ClimateBiomes.addBiome(Climate.HUMID_SUBTROPICAL, BiomeKeys.SWAMP, 10);

		// Ice Cap
		ClimateBiomes.addBiome(Climate.ICE_CAP, BiomeKeys.SNOWY_TUNDRA, 7); // 7 + 3 = 10
		ClimateBiomes.addBiome(Climate.SNOWY, BiomeKeys.ICE_SPIKES, 3);

		// Maritime
		ClimateBiomes.addBiome(Climate.MARITIME, BiomeKeys.PLAINS, 10);
		ClimateBiomes.addBiome(Climate.MARITIME, BiomeKeys.SWAMP, 10);
		ClimateBiomes.addBiome(Climate.MARITIME, BiomeKeys.FOREST, 7); // 7 + 3 = 10
		ClimateBiomes.addBiome(Climate.MARITIME, BiomeKeys.FLOWER_FOREST, 3);

		// Mediterranean
		ClimateBiomes.addBiome(Climate.MEDITERRANEAN, BiomeKeys.FOREST, 10);
		ClimateBiomes.addBiome(Climate.MEDITERRANEAN, BiomeKeys.MOUNTAINS, 6); // 6 + 3 + 1 = 10
		ClimateBiomes.addBiome(Climate.MEDITERRANEAN, BiomeKeys.GRAVELLY_MOUNTAINS, 3);
		ClimateBiomes.addBiome(Climate.MEDITERRANEAN, BiomeKeys.MODIFIED_GRAVELLY_MOUNTAINS, 1);

		// Snowy
		ClimateBiomes.addBiome(Climate.SNOWY, BiomeKeys.SNOWY_TUNDRA, 10);
		ClimateBiomes.addBiome(Climate.SNOWY, BiomeKeys.SNOWY_TAIGA, 7); // 7 + 3 = 10
		ClimateBiomes.addBiome(Climate.SNOWY, BiomeKeys.SNOWY_TAIGA_MOUNTAINS, 3);

		// Temperate Desert
		ClimateBiomes.addBiome(Climate.TEMPERATE_DESERT, BiomeKeys.DESERT_LAKES, 10);
		ClimateBiomes.addBiome(Climate.TEMPERATE_DESERT, BiomeKeys.BADLANDS_PLATEAU, 3);
		ClimateBiomes.addBiome(Climate.TEMPERATE_DESERT, BiomeKeys.BADLANDS, 2); // 3 + 2 = 5

		// Tropical Desert
		ClimateBiomes.addBiome(Climate.TROPICAL_DESERT, BiomeKeys.DESERT, 7); // 7 + 3 = 10
		ClimateBiomes.addBiome(Climate.TROPICAL_DESERT, BiomeKeys.DESERT_LAKES, 3);

		// Tropical Rainforest
		ClimateBiomes.addBiome(Climate.TROPICAL_RAINFOREST, BiomeKeys.JUNGLE, 7); // 7 + 3 = 10
		ClimateBiomes.addBiome(Climate.TROPICAL_RAINFOREST, BiomeKeys.MODIFIED_JUNGLE, 3);
		ClimateBiomes.addBiome(Climate.TROPICAL_RAINFOREST, BiomeKeys.BAMBOO_JUNGLE, 10);

		// Tropical Savannah
		ClimateBiomes.addBiome(Climate.TROPICAL_SAVANNAH, BiomeKeys.SAVANNA, 7); // 7 + 3 = 10
		ClimateBiomes.addBiome(Climate.TROPICAL_SAVANNAH, BiomeKeys.SHATTERED_SAVANNA, 3);

		// Tropical Steppe
		ClimateBiomes.addBiome(Climate.TROPICAL_STEPPE, BiomeKeys.PLAINS, 10); // when terrestria releases steppe this will become a placeholder

		// Warm Temperate
		ClimateBiomes.addBiome(Climate.WARM_TEMPERATE, BiomeKeys.PLAINS, 10);
		ClimateBiomes.addBiome(Climate.WARM_TEMPERATE, BiomeKeys.FOREST, 10);
		ClimateBiomes.addBiome(Climate.WARM_TEMPERATE, BiomeKeys.DARK_FOREST, 5);

		// Placeholders for other mods "fulfiling" the requirement
		if (!traverseLoaded) {
			ClimateBiomes.addBiome(Climate.COOL_TEMPERATE, BiomeKeys.PLAINS, 5); // else meadow
		}

		// Neighbours
		ClimateBiomes.addNeighboursForBiome(BiomeKeys.TAIGA, BiomeKeys.TAIGA_MOUNTAINS, 5);
		ClimateBiomes.addNeighboursForBiome(BiomeKeys.SNOWY_TAIGA, BiomeKeys.SNOWY_TAIGA_MOUNTAINS, 5);
		ClimateBiomes.addNeighboursForBiome(BiomeKeys.ICE_SPIKES, BiomeKeys.SNOWY_TUNDRA, 5);

		ClimateBiomes.addNeighboursForBiome(BiomeKeys.BADLANDS, BiomeKeys.ERODED_BADLANDS, 5);
		ClimateBiomes.addNeighboursForBiome(BiomeKeys.BADLANDS_PLATEAU, BiomeKeys.MODIFIED_BADLANDS_PLATEAU, 3); // 3 + 2 = 5
		ClimateBiomes.addNeighboursForBiome(BiomeKeys.BADLANDS_PLATEAU, BiomeKeys.MODIFIED_WOODED_BADLANDS_PLATEAU, 2);

		ClimateBiomes.addNeighboursForBiome(BiomeKeys.BAMBOO_JUNGLE, BiomeKeys.JUNGLE, 5);

		ClimateBiomes.addNeighboursForBiome(BiomeKeys.DESERT, BiomeKeys.DESERT_LAKES, 3);
		ClimateBiomes.addNeighboursForBiome(BiomeKeys.DESERT, BiomeKeys.BADLANDS, 2);
	}

	public static final Identifier from(String id)
	{
		return new Identifier("cwt", id);
	}
}
