package view.sett.ui.room.prints;

import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.ui.Button;
import com.github.argon.sos.mod.sdk.util.Clipboard;
import com.github.argon.sos.moreoptions.ModModule;
import game.faction.FACTIONS;
import init.sprite.UI.UI;
import init.text.D;
import settlement.main.SETT;
import settlement.room.main.RoomBlueprint;
import settlement.room.main.RoomBlueprintImp;
import settlement.room.main.copy.SavedPrints.SavedPrint;
import snake2d.MButt;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.color.OPACITY;
import snake2d.util.datatypes.DIR;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.gui.clickable.CLICKABLE.ClickableAbs;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.misc.STRING_RECIEVER;
import snake2d.util.sprite.text.Str;
import snake2d.util.sprite.text.StringInputSprite;
import util.colors.GCOLOR;
import util.data.GETTER;
import util.dic.Dic;
import util.gui.misc.*;
import util.gui.table.GTableBuilder;
import util.gui.table.GTableBuilder.GRowBuilder;
import view.interrupter.ISidePanel;
import view.main.VIEW;

public final class UISavedPrints extends ISidePanel{

	public static CharSequence ¤¤title = "¤Room Blueprints";
	static {
		D.ts(UISavedPrints.class);
	}

	// MODDED
	private final static I18nTranslator i18n = ModModule.i18n().get(UISavedPrints.class);
	
	private final List list;
	private final GInput filter;
	private final PlacerSave placerSave = new PlacerSave(this);
	private final GTableBuilder bu;
	SavedPrint placing;
	private SavedPrint flashed;
	private double flashUntil = 0;
	
	private static final int width = 380;
	private static final int height = 40;
	
	public UISavedPrints() {
		titleSet(¤¤title);
		StringInputSprite fi = new StringInputSprite(16, UI.FONT().S);
		fi.placeHolder(Dic.¤¤Filter);
		filter = new GInput(fi);
		list = new List(fi);

		// MODDED
		Button importButton = new Button(i18n.t("UISavedPrints.button.import.name"), i18n.t("UISavedPrints.button.import.desc"));
		importButton.clickActionSet(() -> {
			Clipboard.read().ifPresent(content -> {
				try {
					Json json = new Json(content, "clipboard");
					SavedPrint savedPrint = new SavedPrint(SETT.ROOMS(), json);
					SETT.ROOMS().copy.prints.add(savedPrint);
				} catch (Exception e) {
					ModModule.messages().notifyError("notification.not.import.room.blueprint");
				}
			});
		});

		// MODDED
		GuiSection bar = new GuiSection();
		bar.addRightC(0, filter);
		bar.addRightC(10, importButton);

		section.add(bar);
		bu = new GTableBuilder() {
			
			@Override
			public int nrOFEntries() {
				return list.get().size();
			}
		};
		
		bu.column(null, width, new GRowBuilder() {
			
			@Override
			public RENDEROBJ build(GETTER<Integer> ier) {
				return new Row(ier);
			}
		});
		
		section.addRelBody(8, DIR.S, bu.createHeight(HEIGHT-section.body().height()-8, false));
		
		
	}
	
	public void open() {
		
		VIEW.s().tools.place(placerSave, placerSave.config);
		VIEW.s().panels.add(this, true);
		flashUntil = 0;
		placing = null;
	}

	public void open(RoomBlueprint p) {
		for (RoomBlueprint b : SETT.ROOMS().all()) {
			if (b.getClass() == p.getClass()) {
				if (SETT.ROOMS().copy.prints.all(b).size() > 0) {
					SETT.ROOMS().copy.savedPlacer.place(SETT.ROOMS().copy.prints.all(b).get(0), p);
					VIEW.s().panels.add(UISavedPrints.this, true);
					flashUntil = 0;
					placing = SETT.ROOMS().copy.prints.all(b).get(0);
					return;
				}
			}
		}
	}
	
