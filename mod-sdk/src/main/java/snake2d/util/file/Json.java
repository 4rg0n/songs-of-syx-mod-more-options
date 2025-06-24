package snake2d.util.file;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.json.GameJsonStore;
import snake2d.Errors;
import snake2d.util.sets.KeyMap;
import snake2d.util.sets.LIST;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Json {

	// MODDED
	private final GameJsonStore gameJsonStore = ModSdkModule.gameJsonStore();

	private final JsonParser parser;
	private KeyMap<Boolean> testMap = new KeyMap<Boolean>();
	private static boolean untest = false;

	// TODO storing is disabled for now
	public Json(String content, String path) {
		this(content, path, false);
	}

	public Json(String content, String path, boolean doStore) {
		// MODDED
		if (doStore) {
			String storedJson = gameJsonStore.getContent(Paths.get(path));

			if (storedJson != null) {
				content = storedJson;
			}
		}

		parser = new JsonParser(content, path);
	}

	// MODDED
	public Json(Path p) {
		this(p, false);
	}

	public Json(Path p, boolean doStore) {
		String path = ""+p;

		// MODDED
		String content = null;
		if (doStore) {
			String storedJson = gameJsonStore.getContent(p);

			if (storedJson != null) {
				content = storedJson;
			}
		}

		// MODDED
		if (content != null) {
			parser = new JsonParser(content, path);
		} else {
			try {
				byte[] encoded = Files.readAllBytes(p);
				String s = new String(encoded, StandardCharsets.UTF_8);
				parser = new JsonParser(s, path);

			} catch (NoSuchFileException e2) {
				throw new Errors.DataError("File does not exist", path);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("can't open file:\n" + path + "\n make sure encoding is UTF_8\n" + e.getMessage());
			}
		}
	}

	private Json(JsonParser parser) {
		this.parser = parser;
	}


	public void checkUnused() {
		if (untest)
			return;

		for (String k : keys()) {
			if (!testMap.containsKey(k)) {
				System.err.println("unknown key: " + k + " in object at line: " + parser.linestart + ". "  + path());
				System.err.println("available: ");
				System.err.println(testMap.keysString());
				untest = true;
			}
		}
	}

	public boolean has(String key) {
		testMap.putReplace(key, true);
		return parser.test(key);
	}

	public String text(String key) {
		testMap.putReplace(key, true);
		return parser.string(key);
	}

	public String text(String key, String fallback) {
		if (!has(key))
			return fallback;
		return parser.string(key);
	}

	public String[] texts(String key) {
		testMap.putReplace(key, true);
		return parser.strings(key);
	}

	public String[] textsTry(String key) {
		if (!has(key))
			return new String[0];
		return parser.strings(key);
	}

	public String[] texts(String key, int size) {
		testMap.putReplace(key, true);
		String[] ss = parser.strings(key);
		if (ss.length != size)
			parser.throwError(" invalid length of array: " + ss.length +" Valid: " + size, key);
		return ss;
	}

	public String[] texts(String key, int min, int max) {
		testMap.putReplace(key, true);
		String[] ss = parser.strings(key);
		if (ss.length < min || ss.length > max)
			parser.throwError(" invalid length of array: " + ss.length +" Valid: " + min + "-" + max, key);
		return ss;
	}

	public Json json(String key) {
		testMap.putReplace(key, true);
		return new Json(parser.json(key));
	}

	public Json json(String key, int minKeys) {
		testMap.putReplace(key, true);
		Json j = new Json(parser.json(key));
		if (j.keys().size() < minKeys)
			error("Json contains insufficient entries. At least " + minKeys + " entries wated", key);
		return j;
	}

	public boolean jsonIs(String key) {
		testMap.putReplace(key, true);
		return parser.jsonIs(key);
	}

	public boolean textIs(String key) {
		testMap.putReplace(key, true);
		return parser.textIs(key);
	}


	public boolean jsonsIs(String key) {
		testMap.putReplace(key, true);
		return parser.jsonsIs(key);
	}

	public boolean arrayIs(String key) {
		testMap.putReplace(key, true);
		return parser.arrayIs(key);
	}

	public boolean arrayArrayIs(String key) {
		testMap.putReplace(key, true);
		return parser.arrayArrayIs(key);
	}

	public String path() {
		return parser.path;
	}


	public Json[] jsons(String key) {
		testMap.putReplace(key, true);
		JsonParser[] parsers = parser.jsons(key);
		Json[] jsons = new Json[parsers.length];
		for (int i = 0; i < jsons.length; i++)
			jsons[i] = new Json(parsers[i]);
		return jsons;
	}

	public Json[] jsons(String key, int minSize) {
		testMap.putReplace(key, true);
		Json[] js = jsons(key);
		if (js.length < minSize)
			error("Needs at least " + minSize + " entries", key);
		return js;
	}

	public Json[] jsons(String key, int minSize, int maxSize) {
		testMap.putReplace(key, true);
		Json[] js = jsons(key);
		if (js.length < minSize)
			error("Needs at least " + minSize + " entries", key);
		if (js.length >= maxSize)
			error("Needs at most " + (maxSize-1) + " entries", key);
		return js;
	}

	public double d(String key) {
		String s = value(key);
		try {
			return Double.parseDouble(s);
		}catch(Exception e) {
			parser.throwError("'" + s + "'" + " is not a valid floating point number. Format is X.X", key);
		}
		return 0;
	}

	public double d(String key, double min, double max) {
		double d = d(key);
		if (d < min || d > max)
			parser.throwError(d + " is outside of valid range: " + min + "-" + max, key);
		return d;
	}

	public double dTry(String key, double min, double max, double fallback) {
		if (has(key)) {
			return d(key, min, max);
		}
		return fallback;
	}

	public double[] ds(String key) {
		testMap.putReplace(key, true);
		String[] s = parser.values(key);
		double[] res = new double[s.length];
		for (int i = 0; i < s.length; i++) {
			try {
				res[i] = Double.parseDouble(s[i]);
			}catch(Exception e) {
				parser.throwError("'" + s[i] + "'" + " is not a valid floating point number. Format is X.X", key);
			}
		}
		return res;
	}

	public String rawContent(String key) {
		return parser.content(key);
	}

	public double[] ds(String key, int size) {
		double[] res = ds(key);
		if (res.length != size) {
			parser.throwError("invalid length: '" + res.length + "'" + " should be: " + size, key);
		}
		return res;
	}

	public int i(String key) {
		testMap.putReplace(key, true);
		String s = parser.value(key);
		try {
			return Integer.parseInt(s);
		}catch(Exception e) {
			parser.throwError("'" + s + "'" + " is not a valid integer.", key);
		}
		return 0;
	}

	public int i(String key, int min, int max) {
		int d = i(key);
		if (d < min || min >= max)
			parser.throwError(d + " is outside of valid range: " + min + "-" + max, key);
		return d;
	}

	public int i(String key, int min, int max, int fallback) {
		if (!has(key))
			return fallback;
		int d = i(key);
		if (d < min || min >= max)
			parser.throwError(d + " is outside of valid range: " + min + "-" + max, key);
		return d;
	}

	public int[] is(String key) {
		testMap.putReplace(key, true);
		String[] s = parser.values(key);
		int[] res = new int[s.length];
		for (int i = 0; i < s.length; i++) {
			try {
				res[i] = Integer.parseInt(s[i]);
			}catch(Exception e) {
				parser.throwError("'" + s[i] + "'" + " is not a valid integer", key);
			}
		}
		return res;
	}

	public String value(String key) {
		testMap.putReplace(key, true);
		return parser.value(key);
	}

	public String value(String key, String fallback) {
		if (!has(key))
			return fallback;
		return parser.value(key);
	}

	public String[] values(String key) {
		testMap.putReplace(key, true);
		return parser.values(key);
	}

	public String[] values(String key, int min, int max) {
		String[] vs = values(key);
		if (vs.length < min || vs.length >= max)
			parser.throwError(" invalid length of array. Valid: " + min + "-" + max, key);
		return vs;
	}

	public void error(String message, CharSequence key) {
		parser.throwError(message, key);
	}

	public String errorGet(String message, CharSequence key) {
		return parser.getError(message, key);
	}

	public int line(String key) {
		return parser.line(key);
	}

	public int count() {
		return parser.count();
	}

	/**
	 *
	 * @return a list of all keys in the order of which they are declared in the source
	 */
	public LIST<String> keys() {
		return parser.keys();
	}

	private static final String sTrue = "true";
	private static final String sFalse = "false";

	public boolean bool(String key) {
		String v = value(key);
		if (v.equals(sTrue))
			return true;
		if (v.equals(sFalse))
			return false;
		error("illegal value: '" + v + "' for boolean type. only true/false is valid", key);
		return false;
	}

	public boolean bool(String key, boolean fallback) {
		if (has(key))
			return bool(key);
		return fallback;
	}

	public static class KeyValue {

		public final String key;
		public final double value;

		KeyValue(String key, double value){
			this.key = key;
			this.value = value;
		}

	}

	@Override
	public String toString() {
		return parser.content();
	}

}
