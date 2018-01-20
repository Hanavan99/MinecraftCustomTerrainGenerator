package com.mctg.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.glutilities.terrain.PerlinGenerator;
import com.mctg.World;
import com.mctg.chunk.BlockPos;
import com.mctg.chunk.Chunk;
import com.mctg.gui.NBTViewer;
import com.mctg.gui.windows.WorldWindow;
import com.mctg.nbt.NBTFileIO;
import com.mctg.region.Region;
import com.mctg.thread.DelegatedTask;

public class Main {

	public static void main(String[] args) {
//		try {
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
//			e1.printStackTrace();
//		}

		NBTFileIO reader = new NBTFileIO(new File("C:/Users/Hanavan/AppData/Roaming/.minecraft/saves/SecretWorld/level.dat"));
		reader.read();
		reader.setEntry("Data.LevelName", "MCTG Test YEE");
		reader.write(true);
		NBTViewer nbtViewer = new NBTViewer(reader.getRootTag());

		World world = new World();
		world.addRegion(new Region(0, 0));
		world.addRegion(new Region(1, 0));

		WorldWindow worldWindow = new WorldWindow();
		worldWindow.setWorld(world);
		worldWindow.setBounds(5, 5, 800, 800);
		worldWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		worldWindow.setVisible(true);
		
//		Random r = new Random();
//		PerlinGenerator gen = new PerlinGenerator(r.nextLong());
//		PerlinGenerator underwater = new PerlinGenerator(r.nextLong());
//		Timer t = new Timer(100, (e) -> {
//			worldWindow.updateRegions();
//			worldWindow.repaint();
//		});
//		t.start();
//		List<DelegatedTask> tasks = new ArrayList<DelegatedTask>();
//		for (Region region : world.getRegions()) {
//			tasks.add(new DelegatedTask() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					
//				}
//
//				@Override
//				public float getProgress() {
//					// TODO Auto-generated method stub
//					return 0;
//				}
//				
//			});
//		}
//		t.stop();

	}

	private static void generate(Region region, Random rand, PerlinGenerator gen, PerlinGenerator underwater) {
		gen.setXscale(1d / 50);
		gen.setZscale(1d / 50);
		gen.setXoff(region.getRegionZ() * Region.REGION_SIZE * Chunk.CHUNK_SIZE);
		gen.setZoff(region.getRegionX() * Region.REGION_SIZE * Chunk.CHUNK_SIZE);
		underwater.setXscale(1d / 15);
		underwater.setZscale(1d / 15);
		underwater.setXoff(region.getRegionZ() * Region.REGION_SIZE * Chunk.CHUNK_SIZE);
		underwater.setZoff(region.getRegionX() * Region.REGION_SIZE * Chunk.CHUNK_SIZE);
		for (int x = 0; x < Chunk.CHUNK_SIZE * Region.REGION_SIZE; x++) {
			if (x % 64 == 0)
				System.out.println("Generating world, " + ((float) x / (Chunk.CHUNK_SIZE * Region.REGION_SIZE) * 100) + "% complete...");
			for (int z = 0; z < Chunk.CHUNK_SIZE * Region.REGION_SIZE; z++) {
				for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
					BlockPos pos = new BlockPos(x, y, z);
					int height = (int) (gen.noise(x, 0.5, z) * 20) + 64;

					if (y == 0) {
						region.setBlockID(pos, 7);
						continue;
					}
					if (height <= 64 && y > height && y <= 64) {
						region.setBlockID(pos, 9);
					}
					if (y < height - 3) {
						double ore = rand.nextDouble();
						if (ore < 1.0 / (y + 1)) {

							switch (rand.nextInt(7)) {
							case 0:
								region.setBlockID(pos, 14);
								break;
							case 1:
								region.setBlockID(pos, 15);
								break;
							case 2:
								region.setBlockID(pos, 16);
								break;
							case 3:
								region.setBlockID(pos, 21);
								break;
							case 4:
								region.setBlockID(pos, 56);
								break;
							case 5:
								region.setBlockID(pos, 73);
								break;
							case 6:
								region.setBlockID(pos, 129);
								break;
							}

						} else {
							region.setBlockID(pos, 1);
						}
					} else if (y < height && y >= height - 3) {
						region.setBlockID(pos, 3);
					} else if (y == height) {
						int under = (int) Math.abs(underwater.noise(x, 0.5, z) * 3);
						if (y >= height - 5 && y < 64) {
							switch (under) {
							default:
								region.setBlockID(pos, 12);
								break;
							case 1:
								region.setBlockID(pos, 13);
								break;
							case 2:
								region.setBlockID(pos, 82);
								break;
							case 3:
								region.setBlockID(pos, 3);
								break;
							}
						} else if (y >= 64 && y <= 64 + under) {
							region.setBlockID(pos, 12);
						} else {
							region.setBlockID(pos, 2);
						}

					} else if (y == height + 1 && region.getBlockID(pos.down()) == 2) { // surface
						// generation
						if (rand.nextDouble() < 0.25) {
							region.setBlockID(pos, 31);
							region.setBlockData(pos, 1);
						} else if (rand.nextDouble() < 0.05) {
							region.setBlockID(pos, 38);
							region.setBlockData(pos, rand.nextInt(9));
						} else if (rand.nextDouble() < 0.05) {
							region.setBlockID(pos, 6);
						}
					}
				}
			}
		}
	}

}
