package com.github.argon.sos.mod.sdk.config.json;

import com.github.argon.sos.mod.sdk.config.ConfigVersionHandler;
import lombok.RequiredArgsConstructor;

/**
 * Contains dependencies used in the config version handlers
 *
 * @param <CONFIG> type of the config object
 */
@RequiredArgsConstructor
public abstract class AbstractJsonConfigVersionHandler<CONFIG> implements ConfigVersionHandler<CONFIG> {
    /**
     * For managing configs
     */
    protected final JsonConfigStore jsonConfigStore;
}
