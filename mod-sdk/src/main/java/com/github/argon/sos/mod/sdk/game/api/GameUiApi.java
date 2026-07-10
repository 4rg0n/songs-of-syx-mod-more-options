package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.ui.window.NonHidingPopup;
import com.github.argon.sos.mod.sdk.ui.notification.NotificationPopup;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phase;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.mod.sdk.phase.UninitializedException;
import com.github.argon.sos.mod.sdk.util.ReflectionUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import view.main.Interrupters;
import view.main.VIEW;
import view.sett.SettView;
import view.sett.invasion.SBattleView;
import view.ui.top.UIPanelTop;
import view.world.WorldView;

import java.util.Optional;

/**
 * For hooking into the games UI
 */
@RequiredArgsConstructor
public class GameUiApi implements Phases {

    private final static Logger log = Loggers.getLogger(GameUiApi.class);

    @Getter
    @Accessors(fluent = true)
    private NonHidingPopup popup;

    @Getter
    @Accessors(fluent = true)
    private NotificationPopup notification;

    /**
     * Returns the game ui interrupters.
     *
     * @return ui interrupters
     */
    public Interrupters inters() {
        return VIEW.inters();
    }

    /**
     * Contains the settlements ui elements.
     *
     * @throws UninitializedException when ui isn't initialized yet
     * @return settlement view
     */
    public SettView settlement() {
        SettView view = VIEW.s();

        if (view == null) {
            throw new UninitializedException(Phase.INIT_GAME_UI_PRESENT);
        }

        return view;
    }

    /**
     * Contains the world ui elements.
     *
     * @return world view
     */
    public WorldView world() {
        WorldView view = VIEW.world();

        if (view == null) {
            throw new UninitializedException(Phase.INIT_GAME_UI_PRESENT);
        }

        return view;
    }

    /**
     * Contains the battle ui elements.
     *
     * @return battle view
     */
    public SBattleView battle() {
        SBattleView view = VIEW.s().battle;

        if (view == null) {
            throw new UninitializedException(Phase.INIT_GAME_UI_PRESENT);
        }

        return view;
    }

    /**
     * Tries to find an ui element in the settlement view by class.
     *
     * @param clazz to find
     * @return found ui instance
     * @param <T> type of the ui class to look for
     */
    public <T> Optional<T> findUIElementInSettlementView(Class<T> clazz) {
        return ReflectionUtil.getDeclaredFieldValue("inters", settlement().uiManager)
                .flatMap(inters -> extractFromIterable((Iterable<?>) inters, clazz));
    }

    /**
     * Tries to find an ui element in the world view by class.
     *
     * @param clazz to find
     * @return found ui instance
     * @param <T> type of the ui class to look for
     */
    public <T> Optional<T> findUIElementInWorldView(Class<T> clazz) {
        return ReflectionUtil.getDeclaredFieldValue("inters", world().uiManager)
            .flatMap(inters -> extractFromIterable((Iterable<?>) inters, clazz));
    }

    /**
     * Tries to find an ui element in the battle view by class.
     *
     * @param clazz to find
     * @return found ui instance
     * @param <T> type of the ui class to look for
     */
    public <T> Optional<T> findUIElementInBattleView(Class<T> clazz) {
        return ReflectionUtil.getDeclaredFieldValue("inters", battle().uiManager)
            .flatMap(inters -> extractFromIterable((Iterable<?>) inters, clazz));
    }

    /**
     * Injects an ui element into all ui top panels (battle, world and settlement).
     *
     * @param element to inject
     * @throws GameApiException if injection fails
     */
    public void injectIntoUITopPanels(RENDEROBJ element) throws GameApiException {
        injectIntoBattleUITopPanel(element);
        injectIntoWorldUITopPanel(element);
        injectIntoSettlementUITopPanel(element);
    }

    /**
     * Injects an ui element into the world ui top panel.
     *
     * @param element to inject
     * @throws GameApiException if injection fails
     */
    public void injectIntoWorldUITopPanel(RENDEROBJ element) throws GameApiException {
        Object object;
        log.debug("Injecting ui element into in World UIPanelTop#right");

        try {
            object = findUIElementInWorldView(UIPanelTop.class)
                .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredFieldValue("right", uiPanelTop))
                .orElse(null);
        } catch (Exception e) {
            throw new GameApiException("Could not inject ui element into World UIPanelTop#right", e);
        }

        if (object == null) {
            throw new GameApiException("Could not find ui element in World UIPanelTop");
        }

        GuiSection right = (GuiSection) object;
        right.addRelBody(8, DIR.W, element);
    }

    /**
     * Injects an ui element into the settlement ui top panel.
     *
     * @param element to inject
     * @throws GameApiException if injection fails
     */
    public void injectIntoSettlementUITopPanel(RENDEROBJ element) throws GameApiException {
        Object object;
        log.debug("Injecting ui element into Settlement UIPanelTop#right");

        try {
            object = findUIElementInSettlementView(UIPanelTop.class)
                .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredFieldValue("right", uiPanelTop))
                .orElse(null);
        } catch (Exception e) {
            throw new GameApiException("Could not inject ui element into Settlement UIPanelTop#right", e);
        }

        if (object == null) {
            throw new GameApiException("Could not find ui element in Settlement UIPanelTop");
        }

        GuiSection right = (GuiSection) object;
        right.addRelBody(8, DIR.W, element);
    }

    /**
     * Injects an ui element into the battle ui top panel.
     *
     * @param element to inject
     * @throws GameApiException if injection fails
     */
    public void injectIntoBattleUITopPanel(RENDEROBJ element) throws GameApiException {
        Object object;
        log.debug("Injecting ui element into Battle UIPanelTop#right");

        try {
            object = findUIElementInBattleView(UIPanelTop.class)
                .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredFieldValue("right", uiPanelTop))
                .orElse(null);
        } catch (Exception e) {
            throw new GameApiException("Could not inject ui element into Battle UIPanelTop#right", e);
        }

        if (object == null) {
            throw new GameApiException("Could not find ui element in Battle UIPanelTop");
        }

        GuiSection right = (GuiSection) object;
        right.addRelBody(8, DIR.W, element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initGameUpdating() {
        popup = new NonHidingPopup(VIEW.inters().manager);
        notification = new NotificationPopup(VIEW.inters().manager);
    }

    private <T> Optional<T> extractFromIterable(Iterable<?> iterable, Class<T> clazz) {
        for (Object inter : iterable) {
            if (clazz.isInstance(inter)) {
                log.debug("Found ui element %s", clazz.getSimpleName());
                //noinspection unchecked
                return Optional.of((T) inter);
            }
        }

        return Optional.empty();
    }
}
