package com.github.argon.sos.mod.sdk.config;

import java.util.Optional;

public interface ConfigVersionHandler<CONFIG> {
    Optional<CONFIG> handle(int version);
}
