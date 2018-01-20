package com.mctg.nbt.tags;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.mctg.nbt.NBTEntry;

public class TAGCompound extends NBTEntry<List<NBTEntry<?>>> implements Iterable<NBTEntry<?>> {

	public TAGCompound(String name) {
		super(name, new ArrayList<NBTEntry<?>>());
	}

	public TAGCompound(String name, NBTEntry<?>... entries) {
		super(name, Arrays.asList(entries));
	}

	public TAGCompound(String name, List<NBTEntry<?>> entries) {
		super(name, entries);
	}

	public void addEntry(NBTEntry<?> entry) {
		getValue().add(entry);
	}

	public NBTEntry<?> getEntry(String name) {
		for (NBTEntry<?> entry : getValue()) {
			if (entry.getName().equals(name))
				return entry;
		}
		return null;
	}
	
	public void setEntry(String name, Object value) {
		String[] subnames = name.split("\\.");
		NBTEntry<?> parent = null;
		for (String subname : subnames) {
			if (parent == null) {
				parent = getEntry(subname);
			}
			if (parent instanceof TAGCompound) {
				parent = ((TAGCompound) parent).getEntry(subname);
			} else if (parent instanceof TAGList) {
				parent = ((TAGList) parent).get(Integer.parseInt(subname));
			}
		}
		parent.setObjectValue(value);
	}

	public NBTEntry<?> findEntry(String name) {
		String[] subnames = name.split("\\.");
		NBTEntry<?> parent = null;
		for (String subname : subnames) {
			if (parent == null) {
				parent = getEntry(subname);
			}
			if (parent instanceof TAGCompound) {
				parent = ((TAGCompound) parent).getEntry(subname);
			} else if (parent instanceof TAGList) {
				parent = ((TAGList) parent).get(Integer.parseInt(subname));
			}
		}
		return parent;
	}

	@Override
	public Iterator<NBTEntry<?>> iterator() {
		return new Iterator<NBTEntry<?>>() {

			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < getValue().size();
			}

			@Override
			public NBTEntry<?> next() {
				return getValue().get(index++);
			}

		};
	}

	@Override
	public int getTagID() {
		return 10;
	}

	@Override
	public void write(boolean writeHeader, ByteBuffer buffer) {
		if (writeHeader)
			writeTagHeader(buffer);
		for (NBTEntry<?> entry : getValue()) {
			entry.write(true, buffer);
		}
		buffer.put((byte) 0);
	}

}
