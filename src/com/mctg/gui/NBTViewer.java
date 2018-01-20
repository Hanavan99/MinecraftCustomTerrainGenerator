package com.mctg.gui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import com.mctg.nbt.NBTTreeModel;
import com.mctg.nbt.tags.TAGCompound;

public class NBTViewer {

	public NBTViewer(TAGCompound root) {

		JFrame frame = new JFrame("Test");
		frame.setBounds(50, 50, 500, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		NBTTreeModel model = new NBTTreeModel(root);
		JTree tree = new JTree(model);
		JScrollPane pane = new JScrollPane(tree);
		frame.add(pane);

	}

}
