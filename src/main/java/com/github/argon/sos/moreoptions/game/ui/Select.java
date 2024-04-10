package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.action.*;
import com.github.argon.sos.moreoptions.util.Lists;
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
 * When a button is clicked it will be marked "active".
 * Only one button can be active.
 */
public class Select<Key> extends Section implements
    Valuable<List<Key>>,
    Resettable,
    Refreshable
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

    @Builder
    public Select(ButtonMenu<Key> menu, List<Key> selectedKeys, int maxSelect) {
        this.selectedKeys = new ArrayList<>(menu.getButtons().size());
        this.menu = menu;
        this.maxSelect = maxSelect;
        if (selectedKeys == null) {
            selectedKeys = Lists.of();
        }
        this.initActiveKeys = selectedKeys;

        menu.getButtons().forEach(this::initButton);
        setValue(selectedKeys);
        add(menu);
    }

    private void initButton(Key key, final Button button) {
        button.clickActionSet(() -> {
            clickAction.accept(key);
            toggle(key);
        });

        button.renActionSet(() -> {
            boolean selected = selectedKeys.contains(key);
            button.selectedSet(selected);
        });
    }

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

    public void clearSelection() {
        selectedKeys.clear();
    }

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

    public void onSelect(Action<Key> Action) {
        selectAction = Action;
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
    public List<Key> getValue() {
        return getSelectedKeys();
    }

    @Override
    public void setValue(List<Key> value) {
        clearSelection();
        value.forEach(this::select);
    }

    @Override
    public void reset() {
        this.selectedKeys.clear();
        this.selectedKeys.addAll(initActiveKeys);
    }

    @Override
    public void refresh() {
        refreshAction.accept();
    }

    public Integer getMaxSelect() {
        if (maxSelect > 0) {
            return maxSelect;
        }

        return menu.getButtons().size();
    }
}
