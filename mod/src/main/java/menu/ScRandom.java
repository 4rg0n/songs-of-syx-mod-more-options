package menu;

import game.GAME;
import game.boosting.BOOSTABLES;
import game.boosting.Boostable;
import game.faction.FACTIONS;
import init.sprite.UI.UI;
import init.text.D;
import menu.GUI.COLORS;
import menu.GUI.Shadower;
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
import snake2d.util.sets.ArrayListGrower;
import snake2d.util.sets.KeyMap;
import snake2d.util.sets.LinkedList;
import snake2d.util.sets.Tuple.TupleImp;
import util.data.GETTER.GETTER_IMP;
import util.data.INT.IntImp;
import util.gui.misc.GText;
import util.gui.table.GScrollRows;
import util.info.INFO;
import view.menu.MenuScreen;

class ScRandom extends Shadower implements SC{

	public final KeyMap<ScriptLoad> scripts = new KeyMap<>();
	private final KeyMap<TupleImp<Double, Boolean>> BOOSTS = new KeyMap<>();
	public final KeyMap<IntImp> bInts = new KeyMap<>();
	private CharSequence desc;
	static CharSequence ¤¤name = "¤random game";
	private static CharSequence ¤¤normal = "¤normal";
	private static CharSequence ¤¤easy = "¤easy";
	private static CharSequence ¤¤hard = "¤hard";
	private static CharSequence ¤¤titleDisabled = "¤Title unlocking is disabled when easier difficulty is selected.";
	private static CharSequence ¤¤Scripts = "Scripts";
	private static CharSequence ¤¤Difficulty = "Difficulty";
	private static CharSequence ¤¤go = "go!";
	
	private static CharSequence ¤¤snormal = "Normal";
	private static CharSequence ¤¤snormalD = "Play the game like it's designed to play. Expect to spend hundreds of hours learning it.";
	
	private static CharSequence ¤¤swarrior = "Warrior";
	private static CharSequence ¤¤swarriorD = "Play the game with a focus of battle and conquest.";
	
	private static CharSequence ¤¤trader = "Trader";
	private static CharSequence ¤¤traderD = "Focuses on building one's economy on trade and diplomatic relations.";
	
	private static CharSequence ¤¤homestead = "Homestead";
	private static CharSequence ¤¤homesteadD = "Focus on self-sufficiency";
	
	private static CharSequence ¤¤ssandbox = "Sandbox";
	private static CharSequence ¤¤ssandboxD = "Relax, build and conquer the world the way you want.";
	
	private static CharSequence ¤¤despot = "Despot";
	private static CharSequence ¤¤despotD = "For the few who have what it takes to be one of the greats.";
	
	static {
		D.ts(ScRandom.class);
	}
	
