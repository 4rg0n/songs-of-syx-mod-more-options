package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.game.BiAction;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.Sets;
import init.sprite.UI.UI;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.misc.GHeader;
import util.gui.misc.GInput;
import util.gui.misc.GText;
import util.gui.misc.GTextR;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Contains control elements for enabling and disabling game events.
 */
public class MetricsPanel extends GuiSection implements Valuable<MoreOptionsV2Config.Metrics, MetricsPanel>, Refreshable<MetricsPanel> {

    private final static I18n i18n = I18n.get(MetricsPanel.class);

    private final Toggler<Boolean> onOffToggle;
    private final Slider collectionRate;
    private final Slider exportRate;
    private final UISwitcher exportFilePathView;
    private final UISwitcher statsSection;
    @Getter
    private final StringInputSprite searchInput;
    private final Map<String, Checkbox> statsCheckboxes = new HashMap<>();
    @Getter
    private final Path exportFolderPath;
    @Getter
    private final Button copyExportFileButton;
    @Getter
    @Nullable
    private Path exportFilePath;
    @Getter
    private final Button exportFolderButton;
    @Getter
    private final Toggler<Boolean> searchToggler;

    private Action<MetricsPanel> refreshAction = o -> {};
    private BiAction<MoreOptionsV2Config.Metrics, MetricsPanel> afterSetValueAction = (o1, o2)  -> {};

