package com.github.argon.sos.mod.sdk.ui.slider;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.game.action.Refreshable;
import com.github.argon.sos.mod.sdk.game.action.VoidAction;
import com.github.argon.sos.mod.sdk.ui.util.UiList;
import com.github.argon.sos.mod.sdk.ui.button.AbstractButton;
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

/**
 * Base class for a list containing multiple {@link Slider}s.
 * Will display a button, which on click opens a popup with the slider list.
 *
 * @param <Value> type of the value of all sliders
 */
public abstract class AbstractSliderList<Value> extends AbstractButton<List<Value>> implements Refreshable {

    /**
     * Optional click action, which is executed when the sliders are clicked by {@link AbstractButton#click()}.
     */
    @Nullable
    protected final Action<AbstractSliderList<Value>> clickAction;

    /**
     * Optional refresh action, which is executed when the sliders are refreshed by {@link AbstractSliderList#refresh()}.
     */
    @Setter
    @Accessors(fluent = true, chain = false)
    protected VoidAction refreshAction = () -> {};

    /**
     * Name for the button to display the slider list.
     */
    protected final String label;

    /**
     * Holds the slider ui elements with its values.
     */
    @Getter
    protected final UiList<Value, Slider> uiList;

    /**
     * Creates a new slider list.
     *
     * @param label name for the button to open the slider list
     * @param description optional description when hovering the button
     * @param sliders for the list
     * @param height available height for the list
     * @param elementSupplier used for creating the sliders with given values
     * @param clickAction optional action when the button for opening the slider popup is clicked
     */
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
               ModSdkModule.gameApis().ui().popup().show(this.uiList, this);
            });
        }
    }

    @NotNull
    private String getAmount() {
        return "(" + this.uiList.getValue().size() + ")";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh() {
        String title = this.label + " " +  getAmount();
        GText gText = new GText(UI.FONT().M, title);
        replaceLabel(gText, DIR.C);
        refreshAction.accept();
    }
}
