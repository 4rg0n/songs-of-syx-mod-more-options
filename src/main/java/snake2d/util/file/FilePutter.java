package snake2d.util.file;

import lombok.Getter;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.zip.DeflaterOutputStream;

public final class FilePutter implements Closeable{

	private final OutputStream out;
	private final ObjectOutputStream object;
	private final ByteBuffer buffer;
	// MODDED getter
	@Getter
	private final Path path;

	public FilePutter(Path path, int size) {
		buffer = ByteBuffer.allocate(size);
		this.path = path;
		out = new ByteBufferBackedOutputStream(buffer);
		try {
			object = new ObjectOutputStream(out);
			object.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {

		try {

			object.flush();
			File f = new File(""+path);
			f.createNewFile();
			FileOutputStream outfile = new FileOutputStream(f);
			
			buffer.flip();
			
			outfile.getChannel().write(buffer);
			outfile.flush();
			outfile.close();
			
		} catch (IOException e1) {
			
			throw new RuntimeException(e1);
		}
		
	}
	
	public void zip() {
		try {

			File f = new File(""+path);
			f.createNewFile();
			f.setWritable(true);
			
			
			FileOutputStream out = new FileOutputStream(f);
			DeflaterOutputStream defl = new DeflaterOutputStream(out, true);
			buffer.flip();
			
			ByteBuffer b = ByteBuffer.allocate(4);
			b.putInt(buffer.limit());
			b.flip();
			defl.write(b.array());
			defl.flush();
			
			defl.write(buffer.array(), 0, buffer.limit());
			defl.flush();
			defl.close();
			
			out.close();			
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new RuntimeException(e1);
		}
	}

	public FilePutter bool(boolean bool) {
		b((byte) (bool ? 1 :0));
		return this;
	}
	
	public void mark(String s) {
		i(s.hashCode());
	}

	public void mark(Class<?> c) {
		i(c.getName().hashCode());
	}

	public void mark(Object o) {
		i(o.getClass().getSimpleName().hashCode());
	}

	public void object(Object o) {
		try {
			object.writeObject(o);
			object.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public FilePutter i(int i) {
		writeInt(i);
		return this;
	}
	
	public FilePutter b(byte b) {
		buffer.put(b);
		return this;
	}
	
	public FilePutter l(long l) {
		buffer.putLong(l);
		return this;
	}
	
	public FilePutter ls(long[] ls) {
		for (long l : ls)
			buffer.putLong(l);
		return this;
	}
	
	public void lsE(long[] tiles) {
		i(tiles.length);
		ls(tiles);
	}
	
	public FilePutter ls(long[][] ls) {
		for (long[] l : ls)
			ls(l);
		return this;
	}

	public void writeArray(short[][] tiles) {
		for (short[] sa : tiles) {
			ss(sa);
		}
	}

	public FilePutter s(short s) {
		buffer.putShort(s);
		return this;
	}
	
	public void ss(short[] tiles) {
		for (short s : tiles)
			buffer.putShort(s);
	}
	
	public void ssE(short[] tiles) {
		i(tiles.length);
		for (short s : tiles)
			buffer.putShort(s);
	}

	public void is(int[] tiles) {
		for (int i : tiles)
			buffer.putInt(i);
	}
	
	public void isE(int[] tiles) {
		i(tiles.length);
		for (int i : tiles)
			buffer.putInt(i);
	}
	
	public void ds(double[] tiles) {
		for (double i : tiles)
			buffer.putDouble(i);
	}
	
	public void dsE(double[] tiles) {
		i(tiles.length);
		for (double i : tiles)
			buffer.putDouble(i);
	}
	

	public void fs(float[] data) {
		for (float i : data)
			buffer.putFloat(i);
	}
	
	public void fsE(float[] data) {
		i(data.length);
		for (float i : data)
			buffer.putFloat(i);
	}
	
	public void f(float f) {
		buffer.putFloat(f);
	}

	public void is(int[][] tiles) {
		
		for (int[] sa : tiles) {
			is(sa);
		}
	}
	
	public void isE(int[][] tiles) {
		i(tiles.length);
		for (int[] i : tiles)
			isE(i);
	}

	public void bs(byte[][] bytes) {
		for (byte[] sa : bytes) {
			bs(sa);
		}
	}

	public void bs(byte[] sa) {
		buffer.put(sa);
	}
	
	public void bsE(byte[] sa) {
		i(sa.length);
		buffer.put(sa);
	}

	public void writeInt(int i){
		buffer.putInt(i);
	}
	
	public FilePutter d(double d){
		buffer.putDouble(d);
		return this;
	}
	
	public int writtenInts(){
		return buffer.position()/4;
	}
	
	public int getPosition() {
		return buffer.position();
	}
	
	public void setAtPosition(int pos, int value) {
		int p = buffer.position();
		buffer.position(pos);
		i(value);
		buffer.position(p);
	}
	
	public void chars(CharSequence c) {
		i(0);
		i(c.length());
		for (int i = 0; i < c.length(); i++) {
			buffer.putShort((short) c.charAt(i));
		}
	}

	private static class ByteBufferBackedOutputStream extends OutputStream {
		ByteBuffer buf;

		ByteBufferBackedOutputStream(ByteBuffer buf) {
			this.buf = buf;
		}

		@Override
		public void write(int b) throws IOException {
			buf.put((byte) b);
		}

		@Override
		public void write(byte[] bytes, int off, int len) throws IOException {
			buf.put(bytes, off, len);
		}

	}


}
