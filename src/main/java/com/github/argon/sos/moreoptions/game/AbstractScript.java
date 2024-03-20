package com.github.argon.sos.moreoptions.game;

import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.phase.Phase;
import com.github.argon.sos.moreoptions.phase.PhaseManager;
import com.github.argon.sos.moreoptions.phase.Phases;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.Errors;
import world.WORLD;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Abstracts some basic mechanics.
 */
public abstract class AbstractScript implements script.SCRIPT, Phases {

    private final static Logger log = Loggers.getLogger(AbstractScript.class);
    public final static String LOG_LEVEL_ENV_NAME = "MOD.LOG_LEVEL";

    static {
        Loggers.setLevels(Loggers.LOG_LEVEL_DEFAULT);
    }

    private final PhaseManager phaseManager = PhaseManager.getInstance();

    @Nullable
    private ScriptInstance scriptInstance;

    @Getter
    @Nullable
    private final Level envLogLevel;

    public AbstractScript() {
        // set log level from env variable
        envLogLevel = Optional.ofNullable(System.getenv(LOG_LEVEL_ENV_NAME))
            .flatMap(Level::fromName)
            .orElse(null);
    }

    @Override
    public abstract CharSequence name();

    @Override
    public abstract CharSequence desc();

    protected abstract void registerPhases(PhaseManager phaseManager);


    @Override
    public void initBeforeGameCreated() {
        log.debug("Initializing Script");
        // custom error handling
        Errors.setHandler(new ErrorHandler<>(this));

        Level level = envLogLevel;
        if (level == null) {
            level = initLogLevel();
        }

        Loggers.setLevels(level);

        // initialize game apis first
        GameApis gameApis = GameApis.getInstance();
        for (Phase phase : Phase.values()) {
            phaseManager.register(phase, gameApis);
        }

        // call to register phases
        registerPhases(phaseManager);
        phaseManager.initBeforeGameCreated();
    }

    protected Level initLogLevel() {
        return Loggers.LOG_LEVEL_DEFAULT;
    }

    @Override
    public void initModCreateInstance() {
        phaseManager.initModCreateInstance();
    }

    @Override
    public SCRIPT_INSTANCE createInstance() {
        if (scriptInstance == null) {
            initModCreateInstance();
            log.debug("Creating Instance");
            scriptInstance = new ScriptInstance(this);
        }

        log.debug("World Seed: " + WORLD.GEN().seed);

        // or else the init methods won't be called again when a save game is loaded
        scriptInstance.reset();
        return scriptInstance;
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
