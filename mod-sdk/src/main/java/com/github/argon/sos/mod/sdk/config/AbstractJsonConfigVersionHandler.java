package com.github.argon.sos.mod.sdk.config;

import com.github.argon.sos.mod.sdk.config.json.JsonConfigStore;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractJsonConfigVersionHandler<CONFIG> implements ConfigVersionHandler<CONFIG> {
    protected final JsonConfigStore jsonConfigStore;
}
