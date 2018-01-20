package com.mctg.nbt.tags;

import java.nio.ByteBuffer;

import com.mctg.nbt.NBTEntry;

public class TAGShort extends NBTEntry<Character> {

	public TAGShort(String name, Character value) {
		super(name, value);
	}

	@Override
	public int getTagID() {
		return 2;
	}

	@Override
	public void write(boolean writeHeader, ByteBuffer buffer) {
		if (writeHeader)
			writeTagHeader(buffer);
		buffer.putChar(getValue());
	}

}
