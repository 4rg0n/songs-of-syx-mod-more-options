package com.github.argon.sos.moreoptions.init;

import com.github.argon.sos.moreoptions.MoreOptionsConfigurator;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import com.github.argon.sos.moreoptions.metric.MetricScheduler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

/**
 * Summarizes initialization processes for different classes in different phases
 * Some resources of the game are only available after a certain phase.
 *
 * Is initially called by the {@link com.github.argon.sos.moreoptions.MoreOptionsScript}
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Initializer implements InitPhases {

    private final static Logger log = Loggers.getLogger(Initializer.class);

    @Getter(lazy = true)
    private final static Initializer instance = new Initializer(
        GameApis.getInstance(),
        ConfigStore.getInstance(),
        MetricScheduler.getInstance(),
        MetricExporter.getInstance(),
        MoreOptionsConfigurator.getInstance()
    );
    private final GameApis gameApis;
    private final ConfigStore configStore;
    private final MetricScheduler metricScheduler;
    private final MetricExporter metricExporter;
    private final MoreOptionsConfigurator configurator;

    @Override
    public void initGameUiPresent() {
        log.debug("PHASE: initGamePresent");
        gameApis.initGameUiPresent();
    }

    @Override
    public void initGameRunning() {
        log.debug("PHASE: initGameRunning");
        gameApis.initGameRunning();
    }

    @Override
    public void initBeforeGameCreated() {
        log.debug("PHASE: initBeforeGameCreated");
        gameApis.initBeforeGameCreated();
        configStore.initBeforeGameCreated();
        metricExporter.initBeforeGameCreated();
    }

    @Override
    public void initCreateInstance() {
        log.debug("PHASE: initCreateInstance");
        gameApis.initCreateInstance();
        configStore.initCreateInstance();
    }

    @Override
    public void initGameSaveLoaded(Path saveFilePath) {
        log.debug("PHASE: initGameSaveLoaded");
        gameApis.initGameSaveLoaded(saveFilePath);
        configStore.initGameSaveLoaded(saveFilePath);
    }

    @Override
    public void initGameSaved(Path saveFilePath) {
        log.debug("PHASE: initGameSaved");
        gameApis.initGameSaved(saveFilePath);
        configStore.initGameSaved(saveFilePath);
    }

    @Override
    public void initNewGameSession() {
        log.debug("PHASE: initNewGameSession");
        gameApis.initNewGameSession();
    }

    @Override
    public void initGameSaveReloaded() {
        log.debug("PHASE: initGameSaveReloaded");
        // some stuff has to be reinitialized
        gameApis.clear();
        gameApis.initGameSaveReloaded();
        // re-apply config when new game is loaded (only when there's no backup)
        if (!configStore.getBackupConfig().isPresent()) {
            log.debug("Reapplying config because of game load.");
            configurator.applyConfig(configStore.getCurrentConfig());
        }

        // start a new export file on load
        metricExporter.newExportFile();
    }

    public void crash(Throwable throwable) {
        log.info("Stopping metric scheduler because of game crash");
        metricScheduler.stop();
    }
}
