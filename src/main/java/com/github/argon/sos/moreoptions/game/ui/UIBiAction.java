package com.github.argon.sos.moreoptions.game.ui;

import java.util.Objects;

/**
 * Simple function with two parameters and void return type
 * For UI elements triggering actions e.g. onShow
 *
 * @param <T> type of first parameter handed to the action function
 * @param <E> type of second parameter handed to the action function
 */
@FunctionalInterface
public interface UIBiAction<T, E> {
    void accept(T t, E e);

    default UIBiAction<T, E> andThen(UIBiAction<? super T, ? super E> after) {
        Objects.requireNonNull(after);
        return (T t, E e) -> { accept(t, e); after.accept(t, e); };
    }
}
