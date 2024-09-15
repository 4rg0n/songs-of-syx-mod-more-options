package com.github.argon.sos.mod.sdk.ui;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.game.action.Action;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.datatypes.DIR;
import snake2d.util.sprite.SPRITE;
import snake2d.util.sprite.text.Font;


public class DropDown<Key> extends AbstractButton<Key, DropDown<Key>> {
    @Getter
    private final Switcher<Key> menu;

    /**
     * Builds a button which opens a menu when clicked.
     *
     * @param label name of the select button
     * @param description description text when hovering the select button
     * @param font which font to use for the select button
     * @param menu contains the manu to display
     * @param closeOnSelect whether the menu shall close after clicking on an option
     * @param clickAction what shall happen when you click an option
     * @param closeAction what shall happen when the menu closes
     */
    @Builder
    public DropDown(
        CharSequence label,
        @Nullable CharSequence description,
        Font font,
        Switcher<Key> menu,
        boolean closeOnSelect,
        @Nullable Action<DropDown<Key>> clickAction,
        @Nullable Action<DropDown<Key>> closeAction
    ) {
        super(label, description, (font != null) ? font : UI.FONT().H2);
        this.menu = menu;
        Button activeButton = menu.getActiveButton();

        int menuWidth = menu.body().width();
        if (menuWidth > body().width()) {
            body().setWidth(menuWidth);
        }

        if (activeButton != null) {
            SPRITE activeLabel = activeButton.getLabel();

            replaceLabel(activeLabel.twin(activeLabel, DIR.C, 1), DIR.C);
            bg(activeButton.getBgColor());
        }

        if (clickAction != null) {
            clickActionSet(() -> {
                clickAction.accept(this);
            });
        } else {
            clickActionSet(() -> {
                ModSdkModule.gameApis().ui().popup().show(this.menu, this);
            });
        }

        menu.switchAction(key -> {
            menu.get(key).ifPresent(selectedButton -> {
                replaceLabel(selectedButton.getLabel(), DIR.C);
                bg(selectedButton.getBgColor());

                String value = selectedButton.getValue();
                if (value != null) {
                    try {
                        //noinspection unchecked
                        setValue((Key) value);
                    } catch (Exception e) {
                        // ignore
                    }
                }

                if (closeOnSelect) {
                    if (closeAction != null) {
                        closeAction.accept(this);
                    } else {
                        ModSdkModule.gameApis().ui().popup().close();
                    }
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
