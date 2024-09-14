package com.github.argon.sos.moreoptions.ui.tab.events;

import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.mod.sdk.ui.Checkbox;
import com.github.argon.sos.mod.sdk.ui.ColumnRow;
import com.github.argon.sos.mod.sdk.ui.Slider;
import com.github.argon.sos.mod.sdk.ui.Table;
import com.github.argon.sos.mod.sdk.game.util.UiMapper;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Maps;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.domain.EventsConfig;
import com.github.argon.sos.moreoptions.ui.tab.AbstractConfigTab;
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
    private final static I18nTranslator i18n = ModModule.i18n().get(EventsTab.class);

    private final Map<String, Checkbox> eventsCheckboxes;
    private final Map<String, Slider> tributeSliders;

    public EventsTab(
        String title,
        EventsConfig eventsConfig,
        EventsConfig defaultConfig,
        int availableWidth,
        int availableHeight
    ) {
        super(title, defaultConfig, availableWidth, availableHeight);

        GuiSection eventsSection = new GuiSection();
        GuiSection tributeSection = new GuiSection();
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

        int tableHeight = availableHeight
            - tributeSection.body().height()
            - UI.FONT().H2.height() // headers height
            - 50;

        // Event chances
        Table<Integer> eventsTable = Table.<Integer>builder()
            .rows(UiMapper.toLabeledColumnRows(eventsCheckboxes, i18n))
            .rowPadding(5)
            .columnMargin(20)
            .highlight(true)
            .scrollable(true)
            .backgroundColor(COLOR.WHITE10)
            .displayHeight(tableHeight)
            .build();

        eventsSection.addDown(5, eventsTable);

        addDownC(0, eventsSection);
        addDownC(10, tributeSection);
    }

    @Override
    public EventsConfig getValue() {
       return EventsConfig.builder()
           .events(getEventsConfig())
           .enemyBattleLoot(Range.fromSlider(tributeSliders.get("EventsTab.battle.loot.enemy")))
           .playerBattleLoot(Range.fromSlider(tributeSliders.get("EventsTab.battle.loot.player")))
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

        tributeSliders.get("EventsTab.battle.loot.enemy").setValue(eventsConfig.getEnemyBattleLoot().getValue());
        tributeSliders.get("EventsTab.battle.loot.player").setValue(eventsConfig.getPlayerBattleLoot().getValue());
    }

    protected EventsTab element() {
        return this;
    }
}