	public boolean has(RoomBlueprint p) {
		for (RoomBlueprint b : SETT.ROOMS().all()) {
			
			if (b.getClass() == p.getClass()) {
				
				if (SETT.ROOMS().copy.prints.all(b).size() > 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	public void set(SavedPrint p) {
		list.expand(p);
		int li = 0;
		int lastCat = 0;
		for (Entry e : list.get()) {
			if (e.print != null && e.print == p) {
				if (li-lastCat < 8)
					li = lastCat;
				bu.set(li);
				flashed = p;
				flashUntil = VIEW.renderSecond() + 8;
			}else if (e.cat != null)
				lastCat = li;
			li++;
		}
	}
	
	
	public class Row extends CLICKABLE.ClickWrap{

		private final RCat cat = new RCat();
		private final RPrint print = new RPrint();
		private final CLICKABLE dum = new ClickableAbs(width, height) {
			
			@Override
			protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected, boolean isHovered) {
				
				
			}
		};
		private final GETTER<Integer> ier;
		
		Row(GETTER<Integer> ier){
			super(200, 38);
			this.ier = ier;
		}

		@Override
		protected RENDEROBJ pget() {
			Entry e = list.get().get(ier.get());
			if (e == null) {
				return dum;
			}else if (e.print != null) {
				print.e = e;
				return print;
				
			}else {
				cat.e = e;
				return cat;
			}
		}

		
		
	}
	
	public class RCat extends ClickableAbs {
		
		Entry e;
		
		public RCat() {
			super(width, height);
		}

		@Override
		protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected, boolean isHovered) {
			isActive = !e.isLocked && e.cat.entries > 0;
			Cat c = e.cat;
			isSelected = c.expanded;
			isHovered = hoveredIs();
			
			GButt.ButtPanel.renderBG(r, isActive, isSelected, isHovered, body());
			
			for (int bi = 0; bi < e.cat.prints.size() && bi < 6; bi++) {
				e.cat.prints.get(bi).iconBig().renderCY(r, body.x1()+8+bi*24, body.cY());
			}
			
//			GCOLOR.T().H1.bind();
//			UI.FONT().H2.renderCropped(r, e.cat.name, body.x1()+Icon.L+16, body.cY()-UI.FONT().H2.height()/2, body().width()-Icon.L-16-32-48-8);
//			COLOR.unbind();
			
			UI.FONT().S.renderCY(r,body.x2()-32-48, body.cY(), Str.TMP.clear().add(e.cat.entries));
			
			if (e.cat.expanded) {
				UI.icons().s.chevron(DIR.S).renderCY(r, body.x2()-32, body.cY());
			}else
				UI.icons().s.chevron(DIR.E).renderCY(r, body.x2()-32, body.cY());
			
			if (e.cat.entries == 0) {
				OPACITY.O50.bind();
				COLOR.BLACK.render(r, body);
				OPACITY.unbind();
			}
			
			GButt.ButtPanel.renderFrame(r, body());
		}
		
		@Override
		protected void clickA() {
			if (e.cat.entries > 0)
				e.cat.expanded = !e.cat.expanded;
		}
		
		@Override
		public void hoverInfoGet(GUI_BOX text) {
			GBox b = (GBox) text;
			for (RoomBlueprintImp p : e.cat.prints) {
				b.add(p.iconBig());
				b.textLL(p.info.names);
				b.NL();
			}
			super.hoverInfoGet(text);
		}
		
	}
	
	public class RPrint extends GuiSection implements STRING_RECIEVER{
		
		Entry e;
		
		public RPrint() {
			body().setDim(16, height);
			addRightC(4, new GStat() {
				
				@Override
				public void update(GText text) {
					if (e.isLocked)
						text.errorify();
					else
						text.normalify();
					text.setMaxWidth(150);
					text.setMultipleLines(false);
					text.add(e.print.name);
				}
			});
			addRightCAbs(180, new GStat() {
				
				@Override
				public void update(GText text) {
					text.add(e.print.width).add('x').add(e.print.height);
				}
			});

			// MODDED
			addRightC(72, new  GButt.ButtPanel(UI.icons().s.arrowUp) {
				@Override
				protected void clickA() {
					JsonE json = e.print.save();
					Clipboard.write(json.toString());
				}
			}.hoverInfoSet(i18n.t("UISavedPrints.button.export.desc")));
			// MODDED

			addRightC(0, new  GButt.ButtPanel(UI.icons().s.admin) {
				
				@Override
				protected void clickA() {
					VIEW.inters().input.requestInput(RPrint.this, Dic.¤¤rename);
				}
				
			}.hoverInfoSet(Dic.¤¤rename));
			addRightC(0, new  GButt.ButtPanel(UI.icons().s.cancel) {
				
				@Override
				protected void clickA() {
					SETT.ROOMS().copy.prints.remove(e.print);
				}
				
			}.hoverInfoSet(Dic.¤¤remove));
			body().setWidth(width);
		}
		
		@Override
		public void render(SPRITE_RENDERER r, float ds) {
			boolean isActive = !e.isLocked;
			boolean isHovered = hoveredIs();
			boolean isSelected = placing == e.print;
			GButt.ButtPanel.renderBG(r, isActive, isSelected, isHovered, body());
			super.render(r, ds);
			if (flashed == e.print && flashUntil > VIEW.renderSecond()) {
				OPACITY.O0To25.bind();
				GCOLOR.UI().GOOD.hovered.render(r, body());
				OPACITY.unbind();
			}
			
			GButt.ButtPanel.renderFrame(r, body());
		}

		@Override
		public void acceptString(CharSequence string) {
			if (e != null && string != null && string.length() > 0)
				e.print.name = ""+string;
		}
		
		@Override
		public void hoverInfoGet(GUI_BOX text) {
			if (e.isLocked) {
				text.text(Dic.¤¤Locked);
			}
			text.text(e.print.name);
			super.hoverInfoGet(text);
		}
		
		@Override
		protected void clickA() {
			for (RoomBlueprintImp b : e.cat.prints) {
				if (b.reqs.passes(FACTIONS.player())) {
					SETT.ROOMS().copy.savedPlacer.place(e.print);
					VIEW.s().panels.add(UISavedPrints.this, true);
					flashUntil = 0;
					placing = e.print;
					return;
				}
			}
			super.clickA();
		}
		
	}
	
	@Override
	protected boolean back() {
		MButt.RIGHT.consumeAllClick();
		MButt.LEFT.consumeAllClick();
		if (placing != null) {
			VIEW.s().tools.place(placerSave, placerSave.config);
			VIEW.s().panels.add(this, true);
			flashUntil = 0;
			return true;
		}
		return super.back();
	}
	
	
}
