package com.github.argon.sos.moreoptions.ui.tab.weather;

import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.mod.sdk.game.util.UiMapper;
import com.github.argon.sos.mod.sdk.ui.ColumnRow;
import com.github.argon.sos.mod.sdk.ui.Slider;
import com.github.argon.sos.mod.sdk.ui.Table;
import com.github.argon.sos.mod.sdk.ui.layout.Layout;
import com.github.argon.sos.mod.sdk.ui.layout.VerticalLayout;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.domain.WeatherConfig;
import com.github.argon.sos.moreoptions.ui.MoreOptionsModel;
import com.github.argon.sos.moreoptions.ui.tab.AbstractConfigTab;
import lombok.Getter;
import snake2d.util.color.COLOR;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains slider for controlling the intensity of weather effects
 */
public class WeatherTab extends AbstractConfigTab<WeatherConfig, WeatherTab> {
    private static final Logger log = Loggers.getLogger(WeatherTab.class);
    private static final I18nTranslator i18n = ModModule.i18n().get(WeatherTab.class);

    @Getter
    private final Map<String, Slider> sliders;
    public WeatherTab(
        String title,
        MoreOptionsModel.Weather model,
        int availableWidth,
        int availableHeight
    ) {
        super(title, model.getDefaultConfig(), availableWidth, availableHeight);
        WeatherConfig weatherConfig = model.getConfig();
        this.sliders = UiMapper.toSliders(weatherConfig.getEffects());
        List<ColumnRow<Integer>> rows = UiMapper.toLabeledColumnRows(sliders, i18n);
        Layout.vertical(availableHeight)
            .addDownC(50, new VerticalLayout.Scalable(300, height -> Table.<Integer>builder()
                .rows(rows)
                .evenOdd(true)
                .highlight(true)
                .displayHeight(height)
                .backgroundColor(COLOR.WHITE10)
                .rowPadding(5)
                .columnMargin(5)
                .build()))
            .build(this);
    }

    @Override
    public WeatherConfig getValue() {
        Map<String, Range> effects = sliders.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                tab -> Range.fromSlider(tab.getValue())));

        return WeatherConfig.builder()
            .effects(effects)
            .build();
    }

    @Override
    public void setValue(WeatherConfig config) {
        config.getEffects().forEach((key, range) -> {
            if (sliders.containsKey(key)) {
                sliders.get(key).setValue(range.getValue());
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });
    }

    protected WeatherTab element() {
        return this;
    }
}
