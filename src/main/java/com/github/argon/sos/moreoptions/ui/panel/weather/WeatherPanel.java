package com.github.argon.sos.moreoptions.ui.panel.weather;

import com.github.argon.sos.moreoptions.config.domain.Range;
import com.github.argon.sos.moreoptions.game.ui.ColumnRow;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.Table;
import com.github.argon.sos.moreoptions.game.ui.layout.Layout;
import com.github.argon.sos.moreoptions.game.ui.layout.VerticalLayout;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.UiMapper;
import com.github.argon.sos.moreoptions.ui.panel.AbstractConfigPanel;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains slider for controlling the intensity of weather effects
 */
public class WeatherPanel extends AbstractConfigPanel<Map<String, Range>, WeatherPanel> {
    private static final Logger log = Loggers.getLogger(WeatherPanel.class);
    private static final I18n i18n = I18n.get(WeatherPanel.class);

    @Getter
    private final Map<String, Slider> sliders;
    public WeatherPanel(
        String title,
        Map<String, Range> weatherConfig,
        Map<String, Range> defaultConfig,
        int availableWidth,
        int availableHeight
    ) {
        super(title, defaultConfig, availableWidth, availableHeight);
        this.sliders = UiMapper.toSliders(weatherConfig);
        List<ColumnRow<Integer>> rows = UiMapper.toLabeledColumnRows(sliders, i18n);
        Layout.vertical(availableHeight)
            .addDownC(10, new VerticalLayout.Scalable(300, height -> Table.<Integer>builder()
                .rows(rows)
                .displayHeight(height)
                .rowPadding(5)
                .build()))
            .build(this);
    }

    @Override
    public Map<String, Range> getValue() {
        return sliders.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                tab -> Range.fromSlider(tab.getValue())));
    }

    @Override
    public void setValue(Map<String, Range> config) {
        config.forEach((key, range) -> {
            if (sliders.containsKey(key)) {
                sliders.get(key).setValue(range.getValue());
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });
    }

    protected WeatherPanel element() {
        return this;
    }
}
