package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.util.UiUtil;
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

public class GInput extends CLICKABLE.ClickableAbs {

	private final StringInputSprite input;
	private boolean dragging = false;

	private Supplier<Coo> mouseCooSupplier = UiUtil.MOUSE_COO_SUPPLIER;

	public GInput(StringInputSprite input) {
		this(input, null, 0);
	}
	
	public GInput(StringInputSprite input, @Nullable Supplier<Coo> mouseCooSupplier, int inputWidth) {
		this.input = input;
		if (mouseCooSupplier != null) this.mouseCooSupplier = mouseCooSupplier;
		
		int w = input.font().maxCWidth*input.text().spaceLeft() + 12;

		if (w > inputWidth) {
			w = inputWidth;
		}

		body.setWidth(w);
		body.setHeight(input.height()+12);
		//input.text().clear();
	}
	
	@Override
	protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected, boolean isHovered) {
		
		GCOLOR.UI().bg(isActive, isSelected, isHovered).render(r, body);

		input.renAction();
		if (Mouse.currentClicked == this)
			input.listen();
		
		if (isHovered || Mouse.currentClicked == this) {
			GCOLOR.UI().NORMAL.hovered.render(r, body());
		}
		
		int x1 = body().x1()+6;
		int y1 = body().y1() + (body().height()-input.height())/2;
		
		dragging &= MButt.LEFT.isDown();
		
		if (dragging) {
			input.select(mouseCooSupplier.get().x()-x1);
		}
		
		input.render(r, x1, y1);
		
		GCOLOR.UI().border().renderFrame(r, body, 0, 2);

	}
	
	@Override
	public boolean click() {
		if (super.click()) {
			Mouse.currentClicked = this;
			if (!input.listening() || MButt.LEFT.isDouble()) {
				input.listen();
				input.selectAll();
				dragging = false;
			}else {
				dragging = true;
			
				input.click(mouseCooSupplier.get().x()-body().x1()-6);
				return true;
			}
			
			
			
		}
		return false;
	}
	
	public void focus() {
		Mouse.currentClicked = this;
		input.listen();
		input.selectAll();
	}
	
	public void listen() {
		Mouse.currentClicked = this;
		input.listen();
	}
	
	public Str text() {
		return input.text();
	}
	

	

}
