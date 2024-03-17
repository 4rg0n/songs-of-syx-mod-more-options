package com.github.argon.sos.moreoptions.ui.panel.boosters;

import com.github.argon.sos.moreoptions.booster.Boosters;
import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import com.github.argon.sos.moreoptions.config.domain.Range;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.game.ui.layout.Layouts;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.panel.AbstractConfigPanel;
import com.github.argon.sos.moreoptions.util.Lists;
import game.boosting.BoostableCat;
import game.faction.FACTIONS;
import game.faction.Faction;
import game.faction.player.Player;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import snake2d.util.color.COLOR;
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

    @Getter
    private final Map<Faction, BoostersSection> boostersSections = new HashMap<>();

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
    private final Button pasteFactionsButton;
    @Getter
    private final Button resetCurrentButton;
    @Getter
    private final Button resetFactionsButton;

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
        pasteFactionsButton = new Button(
            i18n.t("BoostersPanel.button.boosters.paste.factions.name"),
            i18n.t("BoostersPanel.button.boosters.paste.factions.desc"));
        resetCurrentButton = new Button(
            i18n.t("BoostersPanel.button.boosters.reset.name"),
            i18n.t("BoostersPanel.button.boosters.reset.desc"));
        resetFactionsButton = new Button(
            i18n.t("BoostersPanel.button.boosters.reset.factions.name"),
            i18n.t("BoostersPanel.button.boosters.reset.factions.desc"));


        ButtonMenu<String> presetButtons = ButtonMenu.<String>builder()
            .button("load", loadPresetButton)
            .button("save", savePresetButton)
            .margin(5)
            .sameWidth(true)
            .build();

        ButtonMenu<String> copyPasteButtons = ButtonMenu.<String>builder()
            .button("copy", copyButton)
            .button("paste", pasteButton)
            .button("pasteFactions", pasteFactionsButton)
            .margin(5)
            .sameWidth(true)
            .build();

        ButtonMenu<String> resetButtons = ButtonMenu.<String>builder()
            .button("reset", resetCurrentButton)
            .button("resetFactions", resetFactionsButton)
            .margin(5)
            .sameWidth(true)
            .build();

        GuiSection buttons = Layouts.horizontal(Lists.of(
            presetButtons,
            copyPasteButtons,
            resetButtons
        ), 20, true);

        ColorBox buttonBox = new ColorBox(COLOR.WHITE15);
        buttonBox.add(buttons);
        buttonBox.pad(5);

        this.tableHeight = availableHeight - buttonBox.body().height() - 20;
        refresh(boosterEntries);

        int maxWidth = UiUtil.getMaxWidth(boostersSections.values());
        GuiSection section = new GuiSection();
        AbstractUISwitcher boosterSwitcher = new AbstractUISwitcher(maxWidth, tableHeight, false) {
            @Override
            protected RENDEROBJ pget() {
                return currentBoosterSection;
            }
        };
        factionList.clickAction(faction -> currentBoosterSection = boostersSections.get(faction));

        section.addDownC(0, buttonBox);
        section.addDownC(20, boosterSwitcher);

        addRightC(0, factionList);
        addRightC(0, new VerticalLine(40, factionList.body().height(), 1));
        addRightC(0, section);
    }

    /**
     * Saves currently selected booster settings
     */
    public Faction copyBoosters() {
        clipboard = currentBoosterSection.getValue();
        return currentBoosterSection.getFaction();
    }

    /**
     * Applies saved booster settings to currently selected
     */
    public boolean pasteBoosters() {
        if (clipboard == null) {
            return false;
        }

        currentBoosterSection.setValue(clipboard);
        return true;
    }

    public int pasteBoostersToNPCFactions() {
        if (clipboard == null) {
            return 0;
        }

        int amount = 0;
        for (Map.Entry<Faction, BoostersSection> entry : boostersSections.entrySet()) {
            Faction faction = entry.getKey();
            BoostersSection boostersSection = entry.getValue();

            if (faction.equals(playerFaction)) {
                continue;
            }

            boostersSection.setValue(clipboard);
            amount ++;
        }

        return amount;
    }

    public void refresh(Map<Faction, List<Entry>> boosterEntries) {
        boostersSections.clear();
        factions.clear();
        boosterEntries.forEach((faction, entries) -> {
                factions.put(faction.name.toString(), faction);
                boostersSections.put(faction, new BoostersSection(faction, entries, tableHeight));
            });

        currentBoosterSection = boostersSections.get(playerFaction);
    }

    @Override
    public BoostersConfig getValue() {
        Map<String, Set<BoostersConfig.Booster>> factionBoosters = boostersSections.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(playerFaction))
            .collect(Collectors.toMap(
                entry -> entry.getKey().name.toString(),
                entry -> {
                    BoostersSection section = entry.getValue();
                    Map<String, Range> boosterValues = section.getValue();
                    return buildConfigBooster(boosterValues);
                }
        ));

        Set<BoostersConfig.Booster> playerBoosters = boostersSections.entrySet().stream()
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

            BoostersSection boostersSection = boostersSections.get(faction);
            if (boostersSection == null) {
                return;
            }

            Map<String, Range> values = toValues(boosters);

            boostersSection.setValue(values);
        });

        BoostersSection playerBoostersSection = boostersSections.get(playerFaction);
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
