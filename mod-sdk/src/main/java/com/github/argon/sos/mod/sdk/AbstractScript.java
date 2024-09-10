package com.github.argon.sos.mod.sdk;

import com.github.argon.sos.mod.sdk.game.ErrorHandler;
import com.github.argon.sos.mod.sdk.game.api.GameApis;
import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phase;
import com.github.argon.sos.mod.sdk.phase.PhaseManager;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.mod.sdk.phase.state.StateManager;
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

    protected final PhaseManager phaseManager;
    protected final StateManager stateManager;
    protected final GameApis gameApis;

    @Nullable
    private ScriptInstance scriptInstance;

    @Getter
    @Nullable
    private final Level envLogLevel;

    public AbstractScript() {
        this(ModSdkModule.phaseManager(), ModSdkModule.stateManager(), ModSdkModule.gameApis());
    }

    public AbstractScript(PhaseManager phaseManager, StateManager stateManager, GameApis gameApis) {
        this.phaseManager = phaseManager;
        this.stateManager = stateManager;
        this.gameApis = gameApis;

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
        for (Phase phase : Phase.values()) {
            phaseManager.register(phase, gameApis);
        }

        phaseManager
            .register(Phase.INIT_BEFORE_GAME_CREATED, ModSdkModule.gameJsonStore())
            .register(Phase.INIT_BEFORE_GAME_CREATED, ModSdkModule.i18nMessages());

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
            scriptInstance = new ScriptInstance(this, stateManager);
        }

        log.debug("World Seed: " + WORLD.GEN().seed);

        // or else the init methods won't be called again when a save game is loaded
        stateManager.reset();
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
    public void onViewSetup() {
        phaseManager.onViewSetup();
    }

    @Override
    public void initSettlementUiPresent() {
        phaseManager.initSettlementUiPresent();
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
