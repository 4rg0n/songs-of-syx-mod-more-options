package com.github.argon.sos.mod.sdk;

import com.github.argon.sos.mod.sdk.game.error.ErrorHandler;
import com.github.argon.sos.mod.sdk.game.api.GameApis;
import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phase;
import com.github.argon.sos.mod.sdk.phase.PhaseManager;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.mod.sdk.phase.state.StateManager;
import com.github.argon.sos.mod.sdk.properties.PropertiesStore;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.Errors;
import snake2d.util.file.FileGetter;
import world.WORLD;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Abstracts some basic mechanics.
 */
public abstract class AbstractModSdkScript implements script.SCRIPT, Phases {

    private final static Logger log = Loggers.getLogger(AbstractModSdkScript.class);

    /**
     * You can force to set the log level via this environment variable.
     *
     * <pre>MOD.LOG_LEVEL=debug</pre>
     */
    public final static String LOG_LEVEL_ENV_NAME = "MOD.LOG_LEVEL";

    protected final PhaseManager phaseManager;
    protected final StateManager stateManager;
    protected final GameApis gameApis;
    protected final PropertiesStore propertiesStore;

    @Nullable
    private ScriptInstance scriptInstance;

    @Getter
    @Nullable
    private final Level envLogLevel;

    public AbstractModSdkScript() {
        this(ModSdkModule.phaseManager(), ModSdkModule.stateManager(), ModSdkModule.gameApis(), ModSdkModule.propertiesStore());
    }

    public AbstractModSdkScript(PhaseManager phaseManager, StateManager stateManager, GameApis gameApis, PropertiesStore propertiesStore) {
        this.phaseManager = phaseManager;
        this.stateManager = stateManager;
        this.gameApis = gameApis;
        this.propertiesStore = propertiesStore;

        // set log level from env variable
        envLogLevel = Optional.ofNullable(System.getenv(LOG_LEVEL_ENV_NAME))
            .flatMap(Level::fromName)
            .orElse(null);

        Level level = initLogging();
        if (envLogLevel != null) {
            level = envLogLevel;
        }

        Loggers.setLevels(level);
    }

    @Override
    public abstract CharSequence name();

    @Override
    public abstract CharSequence desc();

    /**
     * Triggered by {@link this#initBeforeGameCreated()}
     */
    protected abstract void registerPhases(PhaseManager phaseManager);

    /**
     * Will register classes to phases required by the Mod SDK
     */
    protected void registerSdkPhases(PhaseManager phaseManager) {
        // initialize game apis first
        for (Phase phase : Phase.values()) {
            phaseManager.register(phase, gameApis);
        }

        phaseManager
            .register(Phase.ON_GAME_UPDATE, ModSdkModule.notificator())
            .register(Phase.INIT_BEFORE_GAME_CREATED, ModSdkModule.gameJsonStore())
            .register(Phase.INIT_BEFORE_GAME_CREATED, ModSdkModule.i18nMessages());
    }

    /**
     * Triggered by the game
     */
    @Override
    public void initBeforeGameCreated() {
        log.debug("Initializing Script");
        // custom error handling
        Errors.setHandler(new ErrorHandler<>(this));

        // call to register phases
        registerSdkPhases(phaseManager);
        registerPhases(phaseManager);
        phaseManager.initBeforeGameCreated();
    }

    /**
     * Triggered when {@link AbstractModSdkScript} is instantiated
     */
    protected Level initLogging() {
        return Loggers.LOG_LEVEL_DEFAULT;
    }

    /**
     * Triggered by {@link this#createInstance()}
     */
    @Override
    public void initModCreateInstance() {
        phaseManager.initModCreateInstance();
    }

    /**
     * Triggered by the game
     */
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

    /**
     * Triggered by {@link ScriptInstance#update(double)}
     */
    @Override
    public void initGameUpdating() {
        phaseManager.initGameUpdating();
    }

    /**
     * Triggered by {@link ScriptInstance#update(double)}
     */
    @Override
    public void initGameUiPresent() {
        phaseManager.initGameUiPresent();
    }

    /**
     * Triggered by the game
     */
    @Override
    public void initSettlementUiPresent() {
        phaseManager.initSettlementUiPresent();
    }

    /**
     * Triggered by the game
     */
    @Override
    public void onGameLoaded(Path saveFilePath) {
        phaseManager.onGameLoaded(saveFilePath);
    }

    /**
     * Triggered by the game
     */
    @Override
    public void onGameSaved(Path saveFilePath) {
        phaseManager.onGameSaved(saveFilePath);
    }

    /**
     * Triggered by {@link ScriptInstance#load(FileGetter)}
     */
    @Override
    public void initNewGameSession() {
        phaseManager.initNewGameSession();
    }

    /**
     * Triggered by {@link ScriptInstance#load(FileGetter)}
     */
    @Override
    public void onGameSaveReloaded() {
        phaseManager.onGameSaveReloaded();
    }

    /**
     * Triggered by {@link ScriptInstance#update(double)}
     */
    @Override
    public void onGameUpdate(double seconds) {
        phaseManager.onGameUpdate(seconds);
    }

    /**
     * Triggered by {@link ErrorHandler}
     */
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
