package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Checkbox;
import com.github.argon.sos.moreoptions.game.ui.VerticalLine;
import com.github.argon.sos.moreoptions.ui.CheckboxBuilder;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GHeader;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Events extends GuiSection {
    @Getter
    private final Map<String, Checkbox> checkboxes = new HashMap<>();

    public Events(MoreOptionsConfig.Events eventConfig) {
        CheckboxBuilder checkboxBuilder = new CheckboxBuilder();

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
        GuiSection settlement = checkboxBuilder.build(settlementCheckboxes);
        checkboxes.putAll(checkboxBuilder.getCheckboxes());

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
        worldCheckboxes.put("Popup", CheckboxBuilder.CheckboxDescription.builder()
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
        GuiSection world = checkboxBuilder.build(worldCheckboxes);
        checkboxes.putAll(checkboxBuilder.getCheckboxes());

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
}
