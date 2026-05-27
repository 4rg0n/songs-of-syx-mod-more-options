package snake2d.util.file;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.json.GameJsonStore;
import snake2d.Errors;
import snake2d.util.file.JsonValue.JsonValueArray;
import snake2d.util.file.JsonValue.JsonValueJson;
import snake2d.util.sets.KeyMap;
import snake2d.util.sets.LIST;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class Json {

	// MODDED
	private final GameJsonStore gameJsonStore = ModSdkModule.gameJsonStore();

	private JsonValueJson data;
	private KeyMap<Boolean> testMap = new KeyMap<Boolean>();
	private static boolean untest = false;

	private Json(JsonValueJson content) {
		data = content;
	}

	public Json(Path p) {
		// MODDED
		this(p, true);
	}

	public Json(String content, String path) {
		data = new JsonValueJson(null, 0, path, content);
	}

	public Json(Path p, boolean doStore) {
		// MODDED
		if (doStore) {
			String storedJson = gameJsonStore.getContent(p);

			if (storedJson != null) {
				data = new JsonValueJson(null, 0, p.toString(), storedJson);
			} else {
				data = read(p);
			}
		} else {
			data = read(p);
		}
	}

	public Json(Path[] pp) {
		this(pp[pp.length-1]);
		for (int i = pp.length-2; i >= 0; i--) {

			JsonValueJson e = read(pp[i]);
			boolean arrayAdd = e.map.containsKey("_ARRAY_ADD");
			boolean jsonAdd = e.map.containsKey("_JSON_ADD");
			data.overwrite(e, arrayAdd, jsonAdd);

		}



	}

	private static JsonValueJson read(Path p) {
		String path = ""+p;
		try {
			byte[] encoded = Files.readAllBytes(p);
			String s = new String(encoded, StandardCharsets.UTF_8);
			return new JsonValueJson(null, 0, path, s);

		} catch (NoSuchFileException e2) {
			throw new Errors.DataError("File does not exist", path);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("can't open file:\n" + path + "\n make sure encoding is UTF_8\n" + e.getMessage());
		}
	}

	public void checkUnused() {
		if (untest)
			return;

		for (JsonValue v  : data.map.all()) {
			if (!testMap.containsKey(v.key)) {
				System.err.println("unknown key: " + v.key + " in object at line: " + v.line + ". "  + path());
				System.err.println("available: ");
				System.err.println(testMap.keysString());
				untest = true;
			}
		}
	}

	public boolean has(String key) {
		testMap.putReplace(key, true);

		return data.map.containsKey(key);
	}

	private JsonValue get(String key, String type) {

		if (!data.map.containsKey(key))
			data.throwError("Missing a " + type + " with key: " + key);
		testMap.putReplace(key, true);
		return data.map.get(key);
	}

	private String getValue(String key, String type) {

		if (!data.map.containsKey(key))
			data.throwError("Missing a " + type + " with key: " + key);
		testMap.putReplace(key, true);
		JsonValue v = data.map.get(key);
		String res = v.value();
		if (res == null) {
			data.throwError("Missing a " + type + " with key: " + key);
		}
		return res;
	}

	private String[] getValues(String key, String type) {

		if (!data.map.containsKey(key))
			data.throwError("Missing a " + type + " with key: " + key);
		testMap.putReplace(key, true);
		JsonValue v = data.map.get(key);
		String[] res = v.values();
		if (res == null) {
			data.throwError("Missing a " + type + " with key: " + key);
		}
		return res;
	}

	public String text(String key) {
		JsonValue v = get(key, "String");
		String s = v.text();
		if (s == null)
			v.throwError("Expecting a String");
		return s;
	}

	public String text(String key, String fallback) {
		if (!has(key))
			return fallback;
		return text(key);
	}

	public String[] texts(String key) {
		JsonValue v = get(key, "String Array");
		String[] s = v.texts();
		if (s == null)
			v.throwError("Expecting a string Array");
		return s;
	}

	public String[] textsTry(String key) {
		if (!has(key))
			return new String[0];
		return texts(key);
	}

	public String[] texts(String key, int size) {
		JsonValue v = get(key, "String Array");
		String[] ss = v.texts();
		if (ss == null)
			v.throwError("Expecting a string Array");

		if (ss.length != size)
			v.throwError(" invalid length of array: " + ss.length +" Valid: " + size);
		return ss;
	}

	public String[] texts(String key, int min, int max) {
		JsonValue v = get(key, "String Array");
		String[] ss = v.texts();
		if (ss == null)
			v.throwError("Expecting a string Array");
		if (ss.length < min || ss.length > max)
			v.throwError(" invalid length of array: " + ss.length +" Valid: " + min + "-" + max);
		return ss;
	}

	public Json json(String key) {
		JsonValue v = get(key, "Json Object");
		JsonValueJson s = v.json();
		if (s == null)
			v.throwError("Expecting an Object");
		return new Json(s);
	}

	public Json json(String key, int minKeys) {
		Json j = json(key);
		if (j.keys().size() < minKeys)
			error("Json contains insufficient entries. At least " + minKeys + " entries wated", key);
		return j;
	}

	public boolean jsonIs(String key) {
		return has(key) && get(key, "Object").json() != null;
	}

	public boolean textIs(String key) {
		return has(key) && get(key, "String").text() != null;
	}

	public boolean jsonsIs(String key) {
		return has(key) && get(key, "Objects").jsons() != null;
	}

	public boolean arrayIs(String key) {
		return has(key) && get(key, "Object") instanceof JsonValueArray;
	}

	public boolean arrayArrayIs(String key) {
		return has(key) && get(key, "Object").values2() != null;
	}

	public String path() {
		return data.errorPath;
	}

	public Json[] jsons(String key) {
		JsonValue v = get(key, "Object Array");
		JsonValueJson[] ss = v.jsons();
		if (ss == null)
			v.throwError("Expecting a object Array");
		Json[] res = new Json[ss.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = new Json(ss[i]);
		}
		return res;

	}

	public Json[] jsons(String key, int minSize) {

		Json[] js = jsons(key);
		if (js.length < minSize)
			get(key, "Object Array").throwError("Needs at least " + minSize + " entries");
		return js;
	}

	public Json[] jsons(String key, int minSize, int maxSize) {
		Json[] js = jsons(key);
		if (js.length < minSize)
			get(key, "Object Array").throwError("Needs at least " + minSize + " entries");
		if (js.length < minSize)
			get(key, "Object Array").throwError("Needs at least " + minSize + " entries");
		if (js.length >= maxSize)
			get(key, "Object Array").throwError("Needs at most " + (maxSize-1) + " entries");
		return js;
	}

	public double d(String key) {
		String s = getValue(key, "floating point number (0.0)");
		try {
			return Double.parseDouble(s);
		}catch(Exception e) {
			data.map.get(key).throwError("'" + s + "'" + " is not a valid floating point number. Format is X.X");
		}
		return 0;
	}

	public double d(String key, double min, double max) {
		double d = d(key);
		if (d < min || d > max)
			data.map.get(key).throwError(d + " is outside of valid range: " + min + "-" + max);
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
		String[] s = getValues(key, "floating point number array");
		double[] res = new double[s.length];
		for (int i = 0; i < s.length; i++) {
			try {
				res[i] = Double.parseDouble(s[i]);
			}catch(Exception e) {
				data.map.get(key).throwError("'" + s[i] + "'" + " is not a valid floating point number. Format is X.X");
			}
		}
		return res;
	}

	public double[] ds(String key, int size) {
		double[] res = ds(key);
		if (res.length != size) {
			data.map.get(key).throwError("invalid length: '" + res.length + "'" + " should be: " + size);
		}
		return res;
	}

	public int i(String key) {
		String s = getValue(key, "Integer number");
		try {
			return Integer.parseInt(s);
		}catch(Exception e) {
			data.map.get(key).throwError("'" + s + "'" + " is not a valid integer.");
		}
		return 0;
	}

	public int i(String key, int min, int max) {
		int d = i(key);
		if (d < min || min >= max)
			data.map.get(key).throwError(d + " is outside of valid range: " + min + "-" + max);
		return d;
	}

	public int i(String key, int min, int max, int fallback) {
		if (!has(key))
			return fallback;
		int d = i(key);
		if (d < min || min >= max)
			data.map.get(key).throwError(d + " is outside of valid range: " + min + "-" + max);
		return d;
	}

	public int[] is(String key) {
		String[] s = getValues(key, "integer number array");
		int[] res = new int[s.length];
		for (int i = 0; i < s.length; i++) {
			try {
				res[i] = Integer.parseInt(s[i]);
			}catch(Exception e) {
				data.map.get(key).throwError("'" + s[i] + "'" + " is not a valid integer");
			}
		}
		return res;
	}

	public String value(String key) {
		return getValue(key, " a value");
	}

	public String value(String key, String fallback) {
		if (!has(key))
			return fallback;
		return value(key);
	}

	public String[] values(String key) {
		return getValues(key, "value array");
	}

	public String[][] values2(String key) {
		if (!data.map.containsKey(key))
			data.throwError("Missing a value array with key: " + key);
		testMap.putReplace(key, true);
		JsonValue v = data.map.get(key);
		String[][] res = v.values2();
		if (res == null) {
			data.throwError("Missing a value array with key: " + key);
		}
		return res;
	}

	public String[] values(String key, int min, int max) {
		String[] vs = values(key);
		if (vs.length < min || vs.length >= max)
			data.map.get(key).throwError(" invalid length of array. Valid: " + min + "-" + max);
		return vs;
	}

	public void error(String message, CharSequence key) {
		data.throwError(message, ""+key);
	}

	public String errorGet(String message, CharSequence key) {
		return data.getError(message, ""+key);
	}

	public int line(String key) {
		return data.line;
	}

	/**
	 *
	 * @return a list of all keys in the order of which they are declared in the source
	 */
	public LIST<String> keys() {
		return data.keys;
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
		return data.toString();
	}

//	public static void main(String[] args) throws IOException {
//		for (Path p : Files.walk(new File("C:\\Users\\jakob\\Desktop\\jakob\\syx\\code\\syx71\\Syx\\zipdata\\data\\assets\\init").toPath()).toList()) {
//			File f = p.toFile();
//			if (!f.isDirectory()) {
//
//				System.out.println(p.toAbsolutePath());
//				Json2 j = new Json2(p);
//				System.out.println(j.toString());
//			}
//		}
//
//		ArrayListGrower<Path> pps = new ArrayListGrower<Path>();
//
//		for (Path p : Files.walk(new File("C:\\Users\\jakob\\Desktop\\jakob\\syx\\code\\syx71\\Syx\\tool\\tmp").toPath()).toList()) {
//			File f = p.toFile();
//
//			if (!f.isDirectory()) {
//				System.out.println(p.toAbsolutePath());
//				pps.add(p);
//
//			}
//
//		}
//
//		Path[] pp = new Path[pps.size()];
//		for (int i = 0; i < pp.length; i++)
//			pp[i] = pps.get(i);
//
//		System.out.println(new Json(pp).toString());
//
//
//	}

}
