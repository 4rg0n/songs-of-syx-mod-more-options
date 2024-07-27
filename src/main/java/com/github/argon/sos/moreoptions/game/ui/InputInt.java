package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.action.Action;
import com.github.argon.sos.moreoptions.game.action.Refreshable;
import com.github.argon.sos.moreoptions.game.action.Valuable;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.datatypes.Coo;
import snake2d.util.gui.GuiSection;
import snake2d.util.misc.CLAMP;
import snake2d.util.sprite.text.StringInputSprite;
import util.data.INT;
import util.data.INT.INTE;
import util.gui.misc.GButt;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class InputInt extends GuiSection implements Valuable<Integer>, Refreshable {

	private final INTE in;

	@Setter
	@Nullable
	@Accessors(fluent = true, chain = false)
	private Supplier<Integer> valueSupplier;

	@Setter
	@Accessors(fluent = true, chain = false)
	private Consumer<Integer> valueConsumer = o -> {};

	@Setter
	@Accessors(fluent = true, chain = false)
	private Action<Integer> valueChangeAction = o -> {};
	
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

			setValue(num);
			text().clear().add(in.get());
		};
		

	}.placeHolder("0");

	@Builder
	public InputInt(
		int min,
		int max,
		int value,
		boolean showButtons,
		boolean showDoubleButtons,
		int inputWidth,
		@Nullable Supplier<Coo> mouseCooSupplier,
		@Nullable Supplier<Integer> valueSupplier,
		@Nullable Consumer<Integer> valueConsumer,
		@Nullable Action<Integer> valueChangeAction
	){
		this.in = new INT.INTE.IntImp(min, max);
		in.set(value);

		Input inn = new Input(sp, mouseCooSupplier, inputWidth);

		if (valueSupplier != null) this.valueSupplier = valueSupplier;
		if (valueConsumer != null) this.valueConsumer = valueConsumer;
		if (valueChangeAction != null) this.valueChangeAction = valueChangeAction;

		if (showDoubleButtons) {
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

		if (showButtons) {
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
		if (showButtons) {
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

		if (showDoubleButtons) {
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

	@Override
	public Integer getValue() {
		if (valueSupplier != null) {
			return valueSupplier.get();
		}

		return in.get();
	}

	@Override
	public void setValue(Integer value) {
		valueConsumer.accept(value);

		if (!Objects.equals(getValue(), value)) {
			valueChangeAction.accept(value);
		}

		in.set(CLAMP.i(value, in.min(), in.max()));
	}

	@Override
	public void refresh() {
		setValue(getValue());
	}
}
