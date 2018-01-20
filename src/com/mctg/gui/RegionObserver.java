package com.mctg.gui;

import com.mctg.region.Region;

public interface RegionObserver {

	public void chunkLoaded(Region region);
	
	public void chunkSkipped(Region region);
	
	public void regionLoaded(Region region);
	
}
