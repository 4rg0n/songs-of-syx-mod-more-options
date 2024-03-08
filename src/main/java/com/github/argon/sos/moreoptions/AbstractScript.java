package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.phase.PhaseManager;
import com.github.argon.sos.moreoptions.phase.Phase;
import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import org.jetbrains.annotations.Nullable;
import snake2d.Errors;

import java.nio.file.Path;
import java.util.Optional;

public abstract class AbstractScript implements script.SCRIPT, Phases {

    private final static Logger log = Loggers.getLogger(AbstractScript.class);
    public final static Level LOG_LEVEL_DEFAULT = Level.TRACE;

    static {
        Loggers.setLevels(LOG_LEVEL_DEFAULT);
    }

    private final PhaseManager phaseManager = PhaseManager.getInstance();

    @Nullable
    private Instance instance;

    @Override
    public abstract CharSequence name();

    @Override
    public abstract CharSequence desc();

    protected abstract void registerPhases(PhaseManager phaseManager);

    @Override
    public void initBeforeGameCreated() {
        // custom error handling
        Errors.setHandler(new CustomErrorHandler<>(this));

        // determine and set log level
        String logLevelName = System.getenv("MOD.LOG_LEVEL");
        Level level = Optional.ofNullable(logLevelName)
            .flatMap(Level::fromName)
            .orElseGet(this::initLogLevel);
        Loggers.setLevels(level);

        // initialize game apis first
        GameApis gameApis = GameApis.getInstance();
        for (Phase phase : Phase.values()) {
            phaseManager.register(phase, gameApis);
        }

        registerPhases(phaseManager);
        phaseManager.initBeforeGameCreated();
    }

    protected Level initLogLevel() {
        return LOG_LEVEL_DEFAULT;
    }

    @Override
    public void initModCreateInstance() {
        phaseManager.initModCreateInstance();
    }

    @Override
    public SCRIPT_INSTANCE createInstance() {
        if (instance == null) {
            initModCreateInstance();
            log.debug("Creating Instance");
            instance = new Instance(this);
        }

        // or else the init methods won't be called again when a save game is loaded
        instance.reset();
        return instance;
    }

    @Override
    public void initGameUpdating() {
        phaseManager.initGameUpdating();
    }

    @Override
    public void initGameUiPresent() {
        phaseManager.initGameUiPresent();
    }

    @Override
    public void onGameSaveLoaded(Path saveFilePath) {
        phaseManager.onGameSaveLoaded(saveFilePath);
    }

    @Override
    public void onGameSaved(Path saveFilePath) {
        phaseManager.onGameSaved(saveFilePath);
    }

    @Override
    public void initNewGameSession() {
        phaseManager.initNewGameSession();
    }

    @Override
    public void onGameSaveReloaded() {
        phaseManager.onGameSaveReloaded();
    }

    @Override
    public void onGameUpdate(double seconds) {
        phaseManager.onGameUpdate(seconds);
    }

    @Override
    public void onCrash(Throwable throwable) {
        log.warn("Game crash detected!");
        log.info("", throwable);

        try {
            phaseManager.onCrash(throwable);
        } catch (Exception e) {
            log.error("Something bad happened while trying to handle game crash", e);
        }
    }
}
