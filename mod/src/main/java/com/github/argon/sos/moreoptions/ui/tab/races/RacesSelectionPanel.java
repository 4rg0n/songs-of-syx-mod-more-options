package com.github.argon.sos.moreoptions.ui.tab.races;

import com.github.argon.sos.mod.sdk.ui.*;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.util.Lists;
import com.github.argon.sos.mod.sdk.util.Maps;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.ui.UiConfig;
import init.sprite.SPRITES;
import init.sprite.UI.Icon;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.misc.GText;
import game.save.SaveFile;

import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * For selecting a races config file from another save game.
 */
public class RacesSelectionPanel extends GuiSection {

    private final static I18nTranslator i18n = ModModule.i18n().get(RacesSelectionPanel.class);

    @Getter
    private final Table<Entry> racesConfigTable;

    public RacesSelectionPanel(List<Entry> raceConfigEntries, @Nullable Entry currentConfig) {
        // show modal with "empty" message
        if (raceConfigEntries.isEmpty()) {
            racesConfigTable = Table.<Entry>builder()
                .rows(Lists.of())
                .build();
            addDownC(0, new GText(UI.FONT().H2, i18n.t("RacesSelectionPanel.nothing.desc")));
            return;
        }

        // prepare columns for race likings table
        List<ColumnRow<Entry>> rows = raceConfigEntries.stream().map(entry -> {
            SaveFile saveFile = entry.getSaveFile();

            // race config file name
            String fileName = entry.getConfigPath().getFileName().toString();
            GuiSection configPathGui = UiUtil.toGuiSection(new GText(UI.FONT().S, fileName)
                .setMaxWidth(300));
            configPathGui.hoverInfoSet(fileName);
            configPathGui.pad(5);

            // game save name
            String saveFileName = (saveFile != null) ? saveFile.name : "";
            GuiSection saveFileGui = UiUtil.toGuiSection(new GText(UI.FONT().S, saveFileName)
                .setMaxWidth(300)
                .lablifySub());
            saveFileGui.hoverInfoSet(saveFileName);
            saveFileGui.pad(5);

            // race config creation date
            String creationDate = LocalDateTime.ofInstant(entry.getCreationDate(), ZoneOffset.UTC).format(UiConfig.TIME_FORMAT);
            GuiSection creationDateGui = UiUtil.toGuiSection(new GText(UI.FONT().S, creationDate));
            creationDateGui.pad(5);

            // race config update date
            String updateDate = LocalDateTime.ofInstant(entry.getCreationDate(), ZoneOffset.UTC).format(UiConfig.TIME_FORMAT);
            GuiSection updateDateGui = UiUtil.toGuiSection(new GText(UI.FONT().S, updateDate).lablifySub());
            creationDateGui.pad(5);

            // row is currently loaded race config? mark it!
            Icon activeMarkerIcon = SPRITES.icons().m.flag;
            GuiSection activeMarker;
            if (currentConfig != null && currentConfig.getConfigPath().equals(entry.getConfigPath())) {
                activeMarker = UiUtil.toGuiSection(activeMarkerIcon);
                activeMarker.hoverInfoSet(i18n.t("RacesSelectionPanel.marker.active.desc"));
            } else {
                activeMarker = UiUtil.toGuiSection(new Spacer(
                    activeMarkerIcon.width(),
                    activeMarkerIcon.height()));
            }

            // prepare row with columns
            List<GuiSection> columns = Lists.of(configPathGui, saveFileGui, activeMarker, creationDateGui, updateDateGui);
            ColumnRow<Entry> row = ColumnRow.<Entry>builder()
                .columns(columns)
                .searchTerm(fileName)
                .build();
            row.setValue(entry);
            row.hoverInfoSet(i18n.t("RacesSelectionPanel.text.select.name"));
            return row;
        }).collect(Collectors.toList());

        // header for table columns
        Map<String, Button> header = Maps.ofLinked(
            "file", new Button(i18n.t("RacesSelectionPanel.table.file.name"), i18n.t("RacesSelectionPanel.table.file.desc")),
            "save", new Button(i18n.t("RacesSelectionPanel.table.save.name"), i18n.t("RacesSelectionPanel.table.save.desc")),
            "active", new Button(i18n.t("RacesSelectionPanel.table.active.name"), i18n.t("RacesSelectionPanel.table.active.desc")),
            "created", new Button(i18n.t("RacesSelectionPanel.table.created.name"), i18n.t("RacesSelectionPanel.table.created.desc")),
            "updated", new Button(i18n.t("RacesSelectionPanel.table.updated.name"), i18n.t("RacesSelectionPanel.table.updated.desc"))
        );

        // race config table and search
        StringInputSprite searchInput = new StringInputSprite(16, UI.FONT().M).placeHolder(i18n.t("RacesSelectionPanel.search.input.name"));
        racesConfigTable = Table.<Entry>builder()
            .evenOdd(true)
            .scrollable(true)
            .search(searchInput)
            .rows(rows)
            .highlight(true)
            .selectable(true)
            .displayHeight(500)
            .headerButtons(header)
            .rowPadding(5)
            .build();

        addDownC(0, new Input(searchInput));
        addDownC(10, racesConfigTable);
    }

    @Getter
    @Builder
    @EqualsAndHashCode
    public static class Entry {
        private final Path configPath;
        private final Instant creationDate;
        private final Instant updateDate;
        @Nullable
        private SaveFile saveFile;
    }
}
