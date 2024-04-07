package menu.json;

import com.github.argon.sos.moreoptions.game.json.GameJsonStore;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.element.*;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.StringUtil;
import menu.IconView;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class JsonUiTemplate {
    private final JsonUiElementFactory factory;


    private JsonUiTemplate(Path path, JsonObject jsonConfig) {
        this.factory = new JsonUiElementFactory(path, jsonConfig);
    }

    public void reset() {
        factory.reset();
    }

    public JsonUiElement<JsonString, IconView> icon(String key) {
        return factory.icon(key, JsonString.of(""));
    }

    public JsonUiElement<JsonObject, IconView> iconO(String key) {
        return factory.icon(key, new JsonObject());
    }

    public JsonUiElement<JsonString, GInput> text(String key) {
        return factory.text(key, JsonString.of(""));
    }

    public JsonUiElement<JsonArray, Select<String>> selectS(String key, List<String> options) {
        return factory.selectS(key, options, new JsonArray());
    }

    public JsonUiElement<JsonString, DropDown<String>> dropDown(String key, List<String> options) {
        return factory.dropDown(key, options, new JsonString(""));
    }

    public JsonUiElement<JsonBoolean, Checkbox> checkbox(String key) {
        return factory.checkbox(key, JsonBoolean.of(false));
    }

    public JsonUiElement<JsonLong, Slider> slider(String key, List<Integer> allowedValues) {
        int min = 0;
        int max = 1;

        if (!allowedValues.isEmpty()) {
            min = allowedValues.get(0);
            max = allowedValues.get(allowedValues.size() - 1);
        }

        return factory.slider(key, min, max, 1, allowedValues, JsonLong.of(0L));
    }

    public JsonUiElement<JsonLong, Slider> slider(String key, int min, int max, int step) {
        return factory.slider(key, min, max, step, Lists.of(), JsonLong.of(0L));
    }

    public JsonUiElement<JsonLong, Slider> slider(String key, int min, int max) {
        return factory.slider(key, min, max, 1, Lists.of(),JsonLong.of(0L));
    }

    public JsonUiElement<JsonLong, Slider> slider(String key, int min, int max, JsonLong defaultValue) {
        return factory.slider(key, min, max, 1, Lists.of(), defaultValue);
    }

    public JsonUiElement<JsonDouble, Slider> sliderD(String key, int min, int max) {
        return factory.sliderD(key, min, max, 1, 1, JsonDouble.of(0.0));
    }

    public JsonUiElement<JsonDouble, Slider> sliderD(String key, int min, int max, int resolution) {
        return factory.sliderD(key, min, max, 1, resolution, JsonDouble.of(0.0));
    }

    public JsonUiElement<JsonDouble, Slider> sliderD(String key, int min, int max, int resolution, JsonDouble defaultValue) {
        return factory.sliderD(key, min, max, 1, resolution, defaultValue);
    }

    public JsonUiElement<JsonDouble, Slider> sliderD(String key, int min, int max, JsonDouble defaultValue) {
        return factory.sliderD(key, min, max, 1, 1, defaultValue);
    }

    public JsonUiElement<JsonString, ColorPicker> color(String key) {
        return factory.color(key, JsonString.of("255_0_255"));
    }

    public JsonUiElement<JsonNull, RENDEROBJ> separator() {
        return factory.separator();
    }

    public JsonUiElement<JsonNull, RENDEROBJ> header(String text) {
        return factory.header(text);
    }

    public List<ColumnRow<Void>> toColumnRows() {
        return factory.getAll().values().stream()
            .map(JsonUiElement::toColumnRow)
            .collect(Collectors.toList());
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
            throw new JsonUiException("TODO");
        }

        return new JsonUiTemplate(path, json.getRoot());
    }
}
