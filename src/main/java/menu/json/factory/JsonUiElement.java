package menu.json.factory;

import com.github.argon.sos.moreoptions.game.CacheValue;
import com.github.argon.sos.moreoptions.game.action.BiAction;
import com.github.argon.sos.moreoptions.game.action.Resettable;
import com.github.argon.sos.moreoptions.game.action.Valuable;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.json.JsonPath;
import com.github.argon.sos.moreoptions.json.element.JsonDouble;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonLong;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import com.github.argon.sos.moreoptions.util.ClassUtil;
import com.github.argon.sos.moreoptions.util.StringUtil;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import menu.json.JsonUiException;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.nio.file.Path;
import java.util.function.Function;

@Getter
public class JsonUiElement<Value extends JsonElement, Element extends RENDEROBJ> implements
    Valuable<Value>,
    Resettable
{
    private final static I18n i18n = I18n.get(JsonUiElement.class);

    private final Element element;
    private final Value defaultValue;
    private final Value initValue;

    @Nullable
    private final String jsonPath;
    @Setter
    @Getter
    @Accessors(chain = false, fluent = true)
    private JsonObject config;
    private final Path path;
    @Setter
    @Accessors(chain = false, fluent = true)
    private boolean orphan;
    @Setter
    @Accessors(chain = false, fluent = true)
    private Function<JsonUiElement<Value, Element>, Value> valueSupplier;
    @Setter
    @Accessors(chain = false, fluent = true)
    private BiAction<JsonUiElement<Value, Element>, Value> valueConsumer;
    @Setter
    @Accessors(chain = false, fluent = true)
    private BiAction<JsonUiElement<Value, Element>, Value> valueChangeAction;
    @Nullable
    private final JsonPath jsonPathObject;
    @Setter
    @Getter
    @Nullable
    @Accessors(chain = false, fluent = true)
    private String description;

    private final CacheValue<Value> valueCache;

    @Builder
    public JsonUiElement(
        Element element,
        Value defaultValue,
        Value initValue,
        @Nullable String jsonPath,
        @Nullable String description,
        JsonObject config,
        Path path,
        boolean orphan,
        @Nullable Function<JsonUiElement<Value, Element>, Value> valueSupplier,
        @Nullable BiAction<JsonUiElement<Value, Element>, Value> valueConsumer,
        @Nullable BiAction<JsonUiElement<Value, Element>, Value> valueChangeAction
    ) {
        this.element = element;
        this.defaultValue = defaultValue;
        this.initValue = (initValue != null) ? initValue : defaultValue;
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
    }

    public boolean isDirty() {
        if (initValue == null) {
            return false;
        }

        Value value = valueCache.get();
//        if (initValue instanceof JsonDouble && value instanceof JsonLong) {
//            return !initValue.equals(JsonDouble.of((JsonLong) value));
//        } else if (initValue instanceof JsonLong && value instanceof JsonDouble) {
//            return !initValue.equals(JsonLong.of((JsonDouble) value));
//        }

        return !initValue.equals(value);
    }

    public Value getValue() {
        return valueSupplier.apply(this);
    }

    public void setValue(Value value) {
        if (!value.equals(getValue())) {
            valueChangeAction.accept(this, value);
        }

        valueConsumer.accept(this, value);
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
                .key(jsonPath)
                .searchTerm(jsonPath);

            String[] splits = jsonPath.split("\\.");
            Section label;
            if (splits.length > 1) {
                StringBuilder prefix = new StringBuilder();
                for (int i = 0; i < splits.length - 1; i++) {
                    String split = splits[i];
                    prefix.append(split).append(".");
                }

                String labelName = splits[splits.length - 1];

                Label labelPrefix = Label.builder()
                    .name(prefix.toString())
                    .description(jsonPath)
                    .font(UI.FONT().S)
                    .style(Label.Style.LABEL)
                    .build();

                Label label2 = Label.builder()
                    .name(labelName)
                    .description(jsonPath)
                    .font(UI.FONT().S)
                    .style(Label.Style.NORMAL)
                    .build();

                label = new Section();
                label.addRightC(0, labelPrefix);
                label.addRightC(0, label2);
            } else {
                label = Label.builder()
                    .name(jsonPath)
                    .description(jsonPath)
                    .font(UI.FONT().S)
                    .style(Label.Style.NORMAL)
                    .build();
            }

            // indent label
            int indents = StringUtil.countChar(jsonPath, '.');
            columnRowBuilder.column(UiFactory.indent(indents, label));

            if (orphan) {
                GuiSection optionalIcon = UiUtil.toGuiSection(SPRITES.icons().m.cancel)
                    .hoverInfoSet("Not present in vanilla game config.");
                columnRowBuilder.column(optionalIcon);
            } else {
                columnRowBuilder.column(spacer);
            }

            Section dirtyFlag = UiUtil.toSection(SPRITES.icons().m.job_awake);
            dirtyFlag.hoverInfoSet("Setting changed.");

            // only show when value has changed
            dirtyFlag.renderAction(ds -> {
                dirtyFlag.visableSet(isDirty());
            });

            columnRowBuilder.column(dirtyFlag);
        } else {
            columnRowBuilder.isHeader(true);
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

    public static <Value extends JsonElement, Element extends RENDEROBJ> JsonUiElementBuilder<Value, Element> from(
        String jsonPath,
        JsonObject config,
        Value defaultValue,
        Class<Value> valueClazz,
        Function<Value, Element> elementProvider
    ) {
        JsonUiElementBuilder<Value, Element> builder = JsonUiElement.builder();
        JsonPath jsonPathO = JsonPath.get(jsonPath);
        Value value = jsonPathO.get(config)
            .map(jsonElement -> {
                    // convert JsonDouble and JsonLong FIXME: causes bugs with isDirty detection?!
                    if (ClassUtil.instanceOf(valueClazz, JsonDouble.class) && jsonElement instanceof JsonLong) {
                        return JsonDouble.of((JsonLong) jsonElement);
                    } else if (ClassUtil.instanceOf(valueClazz, JsonLong.class) && jsonElement instanceof JsonDouble) {
                        return JsonLong.of((JsonDouble) jsonElement);
                    }

                return jsonElement;
            })
            .filter(valueClazz::isInstance)
            .map(valueClazz::cast)
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
}
