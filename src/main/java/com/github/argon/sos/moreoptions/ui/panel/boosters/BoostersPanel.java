package com.github.argon.sos.moreoptions.ui.panel.boosters;

import com.github.argon.sos.moreoptions.booster.Boosters;
import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import com.github.argon.sos.moreoptions.config.domain.Range;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import com.github.argon.sos.moreoptions.i18n.I18n;
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
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.data.GETTER;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Contains sliders to influence values of game boosters
 */
public class BoostersPanel extends AbstractConfigPanel<BoostersConfig, BoostersPanel> {
    private static final Logger log = Loggers.getLogger(BoostersPanel.class);
    private final static I18n i18n = I18n.get(BoostersPanel.class);

    private final Map<Faction, BoostersSection> factionBoostersSections = new HashMap<>();

    private final Map<String, Faction> factions = new HashMap<>();

    private final Player playerFaction = FACTIONS.player();

    private final int tableHeight;
    @Getter
    private final Button loadPresetButton;
    @Getter
    private final Button savePresetButton;
    @Getter
    private final Button copyButton;
    @Getter
    private final Button pasteButton;

    @Getter
    private BoostersSection currentBoosterSection;

    private Map<String, Range> clipboard;

    @Getter
    private Map<String, Map<String, Range>> presets;

    public BoostersPanel(
        String title,
        Map<Faction, List<Entry>> boosterEntries,
        Map<String, BoostersConfig.BoostersPreset> presets,
        BoostersConfig defaultConfig,
        int availableWidth,
        int availableHeight
    ) {
        super(title, defaultConfig, availableWidth, availableHeight);
        this.presets = presets.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> entry.getValue().getBoosters().stream().collect(Collectors.toMap(
                BoostersConfig.Booster::getKey,
                BoostersConfig.Booster::getRange
            ))
        ));

        GETTER.GETTER_IMP<Faction> getter = new GETTER.GETTER_IMP<>();
        FactionList factionList = new FactionList(getter, availableHeight);

        loadPresetButton = new Button(
            i18n.t("BoostersPanel.button.preset.load.name"),
            i18n.t("BoostersPanel.button.preset.load.desc"));
        savePresetButton = new Button(
            i18n.t("BoostersPanel.button.preset.save.name"),
            i18n.t("BoostersPanel.button.preset.save.desc"));
        copyButton = new Button(
            i18n.t("BoostersPanel.button.boosters.copy.name"),
            i18n.t("BoostersPanel.button.boosters.copy.desc"));
        pasteButton = new Button(
            i18n.t("BoostersPanel.button.boosters.paste.name"),
            i18n.t("BoostersPanel.button.boosters.paste.desc"));

        ButtonMenu<String> buttons = ButtonMenu.<String>builder()
            .button("load", loadPresetButton)
            .button("save", savePresetButton)
            .button("copy", copyButton)
            .button("paste", pasteButton)
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
    public Faction copyBoostersConfig() {
        clipboard = currentBoosterSection.getValue();
        return currentBoosterSection.getFaction();
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
    public BoostersConfig getValue() {
        Map<String, Set<BoostersConfig.Booster>> factionBoosters = factionBoostersSections.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(playerFaction))
            .collect(Collectors.toMap(
                entry -> entry.getKey().name.toString(),
                entry -> {
                    BoostersSection section = entry.getValue();
                    Map<String, Range> boosterValues = section.getValue();
                    return buildConfigBooster(boosterValues);
                }
        ));

        Set<BoostersConfig.Booster> playerBoosters = factionBoostersSections.entrySet().stream()
            .filter(entry -> entry.getKey().equals(playerFaction))
            .findFirst()
            .map(entry -> {
                BoostersSection section = entry.getValue();
                Map<String, Range> boosterValues = section.getValue();
                return buildConfigBooster(boosterValues);
            }).orElse(new HashSet<>());

        return BoostersConfig.builder()
            .faction(factionBoosters)
            .player(playerBoosters)
            .build();
    }

    @NotNull
    private static Set<BoostersConfig.Booster> buildConfigBooster(Map<String, Range> boosterValues) {
        return boosterValues.entrySet().stream().map(stringRangeEntry ->
                BoostersConfig.Booster.builder()
                    .key(stringRangeEntry.getKey())
                    .range(stringRangeEntry.getValue())
                    .build())
            .collect(Collectors.toSet());
    }

    @Override
    public void setValue(BoostersConfig config) {
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

            Map<String, Range> values = toValues(boosters);

            boostersSection.setValue(values);
        });

        BoostersSection playerBoostersSection = factionBoostersSections.get(playerFaction);
        if (playerBoostersSection != null) {
            playerBoostersSection.setValue(toValues(config.getPlayer()));
        }
    }

    @NotNull
    private static Map<String, Range> toValues(Set<BoostersConfig.Booster> boosters) {
        return boosters.stream().collect(Collectors.toMap(
            BoostersConfig.Booster::getKey,
            BoostersConfig.Booster::getRange));
    }

    protected BoostersPanel element() {
        return this;
    }


    @Data
    @Builder
    @EqualsAndHashCode
    public static class Entry {
        private Range range;
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
