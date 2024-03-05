package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.ColumnRow;
import com.github.argon.sos.moreoptions.game.ui.Spacer;
import com.github.argon.sos.moreoptions.game.ui.Table;
import com.github.argon.sos.moreoptions.ui.UiConfig;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.Maps;
import com.github.argon.sos.moreoptions.util.UiUtil;
import init.sprite.SPRITES;
import init.sprite.UI.Icon;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.misc.GInput;
import util.gui.misc.GText;
import util.save.SaveFile;

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
public class RacesConfigSelectionPanel extends GuiSection {

    @Getter
    private final Table<Entry> racesConfigTable;

    public RacesConfigSelectionPanel(List<Entry> raceConfigEntries, @Nullable Entry currentConfig) {
        // show modal with "empty" message
        if (raceConfigEntries.isEmpty()) {
            racesConfigTable = Table.<Entry>builder()
                .rows(Lists.of())
                .build();
            addDownC(0, new GText(UI.FONT().H2, "Nothing there to load :("));
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
                activeMarker.hoverInfoSet("Currently active races config.");
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
                .highlightable(true)
                .columns(columns)
                .build();
            row.setValue(entry);

            return row;
        }).collect(Collectors.toList());

        // header for table columns
        Map<String, Button> header = Maps.ofLinked(
            "name", new Button("Config File", "Name of the races config file.").bg(COLOR.WHITE15),
            "save", new Button("Save", "Name of the game save.").bg(COLOR.WHITE15),
            "active", new Button("Active", "Whether this is the active loaded races config.").bg(COLOR.WHITE15),
            "created", new Button("Created", "Creation date of races config.").bg(COLOR.WHITE15),
            "updated", new Button("Updated", "Last updated date of races config.").bg(COLOR.WHITE15)
        );

        // race config table and search
        StringInputSprite searchInput = new StringInputSprite(16, UI.FONT().M).placeHolder("Search");
        racesConfigTable = Table.<Entry>builder()
            .evenOdd(true)
            .scrollable(true)
            .search(searchInput)
            .rows(rows)
            .selectable(true)
            .displayHeight(600)
            .headerButtons(header)
            .rowPadding(5)
            .build();

        addDownC(0, new GInput(searchInput));
        addDownC(10, new GText(UI.FONT().S, "Doubleclick to load a races config from file.").color(COLOR.WHITE35));
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
