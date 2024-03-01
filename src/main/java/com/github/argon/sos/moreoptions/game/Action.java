package com.github.argon.sos.moreoptions.game;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Simple function with one parameter and void return type
 * For UI elements triggering actions e.g. onShow
 *
 * @param <Param> type of parameter handed to the action function
 */
public interface Action<Param>  {
    void accept(Param param);

    default Consumer<Param> andThen(Consumer<? super Param> after) {
        Objects.requireNonNull(after);
        return (Param param) -> { accept(param); after.accept(param); };
    }
}
