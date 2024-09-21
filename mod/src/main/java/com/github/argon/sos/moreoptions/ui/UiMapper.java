package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.mod.sdk.game.api.GameApis;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.ui.Checkbox;
import com.github.argon.sos.mod.sdk.ui.ColorCircle;
import com.github.argon.sos.mod.sdk.ui.ColumnRow;
import com.github.argon.sos.mod.sdk.ui.Label;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import com.github.argon.sos.moreoptions.game.api.GameBoosterApi;
import com.github.argon.sos.moreoptions.ui.tab.boosters.BoostersTab;
import com.github.argon.sos.moreoptions.ui.tab.events.EventsTab;
import com.github.argon.sos.moreoptions.ui.tab.races.RacesTab;
import game.events.general.EventAbs;
import game.faction.FACTIONS;
import game.faction.Faction;
import init.race.Race;
import init.sprite.SPRITES;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import settlement.room.main.RoomBlueprintImp;
import snake2d.util.color.COLOR;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.SPRITE;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
public class UiMapper {

    private final static Logger log = Loggers.getLogger(UiMapper.class);
    private final static I18nTranslator i18n = ModModule.i18n().get(UiMapper.class);

    private final GameApis gameApis;
    private final GameBoosterApi gameBoosterApi;

    public Map<String, EventsTab.GeneralEvent> toEventsTabGeneralEvents(Map<String, Boolean> generalEvents) {
        Map<String, EventsTab.GeneralEvent> events = new HashMap<>();

        for (Map.Entry<String, Boolean> entry : generalEvents.entrySet()) {
            EventsTab.GeneralEvent event = toEventsTabGeneralEvent(entry.getKey(), entry.getValue());

            if (event == null) {
                continue;
            }

            events.put(event.getKey(), event);
        }

        return events;
    }

    @Nullable
    public EventsTab.GeneralEvent toEventsTabGeneralEvent(String key, Boolean enabled) {
        EventAbs eventAbs = gameApis.events().getEvents().get(key);

        if (eventAbs == null) {
            log.debug("No event '%s' present", key);
            return null;
        }

        return EventsTab.GeneralEvent.builder()
            .key(key)
            .enabled(enabled)
            .event(eventAbs)
            .build();
    }

    public Map<String, List<RacesTab.Entry>> toRacesTabEntries(Set<RacesConfig.Liking> raceLikings) {
        Map<String, List<RacesTab.Entry>> entries = new HashMap<>();

        for (RacesConfig.Liking raceLiking : raceLikings) {
            RacesTab.Entry entry = toRacesTabEntry(raceLiking);
            String raceKey = entry.getRace().info.name.toString();
            entries.putIfAbsent(raceKey, new ArrayList<>());
            entries.get(raceKey).add(entry);
        }

        return entries;
    }

    public RacesTab.Entry toRacesTabEntry(RacesConfig.Liking liking) {
        Race race = gameApis.race().getRace(liking.getRace()).orElse(null);
        Race otherRace = gameApis.race().getRace(liking.getOtherRace()).orElse(null);

        return RacesTab.Entry.builder()
            .raceKey(liking.getRace())
            .otherRaceKey(liking.getOtherRace())
            .race(race)
            .otherRace(otherRace)
            .range(liking.getRange())
            .build();
    }

    public Map<Faction, List<BoostersTab.Entry>> toBoostersTabEntries(BoostersConfig boostersConfig) {
        Map<Faction, List<BoostersTab.Entry>> factionBoosters = new HashMap<>();
        // npc factions
        for (Map.Entry<String, Map<String, BoostersConfig.Booster>> entry : boostersConfig.getFaction().entrySet()) {
            String factionName = entry.getKey();
            Faction faction = gameApis.faction().getByName(factionName);

            if (faction == null) {
                log.debug("No faction for name '%s' found in game", factionName);
                continue;
            }

            factionBoosters.put(
                faction,
                entry.getValue().values().stream()
                    .map(this::toBoostersTabEntry)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }

        // add player faction
        factionBoosters.put(
            gameApis.faction().getPlayer(),
            boostersConfig.getPlayer().values().stream()
                .map(this::toBoostersTabEntry)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        return factionBoosters;
    }

    @Nullable
    public BoostersTab.Entry toBoostersTabEntry(BoostersConfig.Booster booster) {
        return gameBoosterApi.get(booster.getKey()).map(boosters -> BoostersTab.Entry.builder()
            .key(booster.getKey())
            .range(booster.getRange())
            .boosters(boosters)
            .cat(gameBoosterApi.getCat(booster.getKey()))
            .build()
        ).orElse(null);
    }

    public static Map<String, List<BoostersTab.Entry>> toBoostersTabEntriesCategorized(List<BoostersTab.Entry> boosterEntries) {
        return boosterEntries.stream()
            .collect(groupingBy(entry -> entry.getCat().name.toString()));
    }

    public <Element extends RENDEROBJ> List<ColumnRow<String>> toRoomSoundLabeledColumnRows(Map<String, Element> elements) {
        return elements.entrySet().stream()
            .map(entry -> {
                String key = entry.getKey();
                Element element = entry.getValue();

                String name = key;
                SPRITE sprite = SPRITES.icons().m.clear_structure;
                Optional<RoomBlueprintImp> room = gameApis.rooms().getBySound(key);

                if (room.isPresent()) {
                    name = room.get().info.name.toString();
                    sprite = room.get().iconBig().medium;
                }

                // label with element
                return ColumnRow.<String>builder()
                    .key(key)
                    .value(name)
                    .column(Label.builder()
                        .name(name)
                        .build())
                    .column(UiUtil.toRender(sprite))
                    .column(element)
                    .searchTerm(name)
                    .comparator(String::compareTo)
                    .build();
            })
            .sorted()
            .collect(Collectors.toList());
    }

    public static List<ColumnRow<Boolean>> toEventsTabGeneralEventColumnRows(Collection<EventsTab.GeneralEvent> generalEvents) {
        return generalEvents.stream()
            // sort by event name
            .sorted(Comparator.comparing(event -> event.getEvent().sName.toString()))
            .map(UiMapper::toEventsTabGeneralEventColumnRow)
            .collect(Collectors.toList());
    }

    public static ColumnRow<Boolean> toEventsTabGeneralEventColumnRow(EventsTab.GeneralEvent generalEvent) {
        EventAbs event = generalEvent.getEvent();
        Checkbox checkbox = new Checkbox(generalEvent.isEnabled());

        ColorCircle colorCircle = new ColorCircle(3, (event.plockable.passes(FACTIONS.player())) ? COLOR.GREEN100 : COLOR.RED100);
        colorCircle.renderAction(aFloat -> {
            COLOR color = (event.plockable.passes(FACTIONS.player())) ? COLOR.GREEN100 : COLOR.RED100;
            colorCircle.setColor(color);
        });
        colorCircle.hoverInfoSet(i18n.t("EventsTab.tab.events.canFire.desc"));

        return ColumnRow.<Boolean>builder()
            .key(generalEvent.getKey())
            .value(generalEvent.isEnabled())
            .column(colorCircle)
            .column(Label.builder()
                .name(event.sName.toString())
                .hoverGuiAction(event::hover)
                .build())
            .column(UiUtil.toRender(event.icon))
            .column(checkbox)
            .valueConsumer(checkbox::setValue)
            .valueSupplier(checkbox::getValue)
            .searchTerm(event.sName.toString())
            .build();
    }
}
