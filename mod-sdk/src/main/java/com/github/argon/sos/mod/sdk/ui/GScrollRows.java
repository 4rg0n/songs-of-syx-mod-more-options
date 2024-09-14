package com.github.argon.sos.mod.sdk.ui;

import snake2d.MButt;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.misc.CLAMP;
import util.data.INT.INTE;
import util.gui.slider.GSliderVer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GScrollRows {

	private final List<RENDEROBJ> rows;
	private final List<RENDEROBJ> current;
	private final GuiSection srows = new GuiSection();
	private final GuiSection section = new GuiSection() {
		
		@Override
		public void render(snake2d.SPRITE_RENDERER r, float ds) {

			if (hoveredIs()) {
				double d = MButt.clearWheelSpin();
				if (d > 0) {
					first--;
				}else if (d < 0) {
					first++;
				}
			}
			init();
			super.render(r, ds);
		};
	};
	private int first = 0;
	private int last;

	public GScrollRows(Collection<RENDEROBJ> rows, int height){
		this(rows, height, 0);
	}
	
	public GScrollRows(Collection<RENDEROBJ> rows, int height, int width){
		this(rows, height, width, true);
	}

	public GScrollRows(Collection<RENDEROBJ> renrows, int height, int width, boolean slide){
		this.rows = new ArrayList<>(renrows);
		this.current = new ArrayList<>(renrows.size());
		section.body().setHeight(height);
		if (slide) width -= GSliderVer.WIDTH();
		int w = width;
		for (RENDEROBJ r : rows)
			if (r.body().width() > w)
				w = r.body().width();
		section.body().setWidth(w);
		if (slide) {
			GSliderVer slider = new GSliderVer(target, height);
			section.add(slider, section.body().x2(), section.body().y1());
		}
		srows.body().moveX1Y1(section.body());
		section.add(srows);
	}

	public void addRow(RENDEROBJ render) {
		rows.add(render);
	}

	public void addRow(RENDEROBJ render, int index) {
		rows.add(index, render);
	}

	public boolean removeRow(RENDEROBJ render) {
		return rows.remove(render);
	}

	public void clearRows() {
		rows.clear();
	}

	public synchronized boolean moveRow(RENDEROBJ render, int index) {
		boolean success = removeRow(render);

		if (!success) {
			return false;
		}

		addRow(render, index);
		return true;
	}

	public void init() {
		current.clear();
		int size = rows.size();
		for (int i = 0; i < size; i++) {
			if (passesFilter(i, rows.get(i))) {
				current.add(rows.get(i));
			}
		}

		int h = 0;
		last = 0;
		int currentSize = current.size();
		for (int i = currentSize -1; i >= 0; i--) {
			h += current.get(i).body().height();
			if (h > section.body().height()) {
				last = i+1;
				break;
			}
		}
		
		first = CLAMP.i(first, 0, last);
		srows.clear();
		srows.body().moveX1Y1(section.body());
		
		for (int i = first; i < currentSize; i++) {
			RENDEROBJ rr = current.get(i);
			if (srows.body().height()+rr.body().height() > section.body().height())
				break;
			srows.add(rr, srows.body().x1(), srows.getLastY2());
		}
	}

	
	protected boolean passesFilter(int i, RENDEROBJ o) {
		return true;
	}
	
	public CLICKABLE view() {
		return section;
	}
	
	public final INTE target = new INTE() {
		
		@Override
		public int min() {
			return 0;
		}
		
		@Override
		public int max() {
			return last;
		}
		
		@Override
		public int get() {
			return first;
		}
		
		@Override
		public void set(int t) {
			first = t;
			init();
		}
	};

}
