package menu;

import game.GAME;
import game.boosting.BOOSTABLES;
import game.boosting.Boostable;
import game.faction.FACTIONS;
import init.sprite.UI.UI;
import init.text.D;
import menu.GUI.COLORS;
import menu.GUI.Screener;
import script.ScriptEngine;
import script.ScriptLoad;
import snake2d.CORE_STATE;
import snake2d.CORE_STATE.Constructor;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sets.KeyMap;
import snake2d.util.sets.LinkedList;
import snake2d.util.sets.Tuple.TupleImp;
import util.data.GETTER.GETTER_IMP;
import util.data.INT.IntImp;
import util.gui.misc.GText;
import util.gui.table.GScrollRows;
import util.info.INFO;
import view.main.VIEW;

class ScRandom extends GuiSection implements SC{

	public final KeyMap<ScriptLoad> scripts = new KeyMap<>();
	public final KeyMap<TupleImp<Double, Boolean>> BOOSTS = new KeyMap<>();
	static CharSequence ¤¤name = "¤random game";
	private static CharSequence ¤¤normal = "¤normal";
	private static CharSequence ¤¤easy = "¤easy";
	private static CharSequence ¤¤hard = "¤hard";
	private static CharSequence ¤¤titleDisabled = "¤Title unlocking will be disabled.";
	private static CharSequence ¤¤Scripts = "Scripts";
	private static CharSequence ¤¤Difficulty = "Difficulty";
	private static CharSequence ¤¤go = "go!";
	static {
		D.ts(ScRandom.class);
	}
	
	ScRandom(Menu menu){

		
		Screener screen = new Screener(¤¤name, GUI.labelColor) {
			
			@Override
			protected void back() {
				menu.switchScreen(menu.main);
			}
		};
		
		add(screen);
		
		

		CLICKABLE b = new Screener.ScreenButton(¤¤go) {
			@Override
			protected void clickA() {
				menu.start(new Constructor() {
					
					@Override
					public CORE_STATE getState() {
						
						String[] sc = new String[scripts.all().size()];
						int si = 0;
						for (ScriptLoad l : scripts.all())
							sc[si++] = l.key;
						new GAME(sc);
						CORE_STATE s = new VIEW();
						
						for (TupleImp<Double, Boolean> i : BOOSTS.all()) {
							if ((i.b == false && i.a > 0) || (i.b == true && i.a > 1)) {
								GAME.achieve(false);
								break;
							}
							
						}
						for (String k : BOOSTS.keys()) {
							TupleImp<Double, Boolean> i = BOOSTS.get(k);
							if ((i.b == false && i.a != 0) || (i.b == true && i.a != 1)) {
								FACTIONS.player().bonusesCustom.add(k, i.a, i.b);
							}
							
						}
						
						return s;
					}
				});
			}
		};
		
		screen.addButt(b);
		
		GETTER_IMP<CharSequence> hText = new GETTER_IMP<>();
		
		GuiSection options = new GuiSection(); 
		
		options.add(new Boosts(300, hText));
		options.addRelBody(64, DIR.E, new Scripts(300, hText));
		
		options.addRelBody(16, DIR.S, new RenderImp(Screener.inner.width()-4, 64) {
			
			@Override
			public void render(SPRITE_RENDERER r, float ds) {
				COLOR.WHITE150.bind();
				CharSequence s = hText.get();
				hText.set(null);
				if (s != null) {
					UI.FONT().M.renderC(r, body().cX(), body().cY(), s);
				}
				COLOR.unbind();
				COLOR.YELLOW100.bind();
				for (TupleImp<Double, Boolean> i : BOOSTS.all()) {
					if ((i.b == false && i.a > 0) || (i.b == true && i.a > 1)) {
						UI.FONT().M.renderC(r, body().cX(), body().cY()+32, ¤¤titleDisabled);
						//UI.FONT().M.render(r, ¤¤titleDisabled, body().x1(), body().y1(), body().width(), 1);
						break;
					}
				}
				
				COLOR.unbind();
			}
		});
		

		options.body().centerIn(body());
		
		add(options);
		
	}
	
