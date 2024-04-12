package menu.json.factory;

import com.github.argon.sos.moreoptions.game.json.GameJsonStore;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.element.*;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.StringUtil;
import menu.json.JsonUiException;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Used for creating ui elements like sliders, checkboxes, input fields etc.
 *
 * High api layer for creating {@link JsonUiElement}s through the {@link JsonUiElementFactory}.
 * For overloading methods to make the usage easier.
 */
public class JsonUiTemplate {
    private final JsonUiElementFactory factory;
    private final JsonUiElementStore store;

    private JsonUiTemplate(Path path, JsonObject jsonConfig) {
        this.factory = new JsonUiElementFactory(path, jsonConfig);
        this.store =  new JsonUiElementStore(jsonConfig);
    }


    public JsonUiElement<JsonArray, Select<String>> selectS(String jsonPath, List<String> options, boolean maxSelected) {
        return store.element(() -> factory.selectS(jsonPath, options, new JsonArray(), 0, maxSelected)).store();
    }

    public JsonUiElement<JsonArray, Select<String>> selectS(String jsonPath, List<String> options, int maxSelect) {
        return store.element(() -> factory.selectS(jsonPath, options, new JsonArray(), maxSelect, false)).store();
    }

    public JsonUiElement<JsonArray, Select<String>> selectS(String jsonPath, List<String> options) {
        return store.element(() -> factory.selectS(jsonPath, options, new JsonArray(), 0, false)).store();
    }

    public JsonUiElement<JsonArray, MultiDropDown<String>> multiDropDown(String jsonPath, List<String> options, boolean maxSelected) {
        return store.element(() -> factory.multiDropDown(jsonPath, jsonPath, options, new JsonArray(), 0, maxSelected)).store();
    }

    public JsonUiElement<JsonArray, MultiDropDown<String>> multiDropDown(String jsonPath, String title, List<String> options, boolean maxSelected) {
        return store.element(() -> factory.multiDropDown(jsonPath, title, options, new JsonArray(), 0, maxSelected)).store();
    }

    public JsonUiElement<JsonArray, MultiDropDown<String>> multiDropDown(String jsonPath, List<String> options, int maxSelect) {
        return store.element(() -> factory.multiDropDown(jsonPath, jsonPath, options, new JsonArray(), maxSelect, false)).store();
    }

    public JsonUiElement<JsonArray, MultiDropDown<String>> multiDropDown(String jsonPath, String title, List<String> options, int maxSelect) {
        return store.element(() -> factory.multiDropDown(jsonPath, title, options, new JsonArray(), maxSelect, false)).store();
    }

    public JsonUiElement<JsonArray, MultiDropDown<String>> multiDropDown(String jsonPath, List<String> options) {
        return store.element(() -> factory.multiDropDown(jsonPath, jsonPath, options, new JsonArray(), 0, false)).store();
    }

    public JsonUiElement<JsonArray, MultiDropDown<String>> multiDropDown(String jsonPath, String title, List<String> options) {
        return store.element(() -> factory.multiDropDown(jsonPath, title, options, new JsonArray(), 0, false)).store();
    }

    public JsonUiElement<JsonString, DropDown<String>> dropDown(String jsonPath, List<String> options) {
        return store.element(() -> factory.dropDown(jsonPath, options, new JsonString(""))).store();
    }

    public JsonUiElement<JsonBoolean, Checkbox> checkbox(String jsonPath) {
        return store.element(() -> factory.checkbox(jsonPath, JsonBoolean.of(false))).store();
    }

    public JsonUiElement<JsonLong, Slider> slider(String jsonPath, List<Integer> allowedValues) {
        int min = 0;
        int max = 1;

        if (!allowedValues.isEmpty()) {
            min = allowedValues.get(0);
            max = allowedValues.get(allowedValues.size() - 1);
        }

        return factory.slider(jsonPath, min, max, 1, allowedValues, JsonLong.of(0L));
    }

