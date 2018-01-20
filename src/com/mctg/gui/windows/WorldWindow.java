package com.mctg.gui.windows;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import com.mctg.World;
import com.mctg.gui.JWorldViewer;
import com.mctg.gui.RegionObserver;
import com.mctg.region.Region;
import com.mctg.region.RegionFileIO;
import com.mctg.thread.DelegatedTask;
import com.mctg.thread.WorkDelegator;

public class WorldWindow extends JFrame {

	private static final long serialVersionUID = -497088837851334384L;

	private JWorldViewer viewer;
	private JButton openWorld;
	private JButton saveWorld;
	private JButton updateRegions;
	private JProgressBar progress;

	public WorldWindow() {
		setLayout(null);
		viewer = new JWorldViewer();
		viewer.setBounds(170, 10, 700, 700);
		add(viewer);

		openWorld = new JButton("Open World...");
		openWorld.setBounds(10, 10, 150, 25);
		add(openWorld);
		openWorld.addActionListener((e) -> {
			String defaultPath = ".";
			String osName = System.getProperty("os.name");
			if (osName.contains("Windows")) {
				defaultPath = "C:/Users/" + System.getProperty("user.name") + "/AppData/Roaming/.minecraft/saves";
			} else {
				defaultPath = "~/.minecraft/saves";
			}
			JFileChooser open = new JFileChooser(defaultPath);
			open.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (open.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				loadWorld(open.getSelectedFile());
			}
		});

		updateRegions = new JButton("Update Map");
		updateRegions.setBounds(10, 70, 150, 25);
		add(updateRegions);
		updateRegions.addActionListener((e) -> {
			for (Region r : viewer.getWorld().getRegions()) {
				r.invalidate();
			}
			updateRegions();
		});

		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				repaint();
			}
		});

		progress = new JProgressBar();
		progress.setBounds(10, 105, 150, 25);
		add(progress);
	}

	private void loadWorld(File directory) {
		File regionFolder = new File(directory, "region");
		if (regionFolder.exists()) {
			World world = new World();
			setWorld(world);
			int regions = 0;
			List<DelegatedTask> tasks = new ArrayList<DelegatedTask>();
			for (File f : regionFolder.listFiles()) {

				if (f.getName().endsWith(".mca")) {
					tasks.add(new DelegatedTask() {

						private int chunksLoaded = 0;

						@Override
						public void run() {
							Region r = RegionFileIO.read(f, new RegionObserver() {

								@Override
								public void regionLoaded(Region region) {
									// TODO Auto-generated method stub

								}

								@Override
								public void chunkLoaded(Region region) {
									chunksLoaded++;
								}

								@Override
								public void chunkSkipped(Region region) {
									chunksLoaded++;
								}
							});
							world.addRegion(r);
							r.invalidate();
							updateRegions();
							viewer.repaint();
							r = null;
						}

						@Override
						public float getProgress() {
							return (float) chunksLoaded / (Region.REGION_SIZE * Region.REGION_SIZE);
						}
					});

					regions++;
				}
			}
			WorkDelegator td = new WorkDelegator(tasks, 8);
			td.start(progress, 100);
			JOptionPane.showMessageDialog(this, "Success! Loaded " + regions + " region files.");
		} else {
			JOptionPane.showMessageDialog(this, "This directory does not seem to contain any region information. Try selecting a folder in the \"saves\" directory.");
		}
	}

	public void setWorld(World world) {
		viewer.setWorld(world);
	}

	public void updateRegions() {
		viewer.updateRegions();
	}

}
