package com.mctg.nbt.tags;

import java.nio.ByteBuffer;

import com.mctg.nbt.NBTEntry;

public class TAGLong extends NBTEntry<Long> {

	public TAGLong(String name, Long value) {
		super(name, value);
	}

	@Override
	public int getTagID() {
		return 4;
	}

	@Override
	public void write(boolean writeHeader, ByteBuffer buffer) {
		if (writeHeader)
			writeTagHeader(buffer);
		buffer.putLong(getValue());
	}

}
