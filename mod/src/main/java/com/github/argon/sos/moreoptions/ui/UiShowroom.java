package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.game.ui.layout.Layouts;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import com.github.argon.sos.moreoptions.ui.msg.Notificator;
import com.github.argon.sos.mod.sdk.util.Lists;
import com.github.argon.sos.mod.sdk.util.Maps;
import com.github.argon.sos.mod.sdk.util.ReflectionUtil;
import init.sprite.SPRITES;
import init.sprite.UI.Icon;
import init.sprite.UI.Icons;
import init.sprite.UI.UI;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.text.StringInputSprite;
import util.colors.GCOLOR;
import util.gui.misc.GHeader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UiShowroom extends GuiSection {

    private final Notificator notificator = Notificator.getInstance();

    public UiShowroom() {
        int width = 900;
        // Small Icons
        List<GuiSection> iconsSmall = ReflectionUtil.<Icons.S.IconS>getDeclaredFieldValuesMap(Icons.S.IconS.class, SPRITES.icons().s)
            .entrySet().stream()
            .map(entry -> {
                GuiSection section = UiUtil.toGuiSection(entry.getValue());
                section.hoverInfoSet("SPRITES.icons().s." + entry.getKey().getName());
                return section;
            }).collect(Collectors.toList());
        GuiSection iconsSmallSection = Layouts.flow(iconsSmall, null, null, width, 100, 0);

        // Medium Icons
        List<GuiSection> iconsMedium = ReflectionUtil.<Icon>getDeclaredFieldValuesMap(Icon.class, SPRITES.icons().m)
            .entrySet().stream()
            .map(entry -> {
                GuiSection section = UiUtil.toGuiSection(entry.getValue());
                section.hoverInfoSet("SPRITES.icons().m." + entry.getKey().getName());
                return section;
            }).collect(Collectors.toList());
        GuiSection iconsMediumSection = Layouts.flow(iconsMedium, null, null, width,150, 0);

        // Large Icons
        List<GuiSection> iconsLarge = ReflectionUtil.<Icon>getDeclaredFieldValuesMap(Icon.class, SPRITES.icons().l)
            .entrySet().stream()
            .map(entry -> {
                GuiSection section = UiUtil.toGuiSection(entry.getValue());
                section.hoverInfoSet("SPRITES.icons().l." + entry.getKey().getName());
                return section;
            }).collect(Collectors.toList());
        GuiSection iconsLargeSection = Layouts.flow(iconsLarge, null, null, width, 100, 0);

        // COLOR
        List<ColorBox> colors = ReflectionUtil.<COLOR>getDeclaredFieldValuesMap(COLOR.class, COLOR.class)
            .entrySet().stream()
            // sort by "color name"
            .sorted(Map.Entry.comparingByKey(Comparator.comparing(Field::getName)))
            .map(entry -> {
                ColorBox colorBox = new ColorBox(24, 24, entry.getValue());
                colorBox.hoverInfoSet("COLOR." + entry.getKey().getName());
                return colorBox;
            }).collect(Collectors.toList());
        GuiSection colorsSection = Layouts.flow(colors, null, null, width, 100, 0);

        // COLOR.UNIQUE
        List<ColorBox> colorsUnique = new ArrayList<>();
        for (int i = 0; i < COLOR.UNIQUE.size(); i++) {
            COLOR color = COLOR.UNIQUE.get(i);
            ColorBox colorBox = new ColorBox(24, 24, color);
            colorBox.hoverInfoSet("COLOR.UNIQUE[" + i  + "]");
            colorsUnique.add(colorBox);
        }
        GuiSection colorsUniqueSection = Layouts.flow(colorsUnique, null, null, width, 100, 0);

        // GCOLOR.T()
        List<ColorBox> gColorsText = ReflectionUtil.<COLOR>getDeclaredFieldValuesMap(COLOR.class, GCOLOR.T())
            .entrySet().stream()
            // sort by "color name"
            .sorted(Map.Entry.comparingByKey(Comparator.comparing(Field::getName)))
            .map(entry -> {
                ColorBox colorBox = new ColorBox(24, 24, entry.getValue());
                colorBox.hoverInfoSet("GCOLOR.T()." + entry.getKey().getName());
                return colorBox;
            }).collect(Collectors.toList());
        GuiSection gColorsTextSection = Layouts.flow(gColorsText, null, null, width, 100, 0);

        // GCOLOR.UI()
        List<ColorBox> gColorsUI = ReflectionUtil.<COLOR>getDeclaredFieldValuesMap(COLOR.class, GCOLOR.UI())
            .entrySet().stream()
            // sort by "color name"
            .sorted(Map.Entry.comparingByKey(Comparator.comparing(Field::getName)))
            .map(entry -> {
                ColorBox colorBox = new ColorBox(24, 24, entry.getValue());
                colorBox.hoverInfoSet("GCOLOR.UI()." + entry.getKey().getName());
                return colorBox;
            }).collect(Collectors.toList());
        GuiSection gColorsUISection = Layouts.flow(gColorsUI, null, null, width, 100, 0);

        // simple button with description
        Button button = new Button("Button", "A button.");

        // vertical or horizontal arranged set of buttons
        ButtonMenu<String> buttonMenu = ButtonMenu.<String>builder()
            .button("button1", new Button("Menu 1"))
            .button("button2", new Button("Menu 2"))
            .button("button3", new Button("Menu 3"))
            .sameWidth(true)
            .build();

        // set of buttons with the ability to toggle
        Switcher<String> switcher = Switcher.<String>builder()
            .menu(ButtonMenu.<String>builder()
                .button("button1", new Button("Toggle 1"))
                .button("button2", new Button("Toggle 2"))
                .button("button3", new Button("Toggle 3"))
                .sameWidth(true)
                .build())
            .aktiveKey("button1")
            .highlight(true)
            .build();

        // button opening a menu with options to choose
        DropDown<String> dropDown = DropDown.<String>builder()
            .menu(Switcher.<String>builder()
                .menu(ButtonMenu.<String>builder()
                    .button("button1", new Button("Option 1"))
                    .button("button2", new Button("Option 2"))
                    .button("button3", new Button("Option 3"))
                    .sameWidth(true)
                    .build())
                .highlight(true)
                .build())
            .label("DropDown")
            .description("A dropdown.")
            .build();

        // Notifications
        Button errorButton = new Button("Error Notification");
        errorButton.hoverInfoSet("Opens an error notification popup");
        errorButton.clickActionSet(() -> {
            notificator.notifyError("Error Notification");
        });
        errorButton.bg(COLOR.RED50);
        Button successButton = new Button("Success Notification");
        successButton.hoverInfoSet("Opens a success notification popup");
        successButton.clickActionSet(() -> {
            notificator.notifySuccess("Success Notification");
        });
        successButton.bg(COLOR.GREEN40);
        Button normalButton = new Button("Normal Notification");
        normalButton.hoverInfoSet("Opens a normal notification popup");
        normalButton.clickActionSet(() -> {
            notificator.notify("Notification");
        });
        normalButton.bg(COLOR.WHITE85);
        ButtonMenu<Object> notificationButtons = ButtonMenu.builder()
            .button("error", errorButton)
            .button("success", successButton)
            .button("normal", normalButton)
            .sameWidth(true)
            .build();

        List<RENDEROBJ> buttonElements = Lists.of(
            button,
            buttonMenu,
            switcher,
            dropDown,
            notificationButtons
        );
        GuiSection buttonElementsSection = Layouts.flow(buttonElements, null, null, width, 200, 20);

        // Slider with negative values
        Slider slider = Slider.builder()
            .controls(true)
            .input(true)
            .min(-1337)
            .max(1337)
            .value(420)
            .width(400)
            .valueDisplay(Slider.ValueDisplay.ABSOLUTE)
            .build();

        // Simple checkbox
        Checkbox checkbox = new Checkbox(true);

        // Label with a description
        Label label = Label.builder()
            .name("Label")
            .description("A label.")
            .build();

        List<RENDEROBJ> otherElements = Lists.of(
            label,
            checkbox,
            slider
        );
        GuiSection otherElementsSection = Layouts.flow(otherElements, null, null, width, 200, 20);

        // Table with searchable rows
        GuiSection tableWithSearch = new GuiSection();
        StringInputSprite searchInput = new StringInputSprite(16, UI.FONT().M).placeHolder("Search");
        Input search = new Input(searchInput);
        Table<Object> table = Table.builder()
            .row(ColumnRow.builder()
                .searchTerm("row 1")
                .column(Label.builder()
                    .name("Row 1")
                    .build())
                .column(new Button("Button 1"))
                .column(new Checkbox(true))
                .build())
            .row(ColumnRow.builder()
                .searchTerm("row 2")
                .column(Label.builder()
                    .name("Row 2")
                    .build())
                .column(new Button("Button 2"))
                .column(new Checkbox(false))
                .build())
            .row(ColumnRow.builder()
                .searchTerm("row 3")
                .column(Label.builder()
                    .name("Row 3")
                    .build())
                .column(new Button("Button 3"))
                .column(new Checkbox(true))
                .build())
            .row(ColumnRow.builder()
                .searchTerm("row 4")
                .column(Label.builder()
                    .name("Row 4")
                    .build())
                .column(new Button("Button 4"))
                .column(new Checkbox(true))
                .build())
            .row(ColumnRow.builder()
                .searchTerm("row 5")
                .column(Label.builder()
                    .name("Row 5")
                    .build())
                .column(new Button("Button 5"))
                .column(new Checkbox(false))
                .build())
            .row(ColumnRow.builder()
                .searchTerm("row 6")
                .column(Label.builder()
                    .name("Row 6")
                    .build())
                .column(new Button("Button 6"))
                .column(new Checkbox(true))
                .build())
            .search(searchInput)
            .highlight(true)
            .rowPadding(5)
            .evenOdd(true)
            .displayHeight(150)
            .headerButtons(Maps.of(
                "column1", new Button("Column 1"),
                "column2", new Button("Column 2"),
                "column3", new Button("Column 3")
            ))
            .build();

        tableWithSearch.addDownC(0, search);
        tableWithSearch.addDownC(5, table);

        // Tabulator switching through ui elements on a button click
        Tabulator<String, RENDEROBJ, Void> tabulator = Tabulator.<String, RENDEROBJ, Void>builder()
            .tabMenu(Switcher.<String>builder()
                .menu(ButtonMenu.<String>builder()
                    .button("tab1", new Button("Tab 1"))
                    .button("tab2", new Button("Tab 2"))
                    .button("tab3", new Button("Tab 3"))
                    .sameWidth(true)
                    .spacer(true)
                    .margin(20)
                    .horizontal(true)
                    .build())
                .aktiveKey("tab1")
                .highlight(true)
                .build())
            .tabs(Maps.of(
                "tab1", new GHeader("Tab Content 1"),
                "tab2", new GHeader("Tab Content 2"),
                "tab3", new GHeader("Tab Content 3")
            ))
            .direction(DIR.S)
            .margin(50)
            .center(true)
            .build();

        GuiSection tableAndTabulator = new GuiSection();
        tableAndTabulator.addRight(0, tableWithSearch);
        tableAndTabulator.addRight(20, tabulator);

        addDown(0, iconsSmallSection);
        addDown(10, iconsMediumSection);
        addDown(10, iconsLargeSection);
        addDown(10, colorsSection);
        addDown(10, colorsUniqueSection);
        addDown(10, gColorsUISection);
        addDown(10, gColorsTextSection);
        addDown(20, buttonElementsSection);
        addDown(20, otherElementsSection);
        addDown(20, tableAndTabulator);
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        notificator.update(ds);
        super.render(r, ds);
    }
}
