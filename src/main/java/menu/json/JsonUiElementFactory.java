package menu.json;

import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.json.element.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class JsonUiElementFactory implements Resettable<Void>{
    protected final Path path;
    protected final JsonObject config;
    protected final Map<String, JsonUiElement<?, ?>> all = new LinkedHashMap<>();

    @Override
    public void reset() {
        all.values().forEach(JsonUiElement::reset);
    }

    public JsonUiElement<JsonNull, RENDEROBJ> separator() {
        JsonUiElement<JsonNull, RENDEROBJ> separator = JsonUiElement.<JsonNull, RENDEROBJ>builder()
            .element(new Spacer(1, 20))
            .build();

        all.put(separator.toString(), separator);
        return separator;
    }

    public JsonUiElement<JsonString, ColorPicker> color(String key, JsonString defaultValue) {
        JsonUiElement<JsonString, ColorPicker> color = JsonUiElement.from(
            key,
            config,
            defaultValue,
            JsonString.class,
            JsonUiFactory::color)
        .path(path)
        .valueSupplier(colorPicker -> new JsonString(colorPicker.to("_")))
        .valueConsumer((colorPicker, jsonString) -> colorPicker.set(jsonString.getValue(), "_"))
        .build();

        all.put(key, color);
        return color;
    }

    public JsonUiElement<JsonObject, ColorPicker> colorO(String key, JsonObject defaultValue) {
        JsonUiElement<JsonObject, ColorPicker> color = JsonUiElement.from(
                key,
                config,
                defaultValue,
                JsonObject.class,
                JsonUiFactory::color)
            .path(path)
            .valueSupplier(colorPicker -> JsonMapper.colors(
                colorPicker.getRed(),
                colorPicker.getGreen(),
                colorPicker.getBlue()))
            .valueConsumer((colorPicker, json) -> colorPicker.set(
                JsonMapper.colors(json)))
            .build();

        all.put(key, color);
        return color;
    }

    public JsonUiElement<JsonDouble, Slider> sliderD(String key, int min, int max, int step, int resolution, JsonDouble defaultValue) {
        JsonUiElement<JsonDouble, Slider> slider = JsonUiElement.from(
                key,
                config,
                defaultValue,
                JsonDouble.class,
                value -> JsonUiFactory.slider(value, min, max, step, resolution))
            .path(path)
            .valueConsumer((slider1, jsonDouble) -> slider1.setValueD(jsonDouble.getValue()))
            .valueSupplier(slider1 -> new JsonDouble(slider1.getValueD()))
            .build();

        all.put(key, slider);
        return slider;
    }

    public JsonUiElement<JsonLong, Slider> slider(String key, int min, int max, int step, JsonLong defaultValue) {
        JsonUiElement<JsonLong, Slider> slider = JsonUiElement.from(
            key,
            config,
            defaultValue,
            JsonLong.class,
            value -> JsonUiFactory.slider(value, min, max, step))
        .path(path)
        .valueConsumer((slider1, jsonLong) -> slider1.setValue(jsonLong.getValue().intValue()))
        .valueSupplier(slider1 -> new JsonLong(Long.valueOf(slider1.getValue())))
        .build();

        all.put(key, slider);
        return slider;
    }
    
    public JsonUiElement<JsonArray, Select<String>> selectS(String key, List<String> options, JsonArray defaultValue) {
        JsonUiElement<JsonArray, Select<String>> select = JsonUiElement.from(
                key,
                config,
                defaultValue,
                JsonArray.class,
                value -> JsonUiFactory.selectS(value, options))
            .path(path)
            .valueSupplier(stringSelect -> {
                JsonArray jsonValue = new JsonArray();
                stringSelect.getSelectedKeys().forEach(entry -> {
                    jsonValue.add(new JsonString(entry));
                });

                return jsonValue;
            })
            .valueConsumer((stringSelect, jsonArray) -> {
                List<String> selected = jsonArray.as(JsonString.class).stream()
                    .map(JsonString::getValue)
                    .collect(Collectors.toList());

                stringSelect.setValue(selected);
            })
            .build();

        all.put(key, select);
        return select;
    }

    public JsonUiElement<JsonString, DropDown<String>> dropDown(String key, List<String> options, JsonString defaultValue) {
        JsonUiElement<JsonString, DropDown<String>> select = JsonUiElement.from(
                key,
                config,
                defaultValue,
                JsonString.class,
                value -> JsonUiFactory.dropDown(value, options))
            .path(path)
            .valueSupplier(dropDown -> new JsonString(dropDown.getValue()))
            .valueConsumer((dropDown, jsonString) -> dropDown.setValue(jsonString.getValue()))
            .build();

        all.put(key, select);
        return select;
    }

    public JsonUiElement<JsonBoolean, Checkbox> checkbox(String key, JsonBoolean defaultValue) {
        JsonUiElement<JsonBoolean, Checkbox> checkbox = JsonUiElement.from(
                key,
                config,
                defaultValue,
                JsonBoolean.class,
                JsonUiFactory::checkbox)
            .path(path)
            .valueSupplier(checkbox1 -> new JsonBoolean(checkbox1.selectedIs()))
            .valueConsumer((checkbox1, jsonBoolean) -> checkbox1.selectedSet(jsonBoolean.getValue()))
            .build();

        all.put(key, checkbox);
        return checkbox;
    }

    public JsonObject getValue() {
        getAll().values().stream()
            .filter(element -> element.getKey() != null)
            .forEach(element -> element.writeInto(config));
        return config;
    }
}
