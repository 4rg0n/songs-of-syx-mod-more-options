package com.github.argon.sos.moreoptions.ui.tab.jvm;

import com.github.argon.sos.mod.sdk.data.ByteUnit;
import com.github.argon.sos.mod.sdk.game.jvm.JvmArgsService;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.ui.input.InputArea;
import com.github.argon.sos.mod.sdk.ui.simple.ColorBox;
import com.github.argon.sos.mod.sdk.ui.slider.Slider;
import com.github.argon.sos.mod.sdk.ui.table.ColumnRow;
import com.github.argon.sos.mod.sdk.ui.table.Table;
import com.github.argon.sos.mod.sdk.ui.text.Label;
import com.github.argon.sos.mod.sdk.ui.validation.UiValidation;
import com.github.argon.sos.mod.sdk.ui.validation.UiValidationResult;
import com.github.argon.sos.mod.sdk.util.ByteUtil;
import com.github.argon.sos.mod.sdk.util.OperationSystemUtil;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.ui.model.MoreOptionsUiModel;
import com.github.argon.sos.moreoptions.ui.tab.AbstractConfigTab;
import init.sprite.UI.UI;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import util.gui.misc.GHeader;
import util.gui.misc.GText;

import java.util.List;

public class JvmTab extends AbstractConfigTab<MoreOptionsUiModel.Jvm, JvmTab> implements UiValidation {
    private static final I18nTranslator i18n = ModModule.i18n().get(JvmTab.class);

    @Getter
    private final Slider memoryMinSlider;
    @Getter
    private final Slider memoryMaxSlider;
    @Getter
    private final InputArea jvmArgsInputArea;

    public JvmTab(String title, MoreOptionsUiModel.Jvm defaultConfig, int availableWidth, int availableHeight) {
        super(title, defaultConfig, availableWidth, availableHeight);

        double maxAvailableMemory = ByteUtil.fromBytes(OperationSystemUtil.getTotalMemorySize(), ByteUnit.Unit.MEGABYTE);
        this.memoryMinSlider = Slider.builder()
            .width(600)
            .min(512)
            .max((int) maxAvailableMemory)
            .step(64)
            .valueDisplay(Slider.ValueDisplay.ABSOLUTE)
            .threshold((int) (0.25 * maxAvailableMemory), COLOR.YELLOW100.shade(0.7d))
            .threshold((int) (0.50 * maxAvailableMemory), COLOR.ORANGE100.shade(0.7d))
            .threshold((int) (0.75 * maxAvailableMemory), COLOR.RED100.shade(0.7d))
            .threshold((int) (0.90 * maxAvailableMemory), COLOR.RED2RED)
            .build();
        ColumnRow<Void> memoryMinRow = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("JvmTab.label.jvm.memoryMin.name"))
                .description(i18n.t("JvmTab.label.jvm.memoryMin.desc"))
                .build())
            .column(memoryMinSlider)
             .build();

        this.memoryMaxSlider = Slider.builder()
            .width(600)
            .min(4096)
            .max((int) maxAvailableMemory)
            .step(64)
            .valueDisplay(Slider.ValueDisplay.ABSOLUTE)
            .threshold((int) (0.25 * maxAvailableMemory), COLOR.YELLOW100.shade(0.7d))
            .threshold((int) (0.50 * maxAvailableMemory), COLOR.ORANGE100.shade(0.7d))
            .threshold((int) (0.75 * maxAvailableMemory), COLOR.RED100.shade(0.7d))
            .threshold((int) (0.90 * maxAvailableMemory), COLOR.RED2RED)
            .build();
        ColumnRow<Void> memoryMaxRow = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("JvmTab.label.jvm.memoryMax.name"))
                .description(i18n.t("JvmTab.label.jvm.memoryMax.desc"))
                .build())
            .column(memoryMaxSlider)
            .build();

        this.jvmArgsInputArea = new InputArea(600, 400);
        ColumnRow<Void> jvmArgsRow = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("JvmTab.label.jvm.args.name"))
                .description(i18n.t("JvmTab.label.jvm.args.desc"))
                .build())
            .column(jvmArgsInputArea)
            .build();

        ColorBox disclaimerBox = new ColorBox(COLOR.WHITE25);
        GHeader disclaimerHeader = new GHeader(i18n.t("JvmTab.header.disclaimer"));
        disclaimerBox.addDown(0, disclaimerHeader);
        GText disclaimerText1 = new GText(UI.FONT().S, i18n.t("JvmTab.text.disclaimer.1"));
        disclaimerBox.addDown(5, disclaimerText1);
        GText disclaimerText2= new GText(UI.FONT().S, i18n.t("JvmTab.text.disclaimer.2"));
        disclaimerBox.addDown(0, disclaimerText2);
        GText disclaimerText3 = new GText(UI.FONT().S, i18n.t("JvmTab.text.disclaimer.3", JvmArgsService.JVM_ARGS_LAUNCHER_FILE_PATH));
        disclaimerBox.addDown(0, disclaimerText3);

        disclaimerBox.pad(10);
        addDownC(0, disclaimerBox);

        int tableHeight = availableHeight - disclaimerBox.body().height() - 5;
        Table<Void> settings = Table.<Void>builder()
            .rows(List.of(memoryMinRow, memoryMaxRow, jvmArgsRow))
            .displayHeight(tableHeight)
            .backgroundColor(COLOR.WHITE10)
            .columnMargin(20)
            .evenOdd(true)
            .rowPadding(10)
            .build();
        addDownC(10, settings);
    }

    @Override
    public UiValidationResult validate() {
        UiValidationResult uiValidationResult = new UiValidationResult();

        // min memory must be lower than max memory
        if (memoryMinSlider.getValue() > memoryMaxSlider.getValue()) {
            uiValidationResult.addError(
                memoryMinSlider,
                memoryMinSlider.getClass(),
                i18n.t("JvmTab.validation.memoryMin.higher.memoryMax")
            );
        }

        return uiValidationResult;
    }

    @Override
    protected JvmTab element() {
        return this;
    }

    @Override
    public @Nullable MoreOptionsUiModel.Jvm getValue() {
        ByteUnit minByteUnit = new ByteUnit(memoryMinSlider.getValue(), ByteUnit.Unit.MEGABYTE);
        ByteUnit maxByteUnit = new ByteUnit(memoryMaxSlider.getValue(), ByteUnit.Unit.MEGABYTE);

        return MoreOptionsUiModel.Jvm.builder()
            .minMemoryMb(minByteUnit)
            .maxMemoryMb(maxByteUnit)
            .jvmArgs(jvmArgsInputArea.getValue())
            .build();
    }

    @Override
    public void setValue(MoreOptionsUiModel.Jvm jvmUiModel) {
        memoryMinSlider.setValue((int) jvmUiModel.getMinMemoryMb().value());
        memoryMaxSlider.setValue((int) jvmUiModel.getMaxMemoryMb().value());
        jvmArgsInputArea.setValue(jvmUiModel.getJvmArgs());
    }
}
