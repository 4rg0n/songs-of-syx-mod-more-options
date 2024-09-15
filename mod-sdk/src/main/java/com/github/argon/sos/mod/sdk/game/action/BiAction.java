package com.github.argon.sos.mod.sdk.game.action;

import java.util.Objects;

/**
 * Simple function with two parameters and void return type
 * For things triggering actions e.g. onShow
 *
 * @param <Param> type of first parameter handed to the action function
 * @param <Element> type of second parameter handed to the action function
 */
@FunctionalInterface
public interface BiAction<Param, Element> {
    void accept(Param param, Element element);

    default BiAction<Param, Element> andThen(BiAction<? super Param, ? super Element> after) {
        Objects.requireNonNull(after);
        return (Param param, Element element) -> { accept(param, element); after.accept(param, element); };
    }
}
