package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.Getter;
import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.GuiSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Builds a row with buttons to toggle
 */
public class Toggler<Key> extends GuiSection implements
    Valuable<Key, Toggler<Key>>,
    Resettable<Toggler<Key>>,
    Refreshable<Toggler<Key>> {

    private final Collection<UiInfo<Key>> elements;

    @Getter
    private UiInfo<Key> activeInfo;

    @Getter
    private Button activeButton;

    private Action<Key> toggleAction = o -> {};

    private Action<Key> onClickAction = o -> {};

    private Action<Toggler<Key>> refreshAction = o -> {};

    public Toggler(Collection<UiInfo<Key>> elements) {
        this(elements, 0, false);
    }

    /**
     * @param elements list of info elements used for building the buttons
     * @param margin space between buttons
     */
    public Toggler(Collection<UiInfo<Key>> elements, int margin, boolean sameWidth) {
        this.elements = elements;

        // first element in map
        activeInfo = elements.iterator().next();
        List<Button> buttons = new ArrayList<>();

        elements.forEach(info -> {
            Button button = new Button(info.getTitle()) {
                @Override
                protected void clickA() {
                    activeButton = this;
                    onClickAction.accept(info.getKey());
                    toggle(info.getKey());
                }

                @Override
                protected void renAction() {
                    boolean selected = info.getKey().equals(activeInfo.getKey());

                    selectedSet(selected);
                }
            };
            button.hoverInfoSet(info.getDescription());
            buttons.add(button);
        });

        if (sameWidth) {
            int maxWidth = UiUtil.getMaxWidth(buttons);
            buttons.forEach(button -> {
                button.body().setWidth(maxWidth);
                addRight(margin, button);
            });
        } else {
            buttons.forEach(button -> {
                addRight(margin, button);
            });
        }
    }

    public void toggle(Key key) {
        // no toggle happened?
        if (activeInfo.getKey().equals(key)) {
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
            .filter(element -> element.getKey().equals(key))
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

    @Override
    public void onRefresh(Action<Toggler<Key>> refreshAction) {
        this.refreshAction = refreshAction;
    }

    public void onClick(Action<Key> clickAction) {
        this.onClickAction = clickAction;
    }
}
