package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.GuiSection;

import java.util.*;

/**
 * Builds a row or pillar with buttons to toggle / click.
 * When a button is clicked it will be marked "active".
 * Only one button can be active.
 */
public class Toggler<Key> extends GuiSection implements
    Valuable<Key, Toggler<Key>>,
    Resettable<Toggler<Key>>,
    Refreshable<Toggler<Key>> {

    private final Collection<UiInfo<Key>> elements;

    @Getter
    private UiInfo<Key> activeInfo;

    @Getter
    @Nullable
    private Button activeButton;

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Key> toggleAction = o -> {};

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Key> clickAction = o -> {};

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Toggler<Key>> refreshAction = o -> {};

    public Toggler(List<UiInfo<Key>> elements) {
        this(elements, 0, true, true, true);
    }

    /**
     * @param elements list of info elements used for building the buttons
     * @param margin space between buttons
     * @param sameWidth whether all buttons will get the widest with of them all
     * @param highlight whether the selected button shall be highlighted
     */
    @Builder
    public Toggler(List<UiInfo<Key>> elements, int margin, boolean sameWidth, boolean horizontal, boolean highlight) {
        this.elements = elements;

        // first element in map
        activeInfo = elements.iterator().next();
        Map<Key, Button> buttons = new LinkedHashMap<>(); // preserve order

        for (UiInfo<Key> info : elements) {
            Button button = buildButton(highlight, info);
            buttons.put(info.getKey(), button);
        }

        ButtonMenu<Key> keyButtonMenu = ButtonMenu.<Key>builder()
            .buttons(buttons)
            .margin(margin)
            .sameWidth(sameWidth)
            .horizontal(horizontal)
            .build();

        add(keyButtonMenu);
    }

    @NotNull
    private Button buildButton(boolean highlight, UiInfo<Key> info) {
        Button button;
        if (highlight) {
            button = new Button(info.getTitle(), info.getDescription()) {
                @Override
                protected void clickA() {
                    activeButton = this;
                    Toggler.this.clickAction.accept(info.getKey());
                    toggle(info.getKey());
                }

                @Override
                protected void renAction() {
                    boolean selected = false;
                    if (info.getKey() != null) {
                        selected = info.getKey().equals(activeInfo.getKey());
                    }
                    selectedSet(selected);
                }
            };
        } else {
            button = new Button(info.getTitle(), info.getDescription()) {
                @Override
                protected void clickA() {
                    activeButton = this;
                    Toggler.this.clickAction.accept(info.getKey());
                    toggle(info.getKey());
                }
            };
        }
        return button;
    }

    public void toggle(@Nullable Key key) {
        if (key == null) {
            return;
        }

        // no toggle happened?
        if (key.equals(activeInfo.getKey())) {
            return;
        }

        get(key).ifPresent(element -> {
            activeInfo = element;
            toggleAction.accept(key);
        });
    }

    public void onToggle(Action<Key> Action) {
        toggleAction = Action;
    }

    public Optional<UiInfo<Key>> get(Key key) {
        return elements.stream()
            .filter(element -> key.equals(element.getKey()))
            .findFirst();
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        super.render(r, ds);
    }

    @Override
    public Key getValue() {
        return activeInfo.getKey();
    }

    @Override
    public void setValue(Key value) {
        toggle(value);
    }

    @Override
    public void reset() {
        elements.stream()
            .filter(element -> element instanceof Resettable)
            .map(Resettable.class::cast)
            .forEach(Resettable::reset);
    }

    @Override
    public void refresh() {
        refreshAction.accept(this);
    }
}
