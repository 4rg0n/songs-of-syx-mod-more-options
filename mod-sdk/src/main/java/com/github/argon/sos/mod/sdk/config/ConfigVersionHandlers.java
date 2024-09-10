package com.github.argon.sos.mod.sdk.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigVersionHandlers<CONFIG> implements ConfigVersionHandler<CONFIG> {
    private final Map<Integer, ConfigVersionHandler<CONFIG>> handlers = new HashMap<>();

    /**
     * Will handle mapping between different config versions.
     * So your mod can have backwards compatibility for configuration.
     *
     * @param version to handle
     * @return mapped configuration
     * @throws ConfigException when no handler was found for given version
     */
    @Override
    public Optional<CONFIG> handle(int version) {
        return get(version).handle(version);
    }

    /**
     * Will register a specific version handler for the given version.
     *
     * @param version to register the handler for
     * @param versionHandler to register
     */
    public ConfigVersionHandlers<CONFIG> register(int version, ConfigVersionHandler<CONFIG> versionHandler) {
        handlers.put(version, versionHandler);
        return this;
    }

    /**
     * Will return a handler for the given version.
     *
     * @param version to get the handler vor
     * @return version handler for given version
     * @throws ConfigException when no handler is registered
     */
    public ConfigVersionHandler<CONFIG> get(int version) {
        ConfigVersionHandler<CONFIG> handler = handlers.get(version);

        if (handler == null) {
            throw new ConfigException("No handler for config version " + version + " registered");
        }

        return handler;
    }
}
