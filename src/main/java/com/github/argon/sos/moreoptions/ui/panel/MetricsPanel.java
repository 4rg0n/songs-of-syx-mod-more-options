package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.game.BiAction;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.element.*;
import com.github.argon.sos.moreoptions.ui.builder.section.CheckboxesBuilder;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.Sets;
import init.sprite.UI.UI;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
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
                .title("Started")
                .description("Toggle and apply to start the collection and export of game metrics.")
                .build(),
            UiInfo.<Boolean>builder()
                .key(false)
                .title("Stopped")
                .description("Toggle and apply to stop the collection and export of game metrics.")
                .build()
        ), 0, true, true, true);
        BuildResult<List<GuiSection>, List<Toggler<Boolean>>> onOffToggle = LabeledBuilder.<Toggler<Boolean>>builder().translate(
            LabeledBuilder.Definition.<Toggler<Boolean>>builder()
                .labelDefinition(LabelBuilder.Definition.builder()
                    .key("ui.moreOptions.metrics.toggle")
                    .build())
                .element(toggler)
                .build()
        ).build();

        // Collection rate slider
        this.collectionRate = SliderBuilder.builder().definition(
            SliderBuilder.Definition
                .fromRange(metricsConfig.getCollectionRateSeconds())
                .maxWidth(200)
                .build()
        ).build().getResult();
        BuildResult<List<GuiSection>, List<RENDEROBJ>> collectionRate = LabeledBuilder.builder()
            .definition(LabeledBuilder.Definition.builder()
                .labelDefinition(LabelBuilder.Definition.builder()
                    .key("ui.moreOptions.metrics.collection-rate-seconds")
                    .build())
                .element(this.collectionRate)
                .element(new GTextR(UI.FONT().S, "Seconds"))
                .build())
            .build();

        // Export rate slider
        this.exportRate = SliderBuilder.builder().definition(
            SliderBuilder.Definition
                .fromRange(metricsConfig.getExportRateMinutes())
                .maxWidth(200)
                .build()
        ).build().getResult();
        BuildResult<List<GuiSection>, List<RENDEROBJ>> exportRate = LabeledBuilder.builder()
            .definition(LabeledBuilder.Definition.builder()
                .labelDefinition(LabelBuilder.Definition.builder()
                    .key("ui.moreOptions.metrics.export-rate-minutes")
                    .build())
                .element(this.exportRate)
                .element(new GTextR(UI.FONT().S, "Minutes"))
                .build())
            .build();

        GuiSection exportFilePathSection = exportFilePath(exportFolderPath.toString(), exportFilePath.getFileName().toString());

        // Export file path with folder button
        this.exportFilePathView = new UISwitcher(exportFilePathSection, false);
        this.exportFolderButton = new Button("Folder", "Opens the metrics export folder: " + exportFolderPath);
        this.copyExportFileButton = new Button("Copy", "Copies export file path to clipboard.");

        GuiSection exportButtons = new GuiSection();
        exportButtons.addRightC(0, exportFolderButton);
        exportButtons.addRightC(0, copyExportFileButton);

        BuildResult<List<GuiSection>, List<RENDEROBJ>> exportFile = LabeledBuilder.builder().translate(
            LabeledBuilder.Definition.builder()
                .labelDefinition(LabelBuilder.Definition.builder()
                    .key("ui.moreOptions.metrics.export-file")
                    .build())
                .element(this.exportFilePathView)
                .element(exportButtons)
                .build()
        ).build();

        // Search Bar with uncheck and check buttons
        GuiSection searchBar = new GuiSection();
        this.searchInput = new StringInputSprite(16, UI.FONT().M).placeHolder("Search");
        searchBar.addRightC(0, new GInput(searchInput));
        this.searchToggler = new Toggler<>(Lists.of(
            UiInfo.<Boolean>builder()
                .key(true)
                .title("Check")
                .description("Checks all found stats")
                .build(),
            UiInfo.<Boolean>builder()
                .key(false)
                .title("Uncheck")
                .description("Unchecks all found stats")
                .build()
        ),0, true, true, false);
        searchBar.addRightC(10, searchToggler);

        // Export stats section
        SortedSet<String> sortedAvailableStats = Sets.sort(availableStats);
        BuildResult<Table<Boolean>, Map<String, Checkbox>> exportStats = CheckboxesBuilder.builder()
            .displayHeight(400)
            .search(searchInput)
            .highlightColumns(true)
            .definitions(sortedAvailableStats.stream()
                .collect(Collectors.toMap(
                    s -> s,
                    s -> LabeledCheckboxBuilder.Definition.builder()
                        .labelDefinition(LabelBuilder.Definition.builder()
                            .font(UI.FONT().S)
                            .title(s)
                            .build())
                        .checkboxDefinition(CheckboxBuilder.Definition.builder()
                            .enabled(metricsConfig.getStats().isEmpty() || metricsConfig.getStats().contains(s))
                            .build())
                        .build())))
            .build();

        this.statsSection = new UISwitcher(exportStats.getResult(), true);
        this.statsCheckboxes.putAll(exportStats.getInteractable());
        this.onOffToggle = onOffToggle.getInteractable().get(0);
        this.onOffToggle.toggle(metricsConfig.isEnabled());

        List<ColumnRow<Void>> rows = Lists.of(
            onOffToggle.<Void>toColumnRow().getResult(),
            exportFile.<Void>toColumnRow().getResult(),
            collectionRate.<Void>toColumnRow().getResult(),
            exportRate.<Void>toColumnRow().getResult()
        );

        Table<Void> configTable = Table.<Void>builder()
            .evenOdd(true)
            .scrollable(false)
            .rows(rows)
            .build();

        addDownC(0, configTable);

        GHeader statsHeader = new GHeader("Game stats to export");
        statsHeader.hoverInfoSet("These stats will be written into the Export CSV File. " +
            "If you change any of them a new file will be created.");
        addDownC(25, statsHeader);
        addDownC(10, searchBar);
        addDownC(15, statsSection);

        // Actions
        searchToggler.clickAction(aBoolean -> {
            List<String> resultList = exportStats.getResult()
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
