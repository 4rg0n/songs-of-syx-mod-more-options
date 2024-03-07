package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.game.ui.NonHidingPopup;
import com.github.argon.sos.moreoptions.game.ui.NotificationPopup;
import com.github.argon.sos.moreoptions.init.InitPhases;
import com.github.argon.sos.moreoptions.init.UninitializedException;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import view.main.Interrupters;
import view.main.VIEW;
import view.sett.SettView;
import view.world.WorldView;

import java.util.Optional;

/**
 * For hooking into the games UI
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameUiApi implements InitPhases {

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

    @Override
   public void initGameUpdating() {
        log.debug("Init game ui api");
        popup = new NonHidingPopup(VIEW.inters().manager);
        notification = new NotificationPopup(VIEW.inters().manager);
    }
}
