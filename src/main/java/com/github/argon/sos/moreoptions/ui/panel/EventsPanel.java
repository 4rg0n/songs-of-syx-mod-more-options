package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.game.ui.Checkbox;
import com.github.argon.sos.moreoptions.game.ui.VerticalLine;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.CheckboxBuilder;
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

    public EventsPanel(
        Map<String, Boolean> settlementEventsConfig,
        Map<String, Boolean> worldEventsConfig
    ) {
        GuiSection settlement = settlementCheckboxes(settlementEventsConfig);
        GuiSection world = worldCheckboxes(worldEventsConfig);

        GuiSection settlementSection = new GuiSection();
        settlementSection.addDown(0, new GHeader("Settlement"));
        settlementSection.addDown(10, settlement);

        GuiSection worldSection = new GuiSection();
        worldSection.addDown(0, new GHeader("World"));
        worldSection.addDown(10, world);

        addRight(0, settlementSection);
        addRight(0, new VerticalLine(101, settlementSection.body().height(), 1));
        addRight(0, worldSection);

        applyConfig(settlementEventsConfig, worldEventsConfig);
    }

    public Map<String, Boolean> getSettlementEventsConfig() {
        return settlementEventsCheckboxes.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, slider -> slider.getValue().selectedIs()));
    }

    public Map<String, Boolean> getWorldEventsConfig() {
        return worldEventsCheckboxes.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, slider -> slider.getValue().selectedIs()));
    }

    public void applyConfig(Map<String, Boolean> settlementEventsConfig, Map<String, Boolean> worldEventsConfig) {
        log.trace("Applying settlement events config %s", settlementEventsConfig);
        log.trace("Applying world events config %s", worldEventsConfig);

        settlementEventsConfig.forEach((key, value) -> {
            settlementEventsCheckboxes.get(key).selectedSet(value);
        });

        worldEventsConfig.forEach((key, value) -> {
            worldEventsCheckboxes.get(key).selectedSet(value);
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
