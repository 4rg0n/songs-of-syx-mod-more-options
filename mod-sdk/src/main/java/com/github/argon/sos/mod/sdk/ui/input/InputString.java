package com.github.argon.sos.mod.sdk.ui.input;

import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import org.jetbrains.annotations.Nullable;
import snake2d.MButt;
import snake2d.Mouse;
import snake2d.SPRITE_RENDERER;
import snake2d.util.datatypes.Coo;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.sprite.text.Str;
import snake2d.util.sprite.text.StringInputSprite;
import util.colors.GCOLOR;

import java.util.function.Supplier;

/**
 * An input field for text.
 */
public class InputString extends CLICKABLE.ClickableAbs {

	private final StringInputSprite input;
	private boolean dragging = false;

	private Supplier<Coo> mouseCooSupplier = UiUtil.MOUSE_COO_SUPPLIER;

	/**
	 * Creates a new {@link InputString}.
	 *
	 * @param input to use
	 */
	public InputString(StringInputSprite input) {
		this(input, null, 0);
	}

	/**
	 * Creates a new {@link InputString}.
	 *
	 * @param input to use
	 * @param mouseCoordinateSupplier optional supplier for mouse coordinates
	 * @param inputWidth width of the input field
	 */
	public InputString(StringInputSprite input, @Nullable Supplier<Coo> mouseCoordinateSupplier, int inputWidth) {
		this.input = input;
		if (mouseCoordinateSupplier != null) this.mouseCooSupplier = mouseCoordinateSupplier;
		
		int w = input.font().maxCWidth*input.text().spaceLeft() + 12;

		if (inputWidth > 0 && w > inputWidth) {
			w = inputWidth;
		}

		body.setWidth(w);
		body.setHeight(input.height()+12);
		//input.text().clear();
	}

	/**
	 * Executed when the input field is rendered.
	 *
	 * @param renderer to use
	 * @param deltaSeconds since last render loop
	 * @param isActive whether the field can be used
	 * @param isSelected whether the field is selectable
	 * @param isHovered whether the field is hover-able
	 */
	@Override
	protected void render(SPRITE_RENDERER renderer, float deltaSeconds, boolean isActive, boolean isSelected, boolean isHovered) {
		
		GCOLOR.UI().bg(isActive, isSelected, isHovered).render(renderer, body);

		input.renAction();
		if (Mouse.currentClicked == this)
			input.listen();
		
		if (isHovered || Mouse.currentClicked == this) {
			GCOLOR.UI().NORMAL.hovered.render(renderer, body());
		}
		
		int x1 = body().x1()+6;
		int y1 = body().y1() + (body().height()-input.height())/2;
		
		dragging &= MButt.LEFT.isDown();
		
		if (dragging) {
			input.select(mouseCooSupplier.get().x()-x1);
		}
		
		input.render(renderer, x1, y1);
		
		GCOLOR.UI().border().renderFrame(renderer, body, 0, 2);

	}

	/**
	 * Executed when the field is clicked.
	 *
	 * @return whether field is clickable
	 */
	@Override
	public boolean click() {
		if (super.click()) {
			Mouse.currentClicked = this;
			if (!input.listening() || MButt.LEFT.isDouble()) {
				input.listen();
				input.selectAll();
				dragging = false;
			} else {
				dragging = true;
			
				input.click(mouseCooSupplier.get().x()-body().x1()-6);
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets cursor into field.
	 */
	public void focus() {
		Mouse.currentClicked = this;
		input.listen();
		input.selectAll();
	}

	/**
	 * Listens to input.
	 */
	public void listen() {
		Mouse.currentClicked = this;
		input.listen();
	}

	/**
	 * Returns the current text input.
	 *
	 * @return current text input
	 */
	public Str text() {
		return input.text();
	}
}
