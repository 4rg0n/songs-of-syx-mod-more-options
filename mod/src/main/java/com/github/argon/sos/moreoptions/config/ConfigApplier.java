package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.moreoptions.MoreOptionsConfigurator;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.phase.Phases;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigApplier implements Phases {
    @Getter(lazy = true)
    private final static ConfigApplier instance = new ConfigApplier(
        ConfigStore.getInstance(),
        MoreOptionsConfigurator.getInstance()
    );

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
