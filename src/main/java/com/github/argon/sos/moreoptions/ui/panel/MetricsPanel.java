package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.game.BiAction;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.element.*;
import com.github.argon.sos.moreoptions.ui.builder.section.CheckboxesBuilder;
import com.github.argon.sos.moreoptions.util.Lists;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains control elements for enabling and disabling game events.
 */
public class MetricsPanel extends GuiSection implements Valuable<MoreOptionsConfig.Metrics, MetricsPanel>, Refreshable<MetricsPanel> {

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
    private final Button exportFolderButton;
    @Getter
    private final Toggler<Boolean> searchToggler;

    private Action<MetricsPanel> refreshAction = o -> {};
    private BiAction<MoreOptionsConfig.Metrics, MetricsPanel> afterSetValueAction = (o1, o2)  -> {};

    public MetricsPanel(
        MoreOptionsConfig.Metrics metricsConfig,
        List<String> availableStats,
        Path exportFolderPath,
        Path exportFilePath
    ) {
        this.exportFolderPath = exportFolderPath;
        Toggler<Boolean> toggler = new Toggler<>(Lists.of(
            UiInfo.<Boolean>builder()
                .key(true)
                .title("Started")
                .description("Click to start the collection and export of metrics")
                .build(),
            UiInfo.<Boolean>builder()
                .key(false)
                .title("Stopped")
                .description("Click to stop the collection and export of metrics")
                .build()
        ), 0, true, true);

        BuildResult<List<GuiSection>, List<Toggler<Boolean>>> onOffToggle = LabeledBuilder.<Toggler<Boolean>>builder().translate(
            LabeledBuilder.Definition.<Toggler<Boolean>>builder()
                .labelDefinition(LabelBuilder.Definition.builder()
                    .key("ui.moreOptions.metrics.toggle")
                    .description("Enables or disables the collection and export of metric data.")
                    .build())
                .element(toggler)
                .build()
        ).build();
        this.collectionRate = SliderBuilder.builder().definition(
            SliderBuilder.Definition
                .buildFrom(metricsConfig.getCollectionRateSeconds())
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

        this.exportRate = SliderBuilder.builder().definition(
            SliderBuilder.Definition
                .buildFrom(metricsConfig.getExportRateMinutes())
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

        this.exportFilePathView = new UISwitcher(exportFilePathSection, false);
        this.exportFolderButton = new Button("Export Folder");
        exportFolderButton.hoverInfoSet("Opens the metrics export folder: " + exportFolderPath);

        BuildResult<List<GuiSection>, List<RENDEROBJ>> exportFile = LabeledBuilder.builder().translate(
            LabeledBuilder.Definition.builder()
                .labelDefinition(LabelBuilder.Definition.builder()
                    .key("ui.moreOptions.metrics.export-file")
                    .build())
                .element(this.exportFilePathView)
                .element(exportFolderButton)
                .build()
        ).build();

        // Search Bar
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
        ),0, true, false);
        searchBar.addRightC(10, searchToggler);

        BuildResult<Table, Map<String, Checkbox>> checkboxes = CheckboxesBuilder.builder()
            .displayHeight(400)
            .search(searchInput)
            .definitions(availableStats.stream()
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

        this.statsSection = new UISwitcher(checkboxes.getResult(), true);
        this.statsCheckboxes.putAll(checkboxes.getInteractable());
        this.onOffToggle = onOffToggle.getInteractable().get(0);
        this.onOffToggle.toggle(metricsConfig.isEnabled());

        List<ColumnRow> rows = Lists.of(
            onOffToggle.toColumnRow().getResult(),
            exportFile.toColumnRow().getResult(),
            collectionRate.toColumnRow().getResult(),
            exportRate.toColumnRow().getResult()
        );

        Table configTable = TableBuilder.builder()
            .evenOdd(true)
            .scrollable(false)
            .columnRows(rows)
            .build()
            .getResult();

        addDownC(0, configTable);

        GHeader statsHeader = new GHeader("Game stats to export");
        statsHeader.hoverInfoSet("These stats will be written into the Export CSV File. " +
            "If you change any of them a new file will be created.");
        addDownC(25, statsHeader);
        addDownC(10, searchBar);
        addDownC(15, statsSection);

        // Actions
        searchToggler.onClick(aBoolean -> {
            List<String> resultList = checkboxes.getResult()
                .search(searchInput.text().toString());
            resultList.forEach(result -> statsCheckboxes.get(result).setValue(aBoolean));
        });
    }

    public void refresh(@Nullable Path exportFilePath) {
        if (exportFilePath != null) {
            this.exportFilePathView.set(exportFilePath(exportFilePath));
        }
    }

    @Override
    public MoreOptionsConfig.Metrics getValue() {
        return MoreOptionsConfig.Metrics.builder()
            .collectionRateSeconds(MoreOptionsConfig.Range.fromSlider(collectionRate))
            .exportRateMinutes(MoreOptionsConfig.Range.fromSlider(exportRate))
            .stats(getCheckedStats())
            .enabled(onOffToggle.getValue())
            .build();
    }

    @Override
    public void setValue(MoreOptionsConfig.Metrics metricsConfig) {
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

    private List<String> getCheckedStats() {
        return statsCheckboxes.entrySet().stream()
            .filter(entry -> {
                Checkbox checkbox = entry.getValue();
                return checkbox.getValue();
            })
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    private void setCheckedStats(List<String> stats) {
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
    public void onRefresh(Action<MetricsPanel> refreshAction) {
        this.refreshAction = refreshAction;
    }

    @Override
    public void onAfterSetValue(BiAction<MoreOptionsConfig.Metrics, MetricsPanel> afterSetValueUIAction) {
        this.afterSetValueAction = afterSetValueUIAction;
    }
}
