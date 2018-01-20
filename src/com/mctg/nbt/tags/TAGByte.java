package com.mctg.nbt.tags;

import java.nio.ByteBuffer;

import com.mctg.nbt.NBTEntry;

public class TAGByte extends NBTEntry<Byte> {

	public TAGByte(String name, Byte value) {
		super(name, value);
	}

	@Override
	public int getTagID() {
		return 1;
	}

	@Override
	public void write(boolean writeHeader, ByteBuffer buffer) {
		if (writeHeader)
			writeTagHeader(buffer);
		buffer.put(getValue());
	}

}
