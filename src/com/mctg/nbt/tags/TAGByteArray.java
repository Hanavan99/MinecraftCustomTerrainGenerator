package com.mctg.nbt.tags;

import java.nio.ByteBuffer;

import com.mctg.nbt.NBTEntry;

public class TAGByteArray extends NBTEntry<Byte[]> {

	public TAGByteArray(String name, Byte[] value) {
		super(name, value);
	}

	@Override
	public int getTagID() {
		return 7;
	}

	@Override
	public void write(boolean writeHeader, ByteBuffer buffer) {
		if (writeHeader)
			writeTagHeader(buffer);
		buffer.putInt(getValue().length);
		for (int i = 0; i < getValue().length; i++) {
			buffer.put(getValue()[i]);
		}
	}

}
