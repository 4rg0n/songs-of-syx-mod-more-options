package com.github.argon.sos.mod.sdk;

import com.github.argon.sos.mod.sdk.game.api.GameApis;
import com.github.argon.sos.mod.sdk.game.error.ErrorHandler;
import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phase;
import com.github.argon.sos.mod.sdk.phase.PhaseManager;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.mod.sdk.phase.state.StateManager;
import com.github.argon.sos.mod.sdk.properties.PropertiesStore;
import game.GAME;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.Errors;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
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

    /**
     * Manages registration and triggering of the mod's {@link Phase}s
     */
    protected final PhaseManager phaseManager;
    /**
     * Manages the state of the mod across game sessions
     */
    protected final StateManager stateManager;
    /**
     * Provides access to the game's APIs
     */
    protected final GameApis gameApis;
    /**
     * Provides access to the mod's configurable properties
     */
    protected final PropertiesStore propertiesStore;

    @Nullable
    private ScriptInstance scriptInstance;

    @Getter
    @Nullable
    private final Level envLogLevel;

    /**
     * Creates a new mod sdk script with default {@link PhaseManager}, {@link StateManager} and game apis.
     */
    public AbstractModSdkScript() {
        this(ModSdkModule.phaseManager(), ModSdkModule.stateManager(), ModSdkModule.gameApis(), ModSdkModule.propertiesStore());
    }

    /**
     * Creates a new mod sdk script with given dependencies
     *
     * @param phaseManager to use
     * @param stateManager to use
     * @param gameApis to use
     * @param propertiesStore to use
     */
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

    /**
     * Returns the name of the mod / script.
     *
     * @return name of the mod / script
     */
    @Override
    public abstract CharSequence name();

    /**
     * Returns the description of the mod / script.
     *
     * @return description of the mod / script
     */
    @Override
    public abstract CharSequence desc();

    /**
     * Triggered by {@link AbstractModSdkScript#initBeforeGameCreated()}
     *
     * @param phaseManager to register the mod's phases with
     */
    protected abstract void registerPhases(PhaseManager phaseManager);

    /**
     * Will register classes to phases required by the Mod SDK
     *
     * @param phaseManager to register the SDK's phases with
     */
    protected void registerSdkPhases(PhaseManager phaseManager) {
        GAME.addOnInit(phaseManager::initGameResourcesLoaded);

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
     * {@inheritDoc}
     */
    @Override
    public void initBeforeGameCreated() {
        log.debug("Initializing Script");
        log.debug("%s", stateManager.getState());
        // custom error handling
        Errors.setHandler(new ErrorHandler<>(this));

        // call to register phases
        registerSdkPhases(phaseManager);
        registerPhases(phaseManager);
        phaseManager.initBeforeGameCreated();
    }

    /**
     * Triggered when {@link AbstractModSdkScript} is instantiated.
     *
     * @return log level to use
     */
    protected Level initLogging() {
        return Loggers.LOG_LEVEL_DEFAULT;
    }

    /**
     * Triggered by {@link AbstractModSdkScript#createInstance()}.
     */
    @Override
    public void initModCreateInstance() {
        phaseManager.initModCreateInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SCRIPT_INSTANCE createInstance() {
        if (scriptInstance == null) {
            initModCreateInstance();
            log.debug("Creating Instance");
            scriptInstance = new ScriptInstance(this, stateManager, gameApis);
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
     * {@inheritDoc}
     */
    @Override
    public void initSettlementUiPresent() {
        phaseManager.initSettlementUiPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGameLoaded(Path saveFilePath, FileGetter fileGetter) {
        phaseManager.onGameLoaded(saveFilePath, fileGetter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGameSaved(Path saveFilePath, FilePutter filePutter) {
        phaseManager.onGameSaved(saveFilePath, filePutter);
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

    @Override
    public void onBeforeBattle() {
        phaseManager.onBeforeBattle();
    }

    @Override
    public void onBattle() {
        phaseManager.onBattle();
    }

    @Override
    public void onAfterBattle() {
        phaseManager.onAfterBattle();
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
