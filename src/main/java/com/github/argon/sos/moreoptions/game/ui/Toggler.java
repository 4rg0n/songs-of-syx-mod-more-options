package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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

    private Key activeKey;
    private Key initKey;

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
    @Getter
    private final ButtonMenu<Key> menu;


    @Builder
    public Toggler(ButtonMenu<Key> menu, boolean highlight) {
        this.menu = menu;
        menu.getButtons().forEach((key, button) -> {
            if (activeKey == null) {
                activeKey = key;
                initKey = key;
            }

            initButton(key, button, highlight);
        });
        add(menu);
    }

    private void initButton(Key key, final Button button, boolean highlight) {
        button.clickActionSet(() -> {
            activeButton = button;
            clickAction.accept(key);
            toggle(key);
        });

        if (highlight) {
            button.renActionSet(() -> {
                boolean selected = key.equals(activeKey);
                button.selectedSet(selected);
            });
        }
    }

    public void toggle(@Nullable Key key) {
        if (key == null) {
            return;
        }

        // no toggle happened?
        if (key.equals(activeKey)) {
            return;
        }

        get(key).ifPresent(element -> {
            activeKey = key;
            toggleAction.accept(key);
        });
    }

    public void onToggle(Action<Key> Action) {
        toggleAction = Action;
    }

    public Optional<Button> get(Key key) {
        return menu.getButtons().entrySet().stream()
            .filter(entry -> key.equals(entry.getKey()))
            .map(Map.Entry::getValue)
            .findFirst();
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        super.render(r, ds);
    }

    @Override
    public Key getValue() {
        return activeKey;
    }

    @Override
    public void setValue(Key value) {
        toggle(value);
    }

    @Override
    public void reset() {
        this.activeKey = initKey;
    }

    @Override
    public void refresh() {
        refreshAction.accept(this);
    }
}
