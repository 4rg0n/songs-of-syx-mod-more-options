package menu.json.factory;

import com.github.argon.sos.moreoptions.game.json.GameJsonStore;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.element.*;
import com.github.argon.sos.mod.sdk.util.Lists;
import menu.json.JsonUiException;

import java.nio.file.Path;
import java.util.List;

/**
 * Used for creating ui elements like sliders, checkboxes, input fields etc.
 *
 * High api layer for creating {@link JsonUiElementSingle}s through the {@link JsonUiElementFactory}.
 * For overloading methods to make the usage easier.
 */
public class JsonUiTemplate extends AbstractJsonUiTemplate {

    public JsonUiTemplate(JsonUiElementFactory factory, JsonUiElementStore store) {
        super(factory, store);
    }

    public JsonUiElement<JsonArray, Select<String>> selectS(String jsonPath, List<String> options, boolean maxSelected) {
        return store.supplier(() -> factory.selectS(jsonPath, options, new JsonArray(), 0, maxSelected)).store();
    }

    public JsonUiElement<JsonArray, Select<String>> selectS(String jsonPath, List<String> options, int maxSelect) {
        return store.supplier(() -> factory.selectS(jsonPath, options, new JsonArray(), maxSelect, false)).store();
    }

    public JsonUiElement<JsonArray, Select<String>> selectS(String jsonPath, List<String> options) {
        return store.supplier(() -> factory.selectS(jsonPath, options, new JsonArray(), 0, false)).store();
    }

    public JsonUiElement<JsonArray, MultiDropDown<String>> multiDropDown(String jsonPath, List<String> options, boolean maxSelected) {
        return store.supplier(() -> factory.multiDropDown(jsonPath, jsonPath, options, new JsonArray(), 0, maxSelected)).store();
    }

    public JsonUiElement<JsonArray, MultiDropDown<String>> multiDropDown(String jsonPath, String title, List<String> options, boolean maxSelected) {
        return store.supplier(() -> factory.multiDropDown(jsonPath, title, options, new JsonArray(), 0, maxSelected)).store();
    }

    public JsonUiElement<JsonArray, MultiDropDown<String>> multiDropDown(String jsonPath, List<String> options, int maxSelect) {
        return store.supplier(() -> factory.multiDropDown(jsonPath, jsonPath, options, new JsonArray(), maxSelect, false)).store();
    }

    public JsonUiElement<JsonArray, MultiDropDown<String>> multiDropDown(String jsonPath, String title, List<String> options, int maxSelect) {
        return store.supplier(() -> factory.multiDropDown(jsonPath, title, options, new JsonArray(), maxSelect, false)).store();
    }

    public JsonUiElement<JsonArray, MultiDropDown<String>> multiDropDown(String jsonPath, List<String> options) {
        return store.supplier(() -> factory.multiDropDown(jsonPath, jsonPath, options, new JsonArray(), 0, false)).store();
    }

    public JsonUiElement<JsonArray, MultiDropDown<String>> multiDropDown(String jsonPath, String title, List<String> options) {
        return store.supplier(() -> factory.multiDropDown(jsonPath, title, options, new JsonArray(), 0, false)).store();
    }

    public JsonUiElement<JsonString, DropDown<String>> dropDown(String jsonPath, List<String> options) {
        return store.supplier(() -> factory.dropDown(jsonPath, options, new JsonString(""))).store();
    }

    public JsonUiElement<JsonArray, DropDownList> dropDownList(String jsonPath, List<String> options) {
        return store.supplier(() -> factory.dropDownList(jsonPath, jsonPath, options, new JsonArray())).store();
    }

    public JsonUiElement<JsonBoolean, Checkbox> checkbox(String jsonPath) {
        return store.supplier(() -> factory.checkbox(jsonPath, JsonBoolean.of(false))).store();
    }

    public JsonUiElement<JsonLong, Slider> slider(String jsonPath, List<Integer> allowedValues) {
        return store.supplier(() -> factory.slider(jsonPath,1, allowedValues, JsonLong.of(0L))).store();
    }

