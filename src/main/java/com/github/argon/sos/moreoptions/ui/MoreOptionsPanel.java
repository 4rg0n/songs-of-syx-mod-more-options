package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.game.api.GameUiApi;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.ui.panel.*;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.Maps;
import game.VERSION;
import init.paths.ModInfo;
import init.paths.PATHS;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.gui.misc.GText;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Main window containing all other UI elements.
 * Will pop up in the middle of the game and pauses the game.
 */
public class MoreOptionsPanel extends GuiSection implements
    Showable<MoreOptionsPanel>,
    Refreshable<MoreOptionsPanel>,
    Valuable<MoreOptionsV2Config, MoreOptionsPanel> {

    private static final I18n i18n = I18n.get(WeatherPanel.class);

    private final ConfigStore configStore;
    @Getter
    private final EventsPanel eventsPanel;
    @Getter
    private final SoundsPanel soundsPanel;
    @Getter
    private final WeatherPanel weatherPanel;
    @Getter
    private final BoostersPanel boostersPanel;
    @Getter
    private final MetricsPanel metricsPanel;
    @Getter
    private final RacesPanel racesPanel;

    @Getter
    private final Button cancelButton;
    @Getter
    private final Button defaultButton;
    @Getter
    private final Button applyButton;
    @Getter
    private final Button undoButton;
    @Getter
    private final Button okButton;
    @Getter
    private final Button folderButton;
    @Getter
    private final Button moreButton;
    @Getter
    private final Button reloadButton;
    @Getter
    private final Button shareButton;
    @Getter
    private final Button resetButton;
    @Getter
    private final Button dumpLogsButton;
    @Getter
    private final Button gameLogsFolderButton;
    @Getter
    private final ButtonMenu<String> moreButtonMenu;
    @Getter
    private final Tabulator<String, RENDEROBJ, GuiSection> tabulator;

    private Action<MoreOptionsPanel> showAction = o -> {};
    private Action<MoreOptionsPanel> refreshAction = o -> {};

    private double updateTimerSeconds = 0d;
    private final static int UPDATE_INTERVAL_SECONDS = 1;


    /**
     * Builds the UI with given config
     */
    @Builder
    public MoreOptionsPanel(
        MoreOptionsV2Config config,
        ConfigStore configStore,
        List<BoostersPanel.Entry> boosterEntries,
        Map<String, List<RacesPanel.Entry>> raceEntries,
        Set<String> availableStats,
        Path exportFolder,
        Path exportFile,
        int availableWidth,
        int availableHeight,
        @Nullable ModInfo modInfo
    ) {
        this.configStore = configStore;

        GuiSection footer = new GuiSection();
        this.cancelButton = new Button(
            i18n.t("MoreOptionsPanel.button.cancel.name"),
            i18n.t("MoreOptionsPanel.button.cancel.desc"));
        footer.addRightC(0, cancelButton);

        this.undoButton = new Button(
            i18n.t("MoreOptionsPanel.button.undo.name"),
            i18n.t("MoreOptionsPanel.button.undo.desc"));
        undoButton.activeSet(false);
        footer.addRightC(10, undoButton);

        this.moreButton = new Button(
            i18n.t("MoreOptionsPanel.button.more.name"),
            COLOR.WHITE15,
            i18n.t("MoreOptionsPanel.button.more.desc"));
        footer.addRightC(50, moreButton);

        String modVersion = "NO_VER";
        if (modInfo != null) {
            modVersion = modInfo.version;
        }

        footer.addRightC(50, versions(config.getVersion(), modVersion));

        this.applyButton = new Button(i18n.t("MoreOptionsPanel.button.apply.name"), i18n.t("MoreOptionsPanel.button.apply.desc"));
        applyButton.activeSet(false);
        footer.addRightC(50, applyButton);

        this.okButton = new Button(i18n.t("MoreOptionsPanel.button.ok.name"), i18n.t("MoreOptionsPanel.button.ok.desc"));
        footer.addRightC(10, okButton);

        // More Button Menu
        this.defaultButton = new Button(
            i18n.t("MoreOptionsPanel.button.default.name"),
            i18n.t("MoreOptionsPanel.button.default.desc"));
        this.reloadButton = new Button(
            i18n.t("MoreOptionsPanel.button.reload.name"),
            i18n.t("MoreOptionsPanel.button.reload.desc"));
        this.shareButton = new Button(
            i18n.t("MoreOptionsPanel.button.share.name"),
            i18n.t("MoreOptionsPanel.button.share.desc"));
        this.folderButton = new Button(
            i18n.t("MoreOptionsPanel.button.folder.name"),
            i18n.t("MoreOptionsPanel.button.folder.desc"));
        this.dumpLogsButton = new Button(
            i18n.t("MoreOptionsPanel.button.logs.dump.name"),
            i18n.t("MoreOptionsPanel.button.logs.dump.desc"));
        this.gameLogsFolderButton = new Button(
            i18n.t("MoreOptionsPanel.button.logs.folder.name"),
            i18n.t("MoreOptionsPanel.button.logs.folder.desc", PATHS.local().LOGS.get().toString()));
        this.resetButton = new Button(
            i18n.t("MoreOptionsPanel.button.reset.name"),
            i18n.t("MoreOptionsPanel.button.reset.desc"));

        List<Button> buttons = Lists.of(
            defaultButton,
            shareButton,
            folderButton,
            reloadButton,
            dumpLogsButton,
            gameLogsFolderButton,
            resetButton
        );
        this.moreButtonMenu = ButtonMenu.ButtonMenuBuilder.fromList(buttons);
        this.moreButton.clickActionSet(() -> {
            GameUiApi.getInstance().popup().show(this.moreButtonMenu, this.moreButton);
        });

        HorizontalLine horizontalLine = new HorizontalLine(footer.body().width(), 20, 1);
        availableHeight = availableHeight - footer.body().height() - horizontalLine.body().height() - 40;

        soundsPanel = new SoundsPanel(config.getSounds(), availableWidth, availableHeight);
        eventsPanel = new EventsPanel(config.getEvents(), availableWidth, availableHeight);
        weatherPanel = new WeatherPanel(config.getWeather(), availableWidth, availableHeight);
        boostersPanel = new BoostersPanel(boosterEntries, availableWidth, availableHeight);
        metricsPanel = new MetricsPanel(config.getMetrics(), availableStats, exportFolder, exportFile, availableWidth, availableHeight);
        racesPanel = new RacesPanel(raceEntries, availableWidth, availableHeight);

        tabulator = Tabulator.<String, RENDEROBJ, GuiSection>builder()
            .tabs(Maps.ofLinked(
                "sounds", soundsPanel,
                "events", eventsPanel,
                "weather", weatherPanel,
                "boosters", boostersPanel,
                "metrics", metricsPanel,
                "races", racesPanel
            ))
            .tabMenu(Toggler.<String>builder()
                .menu(ButtonMenu.<String>builder()
                    .button("sounds", new Button(
                        i18n.t("MoreOptionsPanel.tab.sounds.name"),
                        i18n.t("MoreOptionsPanel.tab.sounds.desc")))
                    .button("events", new Button(
                        i18n.t("MoreOptionsPanel.tab.events.name"),
                        i18n.t("MoreOptionsPanel.tab.events.desc")))
                    .button("weather", new Button(
                        i18n.t("MoreOptionsPanel.tab.weather.name"),
                        i18n.t("MoreOptionsPanel.tab.weather.desc")))
                    .button("boosters", new Button(
                        i18n.t("MoreOptionsPanel.tab.boosters.name"),
                        i18n.t("MoreOptionsPanel.tab.boosters.desc")))
                    .button("metrics", new Button(
                        i18n.t("MoreOptionsPanel.tab.metrics.name"),
                        i18n.t("MoreOptionsPanel.tab.metrics.desc")))
                    .button("races", new Button(
                        i18n.t("MoreOptionsPanel.tab.races.name"),
                        i18n.t("MoreOptionsPanel.tab.races.desc")))
                    .sameWidth(true)
                    .horizontal(true)
                    .margin(21)
                    .spacer(true)
                    .buttonColor(COLOR.WHITE25)
                    .build())
                .highlight(true)
                .build())
            .center(true)
            .build();

        addDownC(0, tabulator);
        addDownC(0, horizontalLine);
        addDownC(20, footer);
        addDownC(0, new Spacer(body().width(), 20));
    }

    @Override
    public MoreOptionsV2Config getValue() {
        return MoreOptionsV2Config.builder()
                .events(eventsPanel.getValue())
                .sounds(soundsPanel.getValue())
                .weather(weatherPanel.getValue())
                .boosters(boostersPanel.getValue())
                .metrics(metricsPanel.getValue())
                .races(racesPanel.getValue())
                .build();
    }

    @Override
    public void setValue(MoreOptionsV2Config config) {
        eventsPanel.setValue(config.getEvents());
        soundsPanel.setValue(config.getSounds());
        weatherPanel.setValue(config.getWeather());
        boostersPanel.setValue(config.getBoosters());
        metricsPanel.setValue(config.getMetrics());
        racesPanel.setValue(config.getRaces());
    }

    /**
     * @return whether panel configuration is different from {@link ConfigStore#getCurrentConfig()} ()}
     */
    public boolean isDirty() {
        MoreOptionsV2Config config = getValue();

        if (config == null) {
            return false;
        }

        MoreOptionsV2Config currentConfig = configStore.getCurrentConfig();
        return !currentConfig.equals(config);
    }

   private GuiSection versions(int configVersionNumber, String modVersionString) {
        COLOR color = COLOR.WHITE50;
        GuiSection versions = new GuiSection();

        GText gameVersion = new GText(UI.FONT().S, "Game Version: " + VERSION.VERSION_STRING);
        gameVersion.color(color);

        GText modVersion = new GText(UI.FONT().S, "Mod Version: " + modVersionString);
        modVersion.color(color);

        GText configVersion = new GText(UI.FONT().S, "Config Version: " + configVersionNumber);
        configVersion.color(color);

        versions.addDown(0, gameVersion);
        versions.addDown(2, modVersion);
        versions.addDown(2, configVersion);

        return versions;
    }

    @Override
    public void render(SPRITE_RENDERER r, float seconds) {
        updateTimerSeconds += seconds;

        // check for changes in config
        if (updateTimerSeconds >= UPDATE_INTERVAL_SECONDS) {
            updateTimerSeconds = 0d;

            if (applyButton != null) applyButton.activeSet(isDirty());
            if (undoButton != null) undoButton.activeSet(isDirty());
        }
        super.render(r, seconds);
    }

    @Override
    public void show() {
        showAction.accept(this);
    }

    @Override
    public void showAction(Action<MoreOptionsPanel> showAction) {
        this.showAction = showAction;
    }

    @Override
    public void refresh() {
        refreshAction.accept(this);
    }

    @Override
    public void refreshAction(Action<MoreOptionsPanel> refreshAction) {
        this.refreshAction = refreshAction;
    }
}
