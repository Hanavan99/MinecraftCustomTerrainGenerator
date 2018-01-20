package com.mctg.nbt.tags;

import java.nio.ByteBuffer;
import java.util.Iterator;

import com.mctg.nbt.NBTEntry;

public class TAGList extends NBTEntry<NBTEntry<?>[]> implements Iterable<NBTEntry<?>> {

	private byte dataType;
	
	public TAGList(String name, NBTEntry<?>[] value, byte dataType) {
		super(name, value);
		this.dataType = dataType;
	}

	public NBTEntry<?> get(int index) {
		if (index >= 0 && index < getValue().length) {
			return getValue()[index];
		} else {
			return null;
		}
	}

	public void set(int index, NBTEntry<?> value) {
		if (index >= 0 && index < getValue().length) {
			getValue()[index] = value;
		}
	}

	@Override
	public Iterator<NBTEntry<?>> iterator() {
		return new Iterator<NBTEntry<?>>() {

			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < getValue().length;
			}

			@Override
			public NBTEntry<?> next() {
				return getValue()[index++];
			}
		};
	}

	@Override
	public int getTagID() {
		return 9;
	}

	@Override
	public void write(boolean writeHeader, ByteBuffer buffer) {
		if (writeHeader)
			writeTagHeader(buffer);
		buffer.put(dataType);
		buffer.putInt(getValue().length);
		for (NBTEntry<?> entry : getValue()) {
			entry.write(false, buffer);
		}
	}

}