    public JsonUiElement<JsonLong, Slider> slider(String jsonPath, int min, int max, int step) {
        return store.supplier(() -> factory.slider(jsonPath, min, max, step, Lists.of(), JsonLong.of(0L))).store();
    }

    public JsonUiElement<JsonLong, Slider> slider(String jsonPath, int min, int max) {
        return store.supplier(() -> factory.slider(jsonPath, min, max, 1, Lists.of(),JsonLong.of(0L))).store();
    }

    public JsonUiElementList<JsonLong, Slider> sliders(String jsonPath, List<String> names, int min, int max, int step) {
        return store.supplier(() -> factory.asObject(jsonPath, names, false,
                jsonPath1 -> factory.slider(jsonPath1, min, max, step, Lists.of(), JsonLong.of(0L)))).store();
    }

    public JsonUiElementList<JsonLong, Slider> sliders(String jsonPath, List<String> names, boolean asWildcards, int min, int max, int step) {
        return store.supplier(() -> factory.asObject(jsonPath, names, asWildcards,
            jsonPath1 -> factory.slider(jsonPath1, min, max, step, Lists.of(), JsonLong.of(0L)))).store();
    }

    public JsonUiElementList<JsonLong, Slider> sliders(String jsonPath, List<String> names, boolean asWildcards, int min, int max) {
        return store.supplier(() -> factory.asObject(jsonPath, names, asWildcards,
            jsonPath1 -> factory.slider(jsonPath1, min, max, 1, Lists.of(), JsonLong.of(0L)))).store();
    }

    public JsonUiElementList<JsonLong, Slider> sliders(String jsonPath, List<String> names, int min, int max) {
        return store.supplier(() -> factory.asObject(jsonPath, names, false,
            jsonPath1 -> factory.slider(jsonPath1, min, max, 1, Lists.of(), JsonLong.of(0L)))).store();
    }

    public JsonUiElementList<JsonLong, Slider> sliders(String jsonPath, int amount, int min, int max, int step) {
        return store.supplier(() -> factory.asArray(jsonPath, amount,
            jsonPath1 -> factory.slider(jsonPath1, min, max, step, Lists.of(), JsonLong.of(0L)))).store();
    }

    public JsonUiElementList<JsonLong, Slider> sliders(String jsonPath, int amount, int min, int max) {
        return store.supplier(() -> factory.asArray(jsonPath, amount,
            jsonPath1 -> factory.slider(jsonPath1, min, max, 1, Lists.of(), JsonLong.of(0L)))).store();
    }


    public JsonUiElementList<JsonLong, Slider> sliders(String jsonPath, String name, int amount, int min, int max, int step) {
        return store.supplier(() -> factory.asTuples(jsonPath, name, amount,
            jsonPath1 -> factory.slider(jsonPath1, min, max, step, Lists.of(), JsonLong.of(0L)))).store();
    }

    public JsonUiElementList<JsonLong, Slider> sliders(String jsonPath, String name, int amount, int min, int max) {
        return store.supplier(() -> factory.asTuples(jsonPath, name, amount,
            jsonPath1 -> factory.slider(jsonPath1, min, max, 1, Lists.of(), JsonLong.of(0L)))).store();
    }


    public JsonUiElement<JsonDouble, Slider> sliderD(String jsonPath, int min, int max) {
        return store.supplier(() -> factory.sliderD(jsonPath, min, max, 1, 1, JsonDouble.of(0.0))).store();
    }

    public JsonUiElement<JsonDouble, Slider> sliderD(String jsonPath, int min, int max, int resolution) {
        return store.supplier(() -> factory.sliderD(jsonPath, min, max, 1, resolution, JsonDouble.of(0.0))).store();
    }

    public JsonUiElementList<JsonDouble, Slider> slidersD(String jsonPath, List<String> names, boolean asWildcards, int min, int max, int resolution) {
        return store.supplier(() -> factory.asObject(jsonPath, names, asWildcards,
            jsonPath1 -> factory.sliderD(jsonPath1, min, max, 1, resolution, JsonDouble.of(0.0)))).store();
    }

