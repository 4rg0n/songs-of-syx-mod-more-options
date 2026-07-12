package com.github.argon.sos.mod.sdk.phase;

import lombok.Getter;

/**
 * Used in {@link Phases} to indicate that a phase method is not implemented, but called.
 */
public class PhaseNotImplemented extends RuntimeException {

    /**
     * The phase which isn't implemented.
     */
    @Getter
    private final Phase phase;

    /**
     * Creates a new {@link PhaseNotImplemented} exception with given {@link Phase}.
     *
     * @param phase which isn't implemented
     */
    public PhaseNotImplemented(Phase phase) {
        super(phase.toString());
        this.phase = phase;
    }
}
