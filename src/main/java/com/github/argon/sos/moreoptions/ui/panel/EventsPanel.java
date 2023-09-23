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
            .description("A disease can spread at your citizens making them sick.")
            .build());
        settlementCheckboxes.put("slaver", CheckboxBuilder.CheckboxDescription.builder()
            .title("Slaver Visits")
            .enabled(eventConfig.isSlaver())
            .description("A slaver will visit your settlement regularly for buying and selling slaves")
            .build());
        settlementCheckboxes.put("riot", CheckboxBuilder.CheckboxDescription.builder()
            .title("Riots")
            .enabled(eventConfig.isRiot())
            .description("Riots can break out among your citizen.")
            .build());
        settlementCheckboxes.put("uprising", CheckboxBuilder.CheckboxDescription.builder()
            .title("Slave Revolts")
            .enabled(eventConfig.isUprising())
            .description("Slaves can revolt against you.")
            .build());
        settlementCheckboxes.put("killer", CheckboxBuilder.CheckboxDescription.builder()
            .title("Serial Killer")
            .enabled(eventConfig.isKiller())
            .description("A serial killer can kill your citizens until captured.")
            .build());
        settlementCheckboxes.put("temperature", CheckboxBuilder.CheckboxDescription.builder()
            .title("Temperature")
            .enabled(eventConfig.isTemperature())
            .description("The temperature can randomly fluctuate.")
            .build());
        settlementCheckboxes.put("farm", CheckboxBuilder.CheckboxDescription.builder()
            .title("Farm Production")
            .enabled(eventConfig.isFarm())
            .description("Farms can produce less or more randomly.")
            .build());
        settlementCheckboxes.put("pasture", CheckboxBuilder.CheckboxDescription.builder()
            .title("Pasture Production")
            .enabled(eventConfig.isPasture())
            .description("Pastures can produce less or more randomly.")
            .build());
        settlementCheckboxes.put("orchard", CheckboxBuilder.CheckboxDescription.builder()
            .title("Orchard Production")
            .enabled(eventConfig.isOrchard())
            .description("Orchards can produce less or more randomly.")
            .build());
        settlementCheckboxes.put("fish", CheckboxBuilder.CheckboxDescription.builder()
            .title("Fishery Production")
            .enabled(eventConfig.isFish())
            .description("Fisheries can produce less or more randomly.")
            .build());
        settlementCheckboxes.put("raceWars", CheckboxBuilder.CheckboxDescription.builder()
            .title("Race Wars")
            .enabled(eventConfig.isRaceWars())
            .description("A fight between different races in your settlement can break out.")
            .build());
        settlementCheckboxes.put("advice", CheckboxBuilder.CheckboxDescription.builder()
            .title("Advices")
            .enabled(eventConfig.isAdvice())
            .description("If you are low on workers, someone gets sick, loyalty is low or your first crime happens.")
            .build());
        settlementCheckboxes.put("accident", CheckboxBuilder.CheckboxDescription.builder()
            .title("Accidents")
            .enabled(eventConfig.isAccident())
            .description("Workers can harm themselves at workplaces.")
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
            .description("Factions will expand to other territories.")
            .build());
        worldCheckboxes.put("worldFactionBreak", CheckboxBuilder.CheckboxDescription.builder()
            .title("Faction Collapse")
            .enabled(eventConfig.isWorldFactionBreak())
            .description("Factions can randomly collapse under certain conditions.")
            .build());
        worldCheckboxes.put("worldPopup", CheckboxBuilder.CheckboxDescription.builder()
            .title("Popup")
            .enabled(eventConfig.isWorldPopup())
            .description("TODO")
            .build());
        worldCheckboxes.put("worldWar", CheckboxBuilder.CheckboxDescription.builder()
            .title("Faction War")
            .enabled(eventConfig.isWorldWar())
            .description("Factions will fight with each other.")
            .build());
        worldCheckboxes.put("worldWarPlayer", CheckboxBuilder.CheckboxDescription.builder()
            .title("War Player")
            .enabled(eventConfig.isWorldWarPlayer())
            .description("Factions will declare war against a player.")
            .build());
        worldCheckboxes.put("worldWarPeace", CheckboxBuilder.CheckboxDescription.builder()
            .title("War Peace")
            .enabled(eventConfig.isWorldWarPeace())
            .description("Factions can end war randomly under certain conditions.")
            .build());
        worldCheckboxes.put("worldRaider", CheckboxBuilder.CheckboxDescription.builder()
            .title("Raider")
            .enabled(eventConfig.isWorldRaider())
            .description("Raiders will raid your settlement.")
            .build());
        worldCheckboxes.put("worldRebellion", CheckboxBuilder.CheckboxDescription.builder()
            .title("Rebellion")
            .enabled(eventConfig.isWorldRebellion())
            .description("Citizens of factions can rebel when there is low order.")
            .build());
        worldCheckboxes.put("worldPlague", CheckboxBuilder.CheckboxDescription.builder()
            .title("Plague")
            .enabled(eventConfig.isWorldPlague())
            .description("A plague can threaten the some parts of the world.")
            .build());

        CheckboxBuilder checkboxBuilder = new CheckboxBuilder();
        GuiSection world = checkboxBuilder.build(worldCheckboxes);
        checkboxes.putAll(checkboxBuilder.getCheckboxes());

        return world;
    }
}
