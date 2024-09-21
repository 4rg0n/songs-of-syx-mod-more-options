package com.github.argon.sos.mod.sdk.ui;

import com.github.argon.sos.mod.sdk.game.action.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;

import java.util.Map;
import java.util.Optional;

/**
 * Builds a row or pillar with buttons to toggle / click.
 * When a button is clicked it will be marked "active".
 * Only one button can be active.
 */
public class Switcher<Key> extends Section implements
    Valuable<Key>,
    Resettable,
    Refreshable,
    Switchable<Key>
{

    @Getter
    @Nullable
    private Key activeKey;

    @Getter
    @Nullable
    private final Key initKey;

    @Getter
    @Nullable
    private Button activeButton;

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Key> switchAction = o -> {};

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Key> clickAction = o -> {};

    @Setter
    @Accessors(fluent = true, chain = false)
    private VoidAction refreshAction = () -> {};
    @Getter
    private final ButtonMenu<Key> menu;

    @Builder
    public Switcher(ButtonMenu<Key> menu, boolean highlight, @Nullable Key aktiveKey) {
        this.menu = menu;
        this.activeKey = aktiveKey;
        this.initKey = aktiveKey;

        menu.getButtons().forEach((key, button) -> {
            if (activeKey != null && activeKey.equals(key)) {
                activeButton = button;
            }

            initButton(key, button, highlight);
        });
        add(menu);
    }

    private void initButton(Key key, final Button button, boolean highlight) {
        button.clickActionSet(() -> {
            activeButton = button;
            clickAction.accept(key);
            switch_(key);
        });

        if (highlight) {
            button.renActionSet(() -> {
                boolean selected = key.equals(activeKey);
                button.selectedSet(selected);
            });
        }
    }

    @Override
    public void switch_(@Nullable Key key) {
        if (key == null) {
            return;
        }

        // no toggle happened?
        if (key.equals(activeKey)) {
            return;
        }

        get(key).ifPresent(element -> {
            activeKey = key;
            switchAction.accept(key);
        });
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
    @Nullable
    public Key getValue() {
        return activeKey;
    }

    @Override
    public void setValue(Key value) {
        switch_(value);
    }

    @Override
    public void reset() {
        this.activeKey = initKey;
    }

    @Override
    public void refresh() {
        refreshAction.accept();
    }
}
