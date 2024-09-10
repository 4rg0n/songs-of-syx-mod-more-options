package com.github.argon.sos.mod.sdk.config.json;

import com.github.argon.sos.mod.sdk.config.ConfigVersionHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractJsonConfigVersionHandler<CONFIG> implements ConfigVersionHandler<CONFIG> {
    protected final JsonConfigStore jsonConfigStore;
}
