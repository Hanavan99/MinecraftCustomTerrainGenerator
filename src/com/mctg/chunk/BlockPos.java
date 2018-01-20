package com.mctg.chunk;

import com.mctg.region.Region;

public class BlockPos {

	private int x;
	private int y;
	private int z;

	public BlockPos() {
		x = 0;
		y = 0;
		z = 0;
	}

	public BlockPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockPos getLocalChunkPos() {
		return new BlockPos(x % Chunk.CHUNK_SIZE, y, z % Chunk.CHUNK_SIZE);
	}

	public BlockPos getLocalRegionPos() {
		return new BlockPos(x % Region.REGION_SIZE * Chunk.CHUNK_SIZE, y, z % Region.REGION_SIZE * Chunk.CHUNK_SIZE);
	}

	public BlockPos north() {
		return new BlockPos(x, y, z - 1);
	}

	public BlockPos south() {
		return new BlockPos(x, y, z + 1);
	}

	public BlockPos east() {
		return new BlockPos(x + 1, y, z);
	}

	public BlockPos west() {
		return new BlockPos(x - 1, y, z);
	}
	
	public BlockPos up() {
		return new BlockPos(x, y + 1, z);
	}
	
	public BlockPos down() {
		return new BlockPos(x, y - 1, z);
	}

	public int getX() {
		return x;
	}

	public BlockPos setX(int x) {
		return new BlockPos(x, y, z);
	}
	
	public BlockPos addX(int dx) {
		return new BlockPos(x + dx, y, z);
	}

	public int getY() {
		return y;
	}

	public BlockPos setY(int y) {
		return new BlockPos(x, y, z);
	}
	
	public BlockPos addY(int dy) {
		return new BlockPos(x, y + dy, z);
	}

	public int getZ() {
		return z;
	}

	public BlockPos setZ(int z) {
		return new BlockPos(x, y, z);
	}
	
	public BlockPos addZ(int dz) {
		return new BlockPos(x, y, z + dz);
	}

}
