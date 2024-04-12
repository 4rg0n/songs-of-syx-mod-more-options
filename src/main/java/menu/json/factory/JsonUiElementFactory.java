package menu.json.factory;

import com.github.argon.sos.moreoptions.game.Wildcard;
import com.github.argon.sos.moreoptions.game.action.Resettable;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.json.element.*;
import init.sprite.UI.UI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import menu.json.JsonUiMapper;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * For creating {@link JsonUiElement}s.
 * Only this thing should create these elements.
 */
@Getter
@RequiredArgsConstructor
public class JsonUiElementFactory implements Resettable {
    protected final Path path;
    protected final JsonObject config;


    /**
     * Creates a list of {@link JsonUiElement}s and wraps it into {@link JsonUiElementArray}.
     * It will therefor extend the json path by the names of the given list.
     */
    public <Value extends JsonElement, Element extends RENDEROBJ> JsonUiElementArray<Value, Element> asObject(String jsonPath, List<String> names, boolean asWildcards, Function<String, JsonUiElement<Value, Element>> elementProvider) {
        JsonUiElementArray.JsonUiElementArrayBuilder<Value, Element> builder = JsonUiElementArray.builder();

        if (asWildcards) {
            Wildcard.from(names).forEach(name -> {
                String jsonObjectPath = jsonObjectPath(jsonPath, name);
                builder.element(elementProvider.apply(jsonObjectPath));
            });
        } else {
            names.forEach(name -> {
                String jsonObjectPath = jsonObjectPath(jsonPath, name);
                builder.element(elementProvider.apply(jsonObjectPath));
            });
        }

        return builder.build();
    }

    public <Value extends JsonElement, Element extends RENDEROBJ> JsonUiElementArray<Value, Element> asArray(String jsonPath, int amount, Function<String, JsonUiElement<Value, Element>> elementProvider) {
        JsonUiElementArray.JsonUiElementArrayBuilder<Value, Element> builder = JsonUiElementArray.builder();

        for (int i = 0; i < amount; i++) {
            String jsonArrayPath = jsonArrayPath(jsonPath, i);
            builder.element(elementProvider.apply(jsonArrayPath));
        }

        return builder.build();
    }

    public <Value extends JsonElement, Element extends RENDEROBJ> JsonUiElementArray<Value, Element> asTuples(String jsonPath, String name, int amount, Function<String, JsonUiElement<Value, Element>> elementProvider) {
        JsonUiElementArray.JsonUiElementArrayBuilder<Value, Element> builder = JsonUiElementArray.builder();

        for (int i = 0; i < amount; i++) {
            String jsonTuplePath = jsonTuplePath(jsonPath, i, name);
            builder.element(elementProvider.apply(jsonTuplePath));
        }

        return builder.build();
    }

    public JsonUiElement<JsonNull, RENDEROBJ> space(int height) {
        return JsonUiElement.<JsonNull, RENDEROBJ>builder()
            .element(new Spacer(1, height))
            .build();
    }

    public JsonUiElement<JsonNull, Section> header(String text, int indents) {
        return JsonUiElement.<JsonNull, Section>builder()
            .element(UiFactory.indent(indents, UiFactory.header(text, UI.FONT().M)))
            .build();
    }

    public JsonUiElement<JsonNull, Section> header(String text) {
        return JsonUiElement.<JsonNull, Section>builder()
            .element(UiFactory.header(text, UI.FONT().H2))
            .build();
    }

    public JsonUiElement<JsonString, ColorPicker> color(String jsonPath, JsonString defaultValue) {
        JsonUiElement<JsonString, ColorPicker> color = JsonUiElement.from(
            jsonPath,
            config,
            defaultValue,
            JsonString.class,
            UiFactory::color)
        .path(path)
        .valueSupplier(colorPicker -> new JsonString(colorPicker.getElement().to("_")))
        .valueConsumer((colorPicker, jsonString) -> colorPicker.getElement().set(jsonString.getValue(), "_"))
        .build();

        return color;
    }

