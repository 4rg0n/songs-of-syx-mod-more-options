package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.game.ui.Checkbox;
import com.github.argon.sos.moreoptions.game.ui.HorizontalLine;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.VerticalLine;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.element.*;
import com.github.argon.sos.moreoptions.ui.builder.section.CheckboxesBuilder;
import com.github.argon.sos.moreoptions.ui.builder.section.SlidersBuilder;
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
        BuildResult<GuiSection, Checkbox> settlementCheckboxesResult = checkboxes(settlementEventsConfig);
        GuiSection settlement = settlementCheckboxesResult.getResult();
        settlementEventsCheckboxes.putAll(settlementCheckboxesResult.getElements());

        BuildResult<GuiSection, Checkbox> worldCheckboxesResult = checkboxes(worldEventsConfig);
        GuiSection world = worldCheckboxesResult.getResult();
        worldEventsCheckboxes.putAll(worldCheckboxesResult.getElements());

        // todo event reset buttons?

        GuiSection settlementSection = new GuiSection();
        GHeader settlementHeader = new GHeader("Settlement");
        settlementHeader.hoverInfoSet("Events occurring in your settlement.");
        settlementSection.addDown(0, settlementHeader);
        settlementSection.addDown(10, settlement);

        GuiSection worldSection = new GuiSection();
        GHeader worldHeader = new GHeader("World");
        settlementHeader.hoverInfoSet("Events occurring in the world.");
        worldSection.addDown(0, worldHeader);
        worldSection.addDown(10, world);

        GuiSection checkBoxSection = new GuiSection();
        checkBoxSection.addRight(0, settlementSection);
        checkBoxSection.addRight(0, new VerticalLine(101, settlementSection.body().height(), 1));
        checkBoxSection.addRight(0, worldSection);
        addDownC(0, checkBoxSection);

        BuildResult<GuiSection, Slider> buildResult = sliders(eventsChanceConfig);
        GuiSection sliders = buildResult.getResult();
        eventsChanceSliders = buildResult.getElements();

        GuiSection eventsChanceSection = new GuiSection();
        GHeader eventChancesHeader = new GHeader("Event Chances");
        eventChancesHeader.hoverInfoSet("How often an event occurs. Will be multiplied.");
        eventsChanceSection.addDown(0, eventChancesHeader);
        eventsChanceSection.addDown(5, sliders);

        HorizontalLine horizontalLine = new HorizontalLine(eventsChanceSection.body().width(), 14, 1);
        addDownC(10, horizontalLine);
        addDownC(10, eventsChanceSection);
    }

    private BuildResult<GuiSection, Slider> sliders(Map<String, Integer> eventsChanceConfig) {
        Map<String, LabeledSliderBuilder.Definition> sliderDefinitions = eventsChanceConfig.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            config -> LabeledSliderBuilder.Definition.builder()
                .labelDefinition(LabelBuilder.Definition.builder()
                    .key(config.getKey())
                    .title(config.getKey())
                    .build())
                .sliderDefinition(SliderBuilder.Definition.builder()
                    .maxWidth(300)
                    .max(10000)
                    .build())
                .build()));

        return SlidersBuilder.builder()
            .displayHeight(150)
            .definitions(sliderDefinitions)
            .build();
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


    private BuildResult<GuiSection, Checkbox> checkboxes(Map<String, Boolean> eventConfig) {
        Map<String, LabeledCheckboxBuilder.Definition> settlementCheckboxes = eventConfig.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            config -> LabeledCheckboxBuilder.Definition.builder()
                .labelDefinition(LabelBuilder.Definition.builder()
                    .key(config.getKey())
                    .title(config.getKey())
                    .build()
                )
                .checkboxDefinition(CheckboxBuilder.Definition.builder().build())
                .build()));


        return CheckboxesBuilder.builder()
            .displayHeight(300)
            .translate(settlementCheckboxes).build();
    }
}
