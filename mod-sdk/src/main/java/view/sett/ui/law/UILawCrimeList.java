package view.sett.ui.law;

import game.boosting.BOOSTABLES;
import init.sprite.SPRITES;
import init.sprite.UI.Icon;
import init.sprite.UI.UI;
import init.type.CRIMES;
import init.type.CRIMES.CRIME;
import init.type.HCLASS;
import init.type.HTYPES;
import settlement.main.SETT;
import settlement.room.law.PUNISHMENT_SERVICE;
import settlement.room.main.RoomBlueprintImp;
import settlement.stats.STATS;
import settlement.stats.law.StatCrime;
import settlement.stats.law.StatPunishment;
import settlement.stats.standing.STANDINGS;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.Hoverable.HOVERABLE;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sets.ArrayListGrower;
import snake2d.util.sprite.SPRITE;
import util.colors.GCOLOR;
import util.gui.misc.GBox;
import util.gui.misc.GButt;
import util.gui.misc.GHeader;
import util.gui.misc.GMeter;
import util.gui.misc.GStat;
import util.gui.misc.GText;
import util.gui.table.GRows;
import util.gui.table.GScrollRows;
import util.info.GFORMAT;
import util.text.D;
import util.text.Dic;
import view.main.VIEW;

/**
 * MODDED shadow of vanilla {@code view.sett.ui.law.UILawCrimeList} (v71.15).
 * <p>
 * Vanilla declares its {@code ¤¤*} translation fields as instance fields but registers them
 * from a static initializer via {@code D.ts(UILawCrimeList.class)}. {@code D.t} throws
 * "is not Static" when invoked from a class context with non-static fields, so the vanilla
 * class fails its {@code <clinit>} on every new game.
 * <p>
 * This shim is byte-identical in behaviour but with the four {@code ¤¤} fields marked
 * {@code static} so the registration succeeds.
 */
class UILawCrimeList extends GuiSection{

	private final Selector sel;
	private final GuiSection pop = new GuiSection();
	private StatCrime crimeToSet;

	// MODDED: static (vanilla declared these as instance fields, which crashes D.ts).
	private static CharSequence ¤¤desc = "Punishing a crime with a punishment will result in loss of freedom which affects happiness directly. It also increases law (which is added to loyalty), but by how much depends on other law factors, such as guards.";
	private static CharSequence ¤¤lawMul = "Law multiplier";
	private static CharSequence ¤¤projection = "Projection";
	private static CharSequence ¤¤happy = "Happiness (current)";

	static {
		D.ts(UILawCrimeList.class);
	}

