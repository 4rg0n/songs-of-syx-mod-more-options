package com.github.argon.sos.moreoptions.ui.tab.advanced;

import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.mod.sdk.ui.*;
import com.github.argon.sos.mod.sdk.util.Lists;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.domain.ConfigMeta;
import com.github.argon.sos.moreoptions.ui.MoreOptionsModel;
import com.github.argon.sos.moreoptions.ui.UiFactory;
import com.github.argon.sos.moreoptions.ui.tab.AbstractConfigTab;
import init.paths.PATHS;
import init.sprite.UI.UI;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GText;
import util.gui.misc.GTextR;

/**
 * Contains slider for controlling the intensity of weather effects
 */
public class AdvancedTab extends AbstractConfigTab<ConfigMeta, AdvancedTab> {
    private static final I18nTranslator i18n = ModModule.i18n().get(AdvancedTab.class);

    private final DropDown<Level> logLevelDropDown;
    @Getter
    private final Button resetButton;
    @Getter
    private final Button folderButton;
    @Getter
    private final Button dumpLogsButton;
    @Getter
    private final Button gameLogsFolderButton;
    @Getter
    private final Button copyWorldSeedButton;
    private final ViewSwitcher saveStampView;
    @Getter
    private final Button copySaveStampButton;
    @Getter
    private final Button uiShowRoomButton;
    @Getter
    private final Button modLogsButton;
    @Getter
    private final Checkbox logToFileCheckbox;

    @Getter
    private String saveStamp;

    @Getter
    private final int worldSeed;

