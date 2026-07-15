package com.github.argon.sos.mod.sdk.ui.input;

import org.jetbrains.annotations.Nullable;
import snake2d.Input.CHAR_LISTENER;
import snake2d.util.sprite.text.Font;
import snake2d.util.sprite.text.Str;

/**
 * Text buffer with cursor and selection handling for {@link InputArea}.
 * Newlines are stored as regular {@link Font#nl} characters inside the text buffer,
 * so the actual line wrapping / rendering is done by {@link InputArea} itself.
 */
class MultilineInputSprite extends CHAR_LISTENER {

    private static final Str tmp = new Str(4096);

    private Font f;
    @Nullable
    private CharSequence placeholder;
    @Nullable
    private Runnable onChange;

    private int marker = 0;
    private int selectedI = -1;

    MultilineInputSprite(int size, Font font) {
        super(size);
        this.f = font;
    }

    MultilineInputSprite placeHolder(@Nullable CharSequence placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @Nullable
    CharSequence placeholder() {
        return placeholder;
    }

    void onChange(@Nullable Runnable onChange) {
        this.onChange = onChange;
    }

    MultilineInputSprite font(Font f) {
        this.f = f;
        return this;
    }

    Font font() {
        return f;
    }

    /**
     * Current cursor position, clamped into the valid text range.
     *
     * @return current cursor position
     */
    int marker() {
        if (marker > text().length())
            marker = text().length();
        if (marker < 0)
            marker = 0;
        return marker;
    }

    boolean hasSelection() {
        return selectedI >= 0 && selectedI != marker();
    }

    int selectionStart() {
        return Math.min(marker(), selectedI);
    }

    int selectionEnd() {
        return Math.max(marker(), selectedI);
    }

    /**
     * Moves the cursor to the given index and clears any selection.
     *
     * @param index to move the cursor to
     */
    void moveTo(int index) {
        marker = index;
        selectedI = -1;
    }

    /**
     * Extends (or starts) a selection from the current anchor towards the given index.
     *
     * @param index to extend the selection to
     */
    void selectTo(int index) {
        if (selectedI < 0)
            selectedI = marker();
        marker = index;
    }

    void selectAll() {
        marker = 0;
        selectedI = text().length();
    }

    @Override
    public void set(CharSequence name) {
        super.set(name);
        marker = 0;
        selectedI = -1;
    }

    @Override
    protected void acceptChar(char c) {
        if (!listening())
            return;

        if (hasSelection()) {
            deleteSelection();
        }

        if (text().spaceLeft() > 0) {
            insertAt(marker(), c);
            marker++;
        }

        selectedI = -1;
        change();
    }

    /**
     * Enter inserts a newline instead of submitting / closing the field.
     */
    @Override
    protected void enter() {
        acceptChar('\n');
    }

    @Override
    protected void backspace() {
        if (!listening())
            return;

        if (hasSelection()) {
            deleteSelection();
        } else if (marker() > 0) {
            removeAt(marker() - 1);
            marker--;
        }

        selectedI = -1;
        change();
    }

    @Override
    public void del() {
        if (!listening())
            return;

        if (hasSelection()) {
            deleteSelection();
        } else if (marker() < text().length()) {
            removeAt(marker());
        }

        selectedI = -1;
        change();
    }

    @Override
    public void left(boolean mod) {
        if (mod) {
            if (selectedI < 0)
                selectedI = marker();
            selectedI = Math.max(0, selectedI - 1);
        } else if (selectedI >= 0) {
            marker = selectionStart();
            selectedI = -1;
        } else {
            marker = Math.max(0, marker() - 1);
        }
    }

    @Override
    public void right(boolean mod) {
        if (mod) {
            if (selectedI < 0)
                selectedI = marker();
            selectedI = Math.min(text().length(), selectedI + 1);
        } else if (selectedI >= 0) {
            marker = selectionEnd();
            selectedI = -1;
        } else {
            marker = Math.min(text().length(), marker() + 1);
        }
    }

    @Override
    protected void change() {
        if (onChange != null)
            onChange.run();
    }

    private void insertAt(int index, char c) {
        tmp.clear().add(text());
        Str t = text();
        t.clear();

        for (int i = 0; i < tmp.length(); i++) {
            if (i == index)
                t.add(c);
            t.add(tmp.charAt(i));
        }

        if (index >= tmp.length())
            t.add(c);
    }

    private void removeAt(int index) {
        tmp.clear().add(text());
        Str t = text();
        t.clear();

        for (int i = 0; i < tmp.length(); i++) {
            if (i != index)
                t.add(tmp.charAt(i));
        }
    }

    private void deleteSelection() {
        int s = selectionStart();
        int e = selectionEnd();

        tmp.clear().add(text());
        Str t = text();
        t.clear();

        for (int i = 0; i < tmp.length(); i++) {
            if (i < s || i >= e)
                t.add(tmp.charAt(i));
        }

        marker = s;
    }
}
