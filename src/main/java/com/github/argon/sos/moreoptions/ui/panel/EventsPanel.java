package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.game.ui.Checkbox;
import com.github.argon.sos.moreoptions.game.ui.HorizontalLine;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.VerticalLine;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.builder.CheckboxBuilder;
import com.github.argon.sos.moreoptions.ui.builder.SliderBuilder;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GHeader;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class EventsPanel extends GuiSection {

    private static final Logger log = Loggers.getLogger(EventsPanel.class);

    @Getter
    private final Map<String, Checkbox> settlementEventsCheckboxes = new HashMap<>();
    private final Map<String, Checkbox> worldEventsCheckboxes = new HashMap<>();
    private final Map<String, Slider> eventsChanceSliders;

    public EventsPanel(
        Map<String, Boolean> settlementEventsConfig,
        Map<String, Boolean> worldEventsConfig,
        Map<String, Integer> eventsChanceConfig
    ) {
        GuiSection settlement = settlementCheckboxes(settlementEventsConfig);
        GuiSection world = worldCheckboxes(worldEventsConfig);

        // todo event reset buttons

        GuiSection settlementSection = new GuiSection();
        settlementSection.addDown(0, new GHeader("Settlement"));
        settlementSection.addDown(10, settlement);

        GuiSection worldSection = new GuiSection();
        worldSection.addDown(0, new GHeader("World"));
        worldSection.addDown(10, world);

        GuiSection checkBoxSection = new GuiSection();
        checkBoxSection.addRight(0, settlementSection);
        checkBoxSection.addRight(0, new VerticalLine(101, settlementSection.body().height(), 1));
        checkBoxSection.addRight(0, worldSection);
        addDownC(0, checkBoxSection);

        SliderBuilder sliderBuilder = new SliderBuilder();
        Map<String, SliderBuilder.Definition> eventsChanceSlidersConfig = eventsChanceConfig.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            config -> SliderBuilder.Definition.builder()
                .title(config.getKey())
                .maxWidth(200)
                .max(1000)
                .build()));

        GuiSection sliders = sliderBuilder.build(eventsChanceSlidersConfig, 150);
        eventsChanceSliders = sliderBuilder.getSliders();

        GuiSection eventsChanceSection = new GuiSection();
        eventsChanceSection.addDown(0, new GHeader("Event Chances"));
        eventsChanceSection.addDown(5, sliders);

        HorizontalLine horizontalLine = new HorizontalLine(eventsChanceSection.body().width(), 14, 1);
        addDownC(10, horizontalLine);
        addDownC(10, eventsChanceSection);

        applyConfig(settlementEventsConfig, worldEventsConfig, eventsChanceConfig);
    }

    public Map<String, Boolean> getSettlementEventsConfig() {
        return settlementEventsCheckboxes.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, slider -> slider.getValue().selectedIs()));
    }

    public Map<String, Boolean> getWorldEventsConfig() {
        return worldEventsCheckboxes.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, slider -> slider.getValue().selectedIs()));
    }

    public Map<String, Integer> getEventsChanceConfig() {
        return eventsChanceSliders.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, slider -> slider.getValue().getValue()));
    }

    public void applyConfig(
        Map<String, Boolean> settlementEventsConfig,
        Map<String, Boolean> worldEventsConfig,
        Map<String, Integer> eventsChanceConfig
    ) {
        log.trace("Applying UI settlement events config %s", settlementEventsConfig);
        log.trace("Applying UI world events config %s", worldEventsConfig);
        log.trace("Applying UI events chance config %s", eventsChanceConfig);

        settlementEventsConfig.forEach((key, value) -> {
            if (settlementEventsCheckboxes.containsKey(key)) {
                settlementEventsCheckboxes.get(key).selectedSet(value);
            } else {
                log.warn("No checkbox with key %s found in UI", key);
            }
        });

        worldEventsConfig.forEach((key, value) -> {
            if (worldEventsCheckboxes.containsKey(key)) {
                worldEventsCheckboxes.get(key).selectedSet(value);
            } else {
                log.warn("No checkbox with key %s found in UI", key);
            }
        });

        eventsChanceConfig.forEach((key, value) -> {
            if (eventsChanceSliders.containsKey(key)) {
                eventsChanceSliders.get(key).setValue(value);
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });
    }


    private GuiSection settlementCheckboxes(Map<String, Boolean> eventConfig) {
        Map<String, CheckboxBuilder.Definition> settlementCheckboxes = eventConfig.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            config -> CheckboxBuilder.Definition.builder()
                .title(config.getKey())
                .build()));

        CheckboxBuilder checkboxBuilder = new CheckboxBuilder();
        GuiSection settlement = checkboxBuilder.build(settlementCheckboxes);
        settlementEventsCheckboxes.putAll(checkboxBuilder.getCheckboxes());
        return settlement;
    }

    private GuiSection worldCheckboxes(Map<String, Boolean> eventConfig) {
        Map<String, CheckboxBuilder.Definition> worldCheckboxes = eventConfig.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            config -> CheckboxBuilder.Definition.builder()
                .title(config.getKey())
                .build()));

        CheckboxBuilder checkboxBuilder = new CheckboxBuilder();
        GuiSection world = checkboxBuilder.build(worldCheckboxes);
        worldEventsCheckboxes.putAll(checkboxBuilder.getCheckboxes());

        return world;
    }
}
