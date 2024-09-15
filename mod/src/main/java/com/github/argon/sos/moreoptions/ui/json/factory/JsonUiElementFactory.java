package com.github.argon.sos.moreoptions.ui.json.factory;

import com.github.argon.sos.mod.sdk.game.action.Resettable;
import com.github.argon.sos.mod.sdk.ui.*;
import com.github.argon.sos.mod.sdk.json.element.*;
import menu.ui.ColorPicker;
import com.github.argon.sos.mod.sdk.ui.Slider;
import com.github.argon.sos.mod.sdk.ui.SliderDoubleList;
import com.github.argon.sos.moreoptions.ui.json.factory.builder.JsonUiElementListBuilder;
import init.sprite.UI.UI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.github.argon.sos.moreoptions.ui.json.JsonUiMapper;
import menu.ui.UiFactory;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.text.Font;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * For creating {@link JsonUiElementSingle}s.
 * Only this thing should create these elements.
 */
@Getter
@RequiredArgsConstructor
public class JsonUiElementFactory implements Resettable {
    protected final Path path;
    protected final JsonObject config;

    /**
     * Creates a list of {@link JsonUiElementSingle}s and wraps it into {@link JsonUiElementList}.
     * It will therefor extend the json path by the names of the given list.
     */
    public <Value extends JsonElement, Element extends RENDEROBJ> JsonUiElementList<Value, Element> asObject(String jsonPath, List<String> names, boolean asWildcards, Function<String, JsonUiElementSingle<Value, Element>> elementProvider) {
        return JsonUiElementListBuilder.<Value, Element>builder()
            .jsonPath(jsonPath)
            .elementProvider(elementProvider)
            .build()
            .asObject(names, asWildcards);
    }

    public <Value extends JsonElement, Element extends RENDEROBJ> JsonUiElementList<Value, Element> asArray(String jsonPath, int amount, Function<String, JsonUiElementSingle<Value, Element>> elementProvider) {
        return JsonUiElementListBuilder.<Value, Element>builder()
            .jsonPath(jsonPath)
            .elementProvider(elementProvider)
            .build()
            .asArray(amount);
    }

    public <Value extends JsonElement, Element extends RENDEROBJ> JsonUiElementList<Value, Element> asTuples(String jsonPath, String name, int amount, Function<String, JsonUiElementSingle<Value, Element>> elementProvider) {
        return JsonUiElementListBuilder.<Value, Element>builder()
            .jsonPath(jsonPath)
            .elementProvider(elementProvider)
            .build()
            .asTuples(name, amount);
    }

    public JsonUiElementSingle<JsonNull, Section> header(String text, int indents) {
        return JsonUiElementSingle.<JsonNull, Section>builder()
            .path(path)
            .config(config)
            .element(UiFactory.indent(indents, UiFactory.header(text, UI.FONT().M)))
            .build();
    }

    public JsonUiElementSingle<JsonNull, Section> header(String text) {
        return JsonUiElementSingle.<JsonNull, Section>builder()
            .path(path)
            .config(config)
            .element(UiFactory.header(text, UI.FONT().H2))
            .build();
    }

    public JsonUiElementSingle<JsonNull, Section> label(String text, Font font, int indents) {
        return JsonUiElementSingle.<JsonNull, Section>builder()
            .path(path)
            .config(config)
            .element(UiFactory.indent(indents, UiFactory.label(text, font)))
            .build();
    }

    public JsonUiElementSingle<JsonNull, Section> label(String text) {
        return JsonUiElementSingle.<JsonNull, Section>builder()
            .path(path)
            .config(config)
            .element(UiFactory.label(text, UI.FONT().H2))
            .build();
    }

    public JsonUiElementSingle<JsonNull, Spacer> space(int height) {
        return JsonUiElementSingle.<JsonNull, Spacer>builder()
            .path(path)
            .config(config)
            .element(new Spacer(1, height))
            .build();
    }

    public static JsonUiElementSingle<JsonNull, Spacer> root(Path path, JsonObject config) {
        return JsonUiElementSingle.<JsonNull, Spacer>builder()
            .jsonPath("$")
            .path(path)
            .config(config)
            .element(new Spacer(0, 0))
            .build();
    }

