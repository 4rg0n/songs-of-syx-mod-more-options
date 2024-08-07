package com.github.argon.sos.moreoptions.ui.tab.events;

import com.github.argon.sos.moreoptions.config.domain.EventsConfig;
import com.github.argon.sos.moreoptions.config.domain.Range;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.UiMapper;
import com.github.argon.sos.moreoptions.ui.tab.AbstractConfigTab;
import com.github.argon.sos.moreoptions.util.Maps;
import init.sprite.UI.UI;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GHeader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains control elements for enabling and disabling game events.
 */
public class EventsTab extends AbstractConfigTab<EventsConfig, EventsTab> {

    private static final Logger log = Loggers.getLogger(EventsTab.class);
    private final static I18n i18n = I18n.get(EventsTab.class);

    private final Map<String, Checkbox> eventsCheckboxes;
    private final Map<String, Slider> eventsChanceSliders;
    private final Map<String, Slider> tributeSliders;

    public EventsTab(
        String title,
        EventsConfig eventsConfig,
        EventsConfig defaultConfig,
        int availableWidth,
        int availableHeight
    ) {
        super(title, defaultConfig, availableWidth, availableHeight);
        this.eventsCheckboxes = UiMapper.toCheckboxes(eventsConfig.getEvents());

        GuiSection settlementSection = new GuiSection();
        GuiSection worldSection = new GuiSection();
        GuiSection eventsChanceSection = new GuiSection();
        GuiSection tributeSection = new GuiSection();

        GHeader settlementHeader = new GHeader(i18n.t("EventsTab.header.settlement.name"));
        settlementHeader.hoverInfoSet(i18n.t("EventsTab.header.settlement.desc"));
        GHeader worldHeader = new GHeader(i18n.t("EventsTab.header.world.name"));
        worldHeader.hoverInfoSet(i18n.t("EventsTab.header.world.desc"));

        // Event chances
        this.eventsChanceSliders = UiMapper.toSliders(eventsConfig.getChance());
        List<ColumnRow<Integer>> evenChanceRows = UiMapper.toLabeledColumnRows(eventsChanceSliders, i18n);
        Table<Integer> chanceTable = Table.<Integer>builder()
            .rows(evenChanceRows)
            .rowPadding(5)
            .columnMargin(5)
            .backgroundColor(COLOR.WHITE10)
            .highlight(true)
            .build();
        GHeader eventChancesHeader = new GHeader(i18n.t("EventsTab.header.chance.name"));
        eventChancesHeader.hoverInfoSet(i18n.t("EventsTab.header.chance.desc"));
        eventsChanceSection.addDown(0, eventChancesHeader);
        eventsChanceSection.addDown(5, chanceTable);
        HorizontalLine horizontalLine = new HorizontalLine(eventsChanceSection.body().width(), 14, 1);

        // Siege tributes
        GHeader tributeHeader = new GHeader(i18n.t("EventsTab.header.battle.loot.name"));
        tributeHeader.hoverInfoSet(i18n.t("EventsTab.header.battle.loot.desc"));
        tributeSliders = UiMapper.toSliders(Maps.of(
            "EventsTab.battle.loot.player", eventsConfig.getPlayerBattleLoot(),
            "EventsTab.battle.loot.enemy", eventsConfig.getEnemyBattleLoot()
        ));
        List<ColumnRow<Integer>> tributeRows = UiMapper.toLabeledColumnRows(tributeSliders, i18n);
        Table<Integer> tributeTable = Table.<Integer>builder()
            .rows(tributeRows)
            .rowPadding(5)
            .columnMargin(5)
            .backgroundColor(COLOR.WHITE10)
            .highlight(true)
            .build();
        tributeSection.addDown(0, tributeHeader);
        tributeSection.addDown(5, tributeTable);

        int tableHeight = availableHeight
            - horizontalLine.body().height()
            - eventsChanceSection.body().height()
            - tributeSection.body().height()
            - UI.FONT().H2.height() // headers height
            - 50;

        Table<Integer> eventsTable = Table.<Integer>builder()
            .rows(UiMapper.toLabeledColumnRows(eventsCheckboxes, i18n))
            .rowPadding(5)
            .columnMargin(20)
            .highlight(true)
            .scrollable(true)
            .backgroundColor(COLOR.WHITE10)
            .displayHeight(tableHeight)
            .build();

        settlementSection.addDown(0, settlementHeader);
        worldSection.addDown(0, worldHeader);
        worldSection.addDown(10, eventsTable);

        addDownC(0, eventsTable);
        addDownC(10, horizontalLine);
        addDownC(10, eventsChanceSection);
        addDownC(10, tributeSection);
    }

    @Override
    public EventsConfig getValue() {
       return EventsConfig.builder()
           .events(getEventsConfig())
           .chance(getEventsChanceConfig())
           .enemyBattleLoot(Range.fromSlider(tributeSliders.get("EventsTab.battle.loot.enemy")))
           .playerBattleLoot(Range.fromSlider(tributeSliders.get("EventsTab.battle.loot.player")))
           .build();
    }

    public Map<String, Boolean> getEventsConfig() {
        return eventsCheckboxes.entrySet().stream()
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

        eventsConfig.getEvents().forEach((key, value) -> {
            if (eventsCheckboxes.containsKey(key)) {
                eventsCheckboxes.get(key).selectedSet(value);
            } else {
                log.warn("No checkbox with key %s found in UI", key);
            }
        });

        eventsConfig.getChance().forEach((key, range) -> {
            if (eventsChanceSliders.containsKey(key)) {
                eventsChanceSliders.get(key).setValue(range.getValue());
            } else {
                log.warn("No event chance slider with key %s found in UI", key);
            }
        });

        tributeSliders.get("EventsTab.battle.loot.enemy").setValue(eventsConfig.getEnemyBattleLoot().getValue());
        tributeSliders.get("EventsTab.battle.loot.player").setValue(eventsConfig.getPlayerBattleLoot().getValue());
    }

    protected EventsTab element() {
        return this;
    }
}
