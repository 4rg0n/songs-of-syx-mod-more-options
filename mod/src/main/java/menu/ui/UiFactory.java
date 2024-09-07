package menu.ui;

import com.github.argon.sos.mod.sdk.game.ui.*;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import com.github.argon.sos.mod.sdk.json.JsonMapper;
import com.github.argon.sos.mod.sdk.json.element.*;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Lists;
import com.github.argon.sos.moreoptions.game.ui.ColorPicker;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.SliderDoubleList;
import com.github.argon.sos.moreoptions.game.ui.SliderIntegerList;
import init.sprite.UI.UI;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import menu.Ui;
import com.github.argon.sos.moreoptions.ui.json.JsonUiMapper;
import org.jetbrains.annotations.Nullable;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.text.Font;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.misc.GHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UiFactory {
    private final static Logger log = Loggers.getLogger(UiFactory.class);

    public static DropDown<String> dropDown(JsonString jsonString, List<String> options) {
        return DropDown.<String>builder()
            .label(jsonString.getValue())
            .clickAction(dropDown -> Ui.getInstance().popups().show(dropDown.getMenu(), dropDown))
            .closeAction(dropDown -> Ui.getInstance().popups().close(dropDown))
            .menu(Switcher.<String>builder()
                .highlight(true)
                .menu(ButtonMenu.<String>builder()
                    .sameWidth(true)
                    .buttons(options)
                    .build())
                .aktiveKey(jsonString.getValue())
                .build())
            .build();
    }

    public static Section indent(int indents, RENDEROBJ render) {
        Section indentRow = new Section();
        indentRow.addRightC(0, UiFactory.indent(indents));
        indentRow.addRightC(0, render);
        return indentRow;
    }

    public static RENDEROBJ indent(int indents) {
        return new Spacer(30 * indents, 1);
    }

    public static Section header(String text, Font font) {
        GHeader gHeader = new GHeader(text, font);
        Section section = UiUtil.toSection(gHeader);
        section.pad(0, 5);

        return section;
    }

    public static Section label(String text, Font font) {
        return Label.builder()
            .name(text)
            .font(font)
            .style(Label.Style.LABEL)
            .build();
    }


    public static MultiDropDown<String> multiDropDown(String buttonTitle, JsonArray jsonArray, List<String> options, int maxSelect, boolean maxSelected) {
        return MultiDropDown.<String>builder()
            .label(buttonTitle)
            .clickAction(dropDown -> Ui.getInstance().popups().show(dropDown.getSelect(), dropDown))
            .select(selectS(jsonArray,options, maxSelect, maxSelected))
            .build();
    }

    public static DropDownList dropDownList(String buttonTitle, JsonArray jsonArray, List<String> options) {
        List<String> values = jsonArray.getElements().stream()
            .filter(element -> element instanceof JsonString)
            .map(JsonString.class::cast)
            .map(JsonString::getValue).collect(Collectors.toList());

        return DropDownList.builder()
            .values(values)
            .label(buttonTitle)
            .possibleValues(options)
            .clickAction(dropDownList -> Ui.getInstance().popups().show(dropDownList.getUiList(), dropDownList))
            .optionClickAction(dropDown -> Ui.getInstance().popups().show(dropDown.getMenu(), dropDown))
            .optionCloseAction(dropDown -> Ui.getInstance().popups().close(dropDown))
            .height(400)
            .build();
    }

    public static SliderDoubleList sliderDList(String buttonTitle, JsonArray jsonArray, Function<@Nullable Double, Slider> elementSupplier) {
        List<Slider> sliders = jsonArray.getElements().stream()
            .filter(element -> element instanceof JsonDouble)
            .map(JsonDouble.class::cast)
            .map(JsonDouble::getValue)
            .map(elementSupplier)
            .collect(Collectors.toList());

        return SliderDoubleList.builder()
            .label(buttonTitle)
            .sliders(sliders)
            .elementSupplier(elementSupplier)
            .clickAction(sliderList -> Ui.getInstance().popups().show(sliderList.getUiList(), sliderList))
            .height(400)
            .build();
    }

    public static SliderIntegerList sliderList(String buttonTitle, JsonArray jsonArray, Function<@Nullable Integer, Slider> elementSupplier) {
        List<Slider> sliders = jsonArray.getElements().stream()
            .filter(element -> element instanceof JsonLong)
            .map(JsonLong.class::cast)
            .map(JsonLong::getValue)
            .map(Long::intValue)
            .map(elementSupplier)
            .collect(Collectors.toList());

        return SliderIntegerList.builder()
            .label(buttonTitle)
            .sliders(sliders)
            .elementSupplier(elementSupplier)
            .clickAction(sliderList -> Ui.getInstance().popups().show(sliderList.getUiList(), sliderList))
            .height(400)
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
            .width(400)
            .step(step)
            .controls(true)
            .lockScroll(true)
            .allowedValues(allowedValues);
    }

    @Nullable
    public static IconView icon(JsonObject jsonObject) {
        try {
            JsonE jsonE = JsonMapper.mapJson(jsonObject);
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

    public static Input text(JsonString jsonString) {
        StringInputSprite inputSprite = new StringInputSprite(32, UI.FONT().S);
        MenuInput input = new MenuInput(inputSprite);
        input.text().add(jsonString.getValue());

        return input;
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