	@Override
	public boolean back(Menu menu) {
		menu.switchScreen(menu.main);
		return true;
	}
	
	final class Scripts extends GuiSection {

		Scripts(int height, GETTER_IMP<CharSequence> hText){
			LinkedList<RENDEROBJ> rows = new LinkedList<>();
			for (ScriptLoad l : ScriptEngine.getAll()) {
				if (!l.script.isSelectable())
					continue;
				CharSequence name = l.script.name();
				CharSequence desc = l.script.desc();
				
				CLICKABLE c = new GUI.CheckBox(name) {
					@Override
					public boolean hover(COORDINATE mCoo) {
						if (super.hover(mCoo)) {
							hText.set(desc);
							return true;
						}
						return false;
					}
					
					@Override
					protected void clickA() {

						if (scripts.containsKey(l.className)) {
							scripts.remove(l.className);
							selectedSet(false);
						}else {
							scripts.put(l.className, l);
							selectedSet(true);
						}
					}
				
					
				};

				// MODDED enable scripts automatically when force loaded
				c.selectedSet(l.script.forceInit());
				rows.add(c);
			}
			
			add(new GScrollRows(rows, height).view());
			addRelBody(4, DIR.N, new Sprite(UI.FONT().H2.getText(¤¤Scripts), COLORS.label));
			
		}
		
	}
	
	final class Boosts extends GuiSection {
		
		Boosts(int height, GETTER_IMP<CharSequence> hText){
			
			Boostable[] boos;
			double[] values;
			
			LinkedList<RENDEROBJ> rows = new LinkedList<>();
			
			
			boos = new Boostable[] {
				BOOSTABLES.CIVICS().FURNITURE,
				BOOSTABLES.CIVICS().MAINTENANCE,
				BOOSTABLES.CIVICS().SPOILAGE,
			};
			values = new double[] {
					-0.6, -0.4, -0.2, -0.1, 0, 0.25, 0.5, 1, 2
			};
			addLine(rows, boos, values, hText, false);
			
			boos = new Boostable[] {
				BOOSTABLES.BEHAVIOUR().HAPPI,
				BOOSTABLES.CIVICS().RAID_SECURITY,
			};
			values = new double[] {
				0.4, 0.6, 0.8, 1.0, 1.2, 1.5, 2.0,
			};
			
			addLine(rows, boos, values, hText, true);
			
			add(new GScrollRows(rows, height).view());
			
			addRelBody(4, DIR.N, new Sprite(UI.FONT().H2.getText(¤¤Difficulty), COLORS.label));
		}
		
		private void addLine(LinkedList<RENDEROBJ> rows, Boostable[] boos, double[] values, GETTER_IMP<CharSequence> hText, boolean isMul) {
			
			int z = values.length/2;
			
			final int zero = z;
			for (Boostable bo : boos) {
				IntImp intt = new IntImp(0, values.length-1) {
					@Override
					public void set(int t) {
						super.set(t);
						BOOSTS.putReplace(bo.key, new TupleImp<Double, Boolean>(values[t], isMul));
					}
				};
				intt.set(zero);
				INFO info = bo;
				CLICKABLE c = new GUI.OptionLine(intt, info.name) {
					private CharSequence desc = info.desc;
					@Override
					protected void setValue(GText str) {
						
						str.clear();
						if (intt.get() < zero) {
							str.clear().add(¤¤hard);
							str.errorify();
							for (int i = 0; i < zero-intt.get(); i++) {
								str.add('+');
							}
						}else if (intt.get() > zero) {
							str.clear().add(¤¤easy);
							str.normalify2();
							for (int i = zero; i < intt.get(); i++) {
								str.add('+');
							}
						}else {
							str.color(COLOR.WHITE85);
							str.add(¤¤normal);
						}
					}
					@Override
					public boolean hover(COORDINATE mCoo) {
						if (super.hover(mCoo)) {
							hText.set(desc);
							return true;
						}
						return false;
					}
				};
				rows.add(c);
			}
		}
	}

	
}
