package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.*;
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

/**
 * Contains control elements for enabling and disabling game events.
 */
public class EventsPanel extends GuiSection implements Valuable<Void> {

    private static final Logger log = Loggers.getLogger(EventsPanel.class);

    @Getter
    private final Map<String, Checkbox> settlementEventsCheckboxes = new HashMap<>();

    private final Map<String, Checkbox> worldEventsCheckboxes = new HashMap<>();
    private final Map<String, Slider> eventsChanceSliders;

    public EventsPanel(MoreOptionsConfig.Events events) {
        BuildResult<GuiSection, Map<String, Checkbox>> settlementCheckboxesResult = checkboxes(events.getSettlement());
        GuiSection settlement = settlementCheckboxesResult.getResult();
        settlementEventsCheckboxes.putAll(settlementCheckboxesResult.getInteractable());

        BuildResult<GuiSection, Map<String, Checkbox>> worldCheckboxesResult = checkboxes(events.getWorld());
        GuiSection world = worldCheckboxesResult.getResult();
        worldEventsCheckboxes.putAll(worldCheckboxesResult.getInteractable());

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

        BuildResult<GuiSection, Map<String, Slider>> buildResult = sliders(events.getChance());
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

    private BuildResult<GuiSection, Map<String, Slider>> sliders(
        Map<String, MoreOptionsConfig.Range> eventsChanceConfig
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


        return SlidersBuilder.builder()
            .displayHeight(150)
            .definitions(sliderDefinitions)
            .build();
    }

    public MoreOptionsConfig.Events getConfig() {
       return MoreOptionsConfig.Events.builder()
           .settlement(getSettlementEventsConfig())
           .world(getWorldEventsConfig())
           .chance(getEventsChanceConfig())
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

    public Map<String, MoreOptionsConfig.Range> getEventsChanceConfig() {
        return eventsChanceSliders.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                tab -> MoreOptionsConfig.Range.builder()
                    .value(tab.getValue().getValue())
                    .max(tab.getValue().getMax())
                    .min(tab.getValue().getMin())
                    .displayMode(MoreOptionsConfig.Range.DisplayMode
                        .fromValueDisplay(tab.getValue().getValueDisplay()))
                    .applyMode(MoreOptionsConfig.Range.ApplyMode
                        .fromValueDisplay(tab.getValue().getValueDisplay()))
                    .build()));
    }

    public void applyConfig(MoreOptionsConfig.Events events) {
        log.trace("Applying UI settlement events config %s", events);

        events.getSettlement().forEach((key, value) -> {
            if (settlementEventsCheckboxes.containsKey(key)) {
                settlementEventsCheckboxes.get(key).selectedSet(value);
            } else {
                log.warn("No checkbox with key %s found in UI", key);
            }
        });

        events.getWorld().forEach((key, value) -> {
            if (worldEventsCheckboxes.containsKey(key)) {
                worldEventsCheckboxes.get(key).selectedSet(value);
            } else {
                log.warn("No checkbox with key %s found in UI", key);
            }
        });

        events.getChance().forEach((key, range) -> {
            if (eventsChanceSliders.containsKey(key)) {
                eventsChanceSliders.get(key).setValue(range.getValue());
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });
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

    @Override
    public Void getValue() {
        return null;
    }

    @Override
    public void setValue(Void value) {

    }
}
