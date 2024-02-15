package script;

import game.GAME.GameResource;
import game.Profiler;
import init.paths.PATHS;
import script.SCRIPT.SCRIPT_INSTANCE;
import snake2d.Errors;
import snake2d.LOG;
import snake2d.MButt;
import snake2d.Renderer;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import snake2d.util.sets.ArrayList;
import snake2d.util.sets.KeyMap;
import snake2d.util.sets.LIST;
import snake2d.util.sets.LinkedList;
import util.gui.misc.GBox;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public class ScriptEngine extends GameResource {

	private LIST<Script> loads = new ArrayList<Script>(0);


	public ScriptEngine() {

		LIST<ScriptLoad> loads = ScriptLoad.getAll();
		LinkedList<Script> all = new LinkedList<>();
		for (ScriptLoad l : loads) {
			try {
				all.add(new Script(l, null));
				l.script.initBeforeGameCreated();
			}catch(Exception e) {
				error(l, e);
			}
		}
		this.loads = new ArrayList<Script>(all);

	}

	public void initAfter() {
		for (Script s : loads) {
			try {
				s.ins = s.load.script.createInstance();
			}catch(Exception e) {
				error(s.load, e);
			}
		}
	}



	public String[] currentScripts() {
		String[] scripts = new String[loads.size()];
		int i = 0;
		for (Script l : loads)
			scripts[i++] = l.load.file;
		return scripts;
	}

	private void error(ScriptLoad l, Exception e) {

		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.append("error in script " + l.className);
		out.append(System.lineSeparator());
		e.printStackTrace(out);

		throw new Errors.DataError(writer.toString(), l.file);

	}

//	public void set(LIST<ScriptLoad> scripts) {
//		LinkedList<Script> res = new LinkedList<>();
//		for (ScriptLoad l : scripts) {
//			LOG.ln("adding script : " + l.script.name());
//			res.add(new Script(l, l.script.initAfterGameCreated()));
//		}
//		this.scripts = new ArrayList<Script>(res);
//	}

//	public LIST<ScriptLoad> makeCurrent(){
//		LinkedList<ScriptLoad> res = new LinkedList<>();
//		for (Script s : scripts)
//			if (s.load.file != null)
//				res.add(s.load);
//		return res;
//	}

	@Override
	protected void save(FilePutter file) {
		file.mark(this);
		file.i(loads.size());
		for (Script s : loads) {
			file.chars(s.load.file);
			file.chars(s.load.className);
		}
		for (Script s : loads) {
			file.chars(s.load.file);
			file.chars(s.load.className);
			int pos = file.getPosition();
			file.i(0);
			s.ins.save(file);
			int size = (file.getPosition()-pos)-4;
			file.setAtPosition(pos, size);
		}
	}

	@Override
	public void load(FileGetter file) throws IOException {
		file.check(this);
		int am = file.i();


		HashMap<String, Script> alreadyLoaded = new HashMap<>();
		for (Script l : this.loads) {
			alreadyLoaded.put(l.load.file+l.load.className, l);
		}

		KeyMap<ScriptLoad> map = new KeyMap<>();
		{

			for (ScriptLoad l : ScriptLoad.getAll()) {
				map.put(l.file+l.className, l);
			}

			for (int i = 0; i < am; i++) {
				String nfile = file.chars();
				String nclass = file.chars();
				String key = nfile+nclass;
				if (!map.containsKey(key) && PATHS.SCRIPT().jar.exists(nfile)) {
					LIST<ScriptLoad> ll = ScriptLoad.get(nfile);
					for (ScriptLoad l : ll) {
						if (l.className.equals(nclass)) {
							map.put(l.file+l.className, l);
							l.script.initBeforeGameCreated();
							break;
						}
					}
				}
			}

		}

		while(am > 0) {
			am--;
			String nfile = file.chars();
			String nclass = file.chars();
			String key = nfile+nclass;
			int size = file.i();
			int position = file.getPosition();

			if (map.containsKey(key)) {
				SCRIPT_INSTANCE ins;
				if (alreadyLoaded.containsKey(key)) {
					Script script = alreadyLoaded.get(key);
					ins = script.ins;
				} else {
					ScriptLoad ld = map.get(key);
					ins = ld.script.createInstance();
					alreadyLoaded.put(key, new Script(ld, ins));
				}

				ins.load(file);
				if (size != (file.getPosition()-position)) {
					LOG.ln("Unable to load script. Was saved with + " + size + " bytes, but read " + (file.getPosition()-position) + " " + ins.hashCode());
					file.setPosition(position+size);
					if (ins.handleBrokenSavedState()) {
						LOG.ln("Script wants to carry on anyway, so be it.");
					}
				}
			} else {
				LOG.ln("Unable to find script. Skipping. " + size + " " + key);
				for (int i = 0; i < size; i++)
					file.b();
			}
		}

		this.loads = new ArrayList<Script>(alreadyLoaded.values());
	}

	@Override
	protected void update(float ds, Profiler prof) {
		for(Script s : loads)
			try {
				s.ins.update(ds);
			}catch(Exception e) {
				error(s.load, e);
			}
	}

	public void hoverTimer(double mouseTimer, GBox text) {
		for(Script s : loads)
			try {
				s.ins.hoverTimer(mouseTimer, text);
			}catch(Exception e) {
				error(s.load, e);
			}
	}

	public void render(Renderer r, float ds) {
		for(Script s : loads)
			try {
				s.ins.render(r, ds);
			}catch(Exception e) {
				error(s.load, e);
			}
	}

	public void mouseClick(MButt button) {
		for(Script s : loads)
			try {
				s.ins.mouseClick(button);
			}catch(Exception e) {
				error(s.load, e);
			}
	}

	public void hover(COORDINATE mCoo, boolean mouseHasMoved) {
		for(Script s : loads)
			try {
				s.ins.hover(mCoo, mouseHasMoved);
			}catch(Exception e) {
				error(s.load, e);
			}
	}

	private static class Script {

		private final ScriptLoad load;
		private SCRIPT_INSTANCE ins;

		Script(ScriptLoad load, SCRIPT_INSTANCE ins){
			this.load = load;
			this.ins = ins;
		}

	}


}
