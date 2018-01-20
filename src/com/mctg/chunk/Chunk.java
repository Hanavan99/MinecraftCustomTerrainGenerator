package com.mctg.chunk;

import java.util.ArrayList;
import java.util.List;

import com.glutilities.util.ArrayUtils;
import com.mctg.entity.Entity;
import com.mctg.nbt.tags.TAGByte;
import com.mctg.nbt.tags.TAGByteArray;
import com.mctg.nbt.tags.TAGCompound;
import com.mctg.nbt.tags.TAGInt;
import com.mctg.nbt.tags.TAGIntArray;
import com.mctg.nbt.tags.TAGList;
import com.mctg.nbt.tags.TAGLong;

public class Chunk {

	public static final int CHUNK_SIZE = 16;
	public static final int CHUNK_HEIGHT = 256;

	private int x;
	private int z;
	private byte[] blocks;
	private byte[] blockAdd;
	private byte[] blockData;
	private int[] heightMap;
	private byte[] biomes;
	private Entity[] entities;

	public Chunk(int x, int z) {
		this.x = x;
		this.z = z;
		blocks = new byte[CHUNK_SIZE * CHUNK_SIZE * CHUNK_HEIGHT];
		blockAdd = new byte[CHUNK_SIZE * CHUNK_SIZE * CHUNK_HEIGHT / 2];
		blockData = new byte[CHUNK_SIZE * CHUNK_SIZE * CHUNK_HEIGHT / 2];
		heightMap = new int[CHUNK_SIZE * CHUNK_SIZE];
		biomes = new byte[CHUNK_SIZE * CHUNK_SIZE];
		entities = null;
	}

	public Chunk(int x, int z, byte[] blocks, byte[] blockAdd, byte[] blockData, int[] heightMap, byte[] biomes, Entity[] entities) {
		this.blocks = blocks;
		this.blockAdd = blockAdd;
		this.blockData = blockData;
		this.heightMap = heightMap;
		this.biomes = biomes;
		this.entities = entities;
	}

	public int getChunkX() {
		return x;
	}

	public int getChunkZ() {
		return z;
	}

	public int getHeightOfColumn(BlockPos pos) {
		int index = pos.getZ() * CHUNK_SIZE + pos.getX();
		if (index >= 0 && index < heightMap.length) {
			return heightMap[index];
		}
		return -1;
	}

	public int getBlockID(BlockPos pos) {
		return blocks[pos.getY() * CHUNK_SIZE * CHUNK_SIZE + pos.getZ() * CHUNK_SIZE + pos.getX()] | (getAddBlockData(pos) << 8);
	}

	public void setBlockID(BlockPos pos, int id) {
		blocks[pos.getY() * CHUNK_SIZE * CHUNK_SIZE + pos.getZ() * CHUNK_SIZE + pos.getX()] = (byte) id;
		setAddBlockData(pos, (byte) (id >> 8));
	}

	public byte getAddBlockData(BlockPos pos) {
		int index = pos.getY() * CHUNK_SIZE * CHUNK_SIZE + pos.getZ() * CHUNK_SIZE + pos.getX();
		return (byte) (index % 2 == 0 ? blockAdd[index / 2] >> 4 : blockAdd[index / 2] & 0xF);
	}

	public void setAddBlockData(BlockPos pos, byte data) {
		int index = pos.getY() * CHUNK_SIZE * CHUNK_SIZE + pos.getZ() * CHUNK_SIZE + pos.getX();
		blockAdd[index / 2] &= (index % 2 == 0 ? data << 4 | 0xF : data | 0xF0);
	}

	public byte getBlockData(BlockPos pos) {
		int index = pos.getY() * CHUNK_SIZE * CHUNK_SIZE + pos.getZ() * CHUNK_SIZE + pos.getX();
		return (byte) (index % 2 != 0 ? blockData[index / 2] >> 4 : blockData[index / 2] & 0xF);
	}

