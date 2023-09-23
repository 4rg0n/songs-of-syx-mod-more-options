package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigStore {
    private final static Logger log = Loggers.getLogger(ConfigStore.class);

    private final ConfigJsonService configJsonService;

    @Getter(lazy = true)
    private final static ConfigStore instance = new ConfigStore(
        ConfigJsonService.getInstance()
    );

    private MoreOptionsConfig currentConfig;

    /**
     * Used by the mod as current configuration to apply and use
     */
    public void setCurrentConfig(MoreOptionsConfig currentConfig) {
        log.debug("Set current config to: %s", currentConfig);
        this.currentConfig = currentConfig;
    }

    public Optional<MoreOptionsConfig> getCurrentConfig() {
        return Optional.ofNullable(currentConfig);
    }

    /**
     * @return configuration from the games user profile
     */
    public Optional<MoreOptionsConfig> getProfileConfig() {
        return configJsonService.loadProfileConfig();
    }

    public boolean saveProfileConfig(MoreOptionsConfig config) {
       return configJsonService.saveProfileConfig(config);
    }
}
