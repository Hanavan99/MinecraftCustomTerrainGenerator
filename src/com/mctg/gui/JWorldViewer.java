package com.mctg.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;

import com.mctg.World;
import com.mctg.chunk.BlockPos;
import com.mctg.chunk.Chunk;
import com.mctg.entity.Entity;
import com.mctg.region.Region;

public class JWorldViewer extends JComponent {

	private World world;
	private Map<Region, BufferedImage> images = new HashMap<Region, BufferedImage>();

	private static final long serialVersionUID = -6940272036504162606L;
	private float blockSize = 1f;
	private int tempx = 0;
	private int tempy = 0;
	private int dx = 0;
	private int dy = 0;
	private MouseEvent dragStart;

	public JWorldViewer() {
		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				if (dragStart != null) {
					dragStart = null;
					dx += tempx;
					dy += tempy;
					tempx = 0;
					tempy = 0;
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (dragStart == null) {
					dragStart = e;
				} else {
					tempx = e.getX() - dragStart.getX();
					tempy = e.getY() - dragStart.getY();
				}

				JWorldViewer.this.repaint();
			}
		});
		addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				blockSize *= Math.pow(2, -e.getWheelRotation());
				JWorldViewer.this.repaint();
			}
		});
	}

	public JWorldViewer(World world) {
		this.world = world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public World getWorld() {
		return world;
	}

	public void updateRegions() {
		for (Region r : world.getRegions()) {
			if (!r.isValidated())
				images.put(r, updateRegion(r, 64, false));
		}
	}

	@Override
	public void paint(Graphics gfx) {
		Graphics2D g = (Graphics2D) gfx;
		g.translate(dx + tempx, dy + tempy);
		g.scale(blockSize, blockSize);
		// for (Region r : world.getRegions()) {
		// updateRegion(r, 64, g);
		// }

		Iterator<Entry<Region, BufferedImage>> itr = images.entrySet().iterator();
		synchronized (itr) {
			while (itr.hasNext()) {
				Entry<Region, BufferedImage> entry = itr.next();
				Region region = entry.getKey();
				BufferedImage image = entry.getValue();
				g.drawImage(image, Region.REGION_SIZE * Chunk.CHUNK_SIZE * region.getRegionX(), Region.REGION_SIZE * Chunk.CHUNK_SIZE * region.getRegionZ(), image.getWidth(), image.getHeight(), null);
			}
		}
	}

	private BufferedImage updateRegion(Region r, int yLevel, boolean doHeightMap) {
		BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
		for (int chunkZ = 0; chunkZ < 32; chunkZ++) {
			for (int chunkX = 0; chunkX < 32; chunkX++) {
				Chunk c = r.getChunk(chunkX, chunkZ);
				if (c == null)
					continue;
				for (int i = 0; i < 256; i++) {
					BlockPos abs = new BlockPos(chunkZ * 16 + i % 16, 0, chunkX * 16 + i / 16);
					BlockPos pos = abs.getLocalChunkPos();
					int height;
					if (yLevel == -1)
						height = c.getHeightOfColumn(pos) - 1;
					else
						height = yLevel;
					pos = pos.setY(height);
					int block = c.getBlockID(pos);

					Color color = null;
					if (yLevel == -1 && (block == 0 || height > c.getHeightOfColumn(pos) - 1)) {

					} else {
						color = getColorForBlock(block);

						BlockPos left = abs.west();
						BlockPos up = abs.north();
						if (height < r.getHeightMapValue(left) - 1 || height < r.getHeightMapValue(up) - 1)
							color = color.darker();
						int x = (chunkZ * 16) + (i / 16);
						int y = (chunkX * 16) + (i % 16);
						g.setColor(color);
						g.fillRect(x, y, 1, 1);
					}
				}
				g.setColor(Color.WHITE);
				if (c.getEntities() != null) {
					for (Entity e : c.getEntities()) {
						g.drawOval((int) e.getX() - 5, (int) e.getZ() - 5, 10, 10);
					}
				}
			}

		}
		return image;
	}

	private static Color getColorForBlock(int block) {
		Color color = Color.BLACK;
		switch (block) {
		case 1:
		case 4:
		case 13:
			color = Color.GRAY;
			break;
		case 2:
			color = Color.GREEN.darker();
			break;
		case 3:
			color = Color.ORANGE.darker().darker();
		case 5:
		case 17:
			color = Color.ORANGE.darker();
			break;
		case 8:
		case 9:
		case 21:
			color = Color.BLUE;
			break;
		case 73:
		case 74:
			color = Color.RED;
			break;
		case 12:
		case 24:
			color = new Color(255, 255, 128);
			break;
		case 14:
			color = Color.YELLOW;
			break;
		case 15:
			color = new Color(128, 64, 64);
			break;
		case 16:
			color = Color.DARK_GRAY;
			break;
		case 18:
		case 161:
			color = Color.GREEN;
			break;
		case 56:
			color = Color.CYAN;
			break;
		case 10:
		case 11:
			color = new Color(255, 64, 0);
			break;
		case 129:
			color = Color.GREEN;
		case 208:
			color = Color.ORANGE.darker().darker();
			break;
		}
		return color;
	}
}
