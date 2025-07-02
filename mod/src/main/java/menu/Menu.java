package menu;

import game.faction.player.PTitles;
import init.C;
import init.settings.S;
import init.sprite.UI.UI;
import menu.ui.MouseHoverMessage;
import snake2d.CORE;
import snake2d.CORE_STATE;
import snake2d.KEYCODES;
import snake2d.KeyBoard.KeyEvent;
import snake2d.MButt;
import snake2d.PreLoader;
import snake2d.Renderer;
import snake2d.SPRITE_RENDERER;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.datatypes.Coo;
import snake2d.util.datatypes.Rec;
import snake2d.util.gui.GuiSection;
import snake2d.util.light.AmbientLight;
import snake2d.util.light.PointLight;
import snake2d.util.sets.LIST;
import snake2d.util.sprite.TextureCoords;
import util.dic.Dic;

public class Menu extends CORE_STATE {

	// MODDED
	private final MouseHoverMessage mouseHoverMessage;
	final FullWindow<MoreOptionsEditor> moreOptions;
	private double hoverTimer = 0;

	final ScMain main;
	final ScOptions options;
	final ScLoad load;
	final ScLoad[] loads;
	final ScRandom sandbox;
	final ScCredits credits;
	final ScCampaign campaigns;
	private SC current;

	private Coo mCoo = new Coo();

	private final Background bg;

	private final PointLight mouseLight;

	private final Logo logo;
	private static boolean hasLogo = true;

	private final Intro intro;
	private static boolean hasIntro = true;
	private float fadeLight = 0;


	public final RESOURCES res;

	public static void start() {

		CORE.create(S.get().make());
		CORE.getInput().getMouse().showCusor(false);
		CORE.start(new CORE_STATE.Constructor() {
			@Override
			public CORE_STATE getState() {
				return make();
			}
		});

	}

	public static Menu make() {

		Menu menu = new Menu();
		PreLoader.exit();
		return menu;
	}

	public static Menu makeCampaign() {

		Menu menu = new Menu();
		PreLoader.exit();
		menu.switchScreen(menu.campaigns);
		return menu;
	}

	private Menu() {

		res = new RESOURCES();
		final Rec bounds = new Rec(0, C.MIN_WIDTH, 0, 2 * 256);
		bounds.centerIn(C.DIM());

		GUI.init(bounds);

		// MODDED
		this.mouseHoverMessage = new MouseHoverMessage();
		// TODO disabled
		this.moreOptions = null;
//		this.moreOptions = Ui.moreOptionsFullsWindow();

		bg = new Background(this, bounds);

		options = new ScOptions(this);
		sandbox = new ScRandom(this);
		load = ScLoad.load(this);

		loads = new ScLoad[] {ScLoad.scenarios(this), ScLoad.battle(this), ScLoad.showcase(this)};
		credits = new ScCredits(this);
		main = new ScMain(this);
		campaigns = new ScCampaign(this);
		current = main;

		mouseLight = new PointLight(1.0f, 1.0f, 1.3f, 0, 0, 15);
		mouseLight.setFalloff(1);

		intro = new Intro(main, bg);

		logo = new Logo(this);

		S.get().applyRuntimeConfigs();
		PTitles.achieve();

	}

	private void hover(COORDINATE mCoo, boolean mouseHasMoved) {

		this.mCoo.set(mCoo);
		if (hasIntro || hasLogo)
			return;

		// MODDED
		if (mouseHasMoved){
			hoverTimer = 0;
		}

		if (!MenuUi.getInstance().popups().hover(mCoo))
			current.hover(mCoo);
		mouseLight.set(mCoo);

		// MODDED
//		if (hoverTimer >= 0.4) {
		// todo disabled
//			moreOptions.hoverInfoGet(mouseHoverMessage.get());
//		}
	}

	@Override
	public void update(float ds, double slow) {
		hover(CORE.getInput().getMouse().getCoo(), false);
		if (hasLogo) {
			hasLogo = logo.update(ds);
			return;
		}

		res.sound().play();

		// MODDED
		hoverTimer += ds;
		mouseHoverMessage.update(this.mCoo);

		hasIntro = hasIntro && intro.update(ds);
		if (!hasIntro && fadeLight < 1) {
			fadeLight += ds;
			if (fadeLight > 1)
				fadeLight = 1;
		}
	}

	@Override
	public void render(Renderer r, float ds) {
		CORE.renderer().shadeLight(true);
		CORE.renderer().shadowDepthDefault();
		if (hasLogo) {
			logo.render(r, ds);
			return;
		}

		if (hasIntro) {
			intro.render(r, ds);
			return;
		}

		AmbientLight.Strongmoonlight.register(C.DIM());
		UI.decor().mouse.render(r, mCoo.x(), mCoo.y());
		r.newLayer(true, 0);

		mouseLight.setRed(fadeLight);
		mouseLight.setGreen(fadeLight);
		mouseLight.setBlue(fadeLight * 1.3);

		mouseLight.register();
		current.render(rr, ds);

		// MODDED
		MenuUi.getInstance().popups().render(rr, ds);
		mouseHoverMessage.render(r, ds);

		r.newLayer(false, 0);

		current.renderBackground(bg, ds, mCoo);

	}

	@Override
	public void mouseClick(MButt button) {

		if (hasLogo) {
			hasLogo = false;
			return;
		}

		if (hasIntro) {
			hasIntro = false;
			return;
		}

		// MODDED
		if (button == MButt.LEFT) {
			if (!MenuUi.getInstance().popups().click()) {
				if (MenuUi.getInstance().popups().visableIs()) {
					MenuUi.getInstance().popups().close();
				}
				current.click();
			}

		}
		if (button == MButt.RIGHT) {
			if (MenuUi.getInstance().popups().visableIs()) {
				MenuUi.getInstance().popups().close();
			} else {
				current.back(this);
			}
		}
		// MODDED
	}

	void switchScreen(SC screen) {
		current = screen;
		current.hover(mCoo);
	}

	SC screen() {
		return current;
	}

	Coo getMCoo() {
		return mCoo;
	}

	void start(Constructor state) {
		CORE.renderer().clear();

		GuiSection s = new GuiSection();
		GUI.addTitleText(s, Dic.¤¤loading);
		s.body().centerIn(C.DIM());
		s.render(rr, 0);
		AmbientLight.Strongmoonlight.register(C.DIM());
		CORE.renderer().newLayer(false, 0);
		bg.render(CORE.renderer(), 0);

		CORE.swapAndPoll();
		CORE.setCurrentState(state);
	}

	@Override
	protected void keyPush(LIST<KeyEvent> keys, boolean hasCleared) {
		for (int i = 0; i < keys.size(); i++) {
			KeyEvent key = keys.get(i);
			if (hasLogo) {
				hasLogo = false;
				return;
			}

			if (hasIntro) {
				hasIntro = false;
				return;
			}
			if (key.code() == KEYCODES.KEY_ESCAPE) {
				if (!current.back(this)) {
					return;
				}
				break;
			} else {
				current.poll(key);
			}

		}

	}

	private final SPRITE_RENDERER rr = new SPRITE_RENDERER() {

		private final int ss = 4;
		private final int si = 8;

		@Override
		public void renderSprite(int x1, int x2, int y1, int y2, TextureCoords texture) {
			CORE.renderer().renderSprite(x1, x2, y1, y2, texture);
			for (int i = 0; i < si; i++)
				CORE.renderer().renderShadow(x1 + ss + i, x2 + ss + i, y1 - ss - i, y2 - ss - i, texture, (byte) 0);
		}
	};

}
