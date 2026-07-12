package com.github.argon.sos.mod.sdk.ui.button;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.game.action.Refreshable;
import com.github.argon.sos.mod.sdk.game.action.VoidAction;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.datatypes.DIR;
import util.gui.misc.GText;

import java.util.List;

/**
 * Displays a button, which when clicked will open a popup with a list of selectable options.
 * Multiple options can be selected.
 *
 * @param <Key> type of the key to identify each option
 */
public class MultiSelectDropDown<Key> extends AbstractButton<List<Key>>
    implements Refreshable
{
    @Getter
    private final Select<Key> select;
    private final String label;

    @Setter
    @Accessors(fluent = true, chain = false)
    private VoidAction refreshAction = () -> {};

    /**
     * Creates a new {@link MultiSelectDropDown}.
     *
     * @param label of the button
     * @param description optional description of the button when hovered
     * @param select the selection with multiple options
     * @param clickAction for the button
     */
    @Builder
    public MultiSelectDropDown(
        CharSequence label,
        @Nullable CharSequence description,
        Select<Key> select,
        @Nullable Action<MultiSelectDropDown<Key>> clickAction
    ) {
        super(label, description);
        this.label = label.toString();
        this.select = select;

        int menuWidth = select.body().width();
        if (menuWidth > body().width()) {
            body().setWidth(menuWidth);
        }

        refresh();

        if (clickAction != null) {
            clickActionSet(() -> {
                clickAction.accept(this);
            });
        } else {
            clickActionSet(() -> {
                ModSdkModule.gameApis().ui().popup().show(this.select, this);
            });
        }

        select.toggleAction(key -> refresh());
    }

    /**
     * Returns the total amount of options in the selection.
     *
     * @return total amount of options in the selection
     */
    public int getAmount() {
        return this.select.getValue().size();
    }

    /**
     * Returns the amount of selected options.
     *
     * @return amount of selected options
     */
    public int getSelectedAmount() {
        return this.select.getMaxSelect();
    }

    /**
     * Returns the selected option keys.
     *
     * @return selected option keys
     */
    @Override
    public List<Key> getValue() {
        return select.getValue();
    }

    /**
     * Selects the options with the keys.
     *
     * @param keys to select
     */
    @Override
    public void setValue(List<Key> keys) {
        select.setValue(keys);
    }

    /**
     * Refreshes the button title with new selected amounts.
     * Executes the {@link MultiSelectDropDown#refreshAction}.
     */
    @Override
    public void refresh() {
        String title = this.label + " " +  "(" + getSelectedAmount() + "/" + getAmount() + ")";
        GText gText = new GText(UI.FONT().M, title);
        replaceLabel(gText, DIR.C);
        refreshAction.accept();
    }
}
