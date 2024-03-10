package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.game.ui.NonHidingPopup;
import com.github.argon.sos.moreoptions.game.ui.NotificationPopup;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
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

    /**
     * Contains the settlements ui elements
     *
     * @throws UninitializedException when ui isn't initialized yet
     */
    public SettView settlement() {
        SettView settView = VIEW.s();

        if (settView == null) {
            throw new UninitializedException("Games settlement ui isn't initialized yet.");
        }

        return settView;
    }

    public WorldView world() {
        WorldView worldView = VIEW.world();

        if (worldView == null) {
            throw new UninitializedException("Games world ui isn't initialized yet.");
        }

        return worldView;
    }

    public <T> Optional<T> findUIElementInSettlementView(Class<T> clazz) {
        return ReflectionUtil.getDeclaredField("inters", settlement().uiManager)
                .flatMap(inters -> extractFromIterable((Iterable<?>) inters, clazz));
    }

    public <T> Optional<T> findUIElementInWorldView(Class<T> clazz) {
        return ReflectionUtil.getDeclaredField("inters", world().uiManager)
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
            throw new UninitializedException("Games interrupt ui isn't initialized yet.");
        }

        return interrupters;
    }

    public boolean injectIntoWorldUITopPanel(RENDEROBJ element) throws UiException {
        Object object = null;
        log.debug("Injecting %s into UIPanelTop#right in World %s",
            element.getClass().getSimpleName(), UIPanelTop.class.getSimpleName());

        try {
            object = findUIElementInWorldView(UIPanelTop.class)
                .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredField("right", uiPanelTop))
                .orElse(null);
        } catch (Exception e) {
            throw new UiException(
                String.format("Could not inject %s into World %s",
                    element.getClass().getSimpleName(),
                    UIPanelTop.class.getSimpleName()),
                e
            );
        }

        if (object == null) {
            throw new UiException(String.format("Could not find %s in World view", UIPanelTop.class.getSimpleName()));
        }

        GuiSection right = (GuiSection) object;
        right.addRelBody(8, DIR.W, element);
        return true;
    }

    public boolean injectIntoSettlementUITopPanel(RENDEROBJ element) throws UiException {
        Object object = null;
        log.debug("Injecting %s into UIPanelTop#right in Settlement %s",
            element.getClass().getSimpleName(), UIPanelTop.class.getSimpleName());

        try {
            object = findUIElementInSettlementView(UIPanelTop.class)
                .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredField("right", uiPanelTop))
                .orElse(null);
        } catch (Exception e) {
            throw new UiException(
                String.format("Could not inject %s into Settlement %s",
                    element.getClass().getSimpleName(),
                    UIPanelTop.class.getSimpleName()),
                e
            );
        }

        if (object == null) {
            throw new UiException(String.format("Could not find %s in Settlement view", UIPanelTop.class.getSimpleName()));
        }

        GuiSection right = (GuiSection) object;
        right.addRelBody(8, DIR.W, element);
        return true;
    }

    @Override
    public void initGameUpdating() {
        log.debug("Init game ui api");
        popup = new NonHidingPopup(VIEW.inters().manager);
        notification = new NotificationPopup(VIEW.inters().manager);
    }
}
