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
import java.util.stream.Collectors;

public class DropDownList extends AbstractButton<List<String>, DropDownList> implements Refreshable {

    private final List<String> possibleValues;

    private final String label;

    @Nullable
    private final Action<DropDownList> clickAction;
    @Nullable
    private final Action<DropDown<String>> optionClickAction;
    @Nullable
    private final Action<DropDown<String>> optionCloseAction;
    @Getter
    private final UiList<String, DropDown<String>> uiList;

    @Setter
    @Accessors(fluent = true, chain = false)
    private VoidAction refreshAction = () -> {};

    @Builder
    public DropDownList(
        CharSequence label,
        @Nullable CharSequence description,
        List<String> values,
        List<String> possibleValues,
        int height,
        @Nullable Action<DropDownList> clickAction,
        @Nullable Action<DropDown<String>> optionClickAction,
        @Nullable Action<DropDown<String>> optionCloseAction
    ) {
        super(label, description);
        this.label = label.toString();
        assert !possibleValues.isEmpty() : "possibleValues must not be empty";
        this.possibleValues = possibleValues;
        this.clickAction = clickAction;
        this.optionClickAction = optionClickAction;
        this.optionCloseAction = optionCloseAction;

        List<DropDown<String>> dropDowns = values.stream()
            .map(this::dropDown)
            .collect(Collectors.toList());
        uiList = new UiList<>(dropDowns, height, this::dropDown);
        uiList.valueChangeAction(__ -> refresh());

        int menuWidth = uiList.body().width();
        if (menuWidth > body().width()) {
            body().setWidth(menuWidth);
        }
        refresh();

        if (this.clickAction != null) {
            clickActionSet(() -> {
                this.clickAction.accept(this);
            });
        } else {
            clickActionSet(() -> {
                GameUiApi.getInstance().popup().show(this.uiList, this);
            });
        }
    }

    @NotNull
    private String getAmount() {
        return "(" + this.uiList.getValue().size() + ")";
    }

    @Override
    public @Nullable List<String> getValue() {
        return uiList.getValue();
    }

    @Override
    public void setValue(List<String> values) {
        uiList.setValue(values);
    }

    private DropDown<String> dropDown(@Nullable String label) {
        if (label == null) {
            label = possibleValues.get(0);
        }

        return DropDown.<String>builder()
            .label(label)
            .clickAction(optionClickAction)
            .closeAction(optionCloseAction)
            .closeOnSelect(true)
            .menu(Switcher.<String>builder()
                .highlight(true)
                .aktiveKey(label)
                .menu(ButtonMenu.<String>builder()
                    .sameWidth(true)
                    .buttons(possibleValues)
                    .build())
                .build())
            .build();
    }

    @Override
    public void refresh() {
        String title = this.label + " " +  getAmount();
        GText gText = new GText(UI.FONT().M, title);
        replaceLabel(gText, DIR.C);
        refreshAction.accept();
    }

    @Override
    protected DropDownList element() {
        return this;
    }
}
