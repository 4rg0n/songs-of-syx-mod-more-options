package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.mod.sdk.data.TreeNode;
import com.github.argon.sos.mod.sdk.game.api.GameApis;
import com.github.argon.sos.mod.sdk.game.api.GameEventsApi;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.ui.*;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import com.github.argon.sos.moreoptions.game.api.GameBoosterApi;
import com.github.argon.sos.moreoptions.ui.tab.boosters.BoostersTab;
import com.github.argon.sos.moreoptions.ui.tab.races.RacesTab;
import game.event.engine.Event;
import game.event.engine.EventTree;
import game.faction.Faction;
import init.race.Race;
import init.sprite.SPRITES;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import settlement.entity.animal.AnimalSpecies;
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

    public Map<String, EventTree> toEventsTabEvents(Map<String, Boolean> generalEvents) {
        Map<String, EventTree> events = new HashMap<>();

        for (Map.Entry<String, Boolean> entry : generalEvents.entrySet()) {
            EventTree event = toEventTree(entry.getKey(), entry.getValue());

            if (event == null) {
                continue;
            }

            events.put(event.getKey(), event);
        }

        return events;
    }

    @Nullable
    public EventTree toEventTree(String key, Boolean enabled) {
        TreeNode<GameEventsApi.EventContainer> eventTree = gameApis.events().getEventTrees().get(key);

        if (eventTree == null) {
            log.debug("No event with key '%s' present", key);
            return null;
        }

        return EventTree.builder()
            .key(key)
            .enabled(enabled)
            .tree(eventTree)
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

    public <Element extends RENDEROBJ> List<ColumnRow<String>> toAnimalSoundLabeledColumnRows(Map<String, Element> elements) {
        return elements.entrySet().stream()
            .map(entry -> {
                String key = entry.getKey();
                Element element = entry.getValue();

                String name = key;
                SPRITE sprite = SPRITES.icons().m.clear_structure;
                AnimalSpecies animalSpecies = gameApis.animals().getBySound(key).orElse(null);

                if (animalSpecies != null) {
                    name = animalSpecies.name.toString();
                    sprite = animalSpecies.icon;
                }

                Label label = Label.builder()
                    .name(name)
                    .build();

                if (animalSpecies != null) {
                    label.hoverGuiAction(animalSpecies::hover);
                }

                // label with element
                return ColumnRow.<String>builder()
                    .key(key)
                    .value(name)
                    .column(label)
                    .column(UiUtil.toRender(sprite))
                    .column(element)
                    .searchTerm(name)
                    .comparator(String::compareTo)
                    .build();
            })
            .sorted()
            .collect(Collectors.toList());
    }

    public <Element extends RENDEROBJ> List<ColumnRow<String>> toRoomSoundLabeledColumnRows(Map<String, Element> elements) {
        return elements.entrySet().stream()
            .map(entry -> {
                String key = entry.getKey();
                Element element = entry.getValue();

                String name = key;
                SPRITE sprite = SPRITES.icons().m.clear_structure;
                RoomBlueprintImp room = gameApis.rooms().getBySound(key).orElse(null);

                if (room != null) {
                    name = room.info.name.toString();
                    sprite = room.iconBig().medium;
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

    public List<ColumnRow<Boolean>> toEventTabColumnRows(Collection<EventTree> eventTrees) {
        return eventTrees.stream()
            // sort by event name
            .sorted(Comparator.comparing(EventTree::getKey))
            // only show events with an actual name and description
            .filter(eventTree ->
                eventTree.getTree().get().getEvent().info.name.length() > 0 &&
                eventTree.getTree().get().getEvent().info.desc.length() > 0
            )
            .map(this::toEventTabColumnRow)
            .collect(Collectors.toList());
    }

    public ColumnRow<Boolean> toEventTabColumnRow(EventTree eventTree) {
        Event event = eventTree.getTree().get().getEvent();

        Checkbox checkbox = new Checkbox(eventTree.isEnabled());
        ColorCircle colorCircle = new ColorCircle(3, (gameApis.events().isLockedForPlayer(event)) ? COLOR.GREEN100 : COLOR.RED100);
        colorCircle.renderAction(aFloat -> {
            COLOR color = (gameApis.events().isLockedForPlayer(event)) ? COLOR.GREEN100 : COLOR.RED100;
            colorCircle.setColor(color);
        });
        colorCircle.hoverInfoSet(i18n.t("EventsTab.tab.events.canFire.desc"));

        return ColumnRow.<Boolean>builder()
            .key(eventTree.getKey())
            .value(eventTree.isEnabled())
            .column(colorCircle)
            .column(Label.builder()
                .name(event.info.name.toString())
                .hoverGuiAction(eventTree::hover)
                .build())
            .column(UiUtil.toRender(event.info.icon))
            .column(checkbox)
            .valueConsumer(checkbox::setValue)
            .valueSupplier(checkbox::getValue)
            .searchTerm(event.info.name.toString())
            .build();
    }
}
