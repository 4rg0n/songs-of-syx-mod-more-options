package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.element.*;
import com.github.argon.sos.moreoptions.ui.builder.section.CheckboxesBuilder;
import com.github.argon.sos.moreoptions.util.Lists;
import init.sprite.UI.UI;
import org.jetbrains.annotations.NotNull;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.gui.misc.GHeader;
import util.gui.misc.GText;
import util.gui.misc.GTextR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Collectors;

/**
 * Contains control elements for enabling and disabling game events.
 */
public class MetricsPanel extends GuiSection implements Valuable<MoreOptionsConfig.Metrics, MetricsPanel>, Refreshable<MetricsPanel> {
    private static final Logger log = Loggers.getLogger(MetricsPanel.class);

    private final Toggler<Boolean> onOffToggle;
    private final Slider collectionRate;
    private final Slider exportRate;
    private final UISwitcher exportFilePath;
    private final UISwitcher statsSection;
    private final Map<String, Checkbox> statsCheckboxes = new HashMap<>();

    private UIAction<MetricsPanel> refreshAction = o -> {};
    private UIBiAction<MoreOptionsConfig.Metrics, MetricsPanel> afterSetValueUIAction = (o1, o2)  -> {};

    public MetricsPanel(
        MoreOptionsConfig.Metrics metricsConfig
    ) {
        // todo dict
        Toggler<Boolean> toggler = new Toggler<>(Lists.of(
            Toggler.Info.<Boolean>builder()
                .key(true)
                .title("Started")
                .description("Click to start the collection and export of metrics")
                .build(),
            Toggler.Info.<Boolean>builder()
                .key(false)
                .title("Stopped")
                .description("Click to stop the collection and export of metrics")
                .build()
        ));

        BuildResult<List<GuiSection>, Toggler<Boolean>> onOffToggle = LabeledBuilder.<Toggler<Boolean>>builder().translate(
            LabeledBuilder.Definition.<Toggler<Boolean>>builder()
                .labelDefinition(LabelBuilder.Definition.builder()
                    // todo dict
                    .key("metrics.ui.toggle")
                    .title("Metric Export")
                    .description("Enables or disables the collection and export of metric data.")
                    .build())
                .element(toggler)
                .build()
        ).build();

        BuildResult<List<GuiSection>, Slider> collectionRate = LabeledSliderBuilder.builder()
            .definition(LabeledSliderBuilder.Definition.builder()
                .labelDefinition(LabelBuilder.Definition.builder()
                    // todo dict
                    .key("metrics.ui.collection-rate-seconds")
                    .title("Collection rate seconds")
                    .description("How often data shall be pulled from the game in SECONDS.")
                    .build())
                .sliderDefinition(SliderBuilder.Definition
                    .buildFrom(metricsConfig.getCollectionRateSeconds())
                    .maxWidth(200)
                    .build())
                .build())
            .build();

        BuildResult<List<GuiSection>, Slider> exportRate = LabeledSliderBuilder.builder()
            .definition(LabeledSliderBuilder.Definition.builder()
                .labelDefinition(LabelBuilder.Definition.builder()
                    // todo dict
                    .key("metrics.ui.export-rate-minutes")
                    .title("Export rate minutes")
                    .description("How often data shall be exported as CSV file in MINUTES.")
                    .build())
                .sliderDefinition(SliderBuilder.Definition
                    .buildFrom(metricsConfig.getExportRateMinutes())
                    .maxWidth(200)
                    .build())
                .build())
            .build();
        GuiSection exportFilePathSection = exportFilePath("NO_PATH");

        this.exportFilePath = new UISwitcher(exportFilePathSection, false);
        BuildResult<List<GuiSection>, RENDEROBJ> exportFile = LabeledBuilder.builder().translate(
            LabeledBuilder.Definition.builder()
                .labelDefinition(LabelBuilder.Definition.builder()
                    // todo dict
                    .key("metrics.ui.export-file")
                    .title("Metric Export")
                    .description("Enables or disables the collection and export of metric data.")
                    .build())
                .element(this.exportFilePath)
                .build()
        ).build();

        GuiSection statsSectionPlaceholder = statsSectionPlaceholder();
        this.statsSection = new UISwitcher(statsSectionPlaceholder, false);
        this.onOffToggle = onOffToggle.getInteractable();
        this.collectionRate = collectionRate.getInteractable();
        this.exportRate = exportRate.getInteractable();

        List<ColumnRow> rows = Lists.of(
            onOffToggle.toColumnRow().getResult(),
            exportFile.toColumnRow().getResult(),
            collectionRate.toColumnRow().getResult(),
            exportRate.toColumnRow().getResult()
        );

        Table table = TableBuilder.builder()
            .evenOdd(true)
            .scrollable(false)
            .columnRows(rows)
            .build()
            .getResult();

        addDown(0, table);
        addDown(20, new GHeader("Stats to export"));
        addDown(10, statsSection);
    }

    private GuiSection statsSectionPlaceholder() {
        GuiSection section = new GuiSection();
        GTextR text = new GText(UI.FONT().S, "Metric collection hasn't started yet. Come back later ;)")
            .warnify().r(DIR.W);
        section.body().setHeight(400);
        section.body().setWidth(800);
        section.addC(text, section.body().cX(), section.body().cY());

        return section;
    }

    public void refresh(String exportFilePath, SortedSet<String> keyList, List<String> whiteList) {
        // build stat selection list once
        if (!keyList.isEmpty() && statsCheckboxes.isEmpty()) {
            BuildResult<Table, Map<String, Checkbox>> checkboxes = CheckboxesBuilder.builder()
                .displayHeight(400)
                .definitions(keyList.stream()
                    .collect(Collectors.toMap(
                        s -> s,
                        s -> LabeledCheckboxBuilder.Definition.builder()
                            .labelDefinition(LabelBuilder.Definition.builder()
                                .title(s)
                                .build())
                            .checkboxDefinition(CheckboxBuilder.Definition.builder()
                                .enabled(whiteList.isEmpty() || whiteList.contains(s))
                                .build())
                            .build())))
                .build();

            this.statsCheckboxes.putAll(checkboxes.getInteractable());
            this.statsSection.set(checkboxes.getResult());
        }

        this.exportFilePath.set(exportFilePath(exportFilePath));
    }

    @NotNull
    private static GuiSection exportFilePath(String exportFilePath) {
        int maxWidth = 500;
        int height = UI.FONT().S.height() * 3;

        GTextR text = new GText(UI.FONT().S, exportFilePath)
            .setMaxWidth(maxWidth)
            .r(DIR.W);

        text.hoverInfoSet(exportFilePath);

        GuiSection section = new GuiSection();
        section.body().setWidth(maxWidth);
        section.body().setHeight(height);
        section.addRightCAbs(0, text);

        return section;
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
        afterSetValueUIAction.accept(metricsConfig, this);
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
    public void onRefresh(UIAction<MetricsPanel> refreshUIAction) {
        this.refreshAction = refreshUIAction;
    }

    @Override
    public void onAfterSetValue(UIBiAction<MoreOptionsConfig.Metrics, MetricsPanel> afterSetValueUIAction) {
        this.afterSetValueUIAction = afterSetValueUIAction;
    }
}
