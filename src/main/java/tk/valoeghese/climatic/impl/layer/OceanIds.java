package tk.valoeghese.climatic.impl.layer;

import net.minecraft.world.biome.BiomeKeys;
import net.fabricmc.fabric.impl.biome.InternalBiomeUtils;

public interface OceanIds {
	int OCEAN = InternalBiomeUtils.getRawId(BiomeKeys.OCEAN);
	int WARM_OCEAN = InternalBiomeUtils.getRawId(BiomeKeys.WARM_OCEAN);
	int LUKEWARM_OCEAN = InternalBiomeUtils.getRawId(BiomeKeys.LUKEWARM_OCEAN);
	int COLD_OCEAN = InternalBiomeUtils.getRawId(BiomeKeys.COLD_OCEAN);
	int FROZEN_OCEAN = InternalBiomeUtils.getRawId(BiomeKeys.FROZEN_OCEAN);
	int DEEP_WARM_OCEAN = InternalBiomeUtils.getRawId(BiomeKeys.DEEP_WARM_OCEAN);
	int DEEP_LUKEWARM_OCEAN = InternalBiomeUtils.getRawId(BiomeKeys.DEEP_LUKEWARM_OCEAN);
	int DEEP_OCEAN = InternalBiomeUtils.getRawId(BiomeKeys.DEEP_OCEAN);
	int DEEP_COLD_OCEAN = InternalBiomeUtils.getRawId(BiomeKeys.DEEP_COLD_OCEAN);
	int DEEP_FROZEN_OCEAN = InternalBiomeUtils.getRawId(BiomeKeys.DEEP_FROZEN_OCEAN);

	default public boolean isOcean(int int_1) {
		return int_1 == WARM_OCEAN || int_1 == LUKEWARM_OCEAN || int_1 == OCEAN || int_1 == COLD_OCEAN || int_1 == FROZEN_OCEAN || int_1 == DEEP_WARM_OCEAN || int_1 == DEEP_LUKEWARM_OCEAN || int_1 == DEEP_OCEAN || int_1 == DEEP_COLD_OCEAN || int_1 == DEEP_FROZEN_OCEAN;
	}
	
	default public boolean isShallowOcean(int int_1) {
		return int_1 == WARM_OCEAN || int_1 == LUKEWARM_OCEAN || int_1 == OCEAN || int_1 == COLD_OCEAN || int_1 == FROZEN_OCEAN;
	}
	
	default public boolean isDeepOcean(int int_1) {
		return int_1 == DEEP_WARM_OCEAN || int_1 == DEEP_LUKEWARM_OCEAN || int_1 == DEEP_OCEAN || int_1 == DEEP_COLD_OCEAN || int_1 == DEEP_FROZEN_OCEAN;
	}
}
