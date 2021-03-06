package tk.valoeghese.climatic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import net.fabricmc.fabric.impl.biome.InternalBiomeData;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import tk.valoeghese.climatic.api.Climate;
import tk.valoeghese.climatic.api.OceanClimate;

public final class ClimateBiomesImpl {
	private static Climate[] lookup = new Climate[14];
	
	private static final Map<Climate, List<Biome>> BIOMES = new HashMap<>();
	private static final Map<Biome, List<Biome>> NEIGHBOURS = new HashMap<>();
	private static final Map<OceanClimate, List<IslandEntry>> ISLAND_ENTRIES = new HashMap<>();
	private static final Map<Biome, List<EdgeCorrectionEntry>> EDGE_CORRECTIONS = new HashMap<>();
	private static final Set<Biome> EDGES_TO_CORRECT = new HashSet<>();
	
	private static Biome[] biomes = new Biome[] {};
	private static final List<Biome> biomesTracker = new ArrayList<>();
	
	private static int injectionCount;
	
	private ClimateBiomesImpl() {
	}
	
	static {
		lookup[0] = null;
	}

	public static void setInLookup(int value, Climate climate) {
		lookup[value] = climate;
	}

	public static Climate lookup(int climate) {
		return lookup[climate];
	}

	public static Biome get(LayerRandomnessSource rand, Climate climate) {
		List<Biome> list = BIOMES.get(climate);
		return list.get(rand.nextInt(list.size()));
	}

	public static void addBiomeEntry(Climate climate, Biome biome, int weight) {
		List<Biome> list = BIOMES.computeIfAbsent(climate, b -> new ArrayList<>());
		for (int i = 0; i < weight; ++i) {
			list.add(biome);
		}
		if (!biomesTracker.contains(biome)) {
			trackBiome(biome);
		}
	}

	public static void addNeighbours(Biome base, Biome neighbour, int weight) {
		List<Biome> list = NEIGHBOURS.computeIfAbsent(base, biome -> new ArrayList<>());
		for (int i = 0; i < weight; ++i) {
			list.add(neighbour);
		}
		if (!biomesTracker.contains(neighbour)) {
			trackBiome(neighbour);
		}
	}

	public static void addNeighboursTo(List<Biome> list, Biome biome) {
		if (NEIGHBOURS.containsKey(biome)) {
			NEIGHBOURS.get(biome).forEach(b -> list.add(b));
		}
	}
	
	public static void addIsland(OceanClimate climate, Biome sub, int chance) {
		ISLAND_ENTRIES.computeIfAbsent(climate, c -> new ArrayList<>()).add(new IslandEntry(sub, chance));
		if (!biomesTracker.contains(sub)) {
			trackBiome(sub);
		}
	}
	
	public static void addSmallEdgeCorrection(Biome smallEdge, Biome correction, Biome[] allowedSurroundings) {
		EDGE_CORRECTIONS.computeIfAbsent(smallEdge, b -> new ArrayList<>()).add(new EdgeCorrectionEntry(correction, allowedSurroundings));
		EDGES_TO_CORRECT.add(smallEdge);
		
		if (!biomesTracker.contains(correction)) {
			trackBiome(correction);
		}
	}
	
	/*
	public static final Biome[] biomes() {
		// half-stolen injection code from the biomes api
		List<Biome> injectedBiomes = InternalBiomeData.getOverworldInjectedBiomes();
		int injectedBiomesSize = injectedBiomes.size();
		
		if (injectionCount < injectedBiomesSize) {
			List<Biome> toAdd = injectedBiomes.subList(injectionCount, injectedBiomesSize - 1);
			
			toAdd.forEach(biome -> trackBiomeIfAbsent(biome));
			
			injectionCount += toAdd.size();
		}
		
		return biomes;
	}
	*/
	
	public static final int populateIsland(OceanClimate climate, LayerRandomnessSource rand, int defaultResult) {
		List<IslandEntry> entries = ISLAND_ENTRIES.getOrDefault(climate, new ArrayList<>());
		for (IslandEntry entry : entries) {
			if (rand.nextInt(entry.chance) == 0) {
				return BuiltinRegistries.BIOME.getRawId(entry.island);
			}
		}
		
		return defaultResult;
	}
	
	public static final boolean canCorrectEdges(Biome centre) {
		return EDGES_TO_CORRECT.contains(centre);
	}
	
	public static final int correctEdges(Biome centre, Biome border1, Biome border2, Biome border3, Biome border4, int defaultReturn) {
		List<EdgeCorrectionEntry> entries = EDGE_CORRECTIONS.get(centre);
		for (EdgeCorrectionEntry entry : entries) {
			if (entry.shouldGenerate(border1, border2, border3, border4)) {
				return BuiltinRegistries.BIOME.getRawId(entry.correction);
			}
		}
		
		return defaultReturn;
	}

	/**
	 * @return an int from 1 - 8 for humidity. 1 is dry, 8 is humid.
	 */
	public static int getHumidity(double noiseSample) {
		if (noiseSample < -0.6) {
			return 1;
		} else if (noiseSample < -0.4) {
			return 2;
		} else if (noiseSample < -0.2) {
			return 3;
		} else if (noiseSample < 0) {
			return 4;
		} else if (noiseSample < 0.2) {
			return 5;
		} else if (noiseSample < 0.4) {
			return 6;
		} else if (noiseSample < 0.6) {
			return 7;
		} else {
			return 8;
		}
	}
	
	/**
	 * @return an int from 0 - 7 for temperature. 0 is hot, 7 is cold.
	 */
	public static int getTemperature(double noiseSample) {
		if (noiseSample < -0.6) {
			return 0;
		} else if (noiseSample < -0.4) {
			return 1;
		} else if (noiseSample < -0.2) {
			return 2;
		} else if (noiseSample < 0) {
			return 3;
		} else if (noiseSample < 0.2) {
			return 4;
		} else if (noiseSample < 0.4) {
			return 5;
		} else if (noiseSample < 0.6) {
			return 6;
		} else {
			return 7;
		}
	}
	
	static {
		trackBiome(BiomeKeys.COLD_OCEAN);
		trackBiome(BiomeKeys.DEEP_COLD_OCEAN);
		trackBiome(BiomeKeys.DEEP_FROZEN_OCEAN);
		trackBiome(BiomeKeys.DEEP_LUKEWARM_OCEAN);
		trackBiome(BiomeKeys.DEEP_OCEAN);
		trackBiome(BiomeKeys.DEEP_WARM_OCEAN);
		trackBiome(BiomeKeys.FROZEN_OCEAN);
		trackBiome(BiomeKeys.LUKEWARM_OCEAN);
		trackBiome(BiomeKeys.OCEAN);
		trackBiome(BiomeKeys.WARM_OCEAN);
		
		trackBiome(BiomeKeys.BEACH);
		trackBiome(BiomeKeys.SNOWY_BEACH);
		trackBiome(BiomeKeys.STONE_SHORE);
		trackBiome(BiomeKeys.MUSHROOM_FIELD_SHORE);
	}
	
	public static void trackBiomeIfAbsent(Biome biome) {
		if (biomesTracker.contains(biome)) {
			trackBiome(biome);
		}
	}
	
	private static void trackBiome(Biome biome) {
		biomesTracker.add(biome);
		biomes = ArrayUtils.add(biomes, biome);
	}
}
