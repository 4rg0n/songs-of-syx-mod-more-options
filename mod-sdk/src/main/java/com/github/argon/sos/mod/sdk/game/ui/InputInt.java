package com.github.argon.sos.mod.sdk.game.ui;

import init.sprite.UI.UI;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.datatypes.Coo;
import snake2d.util.gui.GuiSection;
import snake2d.util.misc.CLAMP;
import snake2d.util.sprite.text.StringInputSprite;
import util.data.INT.INTE;
import util.gui.misc.GButt;

import java.util.function.Supplier;

public class InputInt extends GuiSection {

	private final INTE in;
	
	private final StringInputSprite sp = new StringInputSprite(8, UI.FONT().S) {
		
		@Override
		protected void change() {
			int num = 0;
			for (int i = 0; i < text().length(); i++) {
				int n = text().charAt(i)- '0';
				
				if (n >= 0 || n < 10) {
					if (num*10 + n > in.max())
						break;
					num*= 10;
					num += n;
				}else {
					unfuck();
					return;
				}
			}
			in.set(CLAMP.i(num, in.min(), in.max()));
			text().clear().add(in.get());
		};
		

	}.placeHolder("0");
	
	public InputInt(INTE in) {
		this(in, false, false, null, 0);
	}
	
	public InputInt(INTE in, boolean butts, boolean doublebutts, @Nullable Supplier<Coo> mouseCooSupplier, int inputWidth){
		this.in = in;
		
		Input inn = new Input(sp, mouseCooSupplier, inputWidth);
		
		if (doublebutts) {
			GButt.ButtPanel pp = new GButt.ButtPanel(UI.icons().s.minifierBig) {
				
				@Override
				protected void clickA() {
					in.inc(-Math.max(1, (in.max()-in.min())/5));
				}
				

				@Override
				protected void renAction() {
					activeSet(in.get() > in.min());
				};
				
			};
			pp.repetativeSet(true);
			pp.body().setHeight(inn.body.height());
			addRightC(0, pp);
			
		}
		
		if (butts) {
			GButt.ButtPanel pp = new GButt.ButtPanel(UI.icons().s.minifier) {
				
				@Override
				protected void clickA() {
					in.inc(-1);
				}
				

				@Override
				protected void renAction() {
					activeSet(in.get() > in.min());
				};
				
			};
			pp.repetativeSet(true);
			pp.body().setHeight(inn.body.height());
			addRightC(0, pp);
		}
		
		
		
		addRightC(0, inn);
		if (butts) {
			GButt.ButtPanel pp = new GButt.ButtPanel(UI.icons().s.magnifier) {
				
				@Override
				protected void clickA() {
					in.inc(1);
				}
				
				@Override
				protected void renAction() {
					activeSet(in.get() < in.max());
				};
				
			};
			pp.repetativeSet(true);
			pp.body().setHeight(inn.body.height());
			addRightC(0, pp);
		}
		
		if (doublebutts) {
			GButt.ButtPanel pp = new GButt.ButtPanel(UI.icons().s.magnifierBig) {
				
				@Override
				protected void clickA() {
					in.inc(Math.max(1, (in.max()-in.min())/5));
				}
				

				@Override
				protected void renAction() {
					activeSet(in.get() < in.max());
				};
				
			};
			pp.repetativeSet(true);
			pp.body().setHeight(inn.body.height());
			addRightC(0, pp);
			
		}
	}
	
	@Override
	public void render(SPRITE_RENDERER r, float ds) {
		unfuck();
		super.render(r, ds);
	}
	
	private void unfuck() {
		sp.text().clear();
		int am = in.get();
		if (am != 0)
			sp.text().add(am);
	}
	
}
