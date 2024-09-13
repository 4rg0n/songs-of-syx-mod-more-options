package com.github.argon.sos.mod.sdk.config;

import java.util.Optional;

/**
 * For providing backwards compatibility for any kind of configuration.
 *
 * @param <CONFIG> type of config object
 */
public interface ConfigVersionHandler<CONFIG> {

    /**
     * Handles preparation of config for the given version
     *
     * @param version of the config
     * @return mapped configuration object or empty
     */
    Optional<CONFIG> handle(int version);
}
