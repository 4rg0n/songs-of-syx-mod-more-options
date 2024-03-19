package com.github.argon.sos.moreoptions.ui.panel.advanced;

import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.UiFactory;
import com.github.argon.sos.moreoptions.ui.panel.AbstractConfigPanel;
import com.github.argon.sos.moreoptions.util.Lists;
import init.paths.PATHS;
import lombok.Getter;

/**
 * Contains slider for controlling the intensity of weather effects
 */
public class AdvancedPanel extends AbstractConfigPanel<Level, AdvancedPanel> {
    private static final Logger log = Loggers.getLogger(AdvancedPanel.class);
    private static final I18n i18n = I18n.get(AdvancedPanel.class);

    @Getter
    private final Button resetButton;
    @Getter
    private final Button folderButton;
    @Getter
    private final Button dumpLogsButton;
    @Getter
    private final Button gameLogsFolderButton;
    private final DropDown<Level> dropDown;

    public AdvancedPanel(
        String title,
        Level logLevel,
        Level defaultLogLevel,
        int availableWidth,
        int availableHeight
    ) {
        super(title, defaultLogLevel, availableWidth, availableHeight);
        dropDown = DropDown.<Level>builder()
            .label(i18n.t("AdvancedPanel.dropDown.log.level.name"))
            .description(i18n.t("AdvancedPanel.dropDown.log.level.desc"))
            .closeOnSelect(true)
            .menu(Toggler.<Level>builder()
                .menu(UiFactory.buildLogLevelButtonMenu()
                    .sameWidth(true)
                    .build())
                .highlight(true)
                .aktiveKey(logLevel)
                .build())
            .build();

        ColumnRow<Void> logLevelSelect = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("AdvancedPanel.label.log.level.name"))
                .description(i18n.t("AdvancedPanel.label.log.level.desc"))
                .build())
            .column(dropDown)
            .margin(20)
            .build();

        this.dumpLogsButton = new Button(
            i18n.t("AdvancedPanel.button.logs.dump.name"),
            i18n.t("AdvancedPanel.button.logs.dump.desc"));
        this.gameLogsFolderButton = new Button(
            i18n.t("AdvancedPanel.button.logs.folder.name"),
            i18n.t("AdvancedPanel.button.logs.folder.desc", PATHS.local().LOGS.get().toString()));

        ColumnRow<Void> logFunctions = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("AdvancedPanel.label.log.functions.name"))
                .description(i18n.t("AdvancedPanel.label.log.functions.desc"))
                .build())
            .column(ButtonMenu.builder()
                .button("dumpLogs", dumpLogsButton)
                .button("gameLogsFolder", gameLogsFolderButton)
                .horizontal(true)
                .sameWidth(true)
                .margin(10)
                .build())
            .margin(20)
            .build();

        this.resetButton = new Button(
            i18n.t("AdvancedPanel.button.reset.name"),
            i18n.t("AdvancedPanel.button.reset.desc"));
        this.folderButton = new Button(
            i18n.t("AdvancedPanel.button.folder.name"),
            i18n.t("AdvancedPanel.button.folder.desc"));

        ColumnRow<Void> modFunctions = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("AdvancedPanel.label.mod.functions.name"))
                .description(i18n.t("AdvancedPanel.label.mod.functions.desc"))
                .build())
            .column(ButtonMenu.builder()
                .button("resetMod", resetButton)
                .button("folderMod", folderButton)
                .horizontal(true)
                .sameWidth(true)
                .margin(10)
                .build())
            .margin(20)
            .build();

        Table<Void> settings = Table.<Void>builder()
            .category(i18n.t("AdvancedPanel.header.log.name"), Lists.of(logLevelSelect, logFunctions))
            .category(i18n.t("AdvancedPanel.header.mod.name"), Lists.of(modFunctions))
            .evenOdd(true)
            .displayHeight(availableHeight)
            .columnMargin(5)
            .rowPadding(10)
            .build();

        addDown(5, settings);
    }

    @Override
    public Level getValue() {
        return dropDown.getValue();
    }

    @Override
    public void setValue(Level logLevel) {
        dropDown.setValue(logLevel);
    }

    protected AdvancedPanel element() {
        return this;
    }
}
