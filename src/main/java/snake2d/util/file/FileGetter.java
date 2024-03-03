package snake2d.util.file;

import lombok.Getter;

import java.io.*;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.zip.InflaterInputStream;

public final class FileGetter {

	// MODDED getter
	@Getter
	private final String path;
	private final ObjectInputStream object;

	private final ByteBuffer buffer;

	public FileGetter(Path path) throws IOException {
		this.path = "" + path;
		File f = new File("" + path);
		if (!f.isFile() || f.length() == 0) {
			throw new IOException(
					"file is corrupt. Try replacing it from an uncorrupted game folder, or delete it and see what happens "
							+ f.isFile() + " " + f.length() + " " + path);
		}

		if (f.length() > Integer.MAX_VALUE)
			throw new RuntimeException("file too large to read");

		buffer = ByteBuffer.allocate((int) f.length());

		try {
			FileInputStream in = new FileInputStream(f);
			FileChannel channel = in.getChannel();
			channel.read(buffer);
			channel.close();
			in.close();
			buffer.flip();
			InputStream inn = new ByteBufferBackedInputStream(buffer);
			object = new ObjectInputStream(inn);

		} catch (Exception e) {
			f.delete();
			throw new RuntimeException(e);
		}
	}

	public FileGetter(Path path, boolean zipped) throws IOException {
		this.path = "" + path;
		File f = new File("" + path);
		if (!Files.exists(path) || !Files.isReadable(path)) {
			throw new IOException(
					" file is corrupt. Try replacing it from an uncorrupted game folder, or delete it and see what happens "
							+ path);
		}

		if (f.length() > Integer.MAX_VALUE)
			throw new RuntimeException("file too large to read");

		try {
			InputStream in = Files.newInputStream(path);
			InflaterInputStream zip = new InflaterInputStream(in);
			byte[] data = new byte[4];
			zip.read(data);
			ByteBuffer size = ByteBuffer.wrap(data);
			data = new byte[size.getInt()];
			int kk = 0;
			while (kk >= 0 && kk < data.length) {
				kk += zip.read(data, kk, data.length - kk);
			}
			zip.close();
			in.close();
			buffer = ByteBuffer.wrap(data);
			InputStream inn = new ByteBufferBackedInputStream(buffer);
			object = new ObjectInputStream(inn);

		} catch (Exception e) {
			f.delete();
			throw new RuntimeException(e);
		}
	}

