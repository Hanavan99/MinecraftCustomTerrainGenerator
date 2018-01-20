package com.mctg.region;

import com.mctg.chunk.BlockPos;
import com.mctg.chunk.Chunk;

public class Region {

	public static final int REGION_SIZE = 32;

	private int regionX;
	private int regionZ;
	private Chunk[] chunks = new Chunk[1024];
	private boolean validated = true;

	public Region(int regionX, int regionZ) {
		this.regionX = regionX;
		this.regionZ = regionZ;
		chunks = new Chunk[REGION_SIZE * REGION_SIZE];
		for (int i = 0; i < chunks.length; i++) {
			chunks[i] = new Chunk(i / 32, i % 32);
		}
	}

	public Region(int regionX, int regionZ, Chunk[] chunks) {
		this.regionX = regionX;
		this.regionZ = regionZ;
		this.chunks = chunks;
	}
	
	public void invalidate() {
		validated = false;
	}
	
	public boolean isValidated() {
		return validated;
	}

	public Chunk getChunk(int x, int z) {
		int index = x * 32 + z;
		if (index >= 0 && index < chunks.length) {
			return chunks[index];
		}
		return null;
	}

	public Chunk getChunkFromBlockPos(BlockPos pos) {
		if (regionContainsBlockPos(pos)) {
			BlockPos local = pos.getLocalRegionPos();
			Chunk chunk = getChunk(local.getX() % Chunk.CHUNK_SIZE, local.getZ() % Chunk.CHUNK_SIZE);
			if (chunk != null) {
				return chunk;
			}
		}
		return null;
	}

	public boolean regionContainsBlockPos(BlockPos pos) {
		return pos.getX() >= REGION_SIZE * regionX && pos.getZ() >= REGION_SIZE * regionZ && pos.getX() < REGION_SIZE * (regionX + 1) && pos.getZ() < REGION_SIZE * (regionZ + 1);
	}

	public int getBlockID(BlockPos pos) {
		Chunk chunk = getChunk(pos.getX() / 16, pos.getZ() / 16);
		if (chunk != null) {
			return chunk.getBlockID(pos.getLocalChunkPos());
		}
		return -1;
	}

	public void setBlockID(BlockPos pos, int id) {
		Chunk chunk = getChunk(pos.getX() / 16, pos.getZ() / 16);
		if (chunk != null) {
			chunk.setBlockID(pos.getLocalChunkPos(), id);
		}
	}

	public void fillBlocks(BlockPos pos, int[] ids, Region region, int dx, int dy, int dz) {
		int i = 0;
		for (int x = 0; x < dx; x++) {
			for (int y = 0; y < dy; y++) {
				for (int z = 0; z < dz; z++) {
					int block = ids[i++];
					if (block != -1)
						region.setBlockID(pos.addX(x).addY(y).addZ(z), block);
				}
			}
		}
	}

	public void setBlockData(BlockPos pos, int data) {
		Chunk chunk = getChunk(pos.getX() / 16, pos.getZ() / 16);
		if (chunk != null) {
			chunk.setBlockData(pos.getLocalChunkPos(), (byte) data);
		}
	}

	public int getHeightMapValue(BlockPos pos) {
		Chunk chunk = getChunk(pos.getX() / 16, pos.getZ() / 16);
		if (chunk != null) {
			return chunk.getHeightOfColumn(pos.getLocalChunkPos());
		}
		return -1;
	}

	public int getRegionX() {
		return regionX;
	}

	public void setRegionX(int regionX) {
		this.regionX = regionX;
	}

	public int getRegionZ() {
		return regionZ;
	}

	public void setRegionZ(int regionZ) {
		this.regionZ = regionZ;
	}

	public Chunk[] getChunks() {
		return chunks;
	}

	public void setChunks(Chunk[] chunks) {
		this.chunks = chunks;
	}

}
