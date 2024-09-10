package com.github.argon.sos.mod.sdk.game;

import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.mod.sdk.game.ui.Checkbox;
import com.github.argon.sos.mod.sdk.game.ui.ColumnRow;
import com.github.argon.sos.mod.sdk.game.ui.Label;
import com.github.argon.sos.mod.sdk.game.ui.Slider;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import init.sprite.UI.UI;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UiMapper {
    public static Slider toSlider(Range range) {
        return Slider.SliderBuilder
            .fromRange(range)
            .lockScroll(true)
            .input(true)
            .controls(true)
            .width(300)
            .build();
    }

    public static Map<String, Slider> toSliders(Map<String, Range> slidersConfig) {
        return slidersConfig.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            config -> toSlider(config.getValue())));
    }

    public static <Value, Element extends RENDEROBJ> List<ColumnRow<Value>> toLabeledColumnRows(Map<String, Element> elements, I18nTranslator i18n) {
        return elements.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> {
                String key = entry.getKey();
                Element element = entry.getValue();

                // label with element
                return ColumnRow.<Value>builder()
                    .column(Label.builder()
                        .name(i18n.n(key))
                        .description(i18n.dn(key))
                        .build())
                    .column(element)
                    .build();
            })
            .collect(Collectors.toList());
    }

    public static <Value, Element extends RENDEROBJ> List<ColumnRow<Value>> toLabeledColumnRows(Map<String, Element> elements) {
        return elements.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> {
                String key = entry.getKey();
                Element element = entry.getValue();

                // label with element
                return ColumnRow.<Value>builder()
                    .column(Label.builder()
                        .name(key)
                        .font(UI.FONT().S)
                        .build())
                    .column(element)
                    .build();
            })
            .collect(Collectors.toList());
    }

    public static Map<String, Checkbox> toCheckboxes(Map<String, Boolean> eventConfig) {
        return eventConfig.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> new Checkbox(entry.getValue())));
    }

    public static Slider.ValueDisplay toValueDisplay(Range.DisplayMode displayMode) {
        switch (displayMode) {
            case PERCENTAGE:
                return Slider.ValueDisplay.PERCENTAGE;
            case ABSOLUTE:
                return Slider.ValueDisplay.ABSOLUTE;
            default:
            case NONE:
                return Slider.ValueDisplay.NONE;
        }
    }

    public static Range.ApplyMode toApplyMode(Slider.ValueDisplay valueDisplay) {
        switch (valueDisplay) {
            case ABSOLUTE:
                return Range.ApplyMode.ADD;
            case PERCENTAGE:
                return Range.ApplyMode.PERCENT;
            case NONE:
            default:
                return Range.ApplyMode.MULTI;
        }
    }

    public static Range.DisplayMode toDisplayMode(Slider.ValueDisplay valueDisplay) {
        switch (valueDisplay) {
            case PERCENTAGE:
                return Range.DisplayMode.PERCENTAGE;
            case ABSOLUTE:
                return Range.DisplayMode.ABSOLUTE;
            default:
            case NONE:
                return Range.DisplayMode.NONE;
        }
    }
}
