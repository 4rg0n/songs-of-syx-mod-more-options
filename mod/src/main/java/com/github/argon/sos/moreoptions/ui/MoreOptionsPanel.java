package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.game.action.Refreshable;
import com.github.argon.sos.mod.sdk.game.action.Showable;
import com.github.argon.sos.mod.sdk.game.action.Valuable;
import com.github.argon.sos.mod.sdk.game.action.VoidAction;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.ui.*;
import com.github.argon.sos.mod.sdk.util.Lists;
import com.github.argon.sos.mod.sdk.util.Maps;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.ui.tab.AbstractConfigTab;
import com.github.argon.sos.moreoptions.ui.tab.advanced.AdvancedTab;
import com.github.argon.sos.moreoptions.ui.tab.boosters.BoostersTab;
import com.github.argon.sos.moreoptions.ui.tab.events.EventsTab;
import com.github.argon.sos.moreoptions.ui.tab.metrics.MetricsTab;
import com.github.argon.sos.moreoptions.ui.tab.races.RacesTab;
import com.github.argon.sos.moreoptions.ui.tab.sounds.SoundsTab;
import com.github.argon.sos.moreoptions.ui.tab.weather.WeatherTab;
import game.VERSION;
import init.paths.ModInfo;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GText;

import java.util.List;


/**
 * Main window containing all other UI elements.
 * Will pop up in the middle of the game and pauses the game.
 */