	public void setBlockData(BlockPos pos, byte data) {
		int index = pos.getY() * CHUNK_SIZE * CHUNK_SIZE + pos.getZ() * CHUNK_SIZE + pos.getX();
		byte oldValue = blockData[index / 2];
		if (index % 2 == 0) {
			oldValue = (byte) (oldValue & 0xF0 | data & 0xF);
		} else {
			oldValue = (byte) (oldValue & 0xF | (data << 4) & 0xF0);
		}
		blockData[index / 2] = oldValue;
	}

	public void setBiome(BlockPos pos, int id) {
		biomes[pos.getZ() * CHUNK_SIZE + pos.getX()] = (byte) id;
	}

	public void setHeightMap(BlockPos pos, int height) {
		heightMap[pos.getZ() * CHUNK_SIZE + pos.getX()] = height;
	}

	public Entity[] getEntities() {
		return entities;
	}

	public TAGCompound createNBT() {
		TAGCompound root = new TAGCompound("");

		// generate level information
		TAGCompound level = new TAGCompound("Level");
		level.addEntry(new TAGInt("xPos", x));
		level.addEntry(new TAGInt("zPos", z));
		level.addEntry(new TAGLong("LastUpdate", System.currentTimeMillis() / 1000));
		level.addEntry(new TAGByte("LightPopulated", (byte) 1));
		level.addEntry(new TAGByte("TerrainPopulated", (byte) 1));
		level.addEntry(new TAGByte("V", (byte) 1));
		level.addEntry(new TAGLong("InhabitedTime", 0L));
		level.addEntry(new TAGList("Entities", new TAGCompound[0], (byte) 0));
		level.addEntry(new TAGList("TileEntities", new TAGCompound[0], (byte) 0));
		root.addEntry(level);

		// generate biome and heightmap information
		Byte[] biomes = ArrayUtils.toObjectArray(this.biomes);

		Integer[] heightMap = new Integer[CHUNK_SIZE * CHUNK_SIZE];
		for (int i = 0; i < heightMap.length; i++) {
			heightMap[i] = this.heightMap[i];
		}
		level.addEntry(new TAGByteArray("Biomes", biomes));
		level.addEntry(new TAGIntArray("HeightMap", heightMap));

		// build sections
		List<TAGCompound> sectionList = new ArrayList<TAGCompound>();
		for (int section = 0; section < 16; section++) {
			TAGCompound sect = new TAGCompound("");
			sect.addEntry(new TAGByte("Y", (byte) section));
			byte[] blocks = new byte[4096];
			System.arraycopy(this.blocks, section * 4096, blocks, 0, 4096);
			sect.addEntry(new TAGByteArray("Blocks", ArrayUtils.toObjectArray(blocks)));
			byte[] add = new byte[2048];
			System.arraycopy(blockAdd, section * 2048, add, 0, 2048);
			sect.addEntry(new TAGByteArray("Add", ArrayUtils.toObjectArray(add)));
			byte[] data = new byte[2048];
			System.arraycopy(blockData, section * 2048, data, 0, 2048);
			sect.addEntry(new TAGByteArray("Data", ArrayUtils.toObjectArray(data)));
			sect.addEntry(new TAGByteArray("BlockLight", ArrayUtils.toObjectArray(new byte[2048])));
			Byte[] skyLight = ArrayUtils.toObjectArray(new byte[2048]);
			ArrayUtils.fill(skyLight, new Byte[] { (byte) 0xFF });
			sect.addEntry(new TAGByteArray("SkyLight", skyLight));
			sectionList.add(sect);
		}
		TAGList sections = new TAGList("Sections", sectionList.toArray(new TAGCompound[0]), (byte) 10);
		level.addEntry(sections);

		return root;
	}

	private byte nibble4(byte[] arr, int index) {
		return (byte) (index % 2 == 0 ? arr[index / 2] & 0x0F : (arr[index / 2] >> 4) & 0x0F);
	}

}
