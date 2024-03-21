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
import init.sprite.UI.UI;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GText;
import util.gui.misc.GTextR;

/**
 * Contains slider for controlling the intensity of weather effects
 */
public class AdvancedPanel extends AbstractConfigPanel<Level, AdvancedPanel> {
    private static final Logger log = Loggers.getLogger(AdvancedPanel.class);
    private static final I18n i18n = I18n.get(AdvancedPanel.class);

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
    private final UISwitcher saveStampView;
    @Getter
    private final Button copySaveStampButton;

    @Getter
    private String saveStamp;

    @Getter
    private final int worldSeed;


    public AdvancedPanel(
        String title,
        String saveStamp,
        int worldSeed,
        Level logLevel,
        Level defaultLogLevel,
        int availableWidth,
        int availableHeight
    ) {
        super(title, defaultLogLevel, availableWidth, availableHeight);
        this.worldSeed = worldSeed;
        this.saveStamp = saveStamp;

        logLevelDropDown = DropDown.<Level>builder()
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
            .column(logLevelDropDown)
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
            .build();

        this.saveStampView = new UISwitcher(saveStamp(saveStamp), false);
        this.copySaveStampButton = new Button(
            i18n.t("AdvancedPanel.button.mod.saveStamp.copy.name"),
            i18n.t("AdvancedPanel.button.mod.saveStamp.copy.desc"));
        ColumnRow<Void> saveStampRow = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("AdvancedPanel.label.mod.saveStamp.name"))
                .description(i18n.t("AdvancedPanel.label.mod.saveStamp.desc"))
                .build())
            .column(saveStampView)
            .column(copySaveStampButton)
            .build();

        this.copyWorldSeedButton = new Button(
            i18n.t("AdvancedPanel.button.world.seed.copy.name"),
            i18n.t("AdvancedPanel.button.world.seed.copy.desc", worldSeed));
        ColumnRow<Void> worldFunctions = ColumnRow.<Void>builder()
            .column(Label.builder()
                .name(i18n.t("AdvancedPanel.label.world.seed.name"))
                .description(i18n.t("AdvancedPanel.label.world.seed.desc"))
                .build())
            .column(new GTextR(UI.FONT().M, String.valueOf(worldSeed)))
            .column(copyWorldSeedButton)
            .build();

        Table<Void> settings = Table.<Void>builder()
            .category(i18n.t("AdvancedPanel.header.log.name"), Lists.of(logLevelSelect, logFunctions))
            .category(i18n.t("AdvancedPanel.header.mod.name"), Lists.of(modFunctions, saveStampRow))
            .category(i18n.t("AdvancedPanel.header.world.name"), Lists.of(worldFunctions))
            .displayHeight(availableHeight)
            .columnMargin(20)
            .evenOdd(true)
            .rowPadding(10)
            .build();

        addDown(5, settings);
    }

    @Override
    public Level getValue() {
        return logLevelDropDown.getValue();
    }

    @Override
    public void setValue(Level logLevel) {
        logLevelDropDown.setValue(logLevel);
    }

    protected AdvancedPanel element() {
        return this;
    }

    public void refresh(@Nullable String saveStamp) {
        if (saveStamp != null) {
            this.saveStamp = saveStamp;
            this.saveStampView.set(saveStamp(saveStamp));
        }
    }

    private static GuiSection saveStamp(String saveStamp) {
        int maxWidth = 300;
        int height = UI.FONT().S.height() * 3;

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
