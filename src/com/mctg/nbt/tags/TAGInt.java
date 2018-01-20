package com.mctg.nbt.tags;

import java.nio.ByteBuffer;

import com.mctg.nbt.NBTEntry;

public class TAGInt extends NBTEntry<Integer> {

	public TAGInt(String name, Integer value) {
		super(name, value);
	}

	@Override
	public int getTagID() {
		return 3;
	}

	@Override
	public void write(boolean writeHeader, ByteBuffer buffer) {
		if (writeHeader)
			writeTagHeader(buffer);
		buffer.putInt(getValue());
	}

}
