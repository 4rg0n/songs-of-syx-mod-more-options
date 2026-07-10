package com.github.argon.sos.mod.sdk.ui.input;

import com.github.argon.sos.mod.sdk.data.DoubleValue;
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
 * An input field for numbers with decimals.
 */
public class InputDouble extends GuiSection implements Valuable<Double>, Resettable {

	private final DoubleValue inputValue;
	private final StringInputSprite inputSprite;
	private final int decimals;

	/**
	 * Creates a new {@link InputDouble}.
	 *
	 * @param inputValue to set when the field is created
	 * @param showButtons whether + and - buttons shall be added
	 * @param showDoubleButtons whether ++ and -- buttons shall be added
	 * @param mouseCoordinatesSupplier optional supplier for mouse coordinates
	 * @param decimals how many decimals shall be allowed
	 * @param inputWidth width of the input field
	 */
	@Builder
	public InputDouble(DoubleValue inputValue, boolean showButtons, boolean showDoubleButtons, @Nullable Supplier<Coo> mouseCoordinatesSupplier, int decimals, int inputWidth){
		this.inputValue = inputValue;
		this.decimals = (decimals > 0) ? decimals : 2;

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
					double value = Double.parseDouble(text().toString());
					setValue(value);
				} catch (NumberFormatException e) {
					reset();
				}
			}
		}.placeHolder("0." + StringUtil.repeat('0', this.decimals));
		InputString inn = new InputString(inputSprite, mouseCoordinatesSupplier, inputWidth);

		if (showDoubleButtons) {
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

		if (showButtons) {
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
		if (showButtons) {
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

		if (showDoubleButtons) {
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
	public @Nullable Double getValue() {
		return inputValue.getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(Double value) {
		double newValue = inputValue.setValue(value);
		updateInputSprite(newValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		updateInputSprite(inputValue.getValue());
	}

	private void updateInputSprite(double value) {
		inputSprite.text().clear().add(value, decimals + 1);
	}
}
