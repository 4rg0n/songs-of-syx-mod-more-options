package com.github.argon.sos.mod.sdk.ui.input;

import com.github.argon.sos.mod.sdk.game.action.Valuable;
import com.github.argon.sos.mod.sdk.game.action.WheelScrollable;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import init.sprite.UI.UI;
import org.jetbrains.annotations.Nullable;
import snake2d.MButt;
import snake2d.Mouse;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.datatypes.Coo;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.sprite.text.Font;
import snake2d.util.sprite.text.Str;
import util.colors.GCOLOR;
import util.data.INT.IntImp;
import util.gui.slider.GSliderVer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A multi line input field for text. Behaves similar to an HTML textarea:
 * text wraps within a fixed width and a vertical scrollbar appears once the
 * text exceeds the configured height.
 */
public class InputArea extends CLICKABLE.ClickableAbs implements Valuable<String>, WheelScrollable {

    private static final int PADDING = 6;
    private static final String CURSOR = "|";

    private final MultilineInputSprite input;
    private final GSliderVer slider;
    private final IntImp scroll = new IntImp();

    private boolean dragging = false;
    private int lastMarker = -1;

    private Supplier<Coo> mouseCooSupplier = UiUtil.MOUSE_COO_SUPPLIER;

    /**
     * Creates a new {@link InputArea} with given fixed size.
     *
     * @param width of the input field in pixels
     * @param height of the input field in pixels
     */
    public InputArea(int width, int height) {
        this(width, height, 2000);
    }

    /**
     * Creates a new {@link InputArea} with given fixed size.
     *
     * @param width of the input field in pixels
     * @param height of the input field in pixels
     * @param maxChars maximum amount of characters which can be inputted
     */
    public InputArea(int width, int height, int maxChars) {
        this(width, height, maxChars, UI.FONT().S, null);
    }

    /**
     * Creates a new {@link InputArea} with given fixed size.
     *
     * @param width of the input field in pixels
     * @param height of the input field in pixels
     * @param maxChars maximum amount of characters which can be inputted
     * @param font to render the text with
     * @param mouseCoordinateSupplier optional supplier for mouse coordinates
     */
    public InputArea(int width, int height, int maxChars, Font font, @Nullable Supplier<Coo> mouseCoordinateSupplier) {
        this.input = new MultilineInputSprite(maxChars, font);
        if (mouseCoordinateSupplier != null) this.mouseCooSupplier = mouseCoordinateSupplier;

        body.setWidth(width);
        body.setHeight(height);

        this.slider = new GSliderVer(scroll, Math.max(1, height - PADDING * 2));
        layoutSlider();
    }

    /**
     * Sets a placeholder text shown when the field is empty and not focused.
     *
     * @param placeholder text to show
     * @return this
     */
    public InputArea placeHolder(@Nullable CharSequence placeholder) {
        input.placeHolder(placeholder);
        return this;
    }

    /**
     * Sets cursor into field and selects everything.
     */
    public void focus() {
        Mouse.currentClicked = this;
        input.listen();
        input.selectAll();
    }

    /**
     * Listens to input without changing the selection.
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return input.text().toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(@Nullable String value) {
        input.set(value == null ? "" : value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void valueChangeAction(Consumer<String> valueChangeAction) {
        input.onChange(() -> valueChangeAction.accept(getValue()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wantsWheelScroll() {
        return scroll.max > scroll.min;
    }

    /**
     * Executed when the input field is hovered.
     *
     * @param mCoo mouse coordinates
     * @return whether the field or its scrollbar is hovered
     */
    @Override
    public boolean hover(COORDINATE mCoo) {
        layoutSlider();
        boolean hovered = super.hover(mCoo);
        slider.hover(mCoo);
        return hovered;
    }

    /**
     * Executed when the field is clicked.
     *
     * @return whether the click was handled
     */
    @Override
    public boolean click() {
        if (slider.click()) {
            return true;
        }

        if (super.click()) {
            Mouse.currentClicked = this;

            List<int[]> rows = computeRows();
            Coo mouse = mouseCooSupplier.get();
            int px = mouse.x() - innerX1();
            int py = mouse.y() - innerY1();

            if (!input.listening() || MButt.LEFT.isDouble()) {
                input.listen();
                input.selectAll();
                dragging = false;
            } else {
                input.moveTo(indexAt(rows, px, py));
                dragging = true;
            }
            return true;
        }
        return false;
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

        layoutSlider();

        List<int[]> rows = computeRows();
        int visibleRows = visibleRows();
        int maxScroll = Math.max(0, rows.size() - visibleRows);

        scroll.min = 0;
        scroll.max = maxScroll;
        if (scroll.i > maxScroll)
            scroll.i = maxScroll;

        if (Mouse.currentClicked == this)
            input.listen();

        int marker = input.marker();
        if (input.listening() && marker != lastMarker) {
            int markerRow = rowIndexOf(rows, marker);
            if (markerRow < scroll.i) {
                scroll.i = markerRow;
            } else if (markerRow >= scroll.i + visibleRows) {
                scroll.i = markerRow - visibleRows + 1;
            }
            scroll.i = Math.max(0, Math.min(scroll.i, maxScroll));
        }
        lastMarker = marker;

        if (isHovered) {
            double wheel = MButt.clearWheelSpin();
            if (wheel > 0) {
                scroll.inc(-1);
            } else if (wheel < 0) {
                scroll.inc(1);
            }
        }

        if (isHovered || Mouse.currentClicked == this) {
            GCOLOR.UI().NORMAL.hovered.render(renderer, body());
        }

        slider.render(renderer, deltaSeconds);

        dragging &= MButt.LEFT.isDown();
        if (dragging) {
            Coo mouse = mouseCooSupplier.get();
            int px = mouse.x() - innerX1();
            int py = mouse.y() - innerY1();
            input.selectTo(indexAt(rows, px, py));
        }

        renderText(renderer, rows);

        GCOLOR.UI().border().renderFrame(renderer, body, 0, 2);
    }

