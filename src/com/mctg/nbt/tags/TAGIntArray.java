package com.mctg.nbt.tags;

import java.nio.ByteBuffer;

import com.mctg.nbt.NBTEntry;

public class TAGIntArray extends NBTEntry<Integer[]> {

	public TAGIntArray(String name, Integer[] value) {
		super(name, value);
	}

	@Override
	public int getTagID() {
		return 11;
	}

	@Override
	public void write(boolean writeHeader, ByteBuffer buffer) {
		if (writeHeader)
			writeTagHeader(buffer);
		buffer.putInt(getValue().length);
		for (int i = 0; i < getValue().length; i++) {
			buffer.putInt(getValue()[i]);
		}
	}

}
