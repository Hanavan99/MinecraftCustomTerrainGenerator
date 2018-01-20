package com.mctg.generator;

import com.mctg.region.Region;

public abstract class TerrainGenerator {

	public abstract Region generateTerrain(int regionX, int regionZ, Sampler sampler);
	
}
