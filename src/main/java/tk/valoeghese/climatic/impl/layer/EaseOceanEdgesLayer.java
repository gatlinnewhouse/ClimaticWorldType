package tk.valoeghese.climatic.impl.layer;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.fabricmc.fabric.impl.biome.InternalBiomeUtils;

public enum EaseOceanEdgesLayer implements CrossSamplingLayer, OceanIds {
	INSTANCE;

	private static final int SAVANNA_ID = InternalBiomeUtils.getRawId(BiomeKeys.SAVANNA);

	@Override
	public int sample(LayerRandomnessSource rand, int border1, int border2, int border3, int border4, int centre) {
		if (isOcean(centre)) {
			if (!isOcean(border1) || !isOcean(border2) || !isOcean(border3) || !isOcean(border4)) {
				if (isShallowOcean(centre)) {
					return OCEAN;
				} else {
					return rand.nextInt(3) == 0 ? OCEAN : DEEP_OCEAN;
				}
			}
			} else if (isOcean(border1) || isOcean(border2) || isOcean(border3) || isOcean(border4)) {
				if (BuiltinRegistries.BIOME.get(centre).getCategory() == Biome.Category.DESERT) {
					return SAVANNA_ID;
				}
			}

			return centre;
		}

	}
