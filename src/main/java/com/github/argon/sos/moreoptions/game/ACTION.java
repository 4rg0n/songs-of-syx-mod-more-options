package com.github.argon.sos.moreoptions.game;

/**
 * Used for wiring e.g. a button click to functionality
 */
public interface ACTION extends snake2d.util.misc.ACTION {
    /**
     * Empty action receiving an object
     */
    @SuppressWarnings("rawtypes")
    ACTION_O NOPO = object -> {};
}
