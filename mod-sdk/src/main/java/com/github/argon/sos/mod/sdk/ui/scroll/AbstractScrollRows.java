package com.github.argon.sos.mod.sdk.ui.scroll;

import com.github.argon.sos.mod.sdk.game.action.Initializable;
import com.github.argon.sos.mod.sdk.game.action.Refreshable;
import snake2d.MButt;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.misc.CLAMP;
import util.data.INT.INTE;
import util.gui.slider.GSliderVer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base class for making content scroll vertically.
 * This was copied and slightly modified from the games {@link util.gui.table.GScrollRows}.
 */
public abstract class AbstractScrollRows implements Refreshable, Initializable<Void> {

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
			refresh();
		}
	};
	private int first = 0;
	private int last;

	/**
	 * Creates new scroll rows with given elements / rows and the available height.
	 * If the content exceeds the height, a scrollbar will be added.
	 *
	 * @param rows to add
	 * @param height available for the view
	 */
	public AbstractScrollRows(Collection<RENDEROBJ> rows, int height){
		this(rows, height, 0);
	}

	/**
	 * Creates new scroll rows with given elements / rows and the available height.
	 * If the content exceeds the height, a scrollbar will be added.
	 *
	 * @param rows to add
	 * @param height available for the view
	 * @param width available for the view
	 */
	public AbstractScrollRows(Collection<RENDEROBJ> rows, int height, int width){
		this(rows, height, width, true);
	}

	/**
	 * Creates new scroll rows with given elements / rows and the available height.
	 * If the content exceeds the height, a scrollbar will be added.
	 *
	 * @param rows to add
	 * @param height available for the view
	 * @param width available for the view
	 * @param slide whether scrollbar shall always be displayed
	 */
	public AbstractScrollRows(Collection<RENDEROBJ> rows, int height, int width, boolean slide){
		this.rows = new ArrayList<>(rows);
		this.current = new ArrayList<>(rows.size());
		section.body().setHeight(height);
		if (slide) width -= GSliderVer.WIDTH();
		int w = width;
		for (RENDEROBJ r : this.rows)
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

	/**
	 * Adds a new row.
	 *
	 * @param row to add
	 */
	public void addRow(RENDEROBJ row) {
		rows.add(row);
	}

	/**
	 * Replaces a row at given position.
	 *
	 * @param row to add
	 * @param position where to add it
	 */
	public void addRow(RENDEROBJ row, int position) {
		rows.add(position, row);
	}

	/**
	 * Removes a given row.
	 *
	 * @param row to remove
	 * @return if the row was present and removed
	 */
	public boolean removeRow(RENDEROBJ row) {
		return rows.remove(row);
	}

	/**
	 * Removes all rows.
	 */
	public void clearRows() {
		rows.clear();
	}

	/**
	 * Moves a row to a given position.
	 *
	 * @param row to move
	 * @param position to move to
	 * @return whether the row was moved successfully
	 */
	public synchronized boolean moveRow(RENDEROBJ row, int position) {
		boolean success = removeRow(row);

		if (!success) {
			return false;
		}

		addRow(row, position);
		return true;
	}

	/**
	 * Reinitializes the rows.
	 */
	@Override
	public void refresh() {
		init();
	}

	/**
	 * Initializes the rows.
	 */
	@Override
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

	/**
	 * For filtering rows when initializing via {@link AbstractScrollRows#init()}
	 *
	 * @param position of the row
	 * @param row to be filtered
	 * @return whether row shall be added or not
	 */
	protected boolean passesFilter(int position, RENDEROBJ row) {
		return true;
	}

	/**
	 * Returns the {@link GuiSection} containing the rows.
	 *
	 * @return the section containing the rows
	 */
	public GuiSection getView() {
		return section;
	}
}
