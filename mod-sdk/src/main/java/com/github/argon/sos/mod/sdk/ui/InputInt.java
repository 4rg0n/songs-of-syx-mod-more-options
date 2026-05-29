package com.github.argon.sos.mod.sdk.ui;

import com.github.argon.sos.mod.sdk.data.IntegerValue;
import com.github.argon.sos.mod.sdk.game.action.Resettable;
import com.github.argon.sos.mod.sdk.game.action.Valuable;
import com.github.argon.sos.mod.sdk.util.StringUtil;
import init.sprite.UI.UI;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.datatypes.Coo;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.misc.GButt;

import java.util.function.Supplier;

public class InputInt extends GuiSection implements Valuable<Integer>, Resettable {

	private final IntegerValue inputValue;
	
	private final StringInputSprite inputSprite;
	
	public InputInt(IntegerValue inputValue) {
		this(inputValue, false, false, null, 0);
	}

	@Builder
	public InputInt(IntegerValue inputValue, boolean butts, boolean doublebutts, @Nullable Supplier<Coo> mouseCooSupplier, int inputWidth){
		this.inputValue = inputValue;

		this.inputSprite = new StringInputSprite(8, UI.FONT().S) {

			@Override
			protected void change() {
				String inputString = text().toString();

				// do not allow invalid characters
				if (!StringUtil.isNumeric(inputString)) {
					reset();
					return;
				}

				try {
					int value = Integer.parseInt(text().toString());
					setValue(value);
				} catch (NumberFormatException e) {
					reset();
				}
			};


		}.placeHolder("0");

		Input inn = new Input(inputSprite, mouseCooSupplier, inputWidth);
		
		if (doublebutts) {
			GButt.ButtPanel pp = new GButt.ButtPanel(UI.icons().s.minifierBig) {

				@Override
				protected void clickA() {
					double decreasedValue = inputValue.inc(-Math.max(1, (inputValue.getMax() - inputValue.getMin()) / 5));
					updateInputSprite(decreasedValue);
				}


				@Override
				protected void renAction() {
					activeSet(!inputValue.isMin());
				}
				
			};
			pp.repetativeSet(true);
			pp.body().setHeight(inn.body.height());
			addRightC(0, pp);
			
		}
		
		if (butts) {
			GButt.ButtPanel pp = new GButt.ButtPanel(UI.icons().s.minifier) {
				@Override
				protected void clickA() {
					double decreasedValue = inputValue.inc(-1);
					updateInputSprite(decreasedValue);
				}

				@Override
				protected void renAction() {
					activeSet(inputValue.getValue() > inputValue.getMin());
				}
				
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
					double increasedValue = inputValue.inc(1);
					updateInputSprite(increasedValue);
				}

				@Override
				protected void renAction() {
					activeSet(!inputValue.isMax());
				}
			};
			pp.repetativeSet(true);
			pp.body().setHeight(inn.body.height());
			addRightC(0, pp);
		}
		
		if (doublebutts) {
			GButt.ButtPanel pp = new GButt.ButtPanel(UI.icons().s.magnifierBig) {
				@Override
				protected void clickA() {
					double increasedValue = inputValue.inc(Math.max(1, (inputValue.getMax() - inputValue.getMin()) / 5));
					updateInputSprite(increasedValue);
				}

				@Override
				protected void renAction() {
					activeSet(inputValue.isMax());
				}
			};
			pp.repetativeSet(true);
			pp.body().setHeight(inn.body.height());
			addRightC(0, pp);
			
		}
	}
	
	@Override
	public void render(SPRITE_RENDERER r, float ds) {
		super.render(r, ds);
	}

	@Override
	public @Nullable Integer getValue() {
		return inputValue.getValue();
	}

	@Override
	public void setValue(Integer value) {
		int newValue = inputValue.setValue(value);
		updateInputSprite(newValue);
	}

	@Override
	public void reset() {
		updateInputSprite(inputValue.getValue());
	}

	private void updateInputSprite(double value) {
		inputSprite.text().clear().add(value);
	}
	
}
