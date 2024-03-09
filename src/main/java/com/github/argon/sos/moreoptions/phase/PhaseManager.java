package com.github.argon.sos.moreoptions.phase;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.Getter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains classes implementing the {@link Phases}, which can be registered
 * via {@link PhaseManager#register(Phase, Phases)} into a certain {@link Phase}.
 *
 * The manager will call the classes in order they were added into the phase,
 * when the game executes a certain phase.
 *
 * This is useful when some classes rely on game resources, which are only available after a certain phase.
 * E.g. injecting ui elements into already present game elements is only possible when the game ui was loaded.
 *
 * See {@link Phases} for a more detailed description of the phases.
 */
public class PhaseManager implements Phases {

    private final static Logger log = Loggers.getLogger(PhaseManager.class);

    @Getter(lazy = true)
    private final static PhaseManager instance = new PhaseManager();

    private final Map<Phase, List<Phases>> phases =new HashMap<>();

    private PhaseManager() {
        for (Phase phase : Phase.values()) {
            phases.put(phase, new ArrayList<>());
        }
    }

    public void register(Phase phase, Phases init) {
        if (phases.get(phase).contains(init)) {
            return;
        }

        phases.get(phase).add(init);
    }

    @Override
    public void initGameUiPresent() {
        log.debug("PHASE: initGameUiPresent");
        phases.get(Phase.INIT_GAME_UI_PRESENT).forEach(Phases::initGameUiPresent);
    }

    @Override
    public void initGameUpdating() {
        log.debug("PHASE: initGameUpdating");
        phases.get(Phase.INIT_GAME_UPDATING).forEach(Phases::initGameUpdating);
    }

    @Override
    public void onGameUpdate(double seconds) {
        //log.trace("PHASE: onGameUpdate");
        phases.get(Phase.ON_GAME_UPDATE).forEach(phase -> phase.onGameUpdate(seconds));
    }

    @Override
    public void initBeforeGameCreated() {
        log.debug("PHASE: initBeforeGameCreated");
        phases.get(Phase.INIT_BEFORE_GAME_CREATED).forEach(Phases::initBeforeGameCreated);
    }

    @Override
    public void initModCreateInstance() {
        log.debug("PHASE: initModCreateInstance");
        phases.get(Phase.INIT_MOD_CREATE_INSTANCE).forEach(Phases::initModCreateInstance);
    }

    @Override
    public void onGameSaveLoaded(Path saveFilePath) {
        log.debug("PHASE: onGameSaveLoaded");
        phases.get(Phase.ON_GAME_SAVE_LOADED).forEach(init -> init.onGameSaveLoaded(saveFilePath));
    }

    @Override
    public void onGameSaved(Path saveFilePath) {
        log.debug("PHASE: onGameSaved");
        phases.get(Phase.ON_GAME_SAVED).forEach(init -> init.onGameSaved(saveFilePath));
    }

    @Override
    public void initNewGameSession() {
        log.debug("PHASE: initNewGameSession");
        phases.get(Phase.INIT_NEW_GAME_SESSION).forEach(Phases::initNewGameSession);
    }

    @Override
    public void onGameSaveReloaded() {
        log.debug("PHASE: onGameSaveReloaded");
        phases.get(Phase.ON_GAME_SAVE_RELOADED).forEach(Phases::onGameSaveReloaded);
    }

    @Override
    public void onCrash(Throwable throwable) {
        log.debug("PHASE: onCrash");
        phases.get(Phase.ON_CRASH).forEach(init -> init.onCrash(throwable));
    }
}
