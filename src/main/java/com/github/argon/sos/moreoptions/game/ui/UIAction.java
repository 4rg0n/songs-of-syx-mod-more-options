package com.github.argon.sos.moreoptions.game.ui;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Simple function with one parameter and void return type
 * For UI elements triggering actions e.g. onShow
 *
 * @param <T> type of parameter handed to the action function
 */
public interface UIAction<T>  {
    void accept(T t);

    default Consumer<T> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }
}
