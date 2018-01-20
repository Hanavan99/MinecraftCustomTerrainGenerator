package com.mctg.nbt.tags;

import java.nio.ByteBuffer;

import com.mctg.nbt.NBTEntry;

public class TAGFloat extends NBTEntry<Float> {

	public TAGFloat(String name, Float value) {
		super(name, value);
	}

	@Override
	public int getTagID() {
		return 5;
	}

	@Override
	public void write(boolean writeHeader, ByteBuffer buffer) {
		if (writeHeader)
			writeTagHeader(buffer);
		buffer.putFloat(getValue());
	}

}
