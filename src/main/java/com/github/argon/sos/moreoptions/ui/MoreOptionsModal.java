package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.ConfigUtil;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.HorizontalLine;
import com.github.argon.sos.moreoptions.game.ui.Toggler;
import com.github.argon.sos.moreoptions.ui.panel.BoostersPanel;
import com.github.argon.sos.moreoptions.ui.panel.EventsPanel;
import com.github.argon.sos.moreoptions.ui.panel.SoundsPanel;
import com.github.argon.sos.moreoptions.ui.panel.WeatherPanel;
import com.github.argon.sos.moreoptions.util.MapUtil;
import game.VERSION;
import init.paths.ModInfo;
import init.sprite.UI.UI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GText;

import java.util.List;

/**
 * Window containing all other UI elements.
 * Will pop up in the middle of the game and pauses the game.
 *
 * FIXME BUG? hovering over elements behind the window will trigger ui elements beneath e.g. showing tooltips from citizens
 */
@RequiredArgsConstructor
public class MoreOptionsModal extends GuiSection {

    @Getter
    private final ConfigStore configStore;
    private final ModInfo modInfo;

    private EventsPanel eventsPanel;
    private  SoundsPanel soundsPanel;
    private  WeatherPanel weatherPanel;

    private  BoostersPanel boostersPanel;

    @Getter
    private Button cancelButton;
    @Getter
    private Button resetButton;

    @Getter
    private Button applyButton;

    @Getter
    private Button undoButton;

    @Getter
    private Button okButton;

    @Getter
    private Button folderButton;

    private double updateTimerSeconds = 0d;
    private final static int UPDATE_INTERVAL_SECONDS = 1;

    /**
     * Builds the UI with given config
     */
    public void init(MoreOptionsConfig config, List<BoostersPanel.Entry> boosterEntries) {
        clear();
        soundsPanel = new SoundsPanel(config.getSoundsAmbience(), config.getSoundsSettlement(), config.getSoundsRoom());
        eventsPanel = new EventsPanel(config.getEventsSettlement(), config.getEventsWorld(), config.getEventsChance());
        weatherPanel = new WeatherPanel(config.getWeather());
        boostersPanel = new BoostersPanel(boosterEntries);

        Toggler<Void> toggler = new Toggler<>(MapUtil.ofLinked(
            Toggler.Info.builder()
                .key("sounds")
                .title("Sounds")
                .description("Tune the volume of various sounds.")
                .build(), soundsPanel,
            Toggler.Info.builder()
                .key("events")
                .title("Events")
                .description("Toggle and tune events.")
                .build(), eventsPanel,
            Toggler.Info.builder()
                .key("weather")
                .title("Weather")
                .description("Influence weather effects.")
                .build(), weatherPanel,
            Toggler.Info.builder()
                .key("boosters")
                .title("Boosters")
                .description("Increase or decrease various bonuses.")
                .build(), boostersPanel
        ), DIR.S, 20, false);

        GuiSection left = new GuiSection();
        left.addDownC(0, toggler);

        GuiSection right = new GuiSection();
        GuiSection version = versions(config.getVersion());
        right.addDownC(0, version);

        GuiSection container = new GuiSection();
        container.addRight(0, left);
        container.addRight(0, right);
        addDownC(20, container);

        HorizontalLine horizontalLine = new HorizontalLine(body().width(), 14, 1);
        addDownC(20, horizontalLine);

        GuiSection footer = footer();
        addDownC(20, footer);
    }

