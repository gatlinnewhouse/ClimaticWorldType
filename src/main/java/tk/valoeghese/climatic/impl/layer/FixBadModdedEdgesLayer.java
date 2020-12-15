package tk.valoeghese.climatic.impl.layer;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import tk.valoeghese.climatic.impl.ClimateBiomesImpl;

public enum FixBadModdedEdgesLayer implements CrossSamplingLayer, OceanIds {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource rand, int border1, int border2, int border3, int border4, int centre) {
		Biome b = BuiltinRegistries.BIOME.get(centre);
		if (ClimateBiomesImpl.canCorrectEdges(b)) {
			return ClimateBiomesImpl.correctEdges(b, BuiltinRegistries.BIOME.get(border1), BuiltinRegistries.BIOME.get(border2), BuiltinRegistries.BIOME.get(border3), BuiltinRegistries.BIOME.get(border4), centre);
		} else {
			return centre;
		}
	}
}