    public JsonUiElementList<JsonDouble, Slider> slidersD(String jsonPath, List<String> names, boolean asWildcards, int min, int max) {
        return store.supplier(() -> factory.asObject(jsonPath, names, asWildcards,
            jsonPath1 -> factory.sliderD(jsonPath1, min, max, 1, 1, JsonDouble.of(0.0)))).store();
    }

    public JsonUiElementList<JsonDouble, Slider> slidersD(String jsonPath, List<String> names, int min, int max) {
        return store.supplier(() -> factory.asObject(jsonPath, names, false,
            jsonPath1 -> factory.sliderD(jsonPath1, min, max, 1, 1, JsonDouble.of(0.0)))).store();
    }

    public JsonUiElementList<JsonDouble, Slider> slidersD(String jsonPath, List<String> names, int min, int max, int resolution) {
        return store.supplier(() -> factory.asObject(jsonPath, names, false,
            jsonPath1 -> factory.sliderD(jsonPath1, min, max, 1, resolution, JsonDouble.of(0.0)))).store();
    }


    public JsonUiElementList<JsonDouble, Slider> slidersD(String jsonPath, String name, int amount, int min, int max) {
        return store.supplier(() -> factory.asTuples(jsonPath, name, amount,
            jsonPath1 -> factory.sliderD(jsonPath1, min, max, 1, 1, JsonDouble.of(0.0)))).store();
    }

    public JsonUiElementList<JsonDouble, Slider> slidersD(String jsonPath, String name, int amount, int min, int max, int resolution) {
        return store.supplier(() -> factory.asTuples(jsonPath, name, amount,
            jsonPath1 -> factory.sliderD(jsonPath1, min, max, 1, resolution, JsonDouble.of(0.0)))).store();
    }


    public JsonUiElementList<JsonDouble, Slider> slidersD(String jsonPath, int amount, int min, int max) {
        return store.supplier(() -> factory.asArray(jsonPath, amount,
            jsonPath1 -> factory.sliderD(jsonPath1, min, max, 1, 1, JsonDouble.of(0.0)))).store();
    }

    public JsonUiElementList<JsonDouble, Slider> slidersD(String jsonPath, int amount, int min, int max, int resolution) {
        return store.supplier(() -> factory.asArray(jsonPath, amount,
            jsonPath1 -> factory.sliderD(jsonPath1, min, max, 1, resolution, JsonDouble.of(0.0)))).store();
    }

    public JsonUiElement<JsonDouble, Slider> sliderD(String jsonPath, int min, int max, JsonDouble defaultValue) {
        return store.supplier(() -> factory.sliderD(jsonPath, min, max, 1, 1, defaultValue)).store();
    }

    public JsonUiElement<JsonString, ColorPicker> color(String jsonPath) {
        return store.supplier(() -> factory.color(jsonPath, JsonString.of("255_0_255"))).store();
    }

    public JsonUiElement<JsonNull, Spacer> space(int height) {
        return store.supplier(() -> factory.space(height)).store();
    }

    public JsonUiElement<JsonNull, Spacer> space() {
        return store.supplier(() -> factory.space(32)).store();
    }

    public JsonUiElement<JsonNull, Section> header(String text) {
        return store.supplier(() -> factory.header(text)).store();
    }

    public JsonUiElement<JsonString, Input> text(String jsonPath) {
        return store.supplier(() -> factory.text(jsonPath, JsonString.of(""))).store();
    }

    public static JsonUiTemplate from(Path path) {
        Json json = GameJsonStore.getInstance().getJson(path);
        if (json == null || json.getRoot() == null) {
            throw new JsonUiException("No or empty json for file path " + path);
        }

        JsonObject jsonConfig = json.getRoot();
        JsonUiElementFactory factory = new JsonUiElementFactory(path, jsonConfig);
        JsonUiElementStore store = new JsonUiElementStore(jsonConfig);

        return new JsonUiTemplate(factory, store);
    }
}
