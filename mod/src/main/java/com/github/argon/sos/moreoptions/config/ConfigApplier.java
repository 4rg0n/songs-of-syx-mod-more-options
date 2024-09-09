package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.moreoptions.MoreOptionsConfigurator;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class ConfigApplier implements Phases {

    private final ConfigStore configStore;
    private final MoreOptionsConfigurator configurator;

    public void setEnvLogLevel(@Nullable Level envLogLevel) {
        configurator.setEnvLogLevel(envLogLevel);
    }

    public boolean applyToGame(@Nullable MoreOptionsV5Config config) {
        return configurator.applyConfig(config);
    }

    public boolean applyToGameAndSave(@Nullable MoreOptionsV5Config config) {
        if (applyToGame(config)) {
            return configStore.save(config);
        }

        return false;
    }
}
