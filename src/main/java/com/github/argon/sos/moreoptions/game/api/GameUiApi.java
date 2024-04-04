package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.game.ui.NonHidingPopup;
import com.github.argon.sos.moreoptions.game.ui.NotificationPopup;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.phase.Phase;
import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.moreoptions.phase.UninitializedException;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import lombok.AccessLevel;
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
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameUiApi implements Phases {

    private final static Logger log = Loggers.getLogger(GameUiApi.class);

    @Getter(lazy = true)
    private final static GameUiApi instance = new GameUiApi();

    @Getter
    @Accessors(fluent = true)
    private NonHidingPopup popup;

    @Getter
    @Accessors(fluent = true)
    private NotificationPopup notification;

    public Interrupters inters() {
        return VIEW.inters();
    }

    /**
     * Contains the settlements ui elements
     *
     * @throws UninitializedException when ui isn't initialized yet
     */
    public SettView settlement() {
        SettView view = VIEW.s();

        if (view == null) {
            throw new UninitializedException(Phase.INIT_GAME_UI_PRESENT);
        }

        return view;
    }

    public WorldView world() {
        WorldView view = VIEW.world();

        if (view == null) {
            throw new UninitializedException(Phase.INIT_GAME_UI_PRESENT);
        }

        return view;
    }

    public SBattleView battle() {
        SBattleView view = VIEW.s().battle;

        if (view == null) {
            throw new UninitializedException(Phase.INIT_GAME_UI_PRESENT);
        }

        return view;
    }

    public <T> Optional<T> findUIElementInSettlementView(Class<T> clazz) {
        return ReflectionUtil.getDeclaredField("inters", settlement().uiManager)
                .flatMap(inters -> extractFromIterable((Iterable<?>) inters, clazz));
    }

    public <T> Optional<T> findUIElementInWorldView(Class<T> clazz) {
        return ReflectionUtil.getDeclaredField("inters", world().uiManager)
            .flatMap(inters -> extractFromIterable((Iterable<?>) inters, clazz));
    }

    public <T> Optional<T> findUIElementInBattleView(Class<T> clazz) {
        return ReflectionUtil.getDeclaredField("inters", battle().uiManager)
            .flatMap(inters -> extractFromIterable((Iterable<?>) inters, clazz));
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

    /**
     * Contains UIs like a yes/no prompt or a text input
     *
     * @throws UninitializedException when ui isn't initialized yet
     */
    public Interrupters interrupters() {
        Interrupters interrupters = VIEW.inters();

        if (interrupters == null) {
            throw new UninitializedException(Phase.INIT_GAME_UI_PRESENT);
        }

        return interrupters;
    }

    public void injectIntoUITopPanels(RENDEROBJ element) throws ApiException {
        injectIntoBattleUITopPanel(element);
        injectIntoWorldUITopPanel(element);
        injectIntoSettlementUITopPanel(element);
    }

    public void injectIntoWorldUITopPanel(RENDEROBJ element) throws ApiException {
        Object object;
        log.debug("Injecting ui element into in World UIPanelTop#right");

        try {
            object = findUIElementInWorldView(UIPanelTop.class)
                .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredField("right", uiPanelTop))
                .orElse(null);
        } catch (Exception e) {
            throw new ApiException("Could not inject ui element into World UIPanelTop#right", e);
        }

        if (object == null) {
            throw new ApiException("Could not find ui element in World UIPanelTop");
        }

        GuiSection right = (GuiSection) object;
        right.addRelBody(8, DIR.W, element);
    }

    public void injectIntoSettlementUITopPanel(RENDEROBJ element) throws ApiException {
        Object object;
        log.debug("Injecting ui element into Settlement UIPanelTop#right");

        try {
            object = findUIElementInSettlementView(UIPanelTop.class)
                .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredField("right", uiPanelTop))
                .orElse(null);
        } catch (Exception e) {
            throw new ApiException("Could not inject ui element into Settlement UIPanelTop#right", e);
        }

        if (object == null) {
            throw new ApiException("Could not find ui element in Settlement UIPanelTop");
        }

        GuiSection right = (GuiSection) object;
        right.addRelBody(8, DIR.W, element);
    }

    public void injectIntoBattleUITopPanel(RENDEROBJ element) throws ApiException {
        Object object;
        log.debug("Injecting ui element into Battle UIPanelTop#right");

        try {
            object = findUIElementInBattleView(UIPanelTop.class)
                .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredField("right", uiPanelTop))
                .orElse(null);
        } catch (Exception e) {
            throw new ApiException("Could not inject ui element into Battle UIPanelTop#right", e);
        }

        if (object == null) {
            throw new ApiException("Could not find ui element in Battle UIPanelTop");
        }

        GuiSection right = (GuiSection) object;
        right.addRelBody(8, DIR.W, element);
    }

    @Override
    public void initGameUpdating() {
        popup = new NonHidingPopup(VIEW.inters().manager);
        notification = new NotificationPopup(VIEW.inters().manager);
    }
}
