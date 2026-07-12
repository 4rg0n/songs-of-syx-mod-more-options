package com.github.argon.sos.mod.sdk.ui.input;

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

/**
 * An input field for numbers without decimals.
 */
public class InputInteger extends GuiSection implements Valuable<Integer>, Resettable {

	private final IntegerValue inputValue;
	
	private final StringInputSprite inputSprite;

	/**
	 * Creates a new {@link InputInteger}.
	 *
	 * @param initialValue to set when the field is created
	 */
	public InputInteger(IntegerValue initialValue) {
		this(initialValue, false, false, null, 0);
	}

	/**
	 * Creates a new {@link InputInteger}.
	 *
	 * @param inputValue to set when the field is created
	 * @param showButtons whether + and - buttons shall be added
	 * @param showDoubleButtons whether ++ and -- buttons shall be added
	 * @param mouseCoordinatesSupplier optional supplier for mouse coordinates
	 * @param inputWidth width of the input field
	 */
	@Builder
	public InputInteger(IntegerValue inputValue, boolean showButtons, boolean showDoubleButtons, @Nullable Supplier<Coo> mouseCoordinatesSupplier, int inputWidth){
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

		InputString inn = new InputString(inputSprite, mouseCoordinatesSupplier, inputWidth);
		
		if (showDoubleButtons) {
			GButt.ButtPanel pp = new GButt.ButtPanel(UI.icons().s.minifierBig) {

				@Override
				protected void clickA() {
					int decreasedValue = inputValue.inc(-Math.max(1, (inputValue.getMax() - inputValue.getMin()) / 5));
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
		
		if (showButtons) {
			GButt.ButtPanel pp = new GButt.ButtPanel(UI.icons().s.minifier) {
				@Override
				protected void clickA() {
					int decreasedValue = inputValue.inc(-1);
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
		if (showButtons) {
			GButt.ButtPanel pp = new GButt.ButtPanel(UI.icons().s.magnifier) {
				@Override
				protected void clickA() {
					int increasedValue = inputValue.inc(1);
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
		
		if (showDoubleButtons) {
			GButt.ButtPanel pp = new GButt.ButtPanel(UI.icons().s.magnifierBig) {
				@Override
				protected void clickA() {
					int increasedValue = inputValue.inc(Math.max(1, (inputValue.getMax() - inputValue.getMin()) / 5));
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

	/**
	 * Executed when the input field is rendered.
	 *
	 * @param renderer to use
	 * @param deltaSeconds since last render loop
	 */
	@Override
	public void render(SPRITE_RENDERER renderer, float deltaSeconds) {
		super.render(renderer, deltaSeconds);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public @Nullable Integer getValue() {
		return inputValue.getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(Integer value) {
		int newValue = inputValue.setValue(value);
		updateInputSprite(newValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		updateInputSprite(inputValue.getValue());
	}

	private void updateInputSprite(int value) {
		inputSprite.text().clear().add(value);
	}
}
