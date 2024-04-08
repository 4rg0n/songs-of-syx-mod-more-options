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
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.GuiSection;
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
    private final String jsonPath;
    @Setter
    @Accessors(chain = true, fluent = true)
    private JsonObject config;
    private final Path path;
    @Setter
    @Accessors(chain = true, fluent = true)
    private boolean optional;
    @Setter
    @Accessors(chain = true, fluent = true)
    private Function<Element, Value> valueSupplier;
    @Setter
    @Accessors(chain = true, fluent = true)
    private BiAction<Element, Value> valueConsumer;
    @Nullable
    private final JsonPath jsonPathObject;
    @Nullable
    @Setter
    @Accessors(chain = true, fluent = true)
    private String description;

    @Builder
    public JsonUiElement(
        Element element,
        Value defaultValue,
        Value initValue,
        @Nullable String jsonPath,
        @Nullable String description,
        JsonObject config,
        Path path,
        boolean optional,
        Function<Element, Value> valueSupplier,
        BiAction<Element, Value> valueConsumer
    ) {
        this.element = element;
        this.defaultValue = defaultValue;
        this.initValue = (initValue != null) ? initValue : defaultValue;
        this.jsonPath = jsonPath;
        this.description = description;
        this.config = config;
        this.path = path;
        this.optional = optional;
        this.valueSupplier = (valueSupplier != null) ? valueSupplier : o -> null;
        this.valueConsumer = (valueConsumer != null) ? valueConsumer : (o1, o2) -> {};
        this.jsonPathObject = (jsonPath != null) ? JsonPath.get(jsonPath) : null;
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
                (jsonPath == null) ? "NO_KEY" : jsonPath
            ));
        }
        RENDEROBJ spacer = new Spacer(SPRITES.icons().m.DIM, SPRITES.icons().m.DIM);

        ColumnRow.ColumnRowBuilder<Void> columnRowBuilder = ColumnRow.builder();
        if (jsonPath != null) {
            columnRowBuilder
                .searchTerm(jsonPath)
                .column(Label.builder()
                    .name(jsonPath)
                    .font(UI.FONT().S)
                    .build());

            if (optional) {
                GuiSection optionalIcon = UiUtil.toGuiSection(SPRITES.icons().m.cancel)
                    .hoverInfoSet("Not present in vanilla game config.");
                columnRowBuilder.column(optionalIcon);
            } else {
                columnRowBuilder.column(spacer);
            }
        } else {
            columnRowBuilder.isHeader(true);
        }

        columnRowBuilder.column(element);

        if (jsonPath != null) {
            Button resetButton = new Button(SPRITES.icons().m.arrow_left);
            resetButton.clickActionSet(this::reset);
            resetButton.hoverInfoSet("Reset " + jsonPath);
            columnRowBuilder.column(resetButton);
        }

        return columnRowBuilder.build();
    }

    public static <Value extends JsonElement, Element extends RENDEROBJ> JsonUiElementBuilder<Value, Element> from(
        String jsonPath,
        JsonObject config,
        Value defaultValue,
        Class<Value> clazz,
        Function<Value, Element> elementProvider
    ) {
        JsonUiElementBuilder<Value, Element> builder = JsonUiElement.builder();
        JsonPath jsonPathO = JsonPath.get(jsonPath);
        Value value = jsonPathO.get(config, clazz)
            .orElseGet(() -> {
                builder.optional(true);
                return defaultValue;
            });

        return builder
            .config(config)
            .element(elementProvider.apply(value))
            .jsonPath(jsonPath)
            .initValue(value)
            .defaultValue(defaultValue);
    }
    public void writeInto(JsonObject config) {
        if (jsonPathObject == null) {
            return;
        }

        JsonElement jsonValue = getValue();
        jsonPathObject.put(config, jsonValue);
    }

    @Override
    public void reset() {
        setValue(initValue);
    }
}
