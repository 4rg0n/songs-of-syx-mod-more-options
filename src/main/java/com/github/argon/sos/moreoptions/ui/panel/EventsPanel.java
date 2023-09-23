package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
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
import java.util.TreeMap;

public class EventsPanel extends GuiSection {

    private static final Logger log = Loggers.getLogger(EventsPanel.class);

    @Getter
    private final Map<String, Checkbox> checkboxes = new HashMap<>();

    public EventsPanel(MoreOptionsConfig.Events eventConfig) {
        GuiSection settlement = settlementCheckboxes(eventConfig);
        GuiSection world = worldCheckboxes(eventConfig);

        GuiSection settlementSection = new GuiSection();
        settlementSection.addDown(0, new GHeader("Settlement"));
        settlementSection.addDown(10, settlement);

        GuiSection worldSection = new GuiSection();
        worldSection.addDown(0, new GHeader("World"));
        worldSection.addDown(10, world);

        addRight(0, settlementSection);
        addRight(0, new VerticalLine(101, settlementSection.body().height(), 1));
        addRight(0, worldSection);
    }

    public MoreOptionsConfig.Events getConfig() {
        return MoreOptionsConfig.Events.builder()
            .accident(checkboxes.get("accident").selectedIs())
            .advice(checkboxes.get("advice").selectedIs())
            .farm(checkboxes.get("farm").selectedIs())
            .fish(checkboxes.get("fish").selectedIs())
            .riot(checkboxes.get("riot").selectedIs())
            .killer(checkboxes.get("killer").selectedIs())
            .orchard(checkboxes.get("orchard").selectedIs())
            .pasture(checkboxes.get("pasture").selectedIs())
            .disease(checkboxes.get("disease").selectedIs())
            .slaver(checkboxes.get("slaver").selectedIs())
            .temperature(checkboxes.get("temperature").selectedIs())
            .raceWars(checkboxes.get("raceWars").selectedIs())
            .uprising(checkboxes.get("uprising").selectedIs())
            .worldFactionBreak(checkboxes.get("worldFactionBreak").selectedIs())
            .worldRaider(checkboxes.get("worldRaider").selectedIs())
            .worldFactionExpand(checkboxes.get("worldFactionExpand").selectedIs())
            .worldRebellion(checkboxes.get("worldRebellion").selectedIs())
            .worldPlague(checkboxes.get("worldPlague").selectedIs())
            .worldWar(checkboxes.get("worldWar").selectedIs())
            .worldPopup(checkboxes.get("worldPopup").selectedIs())
            .worldWarPeace(checkboxes.get("worldWarPeace").selectedIs())
            .worldWarPlayer(checkboxes.get("worldWarPlayer").selectedIs())
            .build();
    }

    public void applyConfig(MoreOptionsConfig.Events config) {
        log.trace("Applying config %s", config);

        checkboxes.get("accident").selectedSet(config.isAccident());
        checkboxes.get("advice").selectedSet(config.isAdvice());
        checkboxes.get("farm").selectedSet(config.isFarm());
        checkboxes.get("fish").selectedSet(config.isFish());
        checkboxes.get("riot").selectedSet(config.isRiot());
        checkboxes.get("killer").selectedSet(config.isKiller());
        checkboxes.get("orchard").selectedSet(config.isOrchard());
        checkboxes.get("pasture").selectedSet(config.isPasture());
        checkboxes.get("disease").selectedSet(config.isDisease());
        checkboxes.get("slaver").selectedSet(config.isSlaver());
        checkboxes.get("temperature").selectedSet(config.isTemperature());
        checkboxes.get("raceWars").selectedSet(config.isRaceWars());
        checkboxes.get("uprising").selectedSet(config.isUprising());
        checkboxes.get("worldFactionBreak").selectedSet(config.isWorldFactionBreak());
        checkboxes.get("worldRaider").selectedSet(config.isWorldRaider());
        checkboxes.get("worldFactionExpand").selectedSet(config.isWorldFactionExpand());
        checkboxes.get("worldRebellion").selectedSet(config.isWorldRebellion());
        checkboxes.get("worldPlague").selectedSet(config.isWorldPlague());
        checkboxes.get("worldWar").selectedSet(config.isWorldWar());
        checkboxes.get("worldPopup").selectedSet(config.isWorldPopup());
        checkboxes.get("worldWarPeace").selectedSet(config.isWorldWarPeace());
        checkboxes.get("worldWarPlayer").selectedSet(config.isWorldWarPlayer());
    }

