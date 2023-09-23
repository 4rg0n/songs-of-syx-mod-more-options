package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.game.GameUiNotAvailableException;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import view.main.VIEW;
import view.sett.SettView;

import java.util.Optional;

/**
 * For hooking into the games UI
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameUiApi {

    private final static Logger log = Loggers.getLogger(GameUiApi.class);

    @Getter(lazy = true)
    private final static GameUiApi instance = new GameUiApi();

    /**
     * Contains the settlements ui elements
     *
     * @throws GameUiNotAvailableException when ui isn't initialized yet
     */
    public SettView settlement() {
        SettView settView = VIEW.s();

        if (settView == null) {
            throw new GameUiNotAvailableException("Games settlement ui isn't initialized yet.");
        }

        return settView;
    }

    /**
     * Tries to find an ui element by class in {@link VIEW#s()} ui manager.
     */
    public <T> Optional<T> findUIElementInSettlementView(Class<T> clazz) {
        return ReflectionUtil.getDeclaredField("inters", settlement().uiManager)
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
}
