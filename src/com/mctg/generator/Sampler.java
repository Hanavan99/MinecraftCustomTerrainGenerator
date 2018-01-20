package com.mctg.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.glutilities.terrain.PerlinGenerator;

public class Sampler {

	private long seed;
	private Random seedGenerator;
	private Map<String, PerlinGenerator> noises = new HashMap<String, PerlinGenerator>();
	private Map<String, Random> randoms = new HashMap<String, Random>();
	
	public Sampler() {
		seed = (long) ((Math.random() - 0.5) * Long.MAX_VALUE);
		seedGenerator = new Random(seed);
	}
	
	public Sampler(long seed) {
		this.seed = seed;
		seedGenerator = new Random(seed);
	}
	
	public void createNoiseGenerator(String name, double xoff, double yoff, double zoff, double xscale, double yscale, double zscale, double outscale) {
		noises.put(name, new PerlinGenerator(seedGenerator.nextLong(), xoff, yoff, zoff, xscale, yscale, zscale, outscale));
	}
	
	public void createRandom(String name) {
		randoms.put(name, new Random(seedGenerator.nextLong()));
	}
	
	public PerlinGenerator getNoiseGenerator(String name) {
		return noises.get(name);
	}
	
	public Random getRandom(String name) {
		return randoms.get(name);
	}
	
}