    public JsonUiElement<JsonLong, Slider> slider(String jsonPath, int min, int max, int step) {
        return store.element(() -> factory.slider(jsonPath, min, max, step, Lists.of(), JsonLong.of(0L))).store();
    }

    public JsonUiElement<JsonLong, Slider> slider(String jsonPath, int min, int max) {
        return store.element(() -> factory.slider(jsonPath, min, max, 1, Lists.of(),JsonLong.of(0L))).store();
    }

    public JsonUiElementArray<JsonLong, Slider> sliders(String jsonPath, List<String> names, int min, int max, int step) {
        return store.elements(() -> factory.asObject(jsonPath, names, false, jsonPath1 -> slider(jsonPath1, min, max, step))).store();
    }

    public JsonUiElementArray<JsonLong, Slider> sliders(String jsonPath, List<String> names, boolean asWildcards, int min, int max, int step) {
        return store.elements(() -> factory.asObject(jsonPath, names, asWildcards, jsonPath1 -> slider(jsonPath1, min, max, step))).store();
    }

    public JsonUiElementArray<JsonLong, Slider> sliders(String jsonPath, List<String> names, boolean asWildcards, int min, int max) {
        return store.elements(() -> factory.asObject(jsonPath, names, asWildcards, jsonPath1 -> slider(jsonPath1, min, max))).store();
    }

    public JsonUiElementArray<JsonLong, Slider> sliders(String jsonPath, List<String> names, int min, int max) {
        return store.elements(() -> factory.asObject(jsonPath, names, false, jsonPath1 -> slider(jsonPath1, min, max))).store();
    }

    public JsonUiElementArray<JsonLong, Slider> sliders(String jsonPath, int amount, int min, int max, int step) {
        return store.elements(() -> factory.asArray(jsonPath, amount, jsonPath1 -> slider(jsonPath1, min, max, step))).store();
    }

    public JsonUiElementArray<JsonLong, Slider> sliders(String jsonPath, int amount, int min, int max) {
        return store.elements(() -> factory.asArray(jsonPath, amount, jsonPath1 -> slider(jsonPath1, min, max))).store();
    }


    public JsonUiElementArray<JsonLong, Slider> sliders(String jsonPath, String name, int amount, int min, int max, int step) {
        return store.elements(() -> factory.asTuples(jsonPath, name, amount, jsonPath1 -> slider(jsonPath1, min, max, step))).store();
    }

    public JsonUiElementArray<JsonLong, Slider> sliders(String jsonPath, String name, int amount, int min, int max) {
        return store.elements(() -> factory.asTuples(jsonPath, name, amount, jsonPath1 -> slider(jsonPath1, min, max))).store();
    }


    public JsonUiElement<JsonDouble, Slider> sliderD(String jsonPath, int min, int max) {
        return store.element(() -> factory.sliderD(jsonPath, min, max, 1, 1, JsonDouble.of(0.0))).store();
    }

    public JsonUiElement<JsonDouble, Slider> sliderD(String jsonPath, int min, int max, int resolution) {
        return store.element(() -> factory.sliderD(jsonPath, min, max, 1, resolution, JsonDouble.of(0.0))).store();
    }

    public JsonUiElementArray<JsonDouble, Slider> slidersD(String jsonPath, List<String> names, boolean asWildcards, int min, int max, int resolution) {
        return store.elements(() -> factory.asObject(jsonPath, names, asWildcards, jsonPath1 -> sliderD(jsonPath1, min, max, resolution))).store();
    }

    public JsonUiElementArray<JsonDouble, Slider> slidersD(String jsonPath, List<String> names, boolean asWildcards, int min, int max) {
        return store.elements(() -> factory.asObject(jsonPath, names, asWildcards, jsonPath1 -> sliderD(jsonPath1, min, max))).store();
    }

    public JsonUiElementArray<JsonDouble, Slider> slidersD(String jsonPath, List<String> names, int min, int max) {
        return store.elements(() -> factory.asObject(jsonPath, names, false, jsonPath1 -> sliderD(jsonPath1, min, max))).store();
    }

