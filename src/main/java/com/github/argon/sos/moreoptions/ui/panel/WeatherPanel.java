package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.Table;
import com.github.argon.sos.moreoptions.game.ui.Valuable;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.section.SlidersBuilder;
import lombok.Getter;
import snake2d.util.gui.GuiSection;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains slider for controlling the intensity of weather effects
 */
public class WeatherPanel extends GuiSection implements Valuable<Map<String, MoreOptionsConfig.Range>> {
    private static final Logger log = Loggers.getLogger(WeatherPanel.class);
    @Getter
    private final Map<String, Slider> sliders;
    public WeatherPanel(Map<String, MoreOptionsConfig.Range> weatherConfig) {
        BuildResult<Table, Map<String, Slider>> slidersBuildResult = SlidersBuilder.builder()
            .displayHeight(400)
            .defaults(weatherConfig)
            .build();

        GuiSection sliderSection = slidersBuildResult.getResult();
        sliders = slidersBuildResult.getInteractable();

        GuiSection section = new GuiSection();
        section.addDown(0, sliderSection);
        addDownC(0, section);
    }

    @Override
    public Map<String, MoreOptionsConfig.Range> getValue() {
        return sliders.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                tab -> MoreOptionsConfig.Range.fromSlider(tab.getValue())));
    }

    @Override
    public void setValue(Map<String, MoreOptionsConfig.Range> config) {
        log.trace("Applying UI weather config %s", config);

        config.forEach((key, range) -> {
            if (sliders.containsKey(key)) {
                sliders.get(key).setValue(range.getValue());
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });
    }
}
