package com.mctg.region;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.glutilities.util.ArrayUtils;
import com.mctg.chunk.Chunk;
import com.mctg.entity.Entity;
import com.mctg.gui.RegionObserver;
import com.mctg.nbt.NBTFileIO;
import com.mctg.nbt.tags.TAGByteArray;
import com.mctg.nbt.tags.TAGCompound;
import com.mctg.nbt.tags.TAGIntArray;
import com.mctg.nbt.tags.TAGList;

public class RegionFileIO {

	public static final int CACHE_SIZE = 0xF0000;
	
	public static Region read(File file) {
		return read(file, null);
	}
	
	// TODO Use a RandomAccessFile to read data

	public static Region read(File file, RegionObserver observer) {
		// parse filename to determine region position
		String[] nameData = file.getName().split("\\.");
		int regionX = Integer.valueOf(nameData[1]);
		int regionZ = Integer.valueOf(nameData[2]);
		Region region = new Region(regionX, regionZ);

		// try to parse file; if anything goes wrong returned region will have
		// limited chunk data
		try {
			FileInputStream fin = new FileInputStream(file);
			byte[] cdata = new byte[(int) file.length()];
			fin.read(cdata);
			fin.close();

			// wrap compressed data
			ByteBuffer buf = ByteBuffer.wrap(cdata);
			for (int z = 0; z < Region.REGION_SIZE; z++) {
				for (int x = 0; x < Region.REGION_SIZE; x++) {
					int infoOffset = getChunkInfoOffset(x, z);
					buf.position(infoOffset);
					int info = buf.getInt();
					buf.position(getChunkDataLocation(info >> 8));
					int length = buf.getInt();
					byte compression = buf.get();
					byte[] chunkData;

					// check if there is info for this chunk
					if (info == 0) {
						// skip this chunk
						observer.chunkSkipped(region);
						continue;
					} else {
						chunkData = new byte[length];
						buf.get(chunkData);
					}

					// uncompress data
					Inflater inf = new Inflater();
					inf.setInput(chunkData);
					byte[] data = new byte[CACHE_SIZE];
					inf.inflate(data);
					NBTFileIO reader = new NBTFileIO(data);
					reader.read();

					// build chunk
					TAGIntArray heightMap = (TAGIntArray) reader.getEntry("Level.HeightMap");
					int[] mapArray = new int[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE];
					for (int i = 0; i < mapArray.length; i++) {
						mapArray[i] = (int) heightMap.getValue()[i];
					}
					byte[] blocks = new byte[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_HEIGHT];
					byte[] add = new byte[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_HEIGHT / 2];
					byte[] bdata = new byte[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_HEIGHT / 2];
					for (int y = 0; y < 16; y++) {
						TAGByteArray sblocks = (TAGByteArray) reader.getEntry("Level.Sections." + y + ".Blocks");
						TAGByteArray sadd = (TAGByteArray) reader.getEntry("Level.Sections." + y + ".Add");
						TAGByteArray sdata = (TAGByteArray) reader.getEntry("Level.Sections." + y + ".Data");
						if (sblocks != null) {
							System.arraycopy(ArrayUtils.toPrimitiveArray(sblocks.getValue()), 0, blocks, y * sblocks.getValue().length, sblocks.getValue().length);
						}
						if (sadd != null) {
							System.arraycopy(ArrayUtils.toPrimitiveArray(sadd.getValue()), 0, add, y * sadd.getValue().length, sadd.getValue().length);
						}
						if (sdata != null) {
							System.arraycopy(ArrayUtils.toPrimitiveArray(sdata.getValue()), 0, bdata, y * sdata.getValue().length, sdata.getValue().length);
						}
					}
					TAGList entities = (TAGList) reader.getEntry("Level.Entities");
					Entity[] entityArray = new Entity[entities.getValue().length];
					for (int i = 0; i < entityArray.length; i++) {
						TAGCompound entry = (TAGCompound) entities.get(i);
						String id = entry.getEntry("id").getValue().toString();
						TAGList pos = (TAGList) entry.getEntry("Pos");
						double ex = (double) pos.get(0).getValue();
						double ey = (double) pos.get(1).getValue();
						double ez = (double) pos.get(2).getValue();
						entityArray[i] = new Entity(id, ex, ey, ez);
					}
					Chunk chunk = new Chunk(x, z, blocks, add, bdata, mapArray, new byte[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE], entityArray);
					region.getChunks()[z * Region.REGION_SIZE + x] = chunk;
					if (observer != null)
						observer.chunkLoaded(region);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DataFormatException e) {
			e.printStackTrace();
		}
		if (observer != null)
			observer.regionLoaded(region);
		return region;
	}

	public static void write(Region region, File file) {
		try {
			// create output stream
			System.out.println("Opening file...");
			FileOutputStream out = new FileOutputStream(file);

			// create chunk data
			List<byte[]> chunks = new ArrayList<byte[]>();
			List<Integer> sizes = new ArrayList<Integer>();
			List<Integer> positions = new ArrayList<Integer>();
			int sector = 0;
			System.out.println("Building chunks...");
			for (Chunk c : region.getChunks()) {
				if (c != null) {
					NBTFileIO reader = new NBTFileIO((byte[]) null);
					reader.setRootTag(c.createNBT());
					byte[] data = reader.build();
					Deflater deflater = new Deflater(9);
					deflater.setInput(data);
					deflater.finish();
					byte[] cdata = new byte[CACHE_SIZE];
					int finalsize = deflater.deflate(cdata);
					byte[] finalcdata = new byte[(finalsize / 4096 + 1) * 4096];
					System.arraycopy(cdata, 0, finalcdata, 0, finalcdata.length);
					chunks.add(finalcdata);
					sizes.add(finalsize + 1);
					positions.add(sector + 2);
					sector += finalsize / 4096 + 1;
				}
			}

			// write header data
			System.out.println("Putting header data into buffer...");
			byte[] header = new byte[8192];
			ByteBuffer headerBuffer = ByteBuffer.wrap(header);
			headerBuffer.flip();
			headerBuffer.limit(8192);
			int i = 0;
			for (Chunk c : region.getChunks()) {
				if (c != null) {
					int pos = getChunkInfoOffset(c.getChunkX(), c.getChunkZ());
					headerBuffer.position(pos);
					int location = positions.get(i) << 8 | (byte) (chunks.get(i).length / 4096);
					headerBuffer.putInt(location);
					headerBuffer.position(pos + 4096);
					headerBuffer.putInt((int) (System.currentTimeMillis() / 1000));
					i++;
				}
			}
			System.out.println("Writing header into buffer...");
			out.write(header);

			// write chunk data
			System.out.println("Putting chunk data into buffer...");
			byte[] chunkData = new byte[(sector + 2) * 4096];
			ByteBuffer chunkBuffer = ByteBuffer.wrap(chunkData);
			chunkBuffer.flip();
			chunkBuffer.limit(chunkData.length);
			for (int j = 0; j < chunks.size(); j++) {
				chunkBuffer.position((positions.get(j) - 2) * 4096);
				chunkBuffer.putInt(sizes.get(j));
				chunkBuffer.put((byte) 2);
				chunkBuffer.put(chunks.get(j));
			}
			System.out.println("Writing chunks to file...");
			out.write(chunkData);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int getChunkInfoOffset(int chunkX, int chunkZ) {
		return 4 * ((chunkX & 31) + (chunkZ & 31) * 32);
	}

	private static int getChunkDataLocation(int value) {
		return value * 4096;
	}

}
