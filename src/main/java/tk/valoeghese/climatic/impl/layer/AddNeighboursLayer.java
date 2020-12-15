package tk.valoeghese.climatic.impl.layer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import tk.valoeghese.climatic.impl.ClimateBiomesImpl;

public enum AddNeighboursLayer implements CrossSamplingLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource rand, int border1, int border2, int border3, int border4, int centre) {
		if (rand.nextInt(4) == 0) {
			List<Biome> list = new ArrayList<>();
			
			ClimateBiomesImpl.addNeighboursTo(list, BuiltinRegistries.BIOME.get(border1));
			ClimateBiomesImpl.addNeighboursTo(list, BuiltinRegistries.BIOME.get(border2));
			ClimateBiomesImpl.addNeighboursTo(list, BuiltinRegistries.BIOME.get(border3));
			ClimateBiomesImpl.addNeighboursTo(list, BuiltinRegistries.BIOME.get(border4));
			
			if (!list.isEmpty()) {
				return BuiltinRegistries.BIOME.getRawId(list.get(rand.nextInt(list.size())));
			} else {
				return centre;
			}
		} else {
			return centre;
		}
	}

}