    public JsonUiElement<JsonObject, ColorPicker> colorO(String jsonPath, JsonObject defaultValue) {
        JsonUiElement<JsonObject, ColorPicker> color = JsonUiElement.from(
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

        return color;
    }

    public JsonUiElement<JsonDouble, Slider> sliderD(String jsonPath, int min, int max, int step, int resolution, JsonDouble defaultValue) {
        JsonUiElement<JsonDouble, Slider> slider = JsonUiElement.from(
                jsonPath,
                config,
                defaultValue,
                JsonDouble.class,
                value -> UiFactory.slider(value, min, max, step, resolution))
            .path(path)
            .valueConsumer((slider1, jsonDouble) -> slider1.getElement().setValueD(jsonDouble.getValue()))
            .valueSupplier(slider1 -> new JsonDouble(slider1.getElement().getValueD()))
            .build();

        return slider;
    }

    public JsonUiElement<JsonLong, Slider> slider(String jsonPath, int min, int max, int step, List<Integer> allowedValues, JsonLong defaultValue) {
        JsonUiElement<JsonLong, Slider> slider = JsonUiElement.from(
            jsonPath,
            config,
            defaultValue,
            JsonLong.class,
            value -> UiFactory.slider(value, min, max, step, allowedValues))
        .path(path)
        .valueConsumer((slider1, jsonLong) -> slider1.getElement().setValue(jsonLong.getValue().intValue()))
        .valueSupplier(slider1 -> new JsonLong(Long.valueOf(slider1.getElement().getValue())))
        .build();

        return slider;
    }
    
    public JsonUiElement<JsonArray, Select<String>> selectS(String jsonPath, List<String> options, JsonArray defaultValue, int maxSelect, boolean maxSelected) {
        JsonUiElement<JsonArray, Select<String>> select = JsonUiElement.from(
                jsonPath,
                config,
                defaultValue,
                JsonArray.class,
                value -> UiFactory.selectS(value, options, maxSelect, maxSelected))
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

        return select;
    }

    public JsonUiElement<JsonString, DropDown<String>> dropDown(String jsonPath, List<String> options, JsonString defaultValue) {
        JsonUiElement<JsonString, DropDown<String>> select = JsonUiElement.from(
                jsonPath,
                config,
                defaultValue,
                JsonString.class,
                value -> UiFactory.dropDown(value, options))
            .path(path)
            .valueSupplier(dropDown -> new JsonString(dropDown.getElement().getValue()))
            .valueConsumer((dropDown, jsonString) -> dropDown.getElement().setValue(jsonString.getValue()))
            .build();

        return select;
    }

    public JsonUiElement<JsonArray, MultiDropDown<String>> multiDropDown(String jsonPath, String title, List<String> options, JsonArray defaultValue, int maxSelect, boolean maxSelected) {
        JsonUiElement<JsonArray, MultiDropDown<String>> select = JsonUiElement.from(
                jsonPath,
                config,
                defaultValue,
                JsonArray.class,
                value -> UiFactory.multiDropDown(title, value, options, maxSelect, maxSelected))
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

        return select;
    }

    public JsonUiElement<JsonBoolean, Checkbox> checkbox(String jsonPath, JsonBoolean defaultValue) {
        JsonUiElement<JsonBoolean, Checkbox> checkbox = JsonUiElement.from(
                jsonPath,
                config,
                defaultValue,
                JsonBoolean.class,
                UiFactory::checkbox)
            .path(path)
            .valueSupplier(checkbox1 -> new JsonBoolean(checkbox1.getElement().selectedIs()))
            .valueConsumer((checkbox1, jsonBoolean) -> checkbox1.getElement().selectedSet(jsonBoolean.getValue()))
            .build();

        return checkbox;
    }

    public JsonUiElement<JsonString, GInput> text(String jsonPath, JsonString defaultValue) {
        JsonUiElement<JsonString, GInput> icon = JsonUiElement.from(
                jsonPath,
                config,
                defaultValue,
                JsonString.class,
                UiFactory::text)
            .path(path)
            .valueSupplier(text1 -> JsonString.of(text1.getElement().text().toString()))
            .valueConsumer((text1, jsonString) -> {
                text1.getElement().text().clear();
                text1.getElement().text().add(jsonString.getValue());
            })
            .build();

        return icon;
    }

    private String jsonTuplePath(String jsonPath, int index, String name) {
        return jsonObjectPath(jsonArrayPath(jsonPath, index), name);
    }
    private String jsonArrayPath(String jsonPath, int index) {
        return jsonPath + "[" + index + "]";
    }

    private String jsonObjectPath(String jsonPath, String name) {
        return jsonPath + "." + name;
    }
}
