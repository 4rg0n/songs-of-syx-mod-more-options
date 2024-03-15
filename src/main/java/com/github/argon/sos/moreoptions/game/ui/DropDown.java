package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.api.GameUiApi;
import lombok.Builder;
import snake2d.util.datatypes.DIR;
import snake2d.util.sprite.SPRITE;


public class DropDown<Key> extends AbstractButton<Key, DropDown<Key>> {
    private final Toggler<Key> menu;

    @Builder
    public DropDown(CharSequence label, CharSequence description, Toggler<Key> menu, final boolean closeOnSelect) {
        super(label, description);
        this.menu = menu;
        Button activeButton = menu.getActiveButton();
        body().setWidth(menu.body().width());

        if (activeButton != null) {
            SPRITE activeLabel = activeButton.getLabel();

            replaceLabel(activeLabel.twin(activeLabel, DIR.C, 1), DIR.C);
            bg(activeButton.getColor());
        }

        clickActionSet(() -> {
            GameUiApi.getInstance().popup().show(this.menu, this);
        });

        menu.onToggle(key -> {
            menu.get(key).ifPresent(selectedButton -> {
                replaceLabel(selectedButton.getLabel(), DIR.C);
                bg(selectedButton.getColor());

                if (closeOnSelect) {
                    GameUiApi.getInstance().popup().close();
                }
            });
        });
    }

    @Override
    public Key getValue() {
        return menu.getValue();
    }

    @Override
    public void setValue(Key value) {
        menu.setValue(value);
    }

    @Override
    protected DropDown<Key> element() {
        return this;
    }
}
