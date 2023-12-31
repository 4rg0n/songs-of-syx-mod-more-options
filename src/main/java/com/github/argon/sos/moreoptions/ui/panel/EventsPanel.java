package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.api.GameEventsApi;
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
import java.util.Optional;
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
        Map<String, MoreOptionsConfig.Range> eventsChanceConfig,
        MoreOptionsConfig.Range factionOpinionAdd
    ) {
        BuildResult<GuiSection, Map<String, Checkbox>> settlementCheckboxesResult = checkboxes(settlementEventsConfig);
        GuiSection settlement = settlementCheckboxesResult.getResult();
        settlementEventsCheckboxes.putAll(settlementCheckboxesResult.getInteractable());

        BuildResult<GuiSection, Map<String, Checkbox>> worldCheckboxesResult = checkboxes(worldEventsConfig);
        GuiSection world = worldCheckboxesResult.getResult();
        worldEventsCheckboxes.putAll(worldCheckboxesResult.getInteractable());

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

        BuildResult<GuiSection, Map<String, Slider>> buildResult = sliders(eventsChanceConfig, factionOpinionAdd);
        GuiSection sliders = buildResult.getResult();
        eventsChanceSliders = buildResult.getInteractable();

        GuiSection eventsChanceSection = new GuiSection();
        GHeader eventChancesHeader = new GHeader("Event Chances");
        eventChancesHeader.hoverInfoSet("How often an event occurs. Will be multiplied.");
        eventsChanceSection.addDown(0, eventChancesHeader);
        eventsChanceSection.addDown(5, sliders);

        HorizontalLine horizontalLine = new HorizontalLine(eventsChanceSection.body().width(), 14, 1);
        addDownC(10, horizontalLine);
        addDownC(10, eventsChanceSection);
    }

    public Optional<Integer> getFactionWarAdd() {
        return Optional.ofNullable(eventsChanceSliders.get(GameEventsApi.FACTION_OPINION_ADD))
            .map(Slider::getValue);
    }

    public void setFactionWarAdd(Integer value) {
        Optional.ofNullable(eventsChanceSliders.get(GameEventsApi.FACTION_OPINION_ADD))
            .ifPresent(slider -> {slider.setValue(value);});
    }

    private BuildResult<GuiSection, Map<String, Slider>> sliders(
        Map<String, MoreOptionsConfig.Range> eventsChanceConfig,
        MoreOptionsConfig.Range factionOpinionAdd
    ) {
        Map<String, LabeledSliderBuilder.Definition> sliderDefinitions = eventsChanceConfig.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            config -> LabeledSliderBuilder.Definition.builder()
                .labelDefinition(LabelBuilder.Definition.builder()
                    .key(config.getKey())
                    .title(config.getKey())
                    .build())
                .sliderDefinition(SliderBuilder.Definition.builder()
                    .maxWidth(300)
                    .min(config.getValue().getMin())
                    .max(config.getValue().getMax())
                    .valueDisplay(Slider.ValueDisplay.valueOf(config.getValue().getDisplayMode().name()))
                    .build())
                .build()));

        sliderDefinitions.put(GameEventsApi.FACTION_OPINION_ADD, LabeledSliderBuilder.Definition.builder()
            .labelDefinition(LabelBuilder.Definition.builder()
                .key(GameEventsApi.FACTION_OPINION_ADD)
                .title(GameEventsApi.FACTION_OPINION_ADD)
                .build())
            .sliderDefinition(SliderBuilder.Definition.builder()
                .maxWidth(300)
                .min(factionOpinionAdd.getMin())
                .max(factionOpinionAdd.getMax())
                .valueDisplay(Slider.ValueDisplay.valueOf(factionOpinionAdd.getDisplayMode().name()))
                .build())
            .build());

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
        Map<String, Integer> eventsChanceConfig,
        Integer factionOpinionAdd
    ) {
        log.trace("Applying UI settlement events config %s", settlementEventsConfig);
        log.trace("Applying UI world events config %s", worldEventsConfig);
        log.trace("Applying UI events chance config %s", eventsChanceConfig);
        log.trace("Applying UI events chance factionOpinionAdd %s", factionOpinionAdd);

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

        setFactionWarAdd(factionOpinionAdd);
    }


    private BuildResult<GuiSection, Map<String, Checkbox>> checkboxes(Map<String, Boolean> eventConfig) {
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
