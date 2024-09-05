package com.github.argon.sos.moreoptions.phase;

import lombok.Getter;

public class PhaseNotImplemented extends RuntimeException {

    @Getter
    private final Phase phase;

    public PhaseNotImplemented(Phase phase) {
        super(phase.toString());
        this.phase = phase;
    }
}
