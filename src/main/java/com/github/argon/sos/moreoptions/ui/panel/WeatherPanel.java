package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.ui.ColumnRow;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.Table;
import com.github.argon.sos.moreoptions.game.ui.Valuable;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.UiMapper;
import lombok.Getter;
import snake2d.util.gui.GuiSection;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains slider for controlling the intensity of weather effects
 */
public class WeatherPanel extends GuiSection implements Valuable<Map<String, MoreOptionsV2Config.Range>, WeatherPanel> {
    private static final Logger log = Loggers.getLogger(WeatherPanel.class);
    private static final I18n i18n = I18n.get(WeatherPanel.class);

    @Getter
    private final Map<String, Slider> sliders;
    public WeatherPanel(Map<String, MoreOptionsV2Config.Range> weatherConfig) {
        this.sliders = UiMapper.toSliders(weatherConfig);
        List<ColumnRow<Integer>> rows = UiMapper.toLabeledColumnRows(sliders, i18n);
        Table<Integer> weatherTable = Table.<Integer>builder()
            .rows(rows)
            .displayHeight(400)
            .rowPadding(5)
            .build();

        addDownC(0, weatherTable);
    }

    @Override
    public Map<String, MoreOptionsV2Config.Range> getValue() {
        return sliders.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                tab -> MoreOptionsV2Config.Range.fromSlider(tab.getValue())));
    }

    @Override
    public void setValue(Map<String, MoreOptionsV2Config.Range> config) {
        config.forEach((key, range) -> {
            if (sliders.containsKey(key)) {
                sliders.get(key).setValue(range.getValue());
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });
    }
}
