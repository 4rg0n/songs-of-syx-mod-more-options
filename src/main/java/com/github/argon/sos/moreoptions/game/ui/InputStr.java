package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.action.Action;
import com.github.argon.sos.moreoptions.game.action.Refreshable;
import com.github.argon.sos.moreoptions.game.action.Valuable;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.Font;
import snake2d.util.sprite.text.StringInputSprite;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class InputStr extends GuiSection implements Valuable<String>, Refreshable {

    private final StringInputSprite stringInput;
    private final Input input;

    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Supplier<String> valueSupplier;

    @Setter
    @Accessors(fluent = true, chain = false)
    private Consumer<String> valueConsumer = o -> {};

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<String> valueChangeAction = o -> {};

    @Builder
    public InputStr(
        @Nullable Integer size,
        @Nullable Font font,
        @Nullable String placeHolder,
        @Nullable Supplier<String> valueSupplier,
        @Nullable Consumer<String> valueConsumer,
        @Nullable Action<String> valueChangeAction
    ) {
        if (size == null) size = 20;
        if (font == null) font = UI.FONT().S;
        if (valueSupplier != null) this.valueSupplier = valueSupplier;
        if (valueConsumer != null) this.valueConsumer = valueConsumer;
        if (valueChangeAction != null) this.valueChangeAction = valueChangeAction;

        stringInput = new StringInputSprite(size, font);
        stringInput.placeHolder(placeHolder);
        input = new Input(stringInput);
        add(input);
    }

    @Override
    public @Nullable String getValue() {
        if (valueSupplier != null) {
            return valueSupplier.get();
        }

        return stringInput.text().toString();
    }

    @Override
    public void setValue(String value) {
        valueConsumer.accept(value);

        if (!Objects.equals(getValue(), value)) {
            valueChangeAction.accept(value);
        }

        stringInput.text().clear().add(value);
    }

    @Override
    public void refresh() {
        setValue(getValue());
    }
}