    public JsonUiElementSingle<JsonString, ColorPicker> color(String jsonPath, JsonString defaultValue) {
        return JsonUiElementSingle.from(
            jsonPath,
            config,
            defaultValue,
            JsonString.class,
            UiFactory::color)
        .path(path)
        .valueSupplier(colorPicker -> new JsonString(colorPicker.getElement().to("_")))
        .valueConsumer((colorPicker, jsonString) -> colorPicker.getElement().set(jsonString.getValue(), "_"))
        .build();
    }

    public JsonUiElementSingle<JsonObject, ColorPicker> colorO(String jsonPath, JsonObject defaultValue) {
        return JsonUiElementSingle.from(
                jsonPath,
                config,
                defaultValue,
                JsonObject.class,
                UiFactory::color)
            .path(path)
            .valueSupplier(colorPicker -> JsonUiMapper.colors(
                colorPicker.getElement().getRed(),
                colorPicker.getElement().getGreen(),
                colorPicker.getElement().getBlue()))
            .valueConsumer((colorPicker, json) -> colorPicker.getElement().set(
                JsonUiMapper.colors(json)))
            .build();
    }

    public JsonUiElementSingle<JsonDouble, Slider> sliderD(String jsonPath, int min, int max, int step, int resolution, JsonDouble defaultValue) {
        return JsonUiElementSingle.from(
                jsonPath,
                config,
                defaultValue,
                JsonDouble.class,
                value -> UiFactory.slider(value, min, max, step, resolution))
            .path(path)
            .valueConsumer((slider, jsonDouble) -> slider.getElement().setValueD(jsonDouble.getValue()))
            .valueSupplier(slider -> new JsonDouble(slider.getElement().getValueD()))
            .build();
    }

    public JsonUiElementSingle<JsonLong, Slider> slider(String jsonPath, int step, List<Integer> allowedValues, JsonLong defaultValue) {
        int min = 0;
        int max = 1;

        if (!allowedValues.isEmpty()) {
            min = allowedValues.get(0);
            max = allowedValues.get(allowedValues.size() - 1);
        }

        return slider(jsonPath, min, max, step, allowedValues, defaultValue);
    }

    public JsonUiElementSingle<JsonLong, Slider> slider(String jsonPath, int min, int max, int step, List<Integer> allowedValues, JsonLong defaultValue) {
        return JsonUiElementSingle.from(
            jsonPath,
            config,
            defaultValue,
            JsonLong.class,
            value -> UiFactory.slider(value, min, max, step, allowedValues))
        .path(path)
        .valueConsumer((slider, jsonLong) -> slider.getElement().setValue(jsonLong.getValue().intValue()))
        .valueSupplier(slider -> new JsonLong(Long.valueOf(slider.getElement().getValue())))
        .build();
    }
    
    public JsonUiElementSingle<JsonArray, Select<String>> selectS(String jsonPath, List<String> options, JsonArray defaultValue, int maxSelect, boolean maxSelected) {
        return JsonUiElementSingle.from(
                jsonPath,
                config,
                defaultValue,
                JsonArray.class,
                value -> UiFactory.selectS(value, options, maxSelect, maxSelected))
            .path(path)
            .valueSupplier(select -> {
                JsonArray jsonValue = new JsonArray();
                select.getElement().getSelectedKeys().forEach(entry -> {
                    jsonValue.add(new JsonString(entry));
                });

                return jsonValue;
            })
            .valueConsumer((select, jsonArray) -> {
                List<String> selected = jsonArray.as(JsonString.class).stream()
                    .map(JsonString::getValue)
                    .collect(Collectors.toList());

                select.getElement().setValue(selected);
            })
            .build();
    }

    public JsonUiElementSingle<JsonString, DropDown<String>> dropDown(String jsonPath, List<String> options, JsonString defaultValue) {
        return JsonUiElementSingle.from(
                jsonPath,
                config,
                defaultValue,
                JsonString.class,
                value -> UiFactory.dropDown(value, options))
            .path(path)
            .valueSupplier(dropDown -> new JsonString(dropDown.getElement().getValue()))
            .valueConsumer((dropDown, jsonString) -> dropDown.getElement().setValue(jsonString.getValue()))
            .build();
    }

