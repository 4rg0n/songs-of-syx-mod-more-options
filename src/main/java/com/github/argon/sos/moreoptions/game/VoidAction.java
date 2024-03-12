package com.github.argon.sos.moreoptions.game;

import java.util.Objects;

/**
 * A function accepting no parameter and with void as return
 */
@FunctionalInterface
public interface VoidAction {
    void accept();

    default VoidAction andThen(VoidAction after) {
        Objects.requireNonNull(after);
        return () -> { accept(); after.accept(); };
    }
}
