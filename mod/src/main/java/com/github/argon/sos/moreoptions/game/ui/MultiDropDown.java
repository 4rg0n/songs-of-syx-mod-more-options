package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.action.Action;
import com.github.argon.sos.moreoptions.game.action.Refreshable;
import com.github.argon.sos.moreoptions.game.action.VoidAction;
import com.github.argon.sos.moreoptions.game.api.GameUiApi;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snake2d.util.datatypes.DIR;
import util.gui.misc.GText;

import java.util.List;


public class MultiDropDown<Key> extends AbstractButton<List<Key>, MultiDropDown<Key>>
    implements Refreshable
{
    @Getter
    private final Select<Key> select;

    private final String label;

    @Setter
    @Accessors(fluent = true, chain = false)
    private VoidAction refreshAction = () -> {};

    @Builder
    public MultiDropDown(
        CharSequence label,
        CharSequence description,
        Select<Key> select,
        @Nullable Action<MultiDropDown<Key>> clickAction
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
                GameUiApi.getInstance().popup().show(this.select, this);
            });
        }

        select.toggleAction(key -> refresh());
    }

    @NotNull
    private String getAmount() {
        return "(" + this.select.getValue().size() + "/" + this.select.getMaxSelect() + ")";
    }

    @Override
    public List<Key> getValue() {
        return select.getValue();
    }

    @Override
    public void setValue(List<Key> value) {
        select.setValue(value);
    }

    @Override
    protected MultiDropDown<Key> element() {
        return this;
    }

    @Override
    public void refresh() {
        String title = this.label + " " +  getAmount();
        GText gText = new GText(UI.FONT().M, title);
        replaceLabel(gText, DIR.C);
        refreshAction.accept();
    }
}
