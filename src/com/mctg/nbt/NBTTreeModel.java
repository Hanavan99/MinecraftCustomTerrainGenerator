package com.mctg.nbt;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.mctg.nbt.tags.TAGCompound;
import com.mctg.nbt.tags.TAGList;

public class NBTTreeModel implements TreeModel {

	private TAGCompound root;
	private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

	public NBTTreeModel(TAGCompound root) {
		this.root = root;
	}

	@Override
	public void addTreeModelListener(TreeModelListener arg0) {
		listeners.add(arg0);
	}

	@Override
	public Object getChild(Object arg0, int arg1) {
		if (arg0 instanceof TAGList) {
			return ((TAGList) arg0).getValue()[arg1];
		}
		if (arg0 instanceof TAGCompound) {
			return ((TAGCompound) arg0).getValue().get(arg1);
		}
		return null;
	}

	@Override
	public int getChildCount(Object arg0) {
		if (arg0 instanceof TAGList) {
			return ((TAGList) arg0).getValue().length;
		}
		if (arg0 instanceof TAGCompound) {
			return ((TAGCompound) arg0).getValue().size();
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object arg0, Object arg1) {
		if (arg1 instanceof NBTEntry<?>) {
			if (arg0 instanceof TAGList) {
				NBTEntry<?>[] entries = ((TAGList) arg0).getValue();
				for (int i = 0; i < entries.length; i++) {
					if (arg1.equals(entries[i])) {
						return i;
					}
				}
			} else if (arg0 instanceof TAGCompound) {
				List<NBTEntry<?>> entries = ((TAGCompound) arg0).getValue();
				for (int i = 0; i < entries.size(); i++) {
					if (arg1.equals(entries.get(i))) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public boolean isLeaf(Object arg0) {
		return false;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener arg0) {
		listeners.remove(arg0);
	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// // TODO Auto-generated method stub
		String path = "";
		for (Object branch : arg0.getPath()) {
			path += "." + ((NBTEntry<?>) branch).getName();
		}
		path = path.substring(1);
	}

}
