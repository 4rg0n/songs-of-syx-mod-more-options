package menu.json;

import com.github.argon.sos.moreoptions.game.BiAction;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import com.github.argon.sos.moreoptions.json.JsonPath;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.nio.file.Path;
import java.util.function.Function;

@Getter
public class JsonUiElement<Value extends JsonElement, Element extends RENDEROBJ>
    implements Valuable<Value, Element>, Resettable<Element>
{
    private final Element element;
    private final Value defaultValue;
    private final Value initValue;
    @Nullable
    private final String key;
    private final JsonObject config;
    private final Path path;
    private final boolean optional;
    private final Function<Element, Value> valueSupplier;
    private final BiAction<Element, Value> valueConsumer;
    @Nullable
    private final JsonPath jsonPath;

    @Builder
    public JsonUiElement(
        Element element,
        Value defaultValue,
        Value initValue,
        @Nullable String key,
        JsonObject config,
        Path path,
        boolean optional,
        Function<Element, Value> valueSupplier,
        BiAction<Element, Value> valueConsumer
    ) {
        this.element = element;
        this.defaultValue = defaultValue;
        this.initValue = (initValue != null) ? initValue : defaultValue;
        this.key = key;
        this.config = config;
        this.path = path;
        this.optional = optional;
        this.valueSupplier = (valueSupplier != null) ? valueSupplier : o -> null;
        this.valueConsumer = (valueConsumer != null) ? valueConsumer : (o1, o2) -> {};
        this.jsonPath = (key != null) ? JsonPath.get(key) : null;
    }

    @Override
    public Value getValue() {
        return valueSupplier.apply(element);
    }

    @Override
    public void setValue(Value value) {
        valueConsumer.accept(element, value);
    }

    public ColumnRow<Void> toColumnRow() {
        if (element == null) {
            throw new JsonUiException(String.format("Element for %s %s is null.",
                path.toString(),
                (key == null) ? "NO_KEY" : key
            ));
        }

        ColumnRow.ColumnRowBuilder<Void> columnRowBuilder = ColumnRow.builder();
        if (key != null) {
            RENDEROBJ optionalIcon = new Spacer(SPRITES.icons().m.DIM, SPRITES.icons().m.DIM);

            if (optional) {
                optionalIcon = UiUtil.toGuiSection(SPRITES.icons().m.questionmark)
                    .hoverInfoSet("Not present in vanilla game config.");
            }

            columnRowBuilder
                .searchTerm(key)
                .column(Label.builder()
                    .name(key)
                    .font(UI.FONT().S)
                    .build())
                .column(optionalIcon);
        } else {
            columnRowBuilder.isHeader(true);
        }

        columnRowBuilder.column(element);

        if (key != null) {
            Button resetButton = new Button(SPRITES.icons().m.arrow_left);
            resetButton.clickActionSet(this::reset);
            columnRowBuilder.column(resetButton);
        }

        return columnRowBuilder.build();
    }

    public static <Value extends JsonElement, Element extends RENDEROBJ> JsonUiElementBuilder<Value, Element> from(
        String key,
        JsonObject config,
        Value defaultValue,
        Class<Value> clazz,
        Function<Value, Element> elementProvider
    ) {
        JsonUiElementBuilder<Value, Element> builder = JsonUiElement.builder();
        JsonPath jsonPath = JsonPath.get(key);
        Value value = jsonPath.extract(config, clazz)
            .orElseGet(() -> {
                builder.optional(true);
                return defaultValue;
            });

        return builder
            .config(config)
            .element(elementProvider.apply(value))
            .key(key)
            .initValue(value)
            .defaultValue(defaultValue);
    }
    public void writeInto(JsonObject config) {
        if (jsonPath == null) {
            return;
        }

        JsonElement jsonValue = getValue();
        jsonPath.put(config, jsonValue);
    }

    @Override
    public void reset() {
        setValue(initValue);
    }
}
