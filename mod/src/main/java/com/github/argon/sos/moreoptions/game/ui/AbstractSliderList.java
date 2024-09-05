package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.action.Action;
import com.github.argon.sos.moreoptions.game.action.Refreshable;
import com.github.argon.sos.moreoptions.game.action.VoidAction;
import com.github.argon.sos.moreoptions.game.api.GameUiApi;
import init.sprite.UI.UI;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snake2d.util.datatypes.DIR;
import util.gui.misc.GText;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractSliderList<Value> extends AbstractButton<List<Value>, AbstractSliderList> implements Refreshable {

    @Nullable
    protected final Action<AbstractSliderList<Value>> clickAction;

    @Setter
    @Accessors(fluent = true, chain = false)
    protected VoidAction refreshAction = () -> {};

    protected final String label;

    @Getter
    protected final UiList<Value, Slider> uiList;

    public AbstractSliderList(
        CharSequence label,
        @Nullable CharSequence description,
        List<Slider> sliders,
        int height,
        Function<@Nullable Value, Slider> elementSupplier,
        @Nullable Action<AbstractSliderList<Value>> clickAction
    ) {
        super(label, description);
        this.label = label.toString();
        this.clickAction = clickAction;
        this.uiList = new UiList<>(sliders, height, elementSupplier);

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

    @Override
    protected AbstractSliderList<Value> element() {
        return this;
    }

    @NotNull
    private String getAmount() {
        return "(" + this.uiList.getValue().size() + ")";
    }

    @Override
    public void refresh() {
        String title = this.label + " " +  getAmount();
        GText gText = new GText(UI.FONT().M, title);
        replaceLabel(gText, DIR.C);
        refreshAction.accept();
    }
}