    public MoreOptionsConfig getConfig() {
        MoreOptionsConfig defaultConfig = configStore.getDefaultConfig();
        MoreOptionsConfig currentConfig = configStore.getCurrentConfig()
                .orElse(defaultConfig);


        return MoreOptionsConfig.builder()
                .eventsSettlement(eventsPanel.getSettlementEventsConfig())
                .eventsWorld(eventsPanel.getWorldEventsConfig())
                .eventsChance(ConfigUtil.mergeIntegerIntoNewRange(eventsPanel.getEventsChanceConfig(), currentConfig.getEventsChance()))
                .soundsAmbience(ConfigUtil.mergeIntegerIntoNewRange(soundsPanel.getSoundsAmbienceConfig(), currentConfig.getSoundsAmbience()))
                .soundsSettlement(ConfigUtil.mergeIntegerIntoNewRange(soundsPanel.getSoundsSettlementConfig(), currentConfig.getSoundsSettlement()))
                .soundsRoom(ConfigUtil.mergeIntegerIntoNewRange(soundsPanel.getSoundsRoomConfig(), currentConfig.getSoundsRoom()))
                .weather(ConfigUtil.mergeIntegerIntoNewRange(weatherPanel.getConfig(), currentConfig.getWeather()))
                .boosters(ConfigUtil.mergeIntoNewRange(boostersPanel.getConfig(), currentConfig.getBoosters()))
                .build();
    }


    public void applyConfig(MoreOptionsConfig config) {
        eventsPanel.applyConfig(
            config.getEventsSettlement(),
            config.getEventsWorld(),
            ConfigUtil.extract(config.getEventsChance())
        );
        soundsPanel.applyConfig(
            ConfigUtil.extract(config.getSoundsAmbience()),
            ConfigUtil.extract(config.getSoundsSettlement()),
            ConfigUtil.extract(config.getSoundsRoom())
        );
        weatherPanel.applyConfig(ConfigUtil.extract(config.getWeather()));
        boostersPanel.applyConfig(config.getBoosters());
    }

    /**
     * @return whether panel configuration is different from {@link ConfigStore#getCurrentConfig()} ()}
     */
    public boolean isDirty() {
        return configStore.getCurrentConfig()
            .map(currentConfig -> !getConfig().equals(currentConfig))
            // no current config in memory
            .orElse(true);
    }

   private GuiSection versions(int configVersionNumber) {
        COLOR color = COLOR.WHITE50;
        GuiSection versions = new GuiSection();

        GText gameVersion = new GText(UI.FONT().S, "Game Version: " + VERSION.VERSION_STRING);
        gameVersion.color(color);

        String modVersionString = "NO_VER";
        if (modInfo != null) {
            modVersionString = modInfo.version;
        }

        GText modVersion = new GText(UI.FONT().S, "Mod Version: " + modVersionString);
        modVersion.color(color);

        GText configVersion = new GText(UI.FONT().S, "Config Version: " + configVersionNumber);
        configVersion.color(color);

        versions.addDown(0, gameVersion);
        versions.addDown(2, modVersion);
        versions.addDown(2, configVersion);

        return versions;
    }

    private GuiSection footer() {
        GuiSection section = new GuiSection();

        this.cancelButton = new Button("Cancel", COLOR.WHITE25);
        cancelButton.hoverInfoSet("Closes window without applying changes");
        section.addRight(0, cancelButton);

        this.folderButton = new Button("Folder", COLOR.WHITE25);
        folderButton.hoverInfoSet("Opens settings folder with mod config");
        section.addRight(10, folderButton);

        this.resetButton = new Button("Default", COLOR.WHITE25);
        resetButton.hoverInfoSet("Resets to default options");
        section.addRight(10, resetButton);

        this.undoButton = new Button("Undo", COLOR.WHITE25);
        undoButton.hoverInfoSet("Undo made changes");
        undoButton.setEnabled(false);
        section.addRight(10, undoButton);

        this.applyButton = new Button("Apply", COLOR.WHITE15);
        applyButton.hoverInfoSet("Apply and save options");
        applyButton.setEnabled(false);
        section.addRight(300, applyButton);

        this.okButton = new Button("OK", COLOR.WHITE15);
        okButton.hoverInfoSet("Apply and exit");
        section.addRight(10, okButton);

        return section;
    }

    @Override
    public void render(SPRITE_RENDERER r, float seconds) {
        updateTimerSeconds += seconds;

        // check for changes in config
        if (updateTimerSeconds >= UPDATE_INTERVAL_SECONDS) {
            updateTimerSeconds = 0d;

            applyButton.setEnabled(isDirty());
            undoButton.setEnabled(isDirty());
        }
        super.render(r, seconds);
    }
}
