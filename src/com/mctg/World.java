package com.mctg;

import java.util.ArrayList;
import java.util.List;

import com.mctg.nbt.tags.TAGCompound;
import com.mctg.region.Region;

public class World {

	enum Property {

		LEVEL_NAME("Data.LevelName"), SEED("Data.RandomSeed");

		private String path;

		private Property(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}

	}

	private List<Region> regions = new ArrayList<Region>();
	private TAGCompound levelData = new TAGCompound("");

	public Region[] getRegions() {
		return regions.toArray(new Region[0]);
	}

	public void addRegion(Region region) {
		regions.add(region);
	}

	public void removeRegion(Region region) {
		regions.remove(region);
	}

	public Object getProperty(Property prop) {
		return levelData.findEntry(prop.getPath()).getValue();
	}

	public void setProperty(Property prop, Object value) {
		levelData.findEntry(prop.getPath()).setObjectValue(value);
	}

}