    private void renderText(SPRITE_RENDERER renderer, List<int[]> rows) {
        Font f = input.font();
        CharSequence text = input.text();
        int lineHeight = f.height();

        int x1 = innerX1();
        int y1 = innerY1();
        int y2 = body().y2() - PADDING;

        boolean listening = input.listening();
        boolean empty = text.length() == 0;

        if (!listening && empty && input.placeholder() != null) {
            COLOR.WHITE65.bind();
            f.render(renderer, input.placeholder(), x1, y1);
            COLOR.unbind();
            return;
        }

        int marker = input.marker();
        boolean hasSelection = input.hasSelection();
        int selStart = hasSelection ? input.selectionStart() : -1;
        int selEnd = hasSelection ? input.selectionEnd() : -1;
        int markerRow = rowIndexOf(rows, marker);

        int y = y1;
        for (int ri = scroll.i; ri < rows.size(); ri++) {
            if (y + lineHeight > y2)
                break;

            int[] row = rows.get(ri);

            if (hasSelection && selStart < row[1] && selEnd > row[0]) {
                int s = Math.max(selStart, row[0]);
                int e = Math.min(selEnd, row[1]);
                int sx = x1 + f.width(text, row[0], s, 1.0);
                int ex = x1 + f.width(text, row[0], e, 1.0);
                if (ex > sx)
                    COLOR.WHITE50.render(renderer, sx, ex, y, y + lineHeight);
            }

            f.render(renderer, text, x1, y, row[0], row[1], 1.0);

            if (listening && ri == markerRow) {
                int mx = x1 + f.width(text, row[0], marker, 1.0);
                COLOR.BLACK2WHITE.bind();
                f.render(renderer, CURSOR, mx, y);
                COLOR.unbind();
            }

            y += lineHeight;
        }
    }

    private void layoutSlider() {
        slider.body().moveX2(body().x2() - PADDING);
        slider.body().moveY1(body().y1() + PADDING);
    }

    private int innerX1() {
        return body().x1() + PADDING;
    }

    private int innerY1() {
        return body().y1() + PADDING;
    }

    private int innerWidth() {
        int width = body().width() - PADDING * 2 - GSliderVer.WIDTH() - 4;
        return Math.max(width, input.font().maxCWidth);
    }

    private int innerHeight() {
        return body().height() - PADDING * 2;
    }

    private int visibleRows() {
        return Math.max(1, innerHeight() / input.font().height());
    }

    /**
     * Splits the current text into wrapped rows, honouring explicit newlines.
     * Each entry contains the start (inclusive) and end (exclusive) character index of a row.
     *
     * @return list of rows as {start, end} index pairs
     */
    private List<int[]> computeRows() {
        List<int[]> rows = new ArrayList<>();
        CharSequence text = input.text();
        Font f = input.font();
        int width = innerWidth();

        if (text.length() == 0) {
            rows.add(new int[]{0, 0});
            return rows;
        }

        int start = 0;
        while (start < text.length()) {
            int end = f.getEndIndex(text, start, text.length(), width, 1.0);
            rows.add(new int[]{start, end});
            start = f.getStartIndex(text, end);
        }

        if (text.charAt(text.length() - 1) == Font.nl) {
            rows.add(new int[]{text.length(), text.length()});
        }

        return rows;
    }

    private int rowIndexOf(List<int[]> rows, int index) {
        int ri = 0;
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i)[0] <= index)
                ri = i;
            else
                break;
        }
        return ri;
    }

    private int indexAt(List<int[]> rows, int px, int py) {
        if (rows.isEmpty())
            return 0;

        int lineHeight = input.font().height();
        int rowIndex = scroll.i + Math.max(0, py) / lineHeight;
        rowIndex = Math.min(rowIndex, rows.size() - 1);
        rowIndex = Math.max(rowIndex, 0);

        return findColumnInRow(rows.get(rowIndex), px);
    }

    private int findColumnInRow(int[] row, int px) {
        CharSequence text = input.text();
        Font f = input.font();

        int end = row[1];
        if (end > row[0] && text.charAt(end - 1) == Font.nl)
            end--;

        int x = 0;
        for (int i = row[0]; i < end; i++) {
            int w = f.width(text.charAt(i), 1.0);
            if (i > row[0])
                w -= f.getBack(text.charAt(i - 1), text.charAt(i), 1.0);
            if (x + w / 2 >= px)
                return i;
            x += w;
        }
        return end;
    }
}