	public void close() {
		try {
			object.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
	
	public Object object() throws IOException {
		try {
			return object.readObject();
		} catch (Exception e) {
			throw new IOException("A code artefact is missing in the current configuration of the game. The usual suspect is a mod version change. Contact the modder in question.", e);
		}
	}

	public void check(String s) throws IOException {
		int i = i();
		int h = s.hashCode();
		if (i != h) {
			// int k = 0;
			// int pp = 0;
			// while(buffer.remaining() > 4) {
			// int q = i();
			// if (q != 0)
			// k++;
			// pp++;
			// }
			//
			throw new IOException("corrupt data, expecting : " + s + " (" + h + ", " + i + "), " + path);
		}
	}

	public void check(Object o) throws IOException {
		check(o.getClass().getSimpleName());
	}

	public void readArray(short[][] shorts) throws IOException {

		for (short[] s : shorts)
			ss(s);

	}

	public void ss(short[] shorts) throws IOException {

		for (int i = 0; i < shorts.length; i++)
			shorts[i] = s();
	}
	
	public void ssE(short[] data) throws IOException {
		int l = i();
		if (l != data.length) {
			short[] b = new short[l];
			ss(b);
			for (int i = 0; i < l && i < data.length; i++)
				data[i] = b[i];
		} else
			ss(data);
	}

	public void is(int[] data) throws IOException {

		for (int i = 0; i < data.length; i++)
			data[i] = i();
	}

	public void isE(int[] data) throws IOException {
		int l = i();
		if (l != data.length) {
			int[] b = new int[l];
			is(b);
			for (int i = 0; i < l && i < data.length; i++)
				data[i] = b[i];
		} else
			is(data);

	}

	public void ds(double[] data) throws IOException {

		for (int i = 0; i < data.length; i++)
			data[i] = d();
	}

	public void dsE(double[] data) throws IOException {
		int l = i();
		if (l != data.length) {
			double[] b = new double[l];
			ds(b);
			for (int i = 0; i < l && i < data.length; i++)
				data[i] = b[i];
		} else
			ds(data);

	}

	public void fs(float[] data) {
		for (int i = 0; i < data.length; i++)
			data[i] = f();
	}

	public void fsE(float[] data) throws IOException {
		int l = i();
		if (l != data.length) {
			float[] b = new float[l];
			fs(b);
			for (int i = 0; i < l && i < data.length; i++)
				data[i] = b[i];
		} else
			fs(data);
	}

	public float f() {
		return buffer.getFloat();
	}

	public void is(int[][] data) throws IOException {

		for (int[] i : data)
			is(i);
	}

	public void isE(int[][] data) throws IOException {
		int l = i();
		for (int i = 0; i < l; i++) {
			if (i < data.length) {
				isE(data[i]);
			} else {
				isE(new int[0]);
			}
		}

	}

	public void readArray(int[][] shorts) throws IOException {

		for (int[] s : shorts)
			is(s);

	}

	public void bs(byte[][] bytes) throws IOException {
		for (byte[] s : bytes)
			bs(s);
	}

	public void bs(byte[] bytes) throws IOException {
		try {
			buffer.get(bytes);
		} catch (BufferUnderflowException e) {
			throw new RuntimeException(e);
		}
	}

	public byte b() throws IOException {
		if (buffer.remaining() < 1)
			throw new IOException();
		return buffer.get();
	}

	public void bsE(byte[] bytes) throws IOException {
		int l = i();
		if (l != bytes.length) {
			byte[] b = new byte[l];
			bs(b);
			for (int i = 0; i < l && i < bytes.length; i++)
				bytes[i] = b[i];
		} else
			bs(bytes);

	}

	/**
	 * 
	 * @return last int of file. Will rewind the file
	 */
	public int lastInt() throws IOException {
		if (buffer.remaining() <= 4)
			throw new IOException();
		int res = buffer.getInt(buffer.limit() - 4);
		return res;
	}

	public int lastInt(int off) throws IOException {
		if (buffer.remaining() <= 4)
			throw new IOException();
		int res = buffer.getInt(buffer.limit() - 4 * off);
		return res;
	}

	public int remainingInts() {
		return buffer.remaining() / 4;
	}

	public int currentInt() {
		return buffer.position() / 4;
	}

	public int i() throws IOException {
		if (buffer.remaining() < 4)
			throw new IOException();
		return buffer.getInt();
	}

	public double d() throws IOException {
		if (buffer.remaining() <= 8)
			throw new IOException();
		return buffer.getDouble();
	}

	public String chars() throws IOException {
		test(0);
		int k = i();
		if (k < 0)
			throw new IOException();
		if (k > 1000000)
			throw new IOException();
		char[] chars = new char[k];
		for (int i = 0; i < chars.length; i++) {
			if (!buffer.hasRemaining())
				throw new IOException();
			chars[i] = (char) (buffer.getShort() & 0x0FF);
		}
		return new String(chars);
	}

	public boolean test(int i) throws IOException {
		int q = i();
		if (q == i)
			return true;
		buffer.position(buffer.position() - 4);
		return false;
	}

	public short s() throws IOException {
		if (buffer.remaining() < 2)
			throw new IOException();
		return buffer.getShort();
	}

	private static class ByteBufferBackedInputStream extends InputStream {

		ByteBuffer buf;

		ByteBufferBackedInputStream(ByteBuffer buf) {
			this.buf = buf;
		}

		@Override
		public int read() {
			if (!buf.hasRemaining()) {
				return -1;
			}
			return buf.get();
		}

		@Override
		public int read(byte[] bytes, int off, int len) {
			len = Math.min(len, buf.remaining());
			buf.get(bytes, off, len);
			return len;
		}
	}

	public boolean bool() throws IOException {
		return b() == 1;
	}

	public long l() {
		return buffer.getLong();
	}

	public void ls(long[] ls) {
		for (int i = 0; i < ls.length; i++)
			ls[i] = buffer.getLong();
	}
	
	public boolean lsE(long[] ls) throws IOException {
		int l = i();
		if (l != ls.length) {
			ls(new long[l]);
			Arrays.fill(ls, 0l);
			return true;
		}else {
			ls(ls);
			return false;
		}
		
	}

	public void ls(long[][] ls) {
		for (int i = 0; i < ls.length; i++)
			ls(ls[i]);
	}

	public int getPosition() {
		return buffer.position();
	}

	public void setPosition(int pos) {
		buffer.position(pos);
	}

}
