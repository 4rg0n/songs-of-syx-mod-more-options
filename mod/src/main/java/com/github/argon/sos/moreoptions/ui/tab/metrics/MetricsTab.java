package com.github.argon.sos.moreoptions.ui.tab.metrics;

import com.github.argon.sos.mod.sdk.game.ui.*;
import com.github.argon.sos.mod.sdk.game.ui.layout.Layout;
import com.github.argon.sos.mod.sdk.game.ui.layout.VerticalLayout;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.util.Lists;
import com.github.argon.sos.mod.sdk.util.Sets;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.domain.MetricsConfig;
import com.github.argon.sos.moreoptions.config.domain.Range;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.ui.tab.AbstractConfigTab;
import init.sprite.UI.UI;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.misc.GHeader;
import util.gui.misc.GText;
import util.gui.misc.GTextR;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Contains control elements for enabling and disabling game events.
 */
public class MetricsTab extends AbstractConfigTab<MetricsConfig, MetricsTab> {

    private final static I18nTranslator i18n = ModModule.i18n().get(MetricsTab.class);

    private final Switcher<Boolean> onOffSwitcher;
    private final Slider collectionRate;
    private final Slider exportRate;
    private final ViewSwitcher exportFilePathView;
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
    private final Switcher<Boolean> checkSwitcher;

    @Setter
    @Accessors(fluent = true, chain = false)
    private Consumer<MetricsConfig> valueConsumer = o -> {};

