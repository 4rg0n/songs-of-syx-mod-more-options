package menu.json;

import com.github.argon.sos.moreoptions.game.action.Resettable;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import com.github.argon.sos.moreoptions.json.element.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.gui.misc.GHeader;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class JsonUiElementFactory implements Resettable {
    protected final Path path;
    @Getter
    protected final JsonObject config;
    protected final Map<String, JsonUiElement<?, ?>> all = new LinkedHashMap<>();

    @Override
    public void reset() {
        all.values().forEach(JsonUiElement::reset);
    }

    public JsonUiElement<JsonNull, RENDEROBJ> separator() {
        JsonUiElement<JsonNull, RENDEROBJ> separator = JsonUiElement.<JsonNull, RENDEROBJ>builder()
            .element(new Spacer(1, 40))
            .build();

        all.put(separator.toString(), separator);
        return separator;
    }

    public JsonUiElement<JsonNull, RENDEROBJ> header(String text) {
        GHeader gHeader = new GHeader(text);
        GuiSection section = UiUtil.toGuiSection(gHeader);
        section.pad(0, 10);

        JsonUiElement<JsonNull, RENDEROBJ> header = JsonUiElement.<JsonNull, RENDEROBJ>builder()
            .element(section)
            .build();

        all.put(header.toString(), header);
        return header;
    }

    public JsonUiElement<JsonString, ColorPicker> color(String key, JsonString defaultValue) {
        JsonUiElement<JsonString, ColorPicker> color = JsonUiElement.from(
            key,
            config,
            defaultValue,
            JsonString.class,
            JsonUiFactory::color)
        .path(path)
        .valueSupplier(colorPicker -> new JsonString(colorPicker.getElement().to("_")))
        .valueConsumer((colorPicker, jsonString) -> colorPicker.getElement().set(jsonString.getValue(), "_"))
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
            .valueSupplier(colorPicker -> JsonUiMapper.colors(
                colorPicker.getElement().getRed(),
                colorPicker.getElement().getGreen(),
                colorPicker.getElement().getBlue()))
            .valueConsumer((colorPicker, json) -> colorPicker.getElement().set(
                JsonUiMapper.colors(json)))
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
            .valueConsumer((slider1, jsonDouble) -> slider1.getElement().setValueD(jsonDouble.getValue()))
            .valueSupplier(slider1 -> new JsonDouble(slider1.getElement().getValueD()))
            .build();

        all.put(key, slider);
        return slider;
    }

    public JsonUiElement<JsonLong, Slider> slider(String key, int min, int max, int step, List<Integer> allowedValues, JsonLong defaultValue) {
        JsonUiElement<JsonLong, Slider> slider = JsonUiElement.from(
            key,
            config,
            defaultValue,
            JsonLong.class,
            value -> JsonUiFactory.slider(value, min, max, step, allowedValues))
        .path(path)
        .valueConsumer((slider1, jsonLong) -> slider1.getElement().setValue(jsonLong.getValue().intValue()))
        .valueSupplier(slider1 -> new JsonLong(Long.valueOf(slider1.getElement().getValue())))
        .build();

        all.put(key, slider);
        return slider;
    }
    
    public JsonUiElement<JsonArray, Select<String>> selectS(String key, List<String> options, JsonArray defaultValue, int maxSelect, boolean maxSelected) {
        JsonUiElement<JsonArray, Select<String>> select = JsonUiElement.from(
                key,
                config,
                defaultValue,
                JsonArray.class,
                value -> JsonUiFactory.selectS(value, options, maxSelect, maxSelected))
            .path(path)
            .valueSupplier(stringSelect -> {
                JsonArray jsonValue = new JsonArray();
                stringSelect.getElement().getSelectedKeys().forEach(entry -> {
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
            .valueSupplier(dropDown -> new JsonString(dropDown.getElement().getValue()))
            .valueConsumer((dropDown, jsonString) -> dropDown.getElement().setValue(jsonString.getValue()))
            .build();

        all.put(key, select);
        return select;
    }

    public JsonUiElement<JsonArray, MultiDropDown<String>> multiDropDown(String key, String title, List<String> options, JsonArray defaultValue, int maxSelect, boolean maxSelected) {
        JsonUiElement<JsonArray, MultiDropDown<String>> select = JsonUiElement.from(
                key,
                config,
                defaultValue,
                JsonArray.class,
                value -> JsonUiFactory.multiDropDown(title, value, options, maxSelect, maxSelected))
            .path(path)
            .valueSupplier(stringSelect -> {
                JsonArray jsonValue = new JsonArray();
                stringSelect.getElement().getValue().forEach(entry -> {
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
            .valueSupplier(checkbox1 -> new JsonBoolean(checkbox1.getElement().selectedIs()))
            .valueConsumer((checkbox1, jsonBoolean) -> checkbox1.getElement().selectedSet(jsonBoolean.getValue()))
            .build();

        all.put(key, checkbox);
        return checkbox;
    }

    public JsonUiElement<JsonString, GInput> text(String key, JsonString defaultValue) {
        JsonUiElement<JsonString, GInput> icon = JsonUiElement.from(
                key,
                config,
                defaultValue,
                JsonString.class,
                JsonUiFactory::text)
            .path(path)
            .valueSupplier(text1 -> JsonString.of(text1.getElement().text().toString()))
            .valueConsumer((text1, jsonString) -> {
                text1.getElement().text().clear();
                text1.getElement().text().add(jsonString.getValue());
            })
            .build();

        all.put(key, icon);
        return icon;
    }

    public JsonObject getValue() {
        getAll().values().stream()
            .filter(element -> element.getJsonPath() != null)
            .forEach(element -> element.writeInto(config));
        return config;
    }

    public List<JsonUiElement<?, ?>> getOrphans() {
        return all.values().stream()
            .filter(JsonUiElement::orphan)
            .collect(Collectors.toList());
    }
}