    public JsonUiElementArray<JsonDouble, Slider> slidersD(String jsonPath, List<String> names, int min, int max, int resolution) {
        return store.elements(() -> factory.asObject(jsonPath, names, false, jsonPath1 -> sliderD(jsonPath1, min, max, resolution))).store();
    }


    public JsonUiElementArray<JsonDouble, Slider> slidersD(String jsonPath, String name, int amount, int min, int max) {
        return store.elements(() -> factory.asTuples(jsonPath, name, amount, jsonPath1 -> sliderD(jsonPath1, min, max))).store();
    }

    public JsonUiElementArray<JsonDouble, Slider> slidersD(String jsonPath, String name, int amount, int min, int max, int resolution) {
        return store.elements(() -> factory.asTuples(jsonPath, name, amount, jsonPath1 -> sliderD(jsonPath1, min, max, resolution))).store();
    }


    public JsonUiElementArray<JsonDouble, Slider> slidersD(String jsonPath, int amount, int min, int max) {
        return store.elements(() -> factory.asArray(jsonPath, amount, jsonPath1 -> sliderD(jsonPath1, min, max))).store();
    }

    public JsonUiElementArray<JsonDouble, Slider> slidersD(String jsonPath, int amount, int min, int max, int resolution) {
        return store.elements(() -> factory.asArray(jsonPath, amount, jsonPath1 -> sliderD(jsonPath1, min, max, resolution))).store();
    }

    public JsonUiElement<JsonDouble, Slider> sliderD(String jsonPath, int min, int max, JsonDouble defaultValue) {
        return store.element(() -> factory.sliderD(jsonPath, min, max, 1, 1, defaultValue)).store();
    }

    public JsonUiElement<JsonString, ColorPicker> color(String jsonPath) {
        return store.element(() -> factory.color(jsonPath, JsonString.of("255_0_255"))).store();
    }

    public JsonUiElement<JsonNull, RENDEROBJ> space(int height) {
        return store.element(() -> factory.space(height)).store();
    }

    public JsonUiElement<JsonNull, RENDEROBJ> space() {
        return store.element(() -> factory.space(32)).store();
    }

    public JsonUiElement<JsonNull, Section> header(String text) {
        return store.element(() -> factory.header(text)).store();
    }

    public List<JsonUiElement<JsonNull, Section>> headers(String jsonPath) {
        List<JsonUiElement<JsonNull, Section>> jsonUiElements = new ArrayList<>();
        String[] split = jsonPath.split("\\.");
        for (int i = 0; i < split.length; i++) {
            String jsonPathKey = split[i];
            JsonUiElement<JsonNull, Section> header = factory.header(jsonPathKey, i);
            jsonUiElements.add(store.element(header).store());
        }

        return jsonUiElements;
    }

    public JsonUiElement<JsonString, GInput> text(String jsonPath) {
        return store.element(() -> factory.text(jsonPath, JsonString.of(""))).store();
    }

    public List<ColumnRow<Void>> toColumnRows() {
        return store.getAll().stream()
            .map(JsonUiElement::toColumnRow)
            .collect(Collectors.toList());
    }

    public boolean isDirty() {
        return store.isDirty();
    }

    public JsonObject getConfig() {
        return store.getConfig();
    }

    public void reset() {
        store.reset();
    }

    public List<JsonUiElement<?, ?>> getOrphans() {
        return store.getOrphans();
    }

    public String getName() {
        return StringUtil.removeTrailing(getPath().getFileName().toString(), ".txt");
    }

    public String getFileName() {
        return getPath().getFileName().toString();
    }

    public Path getPath() {
        return factory.getPath();
    }

    public static JsonUiTemplate from(Path path) {
        Json json = GameJsonStore.getInstance().getJson(path);
        if (json == null || json.getRoot() == null) {
            throw new JsonUiException("No or empty json for path " + path);
        }

        return new JsonUiTemplate(path, json.getRoot());
    }
}