public class MoreOptionsPanel extends GuiSection implements
    Showable,
    Refreshable,
    Valuable<MoreOptionsV5Config> {

    private static final I18nTranslator i18n = ModModule.i18n().get(WeatherTab.class);

    private final ConfigStore configStore;
    @Getter
    private final EventsTab eventsTab;
    @Getter
    private final SoundsTab soundsTab;
    @Getter
    private final WeatherTab weatherTab;
    @Getter
    private final BoostersTab boostersTab;
    @Getter
    private final MetricsTab metricsTab;
    @Getter
    private final RacesTab racesTab;
    @Getter
    private final AdvancedTab advancedTab;

    @Getter
    private final Button cancelButton;
    @Getter
    private final Button defaultTabButton;
    @Getter
    private final Button applyButton;
    @Getter
    private final Button undoButton;
    @Getter
    private final Button okButton;
    @Getter
    private final Button moreButton;
    @Getter
    private final Button defaultAllButton;
    @Getter
    private final Button reloadButton;
    @Getter
    private final Button shareButton;
    @Getter
    private final ButtonMenu<String> moreButtonMenu;
    @Getter
    private final Tabulator<String, AbstractConfigTab<?, ?>, Void> tabulator;

    @Setter
    @Accessors(fluent = true, chain = false)
    private VoidAction showAction = () -> {};
    @Setter
    @Accessors(fluent = true, chain = false)
    private VoidAction refreshAction = () -> {};

    private double updateTimerSeconds = 0d;
    private final static int UPDATE_INTERVAL_SECONDS = 1;


    /**
     * Builds the UI with given config
     */
    @Builder
    public MoreOptionsPanel(
        MoreOptionsModel moreOptionsModel,
        ConfigStore configStore,
        int availableWidth,
        int availableHeight
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

        this.defaultTabButton = new Button(
            i18n.t("MoreOptionsPanel.button.default.tab.name"),
            i18n.t("MoreOptionsPanel.button.default.tab.desc"));
        footer.addRightC(10, defaultTabButton);

        this.moreButton = new Button(
            i18n.t("MoreOptionsPanel.button.more.name"),
            COLOR.WHITE15,
            i18n.t("MoreOptionsPanel.button.more.desc"));
        footer.addRightC(50, moreButton);

        String modVersion = "NO_VER";
        ModInfo modInfo = moreOptionsModel.getModInfo();
        if (modInfo != null) {
            modVersion = modInfo.version;
        }

        MoreOptionsV5Config config = moreOptionsModel.getConfig();
        footer.addRightC(50, versions(config.getVersion(), modVersion));

        this.applyButton = new Button(i18n.t("MoreOptionsPanel.button.apply.name"), i18n.t("MoreOptionsPanel.button.apply.desc"));
        applyButton.activeSet(false);
        footer.addRightC(50, applyButton);

        this.okButton = new Button(i18n.t("MoreOptionsPanel.button.ok.name"), i18n.t("MoreOptionsPanel.button.ok.desc"));
        footer.addRightC(10, okButton);

        // More Button Menu
        this.defaultAllButton = new Button(
            i18n.t("MoreOptionsPanel.button.default.all.name"),
            i18n.t("MoreOptionsPanel.button.default.all.desc"));
        this.reloadButton = new Button(
            i18n.t("MoreOptionsPanel.button.reload.name"),
            i18n.t("MoreOptionsPanel.button.reload.desc"));
        this.shareButton = new Button(
            i18n.t("MoreOptionsPanel.button.share.name"),
            i18n.t("MoreOptionsPanel.button.share.desc"));

        List<Button> buttons = Lists.of(
            defaultAllButton,
            shareButton,
            reloadButton
        );
        this.moreButtonMenu = ButtonMenu.ButtonMenuBuilder.fromList(buttons);
        this.moreButton.clickActionSet(() -> {
            ModSdkModule.gameApis().ui().popup().show(this.moreButtonMenu, this.moreButton);
        });

        HorizontalLine horizontalLine = new HorizontalLine(footer.body().width(), 20, 1);
        availableHeight = availableHeight - footer.body().height() - horizontalLine.body().height() - 40;

        soundsTab = new SoundsTab(i18n.t("MoreOptionsPanel.tab.sounds.name"), moreOptionsModel.getSounds(), availableWidth, availableHeight);
        eventsTab = new EventsTab(i18n.t("MoreOptionsPanel.tab.events.name"), moreOptionsModel.getEvents(), availableWidth, availableHeight);
        weatherTab = new WeatherTab(i18n.t("MoreOptionsPanel.tab.weather.name"), moreOptionsModel.getWeather(), availableWidth, availableHeight);
        boostersTab = new BoostersTab(i18n.t("MoreOptionsPanel.tab.boosters.name"), moreOptionsModel.getBoosters(), availableWidth, availableHeight);
        metricsTab = new MetricsTab(i18n.t("MoreOptionsPanel.tab.metrics.name"), moreOptionsModel.getMetrics(), availableWidth, availableHeight);
        racesTab = new RacesTab(i18n.t("MoreOptionsPanel.tab.races.name"), moreOptionsModel.getRaces(), availableWidth, availableHeight);
        advancedTab = new AdvancedTab(i18n.t("MoreOptionsPanel.tab.advanced.name"), moreOptionsModel.getAdvanced(), availableWidth, availableHeight);

        tabulator = Tabulator.<String, AbstractConfigTab<?, ?>, Void>builder()
            .tabs(Maps.ofLinked(
                "sounds", soundsTab,
                "events", eventsTab,
                "weather", weatherTab,
                "boosters", boostersTab,
                "metrics", metricsTab,
                "races", racesTab,
                "advanced", advancedTab
            ))
            .tabMenu(Switcher.<String>builder()
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
                    .button("advanced", new Button(
                        i18n.t("MoreOptionsPanel.tab.advanced.name"),
                        i18n.t("MoreOptionsPanel.tab.advanced.desc")))
                    .sameWidth(true)
                    .maxWidth(availableWidth)
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
    public MoreOptionsV5Config getValue() {
        return MoreOptionsV5Config.builder()
            .logLevel(advancedTab.getValue())
            .events(eventsTab.getValue())
            .sounds(soundsTab.getValue())
            .weather(weatherTab.getValue())
            .boosters(boostersTab.getValue())
            .metrics(metricsTab.getValue())
            .races(racesTab.getValue())
            .build();
    }

    @Override
    public void setValue(@Nullable MoreOptionsV5Config config) {
        if (config == null) {
            return;
        }

        eventsTab.setValue(config.getEvents());
        soundsTab.setValue(config.getSounds());
        weatherTab.setValue(config.getWeather());
        boostersTab.setValue(config.getBoosters());
        metricsTab.setValue(config.getMetrics());
        racesTab.setValue(config.getRaces());
        advancedTab.setValue(config.getLogLevel());
    }

    /**
     * @return whether panel configuration is different from {@link ConfigStore#getCurrentConfig()} ()}
     */
    public boolean isDirty() {
        MoreOptionsV5Config currentConfig = configStore.getCurrentConfig();
        return isDirty(currentConfig);
    }

    public boolean isDirty(@Nullable MoreOptionsV5Config config) {
        MoreOptionsV5Config uiConfig = getValue();

        if (uiConfig == null) {
            return false;
        }

        return !uiConfig.equals(config);
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
        showAction.accept();
    }

    @Override
    public void refresh() {
        refreshAction.accept();
    }
}
