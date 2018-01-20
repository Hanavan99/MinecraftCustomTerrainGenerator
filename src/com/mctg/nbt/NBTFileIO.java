package com.mctg.nbt;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.mctg.nbt.tags.TAGByte;
import com.mctg.nbt.tags.TAGByteArray;
import com.mctg.nbt.tags.TAGCompound;
import com.mctg.nbt.tags.TAGDouble;
import com.mctg.nbt.tags.TAGFloat;
import com.mctg.nbt.tags.TAGInt;
import com.mctg.nbt.tags.TAGIntArray;
import com.mctg.nbt.tags.TAGList;
import com.mctg.nbt.tags.TAGLong;
import com.mctg.nbt.tags.TAGLongArray;
import com.mctg.nbt.tags.TAGShort;
import com.mctg.nbt.tags.TAGString;

public class NBTFileIO {

	private File file;
	private byte[] data;
	private TAGCompound root;

	public NBTFileIO(File file) {
		this.file = file;
		this.data = null;
	}

	public NBTFileIO(byte[] data) {
		this.data = data;
	}

	public void read() {
		try {
			if (data == null) {
				// open file
				FileInputStream fin = new FileInputStream(file);
				byte[] cdata = new byte[(int) file.length()];
				fin.read(cdata);
				fin.close();

				// wrap compressed data
				ByteBuffer buf = ByteBuffer.wrap(cdata);
				buf = buf.order(ByteOrder.LITTLE_ENDIAN);
				buf.position(cdata.length - 4);
				int finalsize = buf.getInt();

				// uncompress data
				BufferedInputStream in = new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(cdata)));
				data = new byte[finalsize];
				in.read(data);
				in.close();

				// FOR TESTING - write data to file
//				FileOutputStream out = new FileOutputStream(new File("nbtdatain.txt"));
//				out.write(data);
//				out.close();
			}

			// wrap data
			ByteBuffer buf2 = ByteBuffer.wrap(data);
			root = (TAGCompound) readEntry(true, buf2, -1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(boolean compress) {
		try {
			// if (data != null) {
			// build NBT tree
			byte[] data = new byte[0xF00000];
			ByteBuffer buf = ByteBuffer.wrap(data);
			buf.flip();
			buf.limit(data.length);
			root.write(true, buf);
			byte[] newData = new byte[buf.position()];
			System.out.println(buf.position());
			System.arraycopy(data, 0, newData, 0, newData.length);
			buf.flip();
			//
			// PrintWriter writer = new PrintWriter(new File(file.getParent(),
			// "newlevel.txt"));
			// writer.write(new String(newData));
			// writer.close();

			// compress and write data
			if (compress) {
				GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(file));
				out.write(newData);
				out.close();
			} else {
				FileOutputStream out2 = new FileOutputStream(file);
				out2.write(newData);
				out2.close();
			}

			// }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] build() {
		byte[] data = new byte[0xF00000];
		ByteBuffer buffer = ByteBuffer.wrap(data);
		root.write(true, buffer);
		byte[] newData = new byte[buffer.position()];
		System.arraycopy(data, 0, newData, 0, newData.length);
		return newData;
	}

	public TAGCompound getRootTag() {
		return root;
	}

	public void setRootTag(TAGCompound root) {
		this.root = root;
	}

	public NBTEntry<?> getEntry(String name) {
		String[] subnames = name.split("\\.");
		NBTEntry<?> parent = root;
		for (String subname : subnames) {
			if (parent == null) {
				break;
			}
			if (parent instanceof TAGCompound) {
				parent = ((TAGCompound) parent).getEntry(subname);
			} else if (parent instanceof TAGList) {
				parent = ((TAGList) parent).get(Integer.parseInt(subname));
			}
		}
		return parent;
	}

	public void setEntry(String name, Object value) {
		getEntry(name).setObjectValue(value);
	}

	private TAGCompound readEntries(String tagName, ByteBuffer buffer) {
		boolean end = false;
		TAGCompound tag = new TAGCompound(tagName);
		while (!end) {
			NBTEntry<?> entry = readEntry(true, buffer, -1);
			if (entry != null) {
				tag.addEntry(entry);
			} else {
				end = true;
			}
		}
		return tag;
	}

	private NBTEntry<?> readEntry(boolean hasName, ByteBuffer buffer, int searchID) {
		byte id = searchID != -1 ? (byte) searchID : buffer.get();
		if (id == 0) {
			return null;
		} else {
			String name = "";
			if (hasName) {
				int nameLength = (int) buffer.getChar();
				byte[] namedata = new byte[nameLength];
				if (namedata.length == 0) {
					namedata = new byte[0];
				} else {
					buffer.get(namedata);
				}
				name = new String(namedata);
			}
			switch (id) {
			case 0:
				return null;
			case 1:
				return new TAGByte(name, buffer.get());
			case 2:
				return new TAGShort(name, buffer.getChar());
			case 3:
				return new TAGInt(name, buffer.getInt());
			case 4:
				return new TAGLong(name, buffer.getLong());
			case 5:
				return new TAGFloat(name, buffer.getFloat());
			case 6:
				return new TAGDouble(name, buffer.getDouble());
			case 7:
				Byte[] byteData = new Byte[buffer.getInt()];
				for (int i = 0; i < byteData.length; i++) {
					byteData[i] = buffer.get();
				}
				return new TAGByteArray(name, byteData);
			case 8:
				byte[] valuedata = new byte[(int) buffer.getChar()];
				buffer.get(valuedata);
				return new TAGString(name, new String(valuedata));
			case 9:
				byte tagID = buffer.get();
				NBTEntry<?>[] list = new NBTEntry<?>[buffer.getInt()];
				for (int i = 0; i < list.length; i++) {
					list[i] = readEntry(false, buffer, tagID);
				}
				return new TAGList(name, list, tagID);
			case 10:
				return readEntries(name, buffer);
			case 11:
				Integer[] intData = new Integer[buffer.getInt()];
				for (int i = 0; i < intData.length; i++) {
					intData[i] = buffer.getInt();
				}
				return new TAGIntArray(name, intData);
			case 12:
				Long[] longData = new Long[buffer.getInt()];
				for (int i = 0; i < longData.length; i++) {
					longData[i] = buffer.getLong();
				}
				return new TAGLongArray(name, longData);
			}
			throw new IllegalStateException("Invalid tag type at " + buffer.position());
		}
	}

	public void printStructure() {

	}

	public void setFile(File file) {
		this.file = file;
	}

	// private void log(Object o) {
	// String s = "";
	// for (int i = 0; i < level; i++) {
	// s += " ";
	// }
	// System.out.println(s + o.toString());
	// }

}
