package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.element.*;
import com.github.argon.sos.moreoptions.ui.builder.section.CheckboxesBuilder;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.UiUtil;
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
import java.util.stream.Collectors;

/**
 * Contains control elements for enabling and disabling game events.
 */
public class MetricsPanel extends GuiSection implements Valuable<MoreOptionsConfig.Metrics>, Refreshable<MetricsPanel> {

    private static final Logger log = Loggers.getLogger(MetricsPanel.class);
    private final Toggler<Boolean> onOffToggle;
    private final Slider collectionRate;
    private final Slider exportRate;
    private final ClickSwitch exportFilePath;
    private final ClickSwitch statsSection;

    private final Map<String, Checkbox> statsCheckboxes = new HashMap<>();

    private Action<MetricsPanel> refreshAction = o -> {};

    public MetricsPanel(
        MoreOptionsConfig.Metrics metricsConfig
    ) {
        // todo dict
        Toggler<Boolean> toggler = new Toggler<>(Lists.of(
            Toggler.Info.<Boolean>builder()
                .key(true)
                .title("Started")
                .description("")
                .build(),
            Toggler.Info.<Boolean>builder()
                .key(false)
                .title("Stopped")
                .description("")
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
        this.exportFilePath = new ClickSwitch(exportFilePathSection);

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
        this.statsSection = new ClickSwitch(statsSectionPlaceholder);
        this.onOffToggle = onOffToggle.getInteractable();
        this.collectionRate = collectionRate.getInteractable();
        this.exportRate = exportRate.getInteractable();

        addDown(0, onOffToggle.toGridRow().getResult());
        addDown(5, collectionRate.toGridRow().getResult());
        addDown(5, exportRate.toGridRow().getResult());
        addDown(5, exportFile.toGridRow().getResult());
        addDown(20, new GHeader("Stats to export"));
        addDown(10, statsSection);
    }

    private GuiSection statsSectionPlaceholder() {
        GuiSection section = new GuiSection();
        GTextR text = new GText(UI.FONT().S, "Metric collection hasn't started yet.").r(DIR.W);
        section.body().setHeight(400);
        section.body().setWidth(800);
        section.addC(text, section.body().cX(), section.body().cY());

        return section;
    }

    public void refresh(String exportFilePath, List<String> keyList, List<String> whiteList) {
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

        GTextR text = new GText(UI.FONT().S, exportFilePath)
            .setMaxWidth(maxWidth)
            .r(DIR.W);
        text.hoverInfoSet(exportFilePath);

        GuiSection section = UiUtil.toGuiSection(text);
        section.body().setWidth(maxWidth);

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

    private List<String> getCheckedStats() {
        return statsCheckboxes.entrySet().stream()
            .filter(entry -> {
                Checkbox checkbox = entry.getValue();
                return checkbox.selectedIs();
            })
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    @Override
    public void setValue(MoreOptionsConfig.Metrics metricsConfig) {
        // todo
    }

    @Override
    public void refresh() {
        refreshAction.accept(this);
    }

    @Override
    public void onRefresh(Action<MetricsPanel> refreshAction) {
        this.refreshAction = refreshAction;
    }
}
