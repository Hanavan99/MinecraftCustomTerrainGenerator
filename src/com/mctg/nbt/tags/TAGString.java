package com.mctg.nbt.tags;

import java.nio.ByteBuffer;

import com.mctg.nbt.NBTEntry;

public class TAGString extends NBTEntry<String> {

	public TAGString(String name, String value) {
		super(name, value);
	}

	@Override
	public int getTagID() {
		return 8;
	}

	@Override
	public void write(boolean writeHeader, ByteBuffer buffer) {
		if (writeHeader)
			writeTagHeader(buffer);
		buffer.putChar((char) getValue().length());
		buffer.put(getValue().getBytes());
	}

}
