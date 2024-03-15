package com.github.argon.sos.moreoptions.ui.panel.boosters;

import com.github.argon.sos.moreoptions.booster.Boosters;
import com.github.argon.sos.moreoptions.config.MoreOptionsV3Config;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.panel.AbstractConfigPanel;
import game.boosting.BoostableCat;
import game.faction.FACTIONS;
import game.faction.Faction;
import game.faction.player.Player;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.data.GETTER;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Contains sliders to influence values of game boosters
 */
public class BoostersPanel extends AbstractConfigPanel<MoreOptionsV3Config.BoostersConfig, BoostersPanel> {
    private static final Logger log = Loggers.getLogger(BoostersPanel.class);

    private final Map<Faction, BoostersSection> factionBoostersSections = new HashMap<>();

    private final Map<String, Faction> factions = new HashMap<>();

    private final Player playerFaction = FACTIONS.player();

    private final int tableHeight;

    private BoostersSection currentBoosterSection;

    private Map<String, MoreOptionsV3Config.Range> clipboard;

    public BoostersPanel(
        String title,
        Map<Faction, List<Entry>> boosterEntries,
        MoreOptionsV3Config.BoostersConfig defaultConfig,
        int availableWidth,
        int availableHeight
    ) {
        super(title, defaultConfig, availableWidth, availableHeight);

        GETTER.GETTER_IMP<Faction> getter = new GETTER.GETTER_IMP<>();
        FactionList factionList = new FactionList(getter, availableHeight);

        Button loadPresetButton = new Button("Load", "");
        Button savePresetButton = new Button("Save", "");
        Button copyButton = new Button("Copy", "");
        Button pasteButtonButton = new Button("Paste", "");

        ButtonMenu<String> buttons = ButtonMenu.<String>builder()
            .button("load", loadPresetButton)
            .button("save", savePresetButton)
            .button("copy", copyButton)
            .button("paste", pasteButtonButton)
            .horizontal(true)
            .spacer(true)
            .margin(20)
            .build();

        this.tableHeight = availableHeight - buttons.body().height() - 40;
        refresh(boosterEntries);

        int maxWidth = UiUtil.getMaxWidth(factionBoostersSections.values());
        GuiSection section = new GuiSection();
        AbstractUISwitcher boosterSwitcher = new AbstractUISwitcher(maxWidth, tableHeight, false) {
            @Override
            protected RENDEROBJ pget() {
                return currentBoosterSection;
            }
        };
        factionList.clickAction(faction -> currentBoosterSection = factionBoostersSections.get(faction));

        section.addDownC(20, buttons);
        section.addDownC(20, boosterSwitcher);

        addRightC(0, factionList);
        addRightC(0, new VerticalLine(40, factionList.body().height(), 1));
        addRightC(0, section);
    }

    /**
     * Saves currently selected booster settings
     */
    public void copyBoostersConfig() {
        clipboard = currentBoosterSection.getValue();
    }

    /**
     * Applies saved booster settings to currently selected
     */
    public boolean pasteBoostersConfig() {
        if (clipboard == null) {
            return false;
        }

        currentBoosterSection.setValue(clipboard);
        return true;
    }

    public void refresh(Map<Faction, List<Entry>> boosterEntries) {
        factionBoostersSections.clear();
        factions.clear();
        boosterEntries.forEach((faction, entries) -> {
                factions.put(faction.name.toString(), faction);
                factionBoostersSections.put(faction, new BoostersSection(faction, entries, tableHeight));
            });

        currentBoosterSection = factionBoostersSections.get(playerFaction);
    }

    @Override
    public MoreOptionsV3Config.BoostersConfig getValue() {
        Map<String, Set<MoreOptionsV3Config.BoostersConfig.Booster>> factionBoosters = factionBoostersSections.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(playerFaction))
            .collect(Collectors.toMap(
                entry -> entry.getKey().name.toString(),
                entry -> {
                    BoostersSection section = entry.getValue();
                    Map<String, MoreOptionsV3Config.Range> boosterValues = section.getValue();
                    return buildConfigBooster(boosterValues);
                }
        ));

        Set<MoreOptionsV3Config.BoostersConfig.Booster> playerBoosters = factionBoostersSections.entrySet().stream()
            .filter(entry -> entry.getKey().equals(playerFaction))
            .findFirst()
            .map(entry -> {
                BoostersSection section = entry.getValue();
                Map<String, MoreOptionsV3Config.Range> boosterValues = section.getValue();
                return buildConfigBooster(boosterValues);
            }).orElse(new HashSet<>());

        return MoreOptionsV3Config.BoostersConfig.builder()
            .faction(factionBoosters)
            .player(playerBoosters)
            .build();
    }

    @NotNull
    private static Set<MoreOptionsV3Config.BoostersConfig.Booster> buildConfigBooster(Map<String, MoreOptionsV3Config.Range> boosterValues) {
        return boosterValues.entrySet().stream().map(stringRangeEntry ->
                MoreOptionsV3Config.BoostersConfig.Booster.builder()
                    .key(stringRangeEntry.getKey())
                    .range(stringRangeEntry.getValue())
                    .build())
            .collect(Collectors.toSet());
    }

    @Override
    public void setValue(MoreOptionsV3Config.BoostersConfig config) {
        log.trace("Applying Booster config %s", config);

        config.getFaction().forEach((factionName, boosters) -> {
            Faction faction = factions.get(factionName);
            if (faction == null) {
                return;
            }

            BoostersSection boostersSection = factionBoostersSections.get(faction);
            if (boostersSection == null) {
                return;
            }

            Map<String, MoreOptionsV3Config.Range> values = toValues(boosters);

            boostersSection.setValue(values);
        });

        BoostersSection playerBoostersSection = factionBoostersSections.get(playerFaction);
        if (playerBoostersSection != null) {
            playerBoostersSection.setValue(toValues(config.getPlayer()));
        }
    }

    @NotNull
    private static Map<String, MoreOptionsV3Config.Range> toValues(Set<MoreOptionsV3Config.BoostersConfig.Booster> boosters) {
        return boosters.stream().collect(Collectors.toMap(
            MoreOptionsV3Config.BoostersConfig.Booster::getKey,
            MoreOptionsV3Config.BoostersConfig.Booster::getRange));
    }

    protected BoostersPanel element() {
        return this;
    }


    @Data
    @Builder
    @EqualsAndHashCode
    public static class Entry {
        private MoreOptionsV3Config.Range range;
        private String key;

        //  |\__/,|   (`\
        // |_ _  |.--.) )
        // ( T   )     /
        //(((^_(((/(((_/
        @Builder.Default
        private BoostableCat cat = null;

        private Boosters boosters;
    }
}