	UILawCrimeList(int height, HCLASS cl) {
		this.sel = new Selector(height, CRIMES.all(cl));

		addRelBody(8, DIR.S, new CrimeChart(12, cl, sel));
		addRelBody(8, DIR.S, new LawChart(12, cl, sel));

		GRows rr = new GRows(8);

		for (StatPunishment p : STATS.LAW().punishments) {
			if (!p.type.available(cl))
				continue;
			rr.add(new GButt.ButtPanel(p.type.icon.scaled(2)) {

				@Override
				protected void clickA() {
					crimeToSet.punishmentSet(cl, sel.race, p.type);
					super.clickA();
				}

				@Override
				protected void renAction() {
					selectedSet(crimeToSet.punishment(cl, sel.race) == p);
				}

				@Override
				public void hoverInfoGet(GUI_BOX text) {
					GBox b = (GBox) text;
					b.title(p.type.action);
					b.text(p.type.desc);

					b.sep();

					b.text(¤¤desc);
					b.NL(4);

					b.tab(5);
					b.add(BOOSTABLES.BEHAVIOUR().HAPPI.icon);
					b.textLL(BOOSTABLES.BEHAVIOUR().HAPPI.name);
					b.tab(9);
					b.add(BOOSTABLES.CIVICS().LAW.icon);
					b.textLL(BOOSTABLES.CIVICS().LAW.name);
					b.NL();

					b.textL(crimeToSet.type.name);
					b.tab(5);
					b.add(GFORMAT.perc(b.text(), -crimeToSet.type.freedom(cl, sel.race), 1));
					b.tab(9);
					b.add(GFORMAT.percInc(b.text(), crimeToSet.type.law(cl, sel.race)));
					b.NL();

					b.textL(p.type.name);
					b.tab(5);
					mul(b, p.type.freedom(cl, sel.race));
					b.tab(9);
					mul(b, p.type.law(cl, sel.race));
					b.NL();

					double hh = STANDINGS.get(cl).happiness.getD(sel.race);
					b.textL(¤¤happy);
					b.tab(5);
					mul(b, hh);
					b.NL();

					double ll = STATS.LAW().lawWithoutPunishment(cl, sel.race);
					b.textL(¤¤lawMul);
					b.tab(9);
					mul(b, ll);
					b.NL();

					b.sep();

					b.textLL(¤¤projection);
					b.tab(5);
					b.add(GFORMAT.perc(b.text(), -crimeToSet.type.freedom(cl, sel.race)*p.type.freedom(cl, sel.race), 1));
					b.tab(9);
					b.add(GFORMAT.percInc(b.text(), ll*crimeToSet.type.law(cl, sel.race)*p.type.law(cl, sel.race)));
					b.NL();

					super.hoverInfoGet(text);
				}

				private void mul(GBox b, double mul) {
					GText t = b.text();
					t.add('*');
					GFORMAT.f1(t, mul);
					b.add(t);

				}

			});

		}

		for (RENDEROBJ o : rr.rows())
			pop.addDown(0, o);

		ArrayListGrower<RENDEROBJ> rows = new ArrayListGrower<RENDEROBJ>();

		for (CRIME c : CRIMES.all(cl)) {
			rows.add(row(cl, STATS.LAW().crimes.get(c.index())));
		}

		addRelBody(8, DIR.S, new GScrollRows(rows, rows.get(0).body().height()*8).view());



		{
			GuiSection s = new GuiSection();

			for (RoomBlueprintImp room : SETT.ROOMS().imps()) {

				if (room instanceof PUNISHMENT_SERVICE) {

					PUNISHMENT_SERVICE ser = (PUNISHMENT_SERVICE) room;



					HOVERABLE h = new ClickableAbs(60, 48) {


						@Override
						protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected,
								boolean isHovered) {

							if (isHovered) {
								COLOR.WHITE50.render(r, body, 2);
							}

							double d = ser.punishTotal() > 0 ? (double)ser.punishUsed()/ser.punishTotal() : 0;

							GMeter.render(r, GMeter.C_ORANGE, d, body);

							room.icon.renderC(r, body);

							if (ser.punishUsed() >= ser.punishTotal()*0.8) {
								GCOLOR.UI().BAD.normal.bind();
								SPRITES.icons().s.alert.render(r, body().x2()-Icon.S, body().y1());
								COLOR.unbind();
							}


						}

						@Override
						public void hoverInfoGet(GUI_BOX text) {
							GBox b = (GBox) text;
							b.title(room.info.names);
							b.textL(Dic.¤¤Available);
							b.tab(6);
							b.add(GFORMAT.iofkInv(b.text(), ser.punishTotal()-ser.punishUsed(), ser.punishTotal()));

						}


						@Override
						protected void clickA() {
							if (VIEW.s().ui.rooms.open(room) != null) {
								VIEW.s().panels.add(VIEW.s().ui.rooms.open(room), true);
							}
						}


					};

					s.addRightC(4, h);

				}

			}

			addRelBody(8, DIR.S, s);
		}

		{
			addRelBody(8, DIR.S, new GStat() {

				@Override
				public void update(GText text) {
					GFORMAT.perc(text, STATS.LAW().guards.data(cl).getD(sel.race));
				}

				@Override
				public void hoverInfoGet(GBox b) {
					STATS.LAW().hoverGuards(b, cl, sel.race);


				};

			}.increase().hh(HTYPES.GUARD().names, 200));
		}


		addRelBody(8, DIR.W, sel);

	}

	private RENDEROBJ row(HCLASS cl, StatCrime crime) {
		GuiSection s = new GButt.BSection() {

			@Override
			protected void hoverInfoSelf(GUI_BOX box) {
				crime.hover(box, cl, sel.race);
			}

			@Override
			protected void clickA() {
				crimeToSet = crime;
				VIEW.inters().popup.show(pop, this);
				super.clickA();
			}

		};




		s.add(new GHeader(crime.type.name));

		s.addRightCAbs(200, new SPRITE.Imp(Icon.M) {

			@Override
			public void render(SPRITE_RENDERER r, int X1, int X2, int Y1, int Y2) {
				StatPunishment p = crime.punishment(cl, sel.race);
				SPRITE s = p == null ? UI.icons().m.questionmark : p.type.icon;
				s.render(r, X1, Y1);
			}

		});

		s.addRightC(16, new GStat() {

			@Override
			public void update(GText text) {
				GFORMAT.f0(text, -crime.freedom(cl, sel.race));
			}
		});

		s.addRightC(100, new GStat() {

			@Override
			public void update(GText text) {
				GFORMAT.f0(text, crime.law(cl, sel.race));
			}
		});

		s.body().incrW(64);

		s.pad(8, 3);

		return s;

	}


}
