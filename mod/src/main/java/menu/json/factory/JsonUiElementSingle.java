package menu.json.factory;

import com.github.argon.sos.moreoptions.game.CacheValue;
import com.github.argon.sos.moreoptions.game.action.BiAction;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.json.JsonPath;
import com.github.argon.sos.moreoptions.json.element.*;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import com.github.argon.sos.mod.sdk.util.StringUtil;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import menu.json.JsonUiException;
import menu.json.icon.JsonUiElementIcon;
import menu.ui.UiFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

@Getter
public class JsonUiElementSingle<Value extends JsonElement, Element extends RENDEROBJ> implements JsonUiElement<Value, Element> {
    private final static I18n i18n = I18n.get(JsonUiElementSingle.class);

    private final Element element;
    @Nullable
    private final Value defaultValue;
    @Nullable
    private final Value initValue;

    @Nullable
    private final String jsonPath;
    @Nullable
    private final Class<? extends JsonElement> valueClass;
    @Setter
    @Getter
    @Accessors(chain = false, fluent = true)
    private JsonObject config;
    private final Path path;
    @Setter
    private boolean orphan;
    @Setter
    @Accessors(chain = false, fluent = true)
    private Function<JsonUiElementSingle<Value, Element>, Value> valueSupplier;
    @Setter
    @Accessors(chain = false, fluent = true)
    private BiAction<JsonUiElementSingle<Value, Element>, Value> valueConsumer;
    @Setter
    @Accessors(chain = false, fluent = true)
    private BiAction<JsonUiElementSingle<Value, Element>, Value> valueChangeAction;
    @Nullable
    private final JsonPath jsonPathObject;
    @Setter
    @Getter
    @Nullable
    @Accessors(chain = false, fluent = true)
    private String description;

    private final CacheValue<Value> valueCache;

    @Builder
    public JsonUiElementSingle(
        Element element,
        @Nullable Value defaultValue,
        Value initValue,
        @Nullable String jsonPath,
        @Nullable String description,
        JsonObject config,
        Path path,
        boolean orphan,
        @Nullable Function<JsonUiElementSingle<Value, Element>, Value> valueSupplier,
        @Nullable BiAction<JsonUiElementSingle<Value, Element>, Value> valueConsumer,
        @Nullable BiAction<JsonUiElementSingle<Value, Element>, Value> valueChangeAction
    ) {
        this.element = element;
        this.defaultValue = defaultValue;
        if (initValue != null) {
            this.initValue = initValue;
        } else {
            this.initValue = defaultValue;
        }
        this.jsonPath = jsonPath;
        this.description = description;
        this.config = config;
        this.path = path;
        this.orphan = orphan;
        this.valueSupplier = (valueSupplier != null) ? valueSupplier : o -> null;
        this.valueConsumer = (valueConsumer != null) ? valueConsumer : (o1, o2) -> {};
        this.valueChangeAction = (valueChangeAction != null) ? valueChangeAction : (o1, o2) -> {};
        this.jsonPathObject = (jsonPath != null) ? JsonPath.get(jsonPath) : null;
        this.valueCache = CacheValue.of(500, () -> this.valueSupplier.apply(this));

        if (defaultValue != null) {
            this.valueClass = defaultValue.getClass();
        } else if (initValue != null) {
            this.valueClass = initValue.getClass();
        } else {
            this.valueClass = null;
        }
    }

    public boolean isDirty() {
        if (initValue == null) {
            return false;
        }

        Value value = valueCache.get();
        return !initValue.equals(value);
    }

    public Value getValue() {
        return valueSupplier.apply(this);
    }

    public void setValue(Value value) {
        Value currentValue = getValue();
        if (currentValue != value) {
            valueChangeAction.accept(this, value);
        }

        valueConsumer.accept(this, value);
    }

    public Optional<JsonElement> getRawConfigValue() {
        if (jsonPathObject == null) {
            return Optional.empty();
        }

        return jsonPathObject.get(config);
    }

    public Optional<JsonElement> getConfigValue() {
        if (valueClass == null) {
            return Optional.empty();
        }

        return getRawConfigValue()
            .map(element1 -> normalizeValue(element1, valueClass))
            .filter(valueClass::isInstance)
            .map(valueClass::cast);
    }

