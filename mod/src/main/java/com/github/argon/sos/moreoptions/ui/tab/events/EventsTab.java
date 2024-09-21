package com.github.argon.sos.moreoptions.ui.tab.events;

import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.mod.sdk.game.util.UiMapper;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.ui.*;
import com.github.argon.sos.mod.sdk.util.Maps;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.domain.EventsConfig;
import com.github.argon.sos.moreoptions.ui.MoreOptionsModel;
import com.github.argon.sos.moreoptions.ui.tab.AbstractConfigTab;
import game.events.general.EventAbs;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.gui.misc.GHeader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains control elements for enabling and disabling game events.
 */
public class EventsTab extends AbstractConfigTab<EventsConfig, EventsTab> {

    private static final Logger log = Loggers.getLogger(EventsTab.class);
    private final static I18nTranslator i18n = ModModule.i18n().get(EventsTab.class);

    private final Map<String, Checkbox> eventsCheckboxes;
    private final Map<String, Slider> tributeSliders;
    private final Table<Boolean> generalEventsTable;

    public EventsTab(
        String title,
        MoreOptionsModel.Events model,
        int availableWidth,
        int availableHeight
    ) {
        super(title, model.getDefaultConfig(), availableWidth, availableHeight);

        GuiSection eventsSection = new GuiSection();
        GuiSection tributeSection = new GuiSection();
        EventsConfig eventsConfig = model.getConfig();
        this.eventsCheckboxes = UiMapper.toCheckboxes(eventsConfig.getEvents());

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

        Switcher<String> menu = Switcher.<String>builder()
            .aktiveKey("events")
            .highlight(true)
            .menu(ButtonMenu.<String>builder()
                .margin(10)
                .spacer(true)
                .maxWidth(availableWidth)
                .horizontal(true)
                .button("events", new Button(
                    i18n.t("EventsTab.tab.events.name"),
                    i18n.t("EventsTab.tab.events.desc")
                ))
                .button("generalEvents", new Button(
                    i18n.t("EventsTab.tab.generalEvents.name"),
                    i18n.t("EventsTab.tab.generalEvents.desc")
                ))
                .build())
            .build();

        int tableHeight = (availableHeight
            - tributeSection.body().height()
            - menu.body().height()
            - UI.FONT().H2.height());

        // Events
        Table<Integer> eventsTable = Table.<Integer>builder()
            .rows(UiMapper.toLabeledColumnRows(eventsCheckboxes, i18n))
            .rowPadding(5)
            .columnMargin(20)
            .highlight(true)
            .scrollable(true)
            .backgroundColor(COLOR.WHITE10)
            .displayHeight(tableHeight)
            .build();

        // Events
        generalEventsTable = Table.<Boolean>builder()
            .rows(com.github.argon.sos.moreoptions.ui.UiMapper.toEventsTabGeneralEventColumnRows(model.getGeneralEvents().values()))
            .rowPadding(5)
            .columnMargin(20)
            .highlight(true)
            .scrollable(true)
            .displaySearch(true)
            .backgroundColor(COLOR.WHITE10)
            .displayHeight(tableHeight)
            .build();

        Tabulator<String, RENDEROBJ, Void> tabulator = Tabulator.<String, RENDEROBJ, Void>builder()
            .tabMenu(menu)
            .center(true)
            .tabs(Maps.of(
                "events", eventsTable,
                "generalEvents", generalEventsTable
            ))
            .build();

        eventsSection.addDownC(0, menu);
        eventsSection.addDownC(15, tabulator);

        addDownC(0, eventsSection);
        addDownC(15, tributeSection);
    }

    @Override
    public EventsConfig getValue() {
       return EventsConfig.builder()
           .events(getEventsConfig())
           .enemyBattleLoot(Range.fromSlider(tributeSliders.get("EventsTab.battle.loot.enemy")))
           .playerBattleLoot(Range.fromSlider(tributeSliders.get("EventsTab.battle.loot.player")))
           .generalEvents(generalEventsTable.getValue())
           .build();
    }

    public Map<String, Boolean> getEventsConfig() {
        return eventsCheckboxes.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getValue()));
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

        generalEventsTable.setValue(eventsConfig.getGeneralEvents());

        tributeSliders.get("EventsTab.battle.loot.enemy").setValue(eventsConfig.getEnemyBattleLoot().getValue());
        tributeSliders.get("EventsTab.battle.loot.player").setValue(eventsConfig.getPlayerBattleLoot().getValue());
    }

    protected EventsTab element() {
        return this;
    }

    @Data
    @Builder
    @EqualsAndHashCode
    public static class GeneralEvent {
        private String key;
        private EventAbs event;
        private boolean enabled;
    }
}