	ScRandom(Menu menu){

		
		
		
		MenuScreen screen = new MenuScreen(¤¤name, GUI.labelColor) {
			
			@Override
			protected void back() {
				menu.switchScreen(menu.main);
			}
		};
		
		add(screen);
		

		CLICKABLE b = new MenuScreen.ScreenButton(¤¤go) {
			@Override
			protected void clickA() {
				menu.start(new Constructor() {
					
					@Override
					public CORE_STATE getState() {
						
						String[] sc = new String[scripts.all().size()];
						int si = 0;
						for (ScriptLoad l : scripts.all())
							sc[si++] = l.key;
						
						CORE_STATE s = GAME.create(sc);
						
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
		
		options.addRelBody(8, DIR.S, new RenderImp(MenuScreen.inner.width()-4, 32) {
			
			@Override
			public void render(SPRITE_RENDERER r, float ds) {
				COLOR.WHITE150.bind();
				CharSequence s = hText.get();
				hText.set(null);
				if (s != null) {
					UI.FONT().M.renderC(r, body().cX(), body().cY(), s);
				}
				COLOR.unbind();
			}
		});
		
		ArrayListGrower<Setting> setts = new ArrayListGrower<Setting>();
		Setting sett;
		sett = new Setting(¤¤snormal, ¤¤snormalD);
		setts.add(sett);
		
		sett = new Setting(¤¤ssandbox, ¤¤ssandboxD);
		for (String i : bInts.keys()) {
			sett.s(i, 1);
		}
		setts.add(sett);
		

		
		sett = new Setting(¤¤swarrior, ¤¤swarriorD);
		sett.s(BOOSTABLES.CIVICS().RAID_SECURITY, -2);
		sett.s(BOOSTABLES.CIVICS().GOV, 3);
		sett.s(BOOSTABLES.CIVICS().bOpinion, -1);
		sett.s(BOOSTABLES.CIVICS().PASIFISM, -1);
		sett.s(BOOSTABLES.BEHAVIOUR().LOYALTY, 1);
		setts.add(sett);
		
		sett = new Setting(¤¤trader, ¤¤traderD);
		sett.s(BOOSTABLES.CIVICS().RAID_SECURITY, 1);
		sett.s(BOOSTABLES.CIVICS().GOV, -2);
		sett.s(BOOSTABLES.CIVICS().bOpinion, 2);
		sett.s(BOOSTABLES.CIVICS().DEFALTION, 2);
		sett.s(BOOSTABLES.CIVICS().INNOVATION, -2);
		setts.add(sett);
		
		sett = new Setting(¤¤homestead, ¤¤homesteadD);
		sett.s(BOOSTABLES.CIVICS().GOV, -2);
		sett.s(BOOSTABLES.CIVICS().bOpinion, -2);
		sett.s(BOOSTABLES.CIVICS().PASIFISM, 2);
		sett.s(BOOSTABLES.BEHAVIOUR().HAPPI, 2);
		setts.add(sett);
		
		sett = new Setting(¤¤despot, ¤¤despotD);
		for (String i : bInts.keys()) {
			sett.s(i, -1);
		}
		setts.add(sett);
		
		{
			GuiSection s = new GuiSection();
			for (Setting se : setts) {

				CLICKABLE c = new GUI.Button(UI.FONT().M.getText(se.name)) {
					
					@Override
					public boolean hover(COORDINATE mCoo) {
						if ( super.hover(mCoo)) {
							desc = se.desc;
							return true;
						}
						return false;
					}
					
					@Override
					protected void clickA() {
						se.set(bInts);
					}
					
				};

				
				s.addRightC(16, c);
				
			}
//			
//			GuiSection s = new GuiSection();
//			ACTION clear = new ACTION() {
//				
//				@Override
//				public void exe() {
//					for (IntImp i : bInts.all()) {
//						i.set((i.max()+1)/2);
//					}
//				}
//			};
//			ACTION casual = new ACTION() {
//				
//				@Override
//				public void exe() {
//					for (IntImp i : bInts.all()) {
//						i.set(1 + (i.max()+1)/2);
//					}
//				}
//			};
//			casual.exe();
//			
//			CLICKABLE c = GUI.getNavButt(¤¤snormal);
//			c.clickActionSet(clear);
//			s.add(c);
//			
//			c = GUI.getNavButt(¤¤ssandbox);
//			c.clickActionSet(casual);
//			s.addRightC(64, c);
			
			options.addRelBody(16, DIR.S, s);
			
		}
		
		options.addRelBody(16, DIR.S, new RenderImp(MenuScreen.inner.width()-4, 32) {
			
			@Override
			public void render(SPRITE_RENDERER r, float ds) {
				CharSequence dd = desc;
				desc = null;
				
				if (dd != null) {
					UI.FONT().M.renderC(r, body().cX(), body().cY()+32, dd);
				}else {
					COLOR.YELLOW100.bind();
					for (TupleImp<Double, Boolean> i : BOOSTS.all()) {
						if ((i.b == false && i.a > 0) || (i.b == true && i.a > 1)) {
							UI.FONT().M.renderC(r, body().cX(), body().cY()+32, ¤¤titleDisabled);
							COLOR.unbind();
							return;
						}
					}
					
					COLOR.unbind();
				}
				
				
				
				
				
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
	
	private final class Scripts extends GuiSection {

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
			if (rows.size() > 0) {
				add(new GScrollRows(rows, height).view());
				addRelBody(4, DIR.N, new Sprite(UI.FONT().H2.getText(¤¤Scripts), COLORS.label));
			}
			
			
		}
		
	}
	
	final class Boosts extends GuiSection {
		
		Boosts(int height, GETTER_IMP<CharSequence> hText){
			
			Boostable[] boos;
			double[] values;
			
			LinkedList<RENDEROBJ> rows = new LinkedList<>();
			
			
//			boos = new Boostable[] {
//				BOOSTABLES.CIVICS().FURNITURE,
//				BOOSTABLES.CIVICS().MAINTENANCE,
//				BOOSTABLES.CIVICS().SPOILAGE,
//				
//			};
//			values = new double[] {
//					-0.6, -0.4, -0.2, -0.1, 0, 0.5, 1, 2, 4
//			};
//			addLine(rows, boos, values, hText, false);
			
			boos = new Boostable[] {
				BOOSTABLES.BEHAVIOUR().HAPPI,
				BOOSTABLES.BEHAVIOUR().LOYALTY,
				BOOSTABLES.CIVICS().RAID_SECURITY,
				BOOSTABLES.CIVICS().DEFALTION,
				BOOSTABLES.CIVICS().ACCIDENT,
				
				BOOSTABLES.CIVICS().GOV,
				BOOSTABLES.CIVICS().INNOVATION,
				BOOSTABLES.CIVICS().DIPLOMACY,
				BOOSTABLES.CIVICS().IMMIGRATION,
				BOOSTABLES.CIVICS().LAW,
				BOOSTABLES.BEHAVIOUR().SUBMISSION,
				BOOSTABLES.PHYSICS().HEALTH,
				BOOSTABLES.CIVICS().PASIFISM,
				BOOSTABLES.CIVICS().bOpinion,
				BOOSTABLES.CIVICS().FURNITURE,
				BOOSTABLES.CIVICS().MAINTENANCE,
				BOOSTABLES.CIVICS().SPOILAGE,
			};
			values = new double[] {
				0.4, 0.6, 0.8, 1.0, 1.25, 2, 4.0,
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
				bInts.put(bo.key, intt);
				
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

	private static class Setting {
		
		public final CharSequence name;
		public final CharSequence desc;
		public final KeyMap<Integer> ints = new KeyMap<>();
		
		Setting(CharSequence name, CharSequence desc){
			this.name = name;
			this.desc = desc;
		}
		
		void s(Boostable bo, int s) {
			ints.putReplace(bo.key, s);
		}
		
		void s(String key, int s) {
			ints.putReplace(key, s);
		}
		
		void set(KeyMap<IntImp> bInts) {
			for (IntImp i : bInts.all()) {
				i.set((i.max()+1)/2);
			}
			
			for (String k : ints.keys()) {
				if (bInts.containsKey(k)) {
					bInts.get(k).set((bInts.get(k).max+1)/2 + ints.get(k));
				}
			}
		}
		
	}
	
	
}
