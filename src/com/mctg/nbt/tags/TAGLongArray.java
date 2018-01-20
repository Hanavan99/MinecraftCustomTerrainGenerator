package com.mctg.nbt.tags;

import java.nio.ByteBuffer;

import com.mctg.nbt.NBTEntry;

public class TAGLongArray extends NBTEntry<Long[]> {

	public TAGLongArray(String name, Long[] value) {
		super(name, value);
	}

	@Override
	public int getTagID() {
		return 12;
	}

	@Override
	public void write(boolean writeHeader, ByteBuffer buffer) {
		if (writeHeader)
			writeTagHeader(buffer);
		buffer.putInt(getValue().length);
		for (int i = 0; i < getValue().length; i++) {
			buffer.putLong(getValue()[i]);
		}
	}

}
