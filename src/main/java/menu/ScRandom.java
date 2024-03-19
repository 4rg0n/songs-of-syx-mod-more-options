package menu;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import game.GAME;
import game.GameConRandom;
import game.boosting.BOOSTABLES;
import game.boosting.Boostable;
import init.D;
import init.sprite.UI.UI;
import menu.GUI.COLORS;
import snake2d.CORE_STATE;
import snake2d.CORE_STATE.Constructor;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.data.GETTER.GETTER_IMP;
import util.data.INT.IntImp;
import util.gui.misc.GText;
import util.info.INFO;
import view.main.VIEW;

class ScRandom extends GuiSection implements SC{

	private final GameConRandom spec;
	
	final CharSequence ¤¤name = "¤random game";
	final CharSequence ¤¤normal = "¤normal";
	final CharSequence ¤¤easy = "¤easy";
	final CharSequence ¤¤hard = "¤hard";
	final CharSequence ¤¤titleDisabled = "¤Title unlocking will be disabled.";
	
	ScRandom(Menu menu){

		D.t(this);
		
		Screener screen = new Screener(¤¤name, GUI.labelColor) {
			
			@Override
			protected void back() {
				menu.switchScreen(menu.main);
			}
		};
		
		add(screen);
		
		
		spec = new GameConRandom();
		
		CLICKABLE b = new Screener.ScreenButton(D.g("go!")) {
			@Override
			protected void clickA() {
				menu.start(new Constructor() {
					
					@Override
					public CORE_STATE getState() {
						new GAME(spec);
						CORE_STATE s = new VIEW();
						return s;
					}
				});
			}
		};
		
		screen.addButt(b);
		
		GuiSection options = new GuiSection(); 
		
		{
			RENDEROBJ r = advantage();
			r.body().moveY1(0);
			r.body().moveCX(-340);
			options.add(r);
		}

		options.body().moveCX(body().cX() + 200);
		options.body().moveCY(body().cY());

		add(options);
		
	}

	
	private GuiSection advantage() {
		GETTER_IMP<CharSequence> hText = new GETTER_IMP<>();
		GuiSection s = new GuiSection();
		
		
		Boostable[] boos;
		double[] values;

		s.addDown(0, new Sprite(UI.FONT().H2.getText(D.g("Difficulty")), COLORS.label));
		
		boos = new Boostable[] {
			BOOSTABLES.CIVICS().FURNITURE,
			BOOSTABLES.CIVICS().RAIDING,
			BOOSTABLES.CIVICS().MAINTENANCE,
			BOOSTABLES.CIVICS().SPOILAGE,
		};
		values = new double[] {
			-0.5, -0.25, 0, 0.25, 0.5, 1, 2
		};
		addLine(s, boos, values, hText);
		
		boos = new Boostable[] {
			BOOSTABLES.BEHAVIOUR().HAPPI,
		};
		values = new double[] {
			-0.2, -0.1, 0, 0.05, 0.1, 0.2,
		};
		addLine(s, boos, values, hText);

		// MODDED
		s.addDown(4, new Sprite(UI.FONT().H2.getText(MoreOptionsScript.MOD_INFO.name), COLORS.label));
		boos = new Boostable[] {
			BOOSTABLES.START().KNOWLEDGE,
			BOOSTABLES.START().LANDING,
		};
		values = new double[] {
			-0.2, -0.1, 0, 0.05, 0.1, 0.2,
		};
		addLine(s, boos, values, hText);
		
		s.addRelBody(50, DIR.E, new RenderImp(300, UI.FONT().M.height()) {
			@Override
			public void render(SPRITE_RENDERER r, float ds) {
				COLOR.YELLOW100.bind();
				for (Double i : spec.BOOSTS.all()) {
					if (i > 0) {
						UI.FONT().M.render(r, ¤¤titleDisabled, body().x1(), body().y1(), body().width(), 1);
						break;
					}
				}
				
				COLOR.unbind();
				
			}
		});
		
		s.add(new RenderImp(300, 100) {
			
			@Override
			public void render(SPRITE_RENDERER r, float ds) {
				COLOR.WHITE150.bind();
				CharSequence s = hText.get();
				hText.set(null);
				if (s != null) {
					UI.FONT().M.render(r, s, body().x1(), body().y1(), body().width(), 1);
				}
				COLOR.unbind();
				
			}
		}, s.getLastX1(), s.getLastY2()+32);
		

		
		return s;
	}
	
	private void addLine(GuiSection s, Boostable[] boos, double[] values, GETTER_IMP<CharSequence> hText) {
		
		int z = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i] == 0)
				z = i;
		}
		
		final int zero = z;
		for (Boostable bo : boos) {
			IntImp intt = new IntImp(0, values.length-1) {
				@Override
				public void set(int t) {
					super.set(t);
					spec.BOOSTS.put(bo.key, values[t]);
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
			s.addDown(2, c);
		}
	}
	
	@Override
	public boolean back(Menu menu) {
		menu.switchScreen(menu.main);
		return true;
	}
	

	
}
