package com.github.argon.sos.mod.sdk.ui.button;

import com.github.argon.sos.mod.sdk.game.action.*;
import com.github.argon.sos.mod.sdk.ui.util.Section;
import com.github.argon.sos.mod.sdk.ui.menu.ButtonMenu;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Builds a row or pillar with buttons to toggle / click.
 * When a button is clicked, it will be marked "active".
 * Only one button can be active.
 *
 * @param <Key> the type of the keys used when selecting
 */
public class Select<Key> extends Section implements
    Valuable<List<Key>>,
    Resettable,
    Refreshable,
    Selectable<Key, AbstractButton<Key>>
{

    @Getter
    private final List<Key> selectedKeys;

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Key> selectAction = o -> {};

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Key> clickAction = o -> {};

    @Setter
    @Accessors(fluent = true, chain = false)
    private VoidAction refreshAction = () -> {};

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Key> toggleAction = o -> {};

    @Getter
    private final ButtonMenu<Key> menu;

    private final int maxSelect;

    private final List<Key> initActiveKeys;

    /**
     * Displays multiple buttons, which can be selected.
     *
     * @param menu containing the selectable buttons
     * @param selectedKeys which shall be selected by default
     * @param maxSelect amount of max available selectable options; 0 for all
     */
    @Builder
    public Select(ButtonMenu<Key> menu, List<Key> selectedKeys, int maxSelect) {
        this.selectedKeys = new ArrayList<>(menu.getButtons().size());
        this.menu = menu;

        if (maxSelect > 0) {
            this.maxSelect = maxSelect;
        } else {
            this.maxSelect = menu.getButtons().size();
        }

        if (selectedKeys == null) {
            selectedKeys = List.of();
        }
        this.initActiveKeys = selectedKeys;

        menu.getButtons().forEach(this::initButton);
        setValue(selectedKeys);
        add(menu);
    }

    private void initButton(Key key, final AbstractButton<Key> button) {
        button.clickActionSet(() -> {
            clickAction.accept(key);
            toggle(key);
        });

        button.renActionSet(() -> {
            boolean selected = selectedKeys.contains(key);
            button.selectedSet(selected);
        });
    }

    /**
     * Toggles the selected state of an option with given key.
     * Executes the {@link Select#toggleAction}.
     *
     * @param key to toggle
     */
    public void toggle(@Nullable Key key) {
        if (key == null) {
            return;
        }

        if (selectedKeys.contains(key)) {
            selectedKeys.remove(key);
        } else {
            select(key);
        }
        toggleAction.accept(key);
    }

    /**
     * Removes all selections of all options.
     */
    public void clearSelection() {
        selectedKeys.clear();
    }

    @Override
    public void select(List<Key> keys) {
        keys.forEach(this::select);
    }

    /**
     * Selects an options with given key.
     * Executes the {@link Select#selectAction}.
     *
     * @param key to select
     */
    public void select(@Nullable Key key) {
        if (key == null) {
            return;
        }

        if (maxSelect > 0 && selectedKeys.size() >= maxSelect) {
            return;
        }

        if (selectedKeys.contains(key)) {
            return;
        }

        selectedKeys.add(key);
        selectAction.accept(key);
    }

    /**
     * Returns an option with the given key if present.
     *
     * @param key of the option
     * @return option with the given key if present.
     */
    public Optional<? extends AbstractButton<Key>> get(Key key) {
        return menu.getButtons().entrySet().stream()
            .filter(entry -> key.equals(entry.getKey()))
            .map(Map.Entry::getValue)
            .findFirst();
    }

    /**
     * Executed when the select is rendered.
     *
     * @param renderer to use
     * @param deltaSeconds since las render loop
     */
    @Override
    public void render(SPRITE_RENDERER renderer, float deltaSeconds) {
        super.render(renderer, deltaSeconds);
    }

    /**
     * Returns a list with the selected keys.
     *
     * @return list with the selected keys
     */
    @Override
    public List<Key> getValue() {
        return getSelectedKeys();
    }

    /**
     * Returns the selected buttons.
     *
     * @return selected buttons
     */
    @Override
    public List<AbstractButton<Key>> getSelection() {
        return selectedKeys.stream()
            .map(menu::get)
            .toList();
    }

    /**
     * Selects the options with the given keys from the list.
     *
     * @param keys to select
     */
    @Override
    public void setValue(List<Key> keys) {
        clearSelection();
        keys.forEach(this::select);
    }

    /**
     * Resets the selections to the initial state.
     */
    @Override
    public void reset() {
        clearSelection();
        this.selectedKeys.addAll(initActiveKeys);
    }

    /**
     * Executes the {@link Select#refreshAction}
     */
    @Override
    public void refresh() {
        refreshAction.accept();
    }

    /**
     * Returns the amount of selected options, which can be selected.
     *
     * @return amount of selected options, which can be selected
     */
    public int getMaxSelect() {
        if (maxSelect > 0) {
            return maxSelect;
        }

        return menu.getButtons().size();
    }
}
