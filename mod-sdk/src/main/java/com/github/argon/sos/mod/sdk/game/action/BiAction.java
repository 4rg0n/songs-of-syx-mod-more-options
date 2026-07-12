package com.github.argon.sos.mod.sdk.game.action;

import java.util.Objects;

/**
 * Simple function with two parameters and void return type
 * For things triggering actions e.g. onShow
 *
 * @param <Param1> type of first parameter handed to the action function
 * @param <Param2> type of second parameter handed to the action function
 */
@FunctionalInterface
public interface BiAction<Param1, Param2> {

    /**
     * Executes the action
     *
     * @param param1 first parameter for the action
     * @param param2 second parameter for the action
     */
    void accept(Param1 param1, Param2 param2);

    /**
     * For chaining multiple actions.
     *
     * @param after to execute after this one
     * @return the after consumer
     */
    default BiAction<Param1, Param2> andThen(BiAction<? super Param1, ? super Param2> after) {
        Objects.requireNonNull(after);
        return (Param1 param1, Param2 param2) -> { accept(param1, param2); after.accept(param1, param2); };
    }
}