    private GuiSection settlementCheckboxes(MoreOptionsConfig.Events eventConfig) {
        Map<String, CheckboxBuilder.CheckboxDescription> settlementCheckboxes = new TreeMap<>();
        settlementCheckboxes.put("disease", CheckboxBuilder.CheckboxDescription.builder()
            .title("Diseases")
            .enabled(eventConfig.isDisease())
            .description("TODO")
            .build());
        settlementCheckboxes.put("slaver", CheckboxBuilder.CheckboxDescription.builder()
            .title("Slaver Visits")
            .enabled(eventConfig.isSlaver())
            .description("TODO")
            .build());
        settlementCheckboxes.put("riot", CheckboxBuilder.CheckboxDescription.builder()
            .title("Riots")
            .enabled(eventConfig.isRiot())
            .description("TODO")
            .build());
        settlementCheckboxes.put("uprising", CheckboxBuilder.CheckboxDescription.builder()
            .title("Uprising")
            .enabled(eventConfig.isUprising())
            .description("TODO")
            .build());
        settlementCheckboxes.put("killer", CheckboxBuilder.CheckboxDescription.builder()
            .title("Serial Killer")
            .enabled(eventConfig.isKiller())
            .description("TODO")
            .build());
        settlementCheckboxes.put("temperature", CheckboxBuilder.CheckboxDescription.builder()
            .title("Temperature")
            .enabled(eventConfig.isTemperature())
            .description("TODO")
            .build());
        settlementCheckboxes.put("farm", CheckboxBuilder.CheckboxDescription.builder()
            .title("Farm Production")
            .enabled(eventConfig.isFarm())
            .description("TODO")
            .build());
        settlementCheckboxes.put("pasture", CheckboxBuilder.CheckboxDescription.builder()
            .title("Pasture Production")
            .enabled(eventConfig.isPasture())
            .description("TODO")
            .build());
        settlementCheckboxes.put("orchard", CheckboxBuilder.CheckboxDescription.builder()
            .title("Orchard Production")
            .enabled(eventConfig.isOrchard())
            .description("TODO")
            .build());
        settlementCheckboxes.put("fish", CheckboxBuilder.CheckboxDescription.builder()
            .title("Fish Production")
            .enabled(eventConfig.isFish())
            .description("TODO")
            .build());
        settlementCheckboxes.put("raceWars", CheckboxBuilder.CheckboxDescription.builder()
            .title("Race Wars")
            .enabled(eventConfig.isRaceWars())
            .description("TODO")
            .build());
        settlementCheckboxes.put("advice", CheckboxBuilder.CheckboxDescription.builder()
            .title("Advices")
            .enabled(eventConfig.isAdvice())
            .description("TODO")
            .build());
        settlementCheckboxes.put("accident", CheckboxBuilder.CheckboxDescription.builder()
            .title("Accidents")
            .enabled(eventConfig.isAccident())
            .description("TODO")
            .build());

        CheckboxBuilder checkboxBuilder = new CheckboxBuilder();
        GuiSection settlement = checkboxBuilder.build(settlementCheckboxes);
        checkboxes.putAll(checkboxBuilder.getCheckboxes());

        return settlement;
    }

    private GuiSection worldCheckboxes(MoreOptionsConfig.Events eventConfig) {
        Map<String, CheckboxBuilder.CheckboxDescription> worldCheckboxes = new TreeMap<>();
        worldCheckboxes.put("worldFactionExpand", CheckboxBuilder.CheckboxDescription.builder()
            .title("Faction Expand")
            .enabled(eventConfig.isWorldFactionExpand())
            .description("TODO")
            .build());
        worldCheckboxes.put("worldFactionBreak", CheckboxBuilder.CheckboxDescription.builder()
            .title("Faction Break")
            .enabled(eventConfig.isWorldFactionBreak())
            .description("TODO")
            .build());
        worldCheckboxes.put("worldPopup", CheckboxBuilder.CheckboxDescription.builder()
            .title("Popup")
            .enabled(eventConfig.isWorldPopup())
            .description("TODO")
            .build());
        worldCheckboxes.put("worldWar", CheckboxBuilder.CheckboxDescription.builder()
            .title("War")
            .enabled(eventConfig.isWorldWar())
            .description("TODO")
            .build());
        worldCheckboxes.put("worldWarPlayer", CheckboxBuilder.CheckboxDescription.builder()
            .title("War Player")
            .enabled(eventConfig.isWorldWarPlayer())
            .description("TODO")
            .build());
        worldCheckboxes.put("worldWarPeace", CheckboxBuilder.CheckboxDescription.builder()
            .title("War Peace")
            .enabled(eventConfig.isWorldWarPeace())
            .description("TODO")
            .build());
        worldCheckboxes.put("worldRaider", CheckboxBuilder.CheckboxDescription.builder()
            .title("Raider")
            .enabled(eventConfig.isWorldRaider())
            .description("TODO")
            .build());
        worldCheckboxes.put("worldRebellion", CheckboxBuilder.CheckboxDescription.builder()
            .title("Rebellion")
            .enabled(eventConfig.isWorldRebellion())
            .description("TODO")
            .build());
        worldCheckboxes.put("worldPlague", CheckboxBuilder.CheckboxDescription.builder()
            .title("Plague")
            .enabled(eventConfig.isWorldPlague())
            .description("TODO")
            .build());

        CheckboxBuilder checkboxBuilder = new CheckboxBuilder();
        GuiSection world = checkboxBuilder.build(worldCheckboxes);
        checkboxes.putAll(checkboxBuilder.getCheckboxes());

        return world;
    }
}