    public MetricsPanel(
        MoreOptionsV2Config.Metrics metricsConfig,
        Set<String> availableStats,
        Path exportFolderPath,
        Path exportFilePath
    ) {
        this.exportFolderPath = exportFolderPath;

        // Started / Stopped toggle
        Toggler<Boolean> toggler = new Toggler<>(Lists.of(
            UiInfo.<Boolean>builder()
                .key(true)
                .title(i18n.n("toggle.start"))
                .description(i18n.d("toggle.start"))
                .build(),
            UiInfo.<Boolean>builder()
                .key(false)
                .title(i18n.n("toggle.stop"))
                .description(i18n.d("toggle.stop"))
                .build()
        ), 0, true, true, true);
        ColumnRow<Void> onOffToggleRow = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.n("toggle.label"))
                .description(i18n.d("toggle.label"))
                .build())
            .column(toggler)
            .build();

        // Collection rate slider
        this.collectionRate = Slider.SliderBuilder
            .fromRange(metricsConfig.getCollectionRateSeconds())
            .input(true)
            .width(200)
            .build();
        ColumnRow<Void> collectionRateRow = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.n("collectionRate.label"))
                .description(i18n.d("collectionRate.label"))
                .build())
            .column(collectionRate)
            .column(new GTextR(UI.FONT().S, "Seconds"))
            .build();

        // Export rate slider
        this.exportRate = Slider.SliderBuilder
            .fromRange(metricsConfig.getExportRateMinutes())
            .input(true)
            .width(200)
            .build();
        ColumnRow<Void> exportRateRow = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.n("exportRate.label"))
                .description(i18n.d("exportRate.label"))
                .build())
            .column(exportRate)
            .column(new GTextR(UI.FONT().S, "Minutes"))
            .build();


        GuiSection exportFilePathSection = exportFilePath(exportFolderPath.toString(), exportFilePath.getFileName().toString());

        // Export file path with folder button
        this.exportFilePathView = new UISwitcher(exportFilePathSection, false);
        this.exportFolderButton = new Button(i18n.n("button.folder"), i18n.d("button.folder", exportFolderPath));
        this.copyExportFileButton = new Button(i18n.n("button.copy"), i18n.d("button.copy"));

        GuiSection exportButtons = new GuiSection();
        exportButtons.addRightC(0, exportFolderButton);
        exportButtons.addRightC(0, copyExportFileButton);

        ColumnRow<Void> exportFileRow = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.n("exportFile.label"))
                .description(i18n.d("exportFile.label"))
                .build())
            .column(exportFilePathView)
            .column(exportButtons)
            .build();

        // Search Bar with uncheck and check buttons
        GuiSection searchBar = new GuiSection();
        this.searchInput = new StringInputSprite(16, UI.FONT().M).placeHolder(i18n.n("search.input"));
        searchBar.addRightC(0, new GInput(searchInput));
        this.searchToggler = new Toggler<>(Lists.of(
            UiInfo.<Boolean>builder()
                .key(true)
                .title(i18n.n("search.check"))
                .description(i18n.d("search.check"))
                .build(),
            UiInfo.<Boolean>builder()
                .key(false)
                .title(i18n.n("search.uncheck"))
                .description(i18n.d("search.uncheck"))
                .build()
        ),0, true, true, false);
        searchBar.addRightC(10, searchToggler);

        // Export stats section
        SortedSet<String> sortedAvailableStats = Sets.sort(availableStats);
        List<ColumnRow<Boolean>> statRows = sortedAvailableStats.stream().map(statName -> {
            Checkbox checkbox = new Checkbox(metricsConfig.getStats().isEmpty() || metricsConfig.getStats().contains(statName));

            this.statsCheckboxes.put(statName, checkbox);
            return ColumnRow.<Boolean>builder()
                .searchTerm(statName)
                .column(Label.builder()
                    .font(UI.FONT().S)
                    .name(statName)
                    .build())
                .column(checkbox)
                .build();
        }).collect(Collectors.toList());

        Table<Boolean> exportStats = Table.<Boolean>builder()
            .evenOdd(true)
            .displayHeight(300)
            .search(searchInput)
            .rows(statRows)
            .build();

        this.statsSection = new UISwitcher(exportStats, true);
        this.onOffToggle = toggler;
        this.onOffToggle.toggle(metricsConfig.isEnabled());

        List<ColumnRow<Void>> rows = Lists.of(
            onOffToggleRow,
            exportFileRow,
            collectionRateRow,
            exportRateRow
        );

        Table<Void> configTable = Table.<Void>builder()
            .evenOdd(true)
            .scrollable(false)
            .rows(rows)
            .displayHeight(200)
            .build();

        GHeader statsHeader = new GHeader(i18n.n("stats.header"));
        statsHeader.hoverInfoSet(i18n.d("stats.header"));

        addDownC(0, configTable);
        addDownC(15, statsHeader);
        addDownC(10, searchBar);
        addDownC(15, statsSection);

        // Actions
        searchToggler.clickAction(aBoolean -> {
            List<String> resultList = exportStats
                .search(searchInput.text().toString());
            resultList.forEach(result -> statsCheckboxes.get(result).setValue(aBoolean));
        });
    }

    public void refresh(@Nullable Path exportFilePath) {
        if (exportFilePath != null) {
            this.exportFilePath = exportFilePath;
            this.exportFilePathView.set(exportFilePath(exportFilePath));
        }
    }

    @Override
    public MoreOptionsV2Config.Metrics getValue() {
        return MoreOptionsV2Config.Metrics.builder()
            .collectionRateSeconds(MoreOptionsV2Config.Range.fromSlider(collectionRate))
            .exportRateMinutes(MoreOptionsV2Config.Range.fromSlider(exportRate))
            .stats(getCheckedStats())
            .enabled(onOffToggle.getValue())
            .build();
    }

    @Override
    public void setValue(MoreOptionsV2Config.Metrics metricsConfig) {
        onOffToggle.toggle(metricsConfig.isEnabled());
        collectionRate.setValue(metricsConfig.getCollectionRateSeconds().getValue());
        exportRate.setValue(metricsConfig.getExportRateMinutes().getValue());
        setCheckedStats(metricsConfig.getStats());
        afterSetValueAction.accept(metricsConfig, this);
    }

    private static GuiSection exportFilePath(Path exportFilePath) {
        return exportFilePath(exportFilePath.toString(), exportFilePath.getFileName().toString());
    }

    @NotNull
    private static GuiSection exportFilePath(@Nullable String exportFilePath, String exportFileName) {
        int maxWidth = 300;
        int height = UI.FONT().S.height() * 3;

        GTextR text = new GText(UI.FONT().S, exportFileName)
            .setMaxWidth(maxWidth)
            .r(DIR.W);

        if (exportFilePath != null) text.hoverInfoSet(exportFilePath);

        GuiSection section = new GuiSection();
        section.body().setWidth(maxWidth);
        section.body().setHeight(height);
        section.addRightCAbs(0, text);

        return section;
    }

    private Set<String> getCheckedStats() {
        return statsCheckboxes.entrySet().stream()
            .filter(entry -> {
                Checkbox checkbox = entry.getValue();
                return checkbox.getValue();
            })
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    private void setCheckedStats(Set<String> stats) {
        if (stats.isEmpty()) { // enable all when empty stats
            statsCheckboxes.values().forEach(checkbox -> checkbox.setValue(true));
        } else { // enable only when in stats
            statsCheckboxes.forEach((key, checkbox) -> checkbox.setValue(stats.contains(key)));
        }
    }

    @Override
    public void refresh() {
        refreshAction.accept(this);
    }

    @Override
    public void refreshAction(Action<MetricsPanel> refreshAction) {
        this.refreshAction = refreshAction;
    }

    @Override
    public void afterValueSetAction(BiAction<MoreOptionsV2Config.Metrics, MetricsPanel> afterValueSetAction) {
        this.afterSetValueAction = afterValueSetAction;
    }
}
