package menu.json;

import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.json.element.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import menu.Ui;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUiFactory {

    public static DropDown<String> dropDown(JsonString jsonString, List<String> options) {
        return DropDown.<String>builder()
            .label(jsonString.getValue())
            .clickAction(dropDown -> Ui.getInstance().popup().show(dropDown.getMenu(), dropDown))
            .closeAction(dropDown -> Ui.getInstance().popup().close())
            .menu(Toggle.<String>builder()
                .menu(ButtonMenu.<String>builder()
                    .sameWidth(true)
                    .buttons(options)
                    .build())
                .aktiveKey(jsonString.getValue())
                .build())
            .build();
    }

    public static ColorPicker color(JsonString jsonString) {
        return ColorPicker.ofString(jsonString.getValue(), "_");
    }

    public static ColorPicker color(JsonObject json) {
        Integer[] colors = JsonMapper.colors(json);
        return new ColorPicker(colors);
    }

    public static Slider slider(JsonElement jsonElement, int min, int max, int step) {
        if (jsonElement instanceof JsonDouble) {
            return slider((JsonDouble) jsonElement, min, max, step);
        } else if (jsonElement instanceof JsonLong) {
            return slider((JsonLong) jsonElement, min, max, step);
        } else {
            throw new RuntimeException("TODO");
        }
    }

    @Nullable
    public static Select<String> selectS(JsonElement jsonElement, List<String> options) {
        List<String> selected = new ArrayList<>();
        if (jsonElement instanceof JsonArray) {
            selected.addAll(((JsonArray) jsonElement).getElements().stream()
                .filter(element -> element instanceof JsonString)
                .map(JsonString.class::cast)
                .map(JsonString::getValue)
                .collect(Collectors.toList()));
        }

        ButtonMenu.ButtonMenuBuilder<String> menuBuilder = ButtonMenu.builder();
        options.forEach(name -> menuBuilder
            .button(name, new Button(name)));

        return Select.<String>builder()
            .menu(menuBuilder
                .horizontal(true)
                .maxWidth(600)
                .sameWidth(true)
                .build())
            .selectedKeys(selected)
            .build();
    }

    public static Slider slider(JsonDouble jsonElement, int min, int max, int step, int resolution) {
        Slider slider = Slider.builder()
            .valueD(jsonElement.getValue(), resolution)
            .min(min)
            .max(max)
            .step(step)
            .width(400)
            .input(true)
            .lockScroll(true)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .build();

        slider.mouseCooSupplier(() -> Ui.getInstance().getMouseCoo());
        return slider;
    }

    public static Slider slider(JsonLong jsonElement, int min, int max, int step) {
        int value = jsonElement.getValue().intValue();
        Slider slider = Slider.builder()
            .value(value)
            .min(min)
            .max(max)
            .step(step)
            .width(500)
            .input(true)
            .lockScroll(true)
            .valueDisplay(Slider.ValueDisplay.ABSOLUTE)
            .build();

        slider.mouseCooSupplier(() -> Ui.getInstance().getMouseCoo());
        return slider;
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
