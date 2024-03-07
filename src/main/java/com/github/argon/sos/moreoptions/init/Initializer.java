package com.github.argon.sos.moreoptions.init;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.i18n.I18nMessages;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import com.github.argon.sos.moreoptions.metric.MetricScheduler;
import com.github.argon.sos.moreoptions.ui.UiConfig;
import lombok.Getter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Summarizes initialization processes for different classes in different phases
 * Some resources of the game are only available after a certain phase.
 *
 * Is initially called by the {@link com.github.argon.sos.moreoptions.MoreOptionsScript}
 */
public class Initializer implements InitPhases {

    private final static Logger log = Loggers.getLogger(Initializer.class);

    @Getter(lazy = true)
    private final static Initializer instance = new Initializer(
        GameApis.getInstance(),
        ConfigStore.getInstance(),
        MetricScheduler.getInstance(),
        MetricExporter.getInstance(),
        UiConfig.getInstance(),
        I18nMessages.getInstance()
    );
    private final GameApis gameApis;
    private final ConfigStore configStore;
    private final MetricScheduler metricScheduler;
    private final MetricExporter metricExporter;
    private final UiConfig uiConfig;
    private final I18nMessages i18nMessages;

    private final Map<InitPhase, List<InitPhases>> inits =new HashMap<>();

    public Initializer(
        GameApis gameApis,
        ConfigStore configStore,
        MetricScheduler metricScheduler,
        MetricExporter metricExporter,
        UiConfig uiConfig,
        I18nMessages i18nMessages
    ) {
        this.gameApis = gameApis;
        this.configStore = configStore;
        this.metricScheduler = metricScheduler;
        this.metricExporter = metricExporter;
        this.uiConfig = uiConfig;
        this.i18nMessages = i18nMessages;

        for (InitPhase initPhase : InitPhase.values()) {
            inits.put(initPhase, new ArrayList<>());
        }
    }

    public void register(InitPhase phase, InitPhases init) {
        if (inits.get(phase).contains(init)) {
            return;
        }

        inits.get(phase).add(init);
    }

    @Override
    public void initGameUiPresent() {
        log.debug("PHASE: initGameUiPresent");
        gameApis.initGameUiPresent();
        uiConfig.initGameUiPresent();
        inits.get(InitPhase.GAME_UI_PRESENT).forEach(InitPhases::initGameUiPresent);
    }

    @Override
    public void initGameUpdating() {
        log.debug("PHASE: initGameUpdating");
        gameApis.initGameUpdating();
        inits.get(InitPhase.GAME_UPDATING).forEach(InitPhases::initGameUpdating);
    }

    @Override
    public void initBeforeGameCreated() {
        log.debug("PHASE: initBeforeGameCreated");
        gameApis.initBeforeGameCreated();
        configStore.initBeforeGameCreated();
        metricExporter.initBeforeGameCreated();
        i18nMessages.initBeforeGameCreated();
        inits.get(InitPhase.BEFORE_GAME_CREATED).forEach(InitPhases::initBeforeGameCreated);
    }

    @Override
    public void initModCreateInstance() {
        log.debug("PHASE: initModCreateInstance");
        gameApis.initModCreateInstance();
        configStore.initModCreateInstance();
        inits.get(InitPhase.MOD_CREATE_INSTANCE).forEach(InitPhases::initModCreateInstance);
    }

    @Override
    public void initGameSaveLoaded(Path saveFilePath) {
        log.debug("PHASE: initGameSaveLoaded");
        gameApis.initGameSaveLoaded(saveFilePath);
        configStore.initGameSaveLoaded(saveFilePath);
        inits.get(InitPhase.GAME_SAVE_LOADED).forEach(init -> init.initGameSaveLoaded(saveFilePath));
    }

    @Override
    public void initGameSaved(Path saveFilePath) {
        log.debug("PHASE: initGameSaved");
        gameApis.initGameSaved(saveFilePath);
        configStore.initGameSaved(saveFilePath);
        inits.get(InitPhase.GAME_SAVED).forEach(init -> init.initGameSaved(saveFilePath));
    }

    @Override
    public void initNewGameSession() {
        log.debug("PHASE: initNewGameSession");
        gameApis.initNewGameSession();
        inits.get(InitPhase.NEW_GAME_SESSION).forEach(InitPhases::initNewGameSession);
    }

    @Override
    public void initGameSaveReloaded() {
        log.debug("PHASE: initGameSaveReloaded");
        gameApis.initGameSaveReloaded();
        metricExporter.initGameSaveReloaded();
        inits.get(InitPhase.GAME_SAVE_RELOADED).forEach(InitPhases::initGameSaveReloaded);
    }

    public void crash(Throwable throwable) {
        log.info("Stopping metric scheduler because of game crash");
        metricScheduler.stop();
    }
}