    public JsonUiElementSingle<JsonArray, DropDownList> dropDownList(String jsonPath, String buttonTitle, List<String> options, JsonArray defaultValue) {
        return JsonUiElementSingle.from(
                jsonPath,
                config,
                defaultValue,
                JsonArray.class,
                value -> UiFactory.dropDownList(buttonTitle, value, options))
            .path(path)
            .valueSupplier(stringSelect -> {
                JsonArray jsonValue = new JsonArray();
                Objects.requireNonNull(stringSelect.getElement().getValue()).forEach(entry -> {
                    jsonValue.add(new JsonString(entry));
                });

                return jsonValue;
            })
            .valueConsumer((stringSelect, jsonArray) -> {
                List<String> selected = jsonArray.as(JsonString.class).stream()
                    .map(JsonString::getValue)
                    .collect(Collectors.toList());

                stringSelect.getElement().setValue(selected);
            })
            .build();
    }

    public JsonUiElementSingle<JsonArray, SliderDoubleList> sliderDList(String jsonPath, String buttonTitle, int min, int max, int step, int resolution, JsonArray defaultValue) {
        return JsonUiElementSingle.from(
                jsonPath,
                config,
                defaultValue,
                JsonArray.class,
                value -> UiFactory.sliderDList(buttonTitle, value, value1 -> {
                    if (value1 == null) {
                        return null;
                    }

                    return UiFactory.slider(JsonDouble.of(value1), min, max, step, resolution);
                }))
            .path(path)
            .valueSupplier(sliders -> {
                JsonArray jsonValue = new JsonArray();
                Objects.requireNonNull(sliders.getElement().getValue()).forEach(entry -> {
                    jsonValue.add(new JsonDouble(entry));
                });

                return jsonValue;
            })
            .valueConsumer((stringSelect, jsonArray) -> {
                List<Double> selected = jsonArray.as(JsonDouble.class).stream()
                    .map(JsonDouble::getValue)
                    .collect(Collectors.toList());

                stringSelect.getElement().setValue(selected);
            })
            .build();
    }

    public JsonUiElementSingle<JsonArray, MultiDropDown<String>> multiDropDown(String jsonPath, String title, List<String> options, JsonArray defaultValue, int maxSelect, boolean maxSelected) {
        return JsonUiElementSingle.from(
                jsonPath,
                config,
                defaultValue,
                JsonArray.class,
                value -> UiFactory.multiDropDown(title, value, options, maxSelect, maxSelected))
            .path(path)
            .valueSupplier(stringSelect -> {
                JsonArray jsonValue = new JsonArray();
                Objects.requireNonNull(stringSelect.getElement().getValue()).forEach(entry -> {
                    jsonValue.add(new JsonString(entry));
                });

                return jsonValue;
            })
            .valueConsumer((stringSelect, jsonArray) -> {
                List<String> selected = jsonArray.as(JsonString.class).stream()
                    .map(JsonString::getValue)
                    .collect(Collectors.toList());

                stringSelect.getElement().setValue(selected);
            })
            .build();
    }

    public JsonUiElementSingle<JsonBoolean, Checkbox> checkbox(String jsonPath, JsonBoolean defaultValue) {
        return JsonUiElementSingle.from(
                jsonPath,
                config,
                defaultValue,
                JsonBoolean.class,
                UiFactory::checkbox)
            .path(path)
            .valueSupplier(checkbox -> new JsonBoolean(checkbox.getElement().selectedIs()))
            .valueConsumer((checkbox, jsonBoolean) -> checkbox.getElement().selectedSet(jsonBoolean.getValue()))
            .build();
    }

    public JsonUiElementSingle<JsonString, Input> text(String jsonPath, JsonString defaultValue) {
        return JsonUiElementSingle.from(
                jsonPath,
                config,
                defaultValue,
                JsonString.class,
                UiFactory::text)
            .path(path)
            .valueSupplier(text -> JsonString.of(text.getElement().text().toString()))
            .valueConsumer((text, jsonString) -> {
                text.getElement().text().clear();
                text.getElement().text().add(jsonString.getValue());
            })
            .build();
    }
}
