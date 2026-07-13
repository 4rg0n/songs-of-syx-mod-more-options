package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * For applying mod configuration to the game
 */
@RequiredArgsConstructor
public class ConfigApplier implements Phases {

    private final ConfigStore configStore;
    private final Configurator configurator;

    /**
     * Sets the environment log level.
     *
     * @param envLogLevel to set
     */
    public void setEnvLogLevel(@Nullable Level envLogLevel) {
        configurator.setEnvLogLevel(envLogLevel);
    }

    /**
     * Applies the current stored mod configuration to the game.
     */
    public void applyCurrentToGame() {
        applyToGame(configStore.getCurrentConfig());
    }

    /**
     * Applies the given mod configuration to the game.
     *
     * @param config to apply
     * @return whether config was successfully applied
     */
    public boolean applyToGame(@Nullable MoreOptionsV5Config config) {
        return configurator.applyConfig(config);
    }

    /**
     * Applies the given mod configuration to the game and saves it into the config file..
     *
     * @param config to apply
     * @return whether config was successfully applied and saved
     */
    public boolean applyToGameAndSave(@Nullable MoreOptionsV5Config config) {
        if (applyToGame(config)) {
            return configStore.save(config);
        }

        return false;
    }
}
