package menu.json;

import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.json.element.*;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.Lists;
import init.sprite.UI.UI;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import menu.IconView;
import menu.Ui;
import org.jetbrains.annotations.Nullable;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;
import snake2d.util.sprite.text.StringInputSprite;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUiFactory {

    private final static Logger log = Loggers.getLogger(JsonUiFactory.class);

    public static DropDown<String> dropDown(JsonString jsonString, List<String> options) {
        return DropDown.<String>builder()
            .label(jsonString.getValue())
            .clickAction(dropDown -> Ui.getInstance().popup().show(dropDown.getMenu(), dropDown))
            .closeAction(dropDown -> Ui.getInstance().popup().close())
            .menu(Toggle.<String>builder()
                .highlight(true)
                .menu(ButtonMenu.<String>builder()
                    .sameWidth(true)
                    .buttons(options)
                    .build())
                .aktiveKey(jsonString.getValue())
                .build())
            .build();
    }

    public static MultiDropDown<String> multiDropDown(String title, JsonArray jsonArray, List<String> options, int maxSelect, boolean maxSelected) {
        return MultiDropDown.<String>builder()
            .label(title)
            .clickAction(dropDown -> Ui.getInstance().popup().show(dropDown.getSelect(), dropDown))
            .select(selectS(jsonArray,options, maxSelect, maxSelected))
            .build();
    }

    public static ColorPicker color(JsonString jsonString) {
        return ColorPicker.ofString(jsonString.getValue(), "_");
    }

    public static ColorPicker color(JsonObject json) {
        Integer[] colors = JsonUiMapper.colors(json);
        return new ColorPicker(colors);
    }

    @Nullable
    public static Select<String> selectS(JsonElement jsonElement, List<String> options, int maxSelect, boolean maxSelected) {
        List<String> selected = new ArrayList<>();
        if (jsonElement instanceof JsonArray) {
            selected.addAll(((JsonArray) jsonElement).getElements().stream()
                .filter(element -> element instanceof JsonString)
                .map(JsonString.class::cast)
                .map(JsonString::getValue)
                .collect(Collectors.toList()));
        }

        ButtonMenu.ButtonMenuBuilder<String> menuBuilder = ButtonMenu.builder();
        options.forEach(name -> menuBuilder.button(name, new Button(name)));

        if (maxSelected) {
            maxSelect = selected.size();
        }

        return Select.<String>builder()
            .menu(menuBuilder
                .horizontal(true)
                .maxWidth(600)
                .sameWidth(true)
                .build())
            .maxSelect(maxSelect)
            .selectedKeys(selected)
            .build();
    }

    public static Slider slider(JsonDouble jsonElement, int min, int max, int step, int resolution) {
        Slider slider = slider(min, max, step, Lists.of())
            .valueD(jsonElement.getValue(), resolution)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .build();

        slider.mouseCooSupplier(() -> Ui.getInstance().getMouseCoo());
        return slider;
    }

    public static Slider slider(JsonLong jsonElement, int min, int max, int step, List<Integer> allowedValues) {
        int value = jsonElement.getValue().intValue();
        Slider slider = slider(min, max, step, allowedValues)
            .value(value)
            .valueDisplay(Slider.ValueDisplay.ABSOLUTE)
            .build();

        slider.mouseCooSupplier(() -> Ui.getInstance().getMouseCoo());
        return slider;
    }

    private static Slider.SliderBuilder slider(int min, int max, int step, List<Integer> allowedValues) {
        return Slider.builder()
            .min(min)
            .max(max)
            .step(step)
            .width(400)
            .input(true)
            .lockScroll(true)
            .allowedValues(allowedValues);
    }

    @Nullable
    public static IconView icon(JsonObject jsonObject) {
        try {
            JsonE jsonE = com.github.argon.sos.moreoptions.json.JsonMapper.mapJson(jsonObject);
            Json json = new Json(jsonE.toString(), "");
            return new IconView(UI.icons().get(json), null, json, jsonObject);
        } catch (Exception e) {
            log.warn("", e);
        }

        return null;
    }

    @Nullable
    public static IconView icon(JsonString jsonString) {
        try {
            JsonE jsonE = new JsonE();
            jsonE.add("ICON", jsonString.getValue());
            Json json = new Json(jsonE.toString(), "BOCKWURST");

            // fixme can not load icons, because of no content in fileGetter
            return new IconView(
                UI.icons().get(json),
                jsonString.getValue(),
                null,
                null
            );
        } catch (Exception e) {
            log.warn("", e);
        }

        return null;
    }

    public static GInput text(JsonString jsonString) {
        StringInputSprite inputSprite = new StringInputSprite(32, UI.FONT().S);
        GInput gInput = new GInput(inputSprite, Ui.MOUSE_COO_SUPPLIER, 0);
        gInput.text().add(jsonString.getValue());

        return gInput;
    }

    @Nullable
    public static Checkbox checkbox(JsonElement jsonElement) {
        boolean checked;
        if (jsonElement instanceof JsonBoolean) {
            checked = ((JsonBoolean) jsonElement).getValue();
        } else {
            return null;
        }

        return new Checkbox(checked);
    }

}
