package com.github.argon.sos.moreoptions.ui.panel.events;

import com.github.argon.sos.moreoptions.config.domain.EventsConfig;
import com.github.argon.sos.moreoptions.config.domain.Range;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.UiMapper;
import com.github.argon.sos.moreoptions.ui.panel.AbstractConfigPanel;
import init.sprite.UI.UI;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GHeader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains control elements for enabling and disabling game events.
 */
public class EventsPanel extends AbstractConfigPanel<EventsConfig, EventsPanel> {

    private static final Logger log = Loggers.getLogger(EventsPanel.class);
    private final static I18n i18n = I18n.get(EventsPanel.class);

    @Getter
    private final Map<String, Checkbox> settlementEventsCheckboxes;

    private final Map<String, Checkbox> worldEventsCheckboxes;
    private final Map<String, Slider> eventsChanceSliders;

    public EventsPanel(
        String title,
        EventsConfig eventsConfig,
        EventsConfig defaultConfig,
        int availableWidth,
        int availableHeight
    ) {
        super(title, defaultConfig, availableWidth, availableHeight);
        this.settlementEventsCheckboxes = UiMapper.toCheckboxes(eventsConfig.getSettlement());
        this.worldEventsCheckboxes = UiMapper.toCheckboxes(eventsConfig.getWorld());

        GuiSection settlementSection = new GuiSection();
        GuiSection worldSection = new GuiSection();
        GuiSection checkBoxSection = new GuiSection();
        GuiSection eventsChanceSection = new GuiSection();

        GHeader settlementHeader = new GHeader(i18n.t("EventsPanel.header.settlement.name"));
        settlementHeader.hoverInfoSet(i18n.t("EventsPanel.header.settlement.desc"));
        GHeader worldHeader = new GHeader(i18n.t("EventsPanel.header.world.name"));
        settlementHeader.hoverInfoSet(i18n.t("EventsPanel.header.world.desc"));

        this.eventsChanceSliders = UiMapper.toSliders(eventsConfig.getChance());
        List<ColumnRow<Integer>> rows = UiMapper.toLabeledColumnRows(eventsChanceSliders, i18n);
        Table<Integer> chanceTable = Table.<Integer>builder()
            .rows(rows)
            .rowPadding(5)
            .build();

        GHeader eventChancesHeader = new GHeader(i18n.t("EventsPanel.header.chance.name"));
        eventChancesHeader.hoverInfoSet(i18n.t("EventsPanel.header.chance.desc"));

        eventsChanceSection.addDown(0, eventChancesHeader);
        eventsChanceSection.addDown(5, chanceTable);

        HorizontalLine horizontalLine = new HorizontalLine(eventsChanceSection.body().width(), 14, 1);
        int tableHeight = availableHeight
            - horizontalLine.body().height()
            - eventsChanceSection.body().height()
            - UI.FONT().H2.height() // headers height
            - 40;

        Table<Integer> settlementTable = Table.<Integer>builder()
            .rows(UiMapper.toLabeledColumnRows(settlementEventsCheckboxes, i18n))
            .rowPadding(5)
            .displayHeight(tableHeight)
            .build();

        Table<Integer> worldTable = Table.<Integer>builder()
            .rows(UiMapper.toLabeledColumnRows(worldEventsCheckboxes, i18n))
            .rowPadding(5)
            .displayHeight(tableHeight)
            .build();

        settlementSection.addDown(0, settlementHeader);
        settlementSection.addDown(10, settlementTable);
        worldSection.addDown(0, worldHeader);
        worldSection.addDown(10, worldTable);
        checkBoxSection.addRight(0, settlementSection);
        checkBoxSection.addRight(0, new VerticalLine(101, settlementSection.body().height(), 1));
        checkBoxSection.addRight(0, worldSection);

        addDownC(0, checkBoxSection);
        addDownC(10, horizontalLine);
        addDownC(10, eventsChanceSection);
    }

    @Override
    public EventsConfig getValue() {
       return EventsConfig.builder()
           .settlement(getSettlementEventsConfig())
           .world(getWorldEventsConfig())
           .chance(getEventsChanceConfig())
           .build();
    }

    public Map<String, Boolean> getSettlementEventsConfig() {
        return settlementEventsCheckboxes.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getValue()));
    }

    public Map<String, Boolean> getWorldEventsConfig() {
        return worldEventsCheckboxes.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getValue()));
    }

    public Map<String, Range> getEventsChanceConfig() {
        return eventsChanceSliders.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                tab -> Range.fromSlider(tab.getValue())));
    }

    @Override
    public void setValue(EventsConfig eventsConfig) {
        log.trace("Applying UI settlement events config %s", eventsConfig);

        eventsConfig.getSettlement().forEach((key, value) -> {
            if (settlementEventsCheckboxes.containsKey(key)) {
                settlementEventsCheckboxes.get(key).selectedSet(value);
            } else {
                log.warn("No checkbox with key %s found in UI", key);
            }
        });

        eventsConfig.getWorld().forEach((key, value) -> {
            if (worldEventsCheckboxes.containsKey(key)) {
                worldEventsCheckboxes.get(key).selectedSet(value);
            } else {
                log.warn("No checkbox with key %s found in UI", key);
            }
        });

        eventsConfig.getChance().forEach((key, range) -> {
            if (eventsChanceSliders.containsKey(key)) {
                eventsChanceSliders.get(key).setValue(range.getValue());
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });
    }

    protected EventsPanel element() {
        return this;
    }
}
