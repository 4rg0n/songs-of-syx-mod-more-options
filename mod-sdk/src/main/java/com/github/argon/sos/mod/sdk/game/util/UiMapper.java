package com.github.argon.sos.mod.sdk.game.util;

import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.ui.*;
import lombok.experimental.UtilityClass;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.SPRITE;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for mapping data types into ui elements
 */
@UtilityClass
public class UiMapper {
    /**
     * Will create a {@link Slider} from a given {@link Range}.
     *
     * @param range used for the slider
     * @return created slider
     */
    public static Slider toSlider(Range range) {
        return Slider.SliderBuilder
            .fromRange(range)
            .lockScroll(true)
            .input(true)
            .controls(true)
            .width(300)
            .build();
    }

    /**
     * Will create multiple {@link Slider}s from a map with {@link Range}s.
     *
     * @param ranges to create the sliders from
     * @return created sliders
     */
    public static Map<String, Slider> toSliders(Map<String, Range> ranges) {
        return ranges.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            config -> toSlider(config.getValue())));
    }

    /**
     * Will create a list of labeled {@link ColumnRow}s from a given map with i18n keys and ui elements.
     *
     * @param elements map with i18n keys and ui elements
     * @param i18n used for translating the labels
     * @return created list of column rows
     * @param <Value> type of the value in the column row
     * @param <Element> type of the ui element
     */
    public static <Value, Element extends RENDEROBJ> List<ColumnRow<Value>> toLabeledColumnRows(Map<String, Element> elements, I18nTranslator i18n) {
        return elements.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> {
                String key = entry.getKey();
                Element element = entry.getValue();

                // label with element
                return ColumnRow.<Value>builder()
                    .column(Label.builder()
                        .name(i18n.n(key)) // translate label i18n key
                        .description(i18n.dn(key)) // translate label i18n description
                        .build())
                    .column(element)
                    .build();
            })
            .toList();
    }

    /**
     * Will create a map of keys or names with the corresponding {@link Checkbox}es from a map of names or keys with booleans.
     *
     * @param booleans to create the checkboxes from
     * @return map of keys or names with their checkboxes
     */
    public static Map<String, Checkbox> toCheckboxes(Map<String, Boolean> booleans) {
        return booleans.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> toCheckbox(entry.getValue())));
    }

    /**
     * Will create a {@link Checkbox} from a boolean.
     *
     * @param checked whether the checkbox is checked or not
     * @return created checkbox
     */
    public static Checkbox toCheckbox(boolean checked) {
        return new Checkbox(checked);
    }

    /**
     * Will map {@link Range.DisplayMode} to a {@link Slider.ValueDisplay}
     *
     * @param displayMode to map
     * @return mapped slider value display
     */
    public static Slider.ValueDisplay toValueDisplay(Range.DisplayMode displayMode) {
        return switch (displayMode) {
            case PERCENTAGE -> Slider.ValueDisplay.PERCENTAGE;
            case ABSOLUTE -> Slider.ValueDisplay.ABSOLUTE;
            default -> Slider.ValueDisplay.NONE;
        };
    }

    /**
     * Will map {@link Slider.ValueDisplay} to a {@link Range.ApplyMode}
     *
     * @param valueDisplay to map
     * @return mapped range apply mode
     */
    public static Range.ApplyMode toApplyMode(Slider.ValueDisplay valueDisplay) {
        return switch (valueDisplay) {
            case ABSOLUTE -> Range.ApplyMode.ADD;
            case PERCENTAGE -> Range.ApplyMode.PERCENT;
            default -> Range.ApplyMode.MULTI;
        };
    }

    /**
     * Will map {@link Slider.ValueDisplay} to a {@link Range.DisplayMode}
     *
     * @param valueDisplay to map
     * @return mapped range display mode
     */
    public static Range.DisplayMode toDisplayMode(Slider.ValueDisplay valueDisplay) {
        return switch (valueDisplay) {
            case PERCENTAGE -> Range.DisplayMode.PERCENTAGE;
            case ABSOLUTE -> Range.DisplayMode.ABSOLUTE;
            default -> Range.DisplayMode.NONE;
        };
    }

    /**
     * Maps a {@link SPRITE} to a {@link Section}
     *
     * @param sprite to map
     * @return section
     */
    public static Section toSection(SPRITE sprite) {
        RENDEROBJ renderobj = toRender(sprite);
        return toSection(renderobj);
    }

    /**
     * Maps a {@link SPRITE} to a {@link GuiSection}
     *
     * @param sprite to map
     * @return gui section
     */
    public static GuiSection toGuiSection(SPRITE sprite) {
        RENDEROBJ renderobj = toRender(sprite);
        return toGuiSection(renderobj);
    }

    /**
     * Maps a {@link SPRITE} to a {@link RENDEROBJ}
     *
     * @param sprite to map
     * @return render object
     */
    public static RENDEROBJ toRender(SPRITE sprite) {
        return new RENDEROBJ.Sprite(sprite);
    }

    /**
     * Maps a {@link RENDEROBJ} to a {@link Section}
     *
     * @param renderobj to map
     * @return section
     */
    public static Section toSection(RENDEROBJ renderobj) {
        if (renderobj instanceof Section) {
            return (Section) renderobj;
        } else {
            Section section = new Section();
            section.add(renderobj);

            return section;
        }
    }

    /**
     * Maps a {@link RENDEROBJ} to a {@link GuiSection}
     *
     * @param renderobj to map
     * @return gui section
     */
    public static GuiSection toGuiSection(RENDEROBJ renderobj) {
        if (renderobj instanceof GuiSection) {
            return (GuiSection) renderobj;
        } else {
            GuiSection section = new GuiSection();
            section.add(renderobj);

            return section;
        }
    }
}
