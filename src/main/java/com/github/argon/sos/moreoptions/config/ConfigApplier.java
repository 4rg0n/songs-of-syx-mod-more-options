package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.MoreOptionsConfigurator;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV4Config;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
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

    private final static Logger log = Loggers.getLogger(ConfigApplier.class);

    private final ConfigStore configStore;

    private final MoreOptionsConfigurator configurator;

    public void setEnvLogLevel(@Nullable Level envLogLevel) {
        configurator.setEnvLogLevel(envLogLevel);
    }

    public boolean applyToGame(@Nullable MoreOptionsV4Config config) {
        return configurator.applyConfig(config);
    }

    public boolean applyToGameAndSave(@Nullable MoreOptionsV4Config config) {
        if (applyToGame(config)) {
            return configStore.save(config);
        }

        return true;
    }
}