    public MetricsTab(
        String title,
        MetricsConfig metricsConfig,
        MetricsConfig defaultConfig,
        Set<String> availableStats,
        Path exportFolderPath,
        Path exportFilePath,
        int availableWidth,
        int availableHeight
    ) {
        super(title, defaultConfig, availableWidth, availableHeight);
        this.exportFolderPath = exportFolderPath;

        // Started / Stopped toggle
        Switcher<Boolean> switcher = Switcher.<Boolean>builder()
            .menu(ButtonMenu.<Boolean>builder()
                .button(true, new Button(i18n.t("MetricsTab.toggle.start.name"), i18n.t("MetricsTab.toggle.start.desc")))
                .button(false, new Button(i18n.t("MetricsTab.toggle.stop.name"), i18n.t("MetricsTab.toggle.stop.desc")))
                .sameWidth(true)
                .horizontal(true)
                .build())
            .aktiveKey(metricsConfig.isEnabled())
            .highlight(true)
            .build();
        ColumnRow<Void> onOffToggleRow = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("MetricsTab.toggle.label.name"))
                .description(i18n.t("MetricsTab.toggle.label.desc"))
                .build())
            .column(switcher)
            .build();

        // Collection rate slider
        this.collectionRate = Slider.SliderBuilder
            .fromRange(metricsConfig.getCollectionRateSeconds())
            .controls(true)
            .input(true)
            .width(200)
            .build();
        ColumnRow<Void> collectionRateRow = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("MetricsTab.collectionRate.label.name"))
                .description(i18n.t("MetricsTab.collectionRate.label.desc"))
                .build())
            .column(collectionRate)
            .column(new GTextR(UI.FONT().S, i18n.t("MetricsTab.text.seconds")))
            .build();

        // Export rate slider
        this.exportRate = Slider.SliderBuilder
            .fromRange(metricsConfig.getExportRateMinutes())
            .controls(true)
            .input(true)
            .width(200)
            .build();
        ColumnRow<Void> exportRateRow = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("MetricsTab.exportRate.label.name"))
                .description(i18n.t("MetricsTab.exportRate.label.desc"))
                .build())
            .column(exportRate)
            .column(new GTextR(UI.FONT().S, i18n.t("MetricsTab.text.minutes")))
            .build();


        GuiSection exportFilePathSection = exportFilePath(exportFolderPath.toString(), exportFilePath.getFileName().toString());

        // Export file path with folder button
        this.exportFilePathView = new ViewSwitcher(exportFilePathSection, false);
        this.exportFolderButton = new Button(i18n.t("MetricsTab.button.folder.name"), i18n.t("MetricsTab.button.folder.desc", exportFolderPath));
        this.copyExportFileButton = new Button(i18n.t("MetricsTab.button.copy.name"), i18n.t("MetricsTab.button.copy.desc"));

        GuiSection exportButtons = new GuiSection();
        exportButtons.addRightC(0, exportFolderButton);
        exportButtons.addRightC(0, copyExportFileButton);

        ColumnRow<Void> exportFileRow = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("MetricsTab.exportFile.label.name"))
                .description(i18n.t("MetricsTab.exportFile.label.desc"))
                .build())
            .column(exportFilePathView)
            .column(exportButtons)
            .build();

        // Search Bar with uncheck and check buttons
        GuiSection searchBar = new GuiSection();
        this.searchInput = new StringInputSprite(16, UI.FONT().M).placeHolder(i18n.t("MetricsTab.search.input.name"));
        searchBar.addRightC(0, new Input(searchInput));
        this.checkSwitcher = Switcher.<Boolean>builder()
            .menu(ButtonMenu.<Boolean>builder()
                .button(true, new Button(i18n.t("MetricsTab.search.check.name"), i18n.t("MetricsTab.search.check.desc")))
                .button(false, new Button(i18n.t("MetricsTab.search.uncheck.name"), i18n.t("MetricsTab.search.uncheck.desc")))
                .horizontal(true)
                .build())
            .highlight(false)
            .build();
        searchBar.addRightC(10, checkSwitcher);

        // Export stats section
        SortedSet<String> sortedAvailableStats = Sets.sort(availableStats);
        List<ColumnRow<Boolean>> statRows = sortedAvailableStats.stream().map(statName -> {
            Checkbox checkbox = new Checkbox(metricsConfig.getStats().isEmpty() || metricsConfig.getStats().contains(statName));

            this.statsCheckboxes.put(statName, checkbox);
            return ColumnRow.<Boolean>builder()
                .searchTerm(statName)
                .column(Label.builder()
                    .font(UI.FONT().S)
                    .style(Label.Style.NORMAL)
                    .name(statName)
                    .build())
                .column(checkbox)
                .build();
        }).collect(Collectors.toList());

        this.onOffSwitcher = switcher;
        this.onOffSwitcher.switch_(metricsConfig.isEnabled());

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
            .backgroundColor(COLOR.WHITE10)
            .rowPadding(5)
            .columnMargin(5)
            .displayHeight(200)
            .build();

        GHeader statsHeader = new GHeader(i18n.t("MetricsTab.stats.header.name"));
        statsHeader.hoverInfoSet(i18n.t("MetricsTab.stats.header.desc"));

        VerticalLayout.Scalables scalables = Layout.vertical(availableHeight)
            .addDownC(0, configTable)
            .addDownC(15, statsHeader)
            .addDownC(10, searchBar)
            .addDownC(10, new VerticalLayout.Scalable(200, height -> Table.<Boolean>builder()
                .evenOdd(true)
                .displayHeight(height)
                .search(searchInput)
                .rows(statRows)
                .rowPadding(2)
                .columnMargin(5)
                .highlight(true)
                .build()))
            .build(this);

        Table<Boolean> exportStats = scalables.getAs(0);

        // Actions
        checkSwitcher.clickAction(aBoolean -> {
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
    public MetricsConfig getValue() {
        return MetricsConfig.builder()
            .collectionRateSeconds(Range.fromSlider(collectionRate))
            .exportRateMinutes(Range.fromSlider(exportRate))
            .stats(getCheckedStats())
            .enabled(onOffSwitcher.getValue())
            .build();
    }

    @Override
    public void setValue(MetricsConfig metricsConfig) {
        onOffSwitcher.switch_(metricsConfig.isEnabled());
        collectionRate.setValue(metricsConfig.getCollectionRateSeconds().getValue());
        exportRate.setValue(metricsConfig.getExportRateMinutes().getValue());
        setCheckedStats(metricsConfig.getStats());
        valueConsumer.accept(metricsConfig);
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

    protected MetricsTab element() {
        return this;
    }
}