    public ColumnRow<JsonUiElementSingle<? extends JsonElement, ? extends RENDEROBJ>> toColumnRow() {
        if (element == null) {
            throw new JsonUiException(String.format("Element for %s %s is null.",
                path.toString(),
                (jsonPath == null) ? "NO_KEY" : jsonPath
            ));
        }
        ColumnRow.ColumnRowBuilder<JsonUiElementSingle<? extends JsonElement, ? extends RENDEROBJ>> columnRowBuilder = ColumnRow.builder();
        columnRowBuilder.value(this);

        if (jsonPath != null) {
            columnRowBuilder
                .key(jsonPath)
                .searchTerm(jsonPath);

            String[] splits = jsonPath.split("\\.");
            Section label;
            if (splits.length > 0) {
                String labelName = splits[splits.length - 1];
                label = Label.builder()
                    .name(labelName)
                    .description(jsonPath)
                    .font(UI.FONT().S)
                    .style(Label.Style.NORMAL)
                    .build();
            } else {
                label = Label.builder()
                    .description(jsonPath)
                    .font(UI.FONT().S)
                    .style(Label.Style.NORMAL)
                    .build();
            }

            // indented label
            int indents = StringUtil.countChar(jsonPath, '.');
            columnRowBuilder.column(UiFactory.indent(indents, label));

            // orphan icon
            if (orphan) {
                GuiSection optionalIcon = UiUtil.toGuiSection(SPRITES.icons().m.cancel)
                    .hoverInfoSet("Not present in vanilla game config.");
                columnRowBuilder.column(optionalIcon);
            } else {
                columnRowBuilder.column(new Spacer(SPRITES.icons().m.DIM, SPRITES.icons().m.DIM));
            }

            // dirty icon
            Section dirtyIcon = UiUtil.toSection(SPRITES.icons().m.cancel);
            dirtyIcon.hoverInfoSet("Setting changed.");

            // only show when value has changed
            dirtyIcon.renderAction(ds -> {
                dirtyIcon.visableSet(isDirty());
            });
            columnRowBuilder.column(dirtyIcon);
            columnRowBuilder.column(new JsonUiElementIcon<>(this));
        } else {
            columnRowBuilder
                .searchTerm("") // empty string will hide the row when searching
                .isHeader(true);
        }

        columnRowBuilder.column(element);

        if (jsonPath != null) {
            Button resetButton = new Button(SPRITES.icons().m.arrow_left);
            resetButton.clickActionSet(this::reset);
            resetButton.hoverInfoSet("Reset " + jsonPath  + " to initially loaded from file.");
            columnRowBuilder.column(resetButton);
        }

        return columnRowBuilder.build();
    }

    public void writeInto(JsonObject config) {
        if (jsonPathObject == null) {
            return;
        }

        JsonElement jsonValue = getValue();
        jsonPathObject.put(config, jsonValue);
    }

    public void reset() {
        setValue(initValue);
    }

    public static <Value extends JsonElement, Element extends RENDEROBJ> JsonUiElementSingleBuilder<Value, Element> from(
        String jsonPath,
        JsonObject config,
        Value defaultValue,
        Class<Value> valueClass,
        Function<Value, Element> elementProvider
    ) {
        JsonUiElementSingleBuilder<Value, Element> builder = JsonUiElementSingle.builder();
        JsonPath jsonPathO = JsonPath.get(jsonPath);
        Value value = jsonPathO.get(config)
            .map(element -> normalizeValue(element, valueClass))
            .filter(valueClass::isInstance)
            .map(valueClass::cast)
            .orElseGet(() -> {
                builder.orphan(true);
                return defaultValue;
            });

        String description = i18n.pdn(jsonPathO.toStringWithoutIndexes());
        return builder
            .config(config)
            .element(elementProvider.apply(value))
            .jsonPath(jsonPath)
            .initValue(value)
            .description(description)
            .defaultValue(defaultValue)
            .valueChangeAction((element, changedValue) -> {
                element.writeInto(config);
            });
    }

    @NotNull
    private static <Value extends JsonElement> JsonElement normalizeValue(JsonElement jsonElement, Class<Value> valueClazz) {
        if (ClassUtil.instanceOf(valueClazz, JsonDouble.class) && jsonElement instanceof JsonLong) {
            return JsonDouble.of((JsonLong) jsonElement);
        } else if (ClassUtil.instanceOf(valueClazz, JsonLong.class) && jsonElement instanceof JsonDouble) {
            return JsonLong.of((JsonDouble) jsonElement);
        }

        return jsonElement;
    }
}
