package com.mctg.nbt;

import java.nio.ByteBuffer;

public abstract class NBTEntry<E> {

	private String name;
	private E value;

	public NBTEntry(String name, E value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public E getValue() {
		return value;
	}
	
	public void setValue(E value) {
		this.value = value;
	}
	
	public void setObjectValue(Object value) {
		this.value = (E) value;
	}
	
	public abstract int getTagID();

	//public abstract void read(ByteBuffer buffer);

	public abstract void write(boolean writeHeader, ByteBuffer buffer);
	
	protected void writeTagHeader(ByteBuffer buffer) {
		buffer.put((byte) getTagID());
		buffer.putChar((char) getName().length());
		buffer.put(getName().getBytes());
	}
	
	@Override
	public String toString() {
		return "(" + value.getClass().getSimpleName() + ") " + name;
	}

}
