package snake2d;

import com.github.argon.sos.moreoptions.game.DumpLogsException;
import lombok.Setter;
import snake2d.util.misc.ERROR_HANDLER;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.FileSystems;

import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glGetInteger;

public final class Errors {
	
	private static Errors i;
	@Setter
	private static ERROR_HANDLER handler;
	private final Logger out = new Logger(System.out);
	private final Logger err = new Logger(System.err);
	
	public static void init(ERROR_HANDLER handler) {
		if (i != null) {
			throw new RuntimeException("handler already setup");
		}
		i = new Errors(handler);
	}
	
	private Errors(ERROR_HANDLER handler) {
		Errors.handler = handler;
		try {
			System.setOut(new PrintStream(out));
			System.setErr(new PrintStream(err));
		} catch (Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException("can't set stdOut");
    	}
		
	}
	
	public static void handle(Throwable e) {

		if (e == null) {
			check();
			return;
		}
		
		e.printStackTrace();
		diagnozeMem();
		if (handler == null)
			return;
		
		String dump = getDumpFile();

		if(e instanceof DataError) {
			handler.handle((DataError) e, dump);
		}else if (e instanceof GameError) {
			handler.handle((GameError) e, dump);
		}else {
			handler.handle(e, dump);
		}
		
	}

	/**
	 * MODDED
	 */
	public static void forceDump(String message) {
		if (handler == null) {
			return;
		}

		String n = System.getProperty("line.separator");
		String fin = i.out.data.toString();
		String err = i.err.data.toString();

		fin = fin.replaceAll("\"", "'");
		err = err.replaceAll("\"", "'");

		String dump = ""
			+ n + "|-------------------|"
			+ n + "|     ERROR LOG     |"
			+ n + "|-------------------|"
			+ n + err
			+ n + ""
			+ n + "|-------------------|"
			+ n + "|      STD OUT      |"
			+ n + "|-------------------|"
			+ n + fin;

		handler.handle(new DumpLogsException(message), dump);
	}
	
	public static void check() {
		
		if (hasDump()) {
			String dd = i.err.data.toString();
			String dump = getDumpFile();
			if (dump != null && handler != null)
				handler.handle(dd, dump);
		}
		
		
		
	}
	
	private static boolean hasDump() {
		return i.err.data.length() != 0;
	}
	
	private static String getDumpFile() {
		if (i.err.data.length() != 0) {
			
			String n = System.getProperty("line.separator");
			String fin = i.out.data.toString();
			String err = i.err.data.toString();

			fin = fin.replaceAll("\"", "'");
			err = err.replaceAll("\"", "'");

			String d = ""
					+ n + "|-------------------|"
					+ n + "|     ERROR LOG     |"
					+ n + "|-------------------|" 
					+ n + err
					+ n + ""
					+ n + "|-------------------|"
					+ n + "|      STD OUT      |"
					+ n + "|-------------------|" 
					+ n + fin;
			
			
			
			i.err.data = new StringBuffer();
			i.out.data = new StringBuffer();
			return d;
			
		}
		i.err.data = new StringBuffer();
		i.out.data = new StringBuffer();
		return null;
	}
	
	public static class DataError extends RuntimeException {

		private static final long serialVersionUID = 1L;
		public final String error;
		public final String path;

		public DataError(String error, String path) {
			super(error + " path: " + path);
			this.error = error;
			this.path = path;
		}

		public DataError(String error, java.nio.file.Path sourcePath) {
			this(error, path(sourcePath));
		}
		
		private static String path(java.nio.file.Path sourcePath) {
			if (sourcePath.getFileSystem() != FileSystems.getDefault()) {
				return sourcePath.getFileSystem() + "->" + sourcePath.toAbsolutePath();
			}
			return ""+sourcePath.toAbsolutePath();
		}
		
		public DataError(String error) {
			this(error, "");
		}

	}
	
	public static class GameError extends RuntimeException {

		private static final long serialVersionUID = 1L;
		public final String error;

		public GameError(String error) {
			super(error);
			this.error = error;
		}

	}
	
	private static class Logger extends OutputStream{

		private StringBuffer data = new StringBuffer();
		private final PrintStream out;
		
		private Logger(PrintStream out) {
			this.out = out;
		}
		
		@Override
		public synchronized void write(int b) throws IOException {
			
			out.write(b);
			if (data.length() > 250000)
				data.setLength(0);
			data.append((char) b);
			
		}
		
	}
	
	private static void diagnozeMem() {
		
		int mb = 1014*1024;
		try {
			System.err.println("Time until crash: " + (CORE.getUpdateInfo() == null ? 0 : CORE.getUpdateInfo().getSecondsSinceFirstUpdate()));
			System.err.println("MEM DIAGNOSE");
			Runtime run = Runtime.getRuntime();
			// available memory
			System.err.println("--JRE Memory");
			System.err.println("--JRE Total: " + run.totalMemory() / mb);
			System.err.println("--JRE Free: " + run.freeMemory() / mb);
			System.err.println("--JRE Used: "
					+ (run.totalMemory() - run.freeMemory()) / mb);
			System.err.println("--JRE Max: " + run.maxMemory() / mb);
			System.gc();
			System.err.println("--JRE Memory After GC");
			System.err.println("--JRE Total: " + run.totalMemory() / mb);
			System.err.println("--JRE Free: " + run.freeMemory() / mb);
			System.err.println("--JRE Used: "
					+ (run.totalMemory() - run.freeMemory()) / mb);
			System.err.println("--JRE Max: " + run.maxMemory() / mb);
			int i;
			System.err.println("NVIDIA: ");
			i = glGetInteger(org.lwjgl.opengl.NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_DEDICATED_VIDMEM_NVX);
			System.err.println("--GPU Dedicated: " + i);
			i = glGetInteger(org.lwjgl.opengl.NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_TOTAL_AVAILABLE_MEMORY_NVX);
			System.err.println("--GPU Total Available: " + i);
			i = glGetInteger(org.lwjgl.opengl.NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX);
			System.err.println("--GPU Current Available: " + i);
			i = glGetInteger(org.lwjgl.opengl.NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_EVICTION_COUNT_NVX);
			System.err.println("--GPU Evictions: " + i);
			i = glGetInteger(org.lwjgl.opengl.NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_EVICTED_MEMORY_NVX);
			System.err.println("--GPU Evicted: " + i);
			
			System.err.println("ATI: ");
			i = glGetInteger(org.lwjgl.opengl.ATIMeminfo.GL_RENDERBUFFER_FREE_MEMORY_ATI);
			System.err.println("--Renderbuffer Free: " + i);
			i = glGetInteger(org.lwjgl.opengl.ATIMeminfo.GL_TEXTURE_FREE_MEMORY_ATI);
			System.err.println("--Texture Free: " + i);
			i = glGetInteger(org.lwjgl.opengl.ATIMeminfo.GL_VBO_FREE_MEMORY_ATI);
			System.err.println("--Vbo Free: " + i);
			glGetError();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}