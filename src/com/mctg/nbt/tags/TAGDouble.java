package com.mctg.nbt.tags;

import java.nio.ByteBuffer;

import com.mctg.nbt.NBTEntry;

public class TAGDouble extends NBTEntry<Double> {

	public TAGDouble(String name, Double value) {
		super(name, value);
	}

	@Override
	public int getTagID() {
		return 6;
	}

	@Override
	public void write(boolean writeHeader, ByteBuffer buffer) {
		if (writeHeader)
			writeTagHeader(buffer);
		buffer.putDouble(getValue());
	}

}
