package com.github.argon.sos.moreoptions.ui.tab.events;

import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.mod.sdk.game.ui.ColumnRow;
import com.github.argon.sos.mod.sdk.game.ui.Slider;
import com.github.argon.sos.mod.sdk.game.ui.Table;
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

        GuiSection eventsChanceSection = new GuiSection();
        GuiSection tributeSection = new GuiSection();

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

        GHeader eventChancesHeader = new GHeader(i18n.t("EventsTab.header.chance.name"));
        eventChancesHeader.hoverInfoSet(i18n.t("EventsTab.header.chance.desc"));

        int tableHeight = availableHeight
            - eventChancesHeader.body().height()
            - tributeSection.body().height()
            - UI.FONT().H2.height() // headers height
            - 50;

        // Event chances
        this.eventsChanceSliders = UiMapper.toSliders(eventsConfig.getChance());
        List<ColumnRow<Integer>> evenChanceRows = UiMapper.toLabeledColumnRows(eventsChanceSliders, i18n);
        Table<Integer> chanceTable = Table.<Integer>builder()
            .rows(evenChanceRows)
            .rowPadding(5)
            .columnMargin(5)
            .scrollable(true)
            .displayHeight(tableHeight)
            .backgroundColor(COLOR.WHITE10)
            .highlight(true)
            .build();

        eventsChanceSection.addDown(0, eventChancesHeader);
        eventsChanceSection.addDown(5, chanceTable);

        addDownC(0, eventsChanceSection);
        addDownC(10, tributeSection);
    }

    @Override
    public EventsConfig getValue() {
       return EventsConfig.builder()
           .chance(getEventsChanceConfig())
           .enemyBattleLoot(Range.fromSlider(tributeSliders.get("EventsTab.battle.loot.enemy")))
           .playerBattleLoot(Range.fromSlider(tributeSliders.get("EventsTab.battle.loot.player")))
           .build();
    }

    public Map<String, Range> getEventsChanceConfig() {
        return eventsChanceSliders.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                tab -> Range.fromSlider(tab.getValue())));
    }

    @Override
    public void setValue(EventsConfig eventsConfig) {
        log.trace("Applying UI settlement events config %s", eventsConfig);

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