    public AdvancedTab(
        String title,
        MoreOptionsModel.Advanced model,
        int availableWidth,
        int availableHeight
    ) {
        super(title, model.getDefaultConfig(), availableWidth, availableHeight);
        this.worldSeed = model.getWorldSeed();
        this.saveStamp = (model.getSaveStamp() != null) ? model.getSaveStamp() : "NONE";

        logLevelDropDown = DropDown.<Level>builder()
            .label(i18n.t("AdvancedTab.dropDown.log.level.name"))
            .description(i18n.t("AdvancedTab.dropDown.log.level.desc"))
            .closeOnSelect(true)
            .menu(Switcher.<Level>builder()
                .menu(UiFactory.buildLogLevelButtonMenu()
                    .maxHeight(500)
                    .sameWidth(true)
                    .build())
                .highlight(true)
                .aktiveKey(model.getLogLevel())
                .build())
            .build();

        logToFileCheckbox = new Checkbox(model.isLogToFile());
        logToFileCheckbox.hoverInfoSet(i18n.t("AdvancedTab.label.file.logging.desc", model.getLogFilePath()));

        this.modLogsButton = new Button(
            i18n.t("AdvancedTab.button.logs.mod.name"),
            i18n.t("AdvancedTab.button.logs.mod.desc", model.getLogFilePath()));

        GuiSection loggingSection = new GuiSection();
        loggingSection.addRightC(0, logLevelDropDown);
        loggingSection.addRightC(10, logToFileCheckbox);
        loggingSection.addRightC(10, modLogsButton);

        ColumnRow<Void> logLevelSelect = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("AdvancedTab.label.logging.name"))
                .description(i18n.t("AdvancedTab.label.logging.desc"))
                .build())
            .column(loggingSection)
            .build();

        this.dumpLogsButton = new Button(
            i18n.t("AdvancedTab.button.logs.dump.name"),
            i18n.t("AdvancedTab.button.logs.dump.desc"));
        this.gameLogsFolderButton = new Button(
            i18n.t("AdvancedTab.button.logs.folder.name"),
            i18n.t("AdvancedTab.button.logs.folder.desc", PATHS.local().LOGS.get().toString()));

        ColumnRow<Void> logFunctions = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("AdvancedTab.label.log.functions.name"))
                .description(i18n.t("AdvancedTab.label.log.functions.desc"))
                .build())
            .column(ButtonMenu.builder()
                .button("dumpLogs", dumpLogsButton)
                .button("gameLogsFolder", gameLogsFolderButton)
                .horizontal(true)
                .maxWidth(500)
                .sameWidth(true)
                .margin(10)
                .build())
            .build();

        this.resetButton = new Button(
            i18n.t("AdvancedTab.button.reset.name"),
            i18n.t("AdvancedTab.button.reset.desc"));
        this.folderButton = new Button(
            i18n.t("AdvancedTab.button.folder.name"),
            i18n.t("AdvancedTab.button.folder.desc"));
        this.uiShowRoomButton = new Button(
            i18n.t("AdvancedTab.button.uiShowRoom.name"),
            i18n.t("AdvancedTab.button.uiShowRoom.desc"));

        ColumnRow<Void> modFunctions = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("AdvancedTab.label.mod.functions.name"))
                .description(i18n.t("AdvancedTab.label.mod.functions.desc"))
                .build())
            .column(ButtonMenu.builder()
                .button("resetMod", resetButton)
                .button("folderMod", folderButton)
                .button("uiShowRoom", uiShowRoomButton)
                .horizontal(true)
                .sameWidth(true)
                .margin(10)
                .build())
            .build();

        this.saveStampView = new ViewSwitcher(saveStamp(saveStamp), false);
        this.copySaveStampButton = new Button(
            i18n.t("AdvancedTab.button.mod.saveStamp.copy.name"),
            i18n.t("AdvancedTab.button.mod.saveStamp.copy.desc"));
        ColumnRow<Void> saveStampRow = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("AdvancedTab.label.mod.saveStamp.name"))
                .description(i18n.t("AdvancedTab.label.mod.saveStamp.desc"))
                .build())
            .column(saveStampView)
            .column(copySaveStampButton)
            .build();

        this.copyWorldSeedButton = new Button(
            i18n.t("AdvancedTab.button.world.seed.copy.name"),
            i18n.t("AdvancedTab.button.world.seed.copy.desc", worldSeed));
        ColumnRow<Void> worldFunctions = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("AdvancedTab.label.world.seed.name"))
                .description(i18n.t("AdvancedTab.label.world.seed.desc"))
                .build())
            .column(new GTextR(UI.FONT().M, String.valueOf(worldSeed)))
            .column(copyWorldSeedButton)
            .build();

        Table<Void> settings = Table.<Void>builder()
            .category(i18n.t("AdvancedTab.header.log.name"), Lists.of(logLevelSelect, logFunctions))
            .category(i18n.t("AdvancedTab.header.mod.name"), Lists.of(modFunctions, saveStampRow))
            .category(i18n.t("AdvancedTab.header.world.name"), Lists.of(worldFunctions))
            .displayHeight(availableHeight)
            .backgroundColor(COLOR.WHITE10)
            .columnMargin(20)
            .evenOdd(true)
            .rowPadding(10)
            .build();

        addDown(5, settings);
    }

    @Override
    public ConfigMeta getValue() {
        return ConfigMeta.builder()
            .logLevel(logLevelDropDown.getValue())
            .logToFile(logToFileCheckbox.selectedIs())
            .build();
    }

    @Override
    public void setValue(ConfigMeta configMeta) {
        logLevelDropDown.setValue(configMeta.getLogLevel());
        logToFileCheckbox.setValue(configMeta.isLogToFile());
    }

    protected AdvancedTab element() {
        return this;
    }

    public void refresh(@Nullable String saveStamp) {
        if (saveStamp != null) {
            this.saveStamp = saveStamp;
            this.saveStampView.set(saveStamp(saveStamp));
        }
    }

    private static GuiSection saveStamp(@Nullable String saveStamp) {
        int maxWidth = 300;
        int height = UI.FONT().S.height() * 3;

        if (saveStamp == null) {
            saveStamp = "NONE";
        }

        GTextR text = new GText(UI.FONT().S, saveStamp)
            .setMaxWidth(maxWidth)
            .r(DIR.W);

        GuiSection section = new GuiSection();
        section.body().setWidth(maxWidth);
        section.body().setHeight(height);
        section.addRightCAbs(0, text);

        return section;
    }

}
