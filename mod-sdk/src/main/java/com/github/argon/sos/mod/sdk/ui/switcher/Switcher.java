package com.github.argon.sos.mod.sdk.ui.switcher;

import com.github.argon.sos.mod.sdk.game.action.*;
import com.github.argon.sos.mod.sdk.ui.util.Section;
import com.github.argon.sos.mod.sdk.ui.button.AbstractButton;
import com.github.argon.sos.mod.sdk.ui.menu.ButtonMenu;
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
 * When a button is clicked, it will be marked "active".
 * Only one button can be active.
 *
 * @param <Key> the type of the key used for switching
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
    private AbstractButton<Key> activeButton;

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Key> switchAction = o -> {};

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Key> clickAction = o -> {};

    @Setter
    @Accessors(fluent = true, chain = false)
    private VoidAction refreshAction = () -> {};

    @Setter
    @Accessors(fluent = true, chain = false)
    private VoidAction resetAction = () -> {};

    @Getter
    private final ButtonMenu<Key> menu;

    /**
     * Creates a new {@link Switcher} with given {@link ButtonMenu} to switch between views.
     *
     * @param menu to use for switching between views
     * @param highlight whether the button for the active view shall be highlighted
     * @param activeKey of which view shall be active when rendered
     */
    @Builder
    public Switcher(ButtonMenu<Key> menu, boolean highlight, @Nullable Key activeKey) {
        this.menu = menu;
        this.activeKey = activeKey;
        this.initKey = activeKey;

        menu.getButtons().forEach((key, button) -> {
            if (this.activeKey != null && this.activeKey.equals(key)) {
                activeButton = button;
            }

            initButton(key, button, highlight);
        });
        add(menu);
    }

    private void initButton(Key key, final AbstractButton<Key> button, boolean highlight) {
        button.clickActionSet(() -> {
            activeButton = button;
            clickAction.accept(key);
            doSwitch(key);
        });

        if (highlight) {
            button.renActionSet(() -> {
                boolean selected = key.equals(activeKey);
                button.selectedSet(selected);
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSwitch(@Nullable Key key) {
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

    /**
     * Tries to get the view vy the given key.
     *
     * @param key of the view
     * @return found view if present
     */
    public Optional<? extends AbstractButton<Key>> get(Key key) {
        return menu.getButtons().entrySet().stream()
            .filter(entry -> key.equals(entry.getKey()))
            .map(Map.Entry::getValue)
            .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(SPRITE_RENDERER renderer, float deltaSeconds) {
        super.render(renderer, deltaSeconds);
    }

    /**
     * Returns the active vew key.
     *
     * @return active vew key
     */
    @Override
    @Nullable
    public Key getValue() {
        return activeKey;
    }

    /**
     * @see Switcher#doSwitch(Object)
     */
    @Override
    public void setValue(Key value) {
        doSwitch(value);
    }

    /**
     * Displays the view, which the switcher was created with.
     * Executes {@link Switcher#resetAction}
     */
    @Override
    public void reset() {
        this.activeKey = initKey;
        resetAction.accept();
    }

    /**
     * Executes {@link Switcher#refreshAction}
     */
    @Override
    public void refresh() {
        refreshAction.accept();
    }
}
