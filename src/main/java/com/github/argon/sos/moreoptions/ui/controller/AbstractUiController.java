package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.MoreOptionsConfigurator;
import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV3Config;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@RequiredArgsConstructor
public abstract class AbstractUiController<Element> {
    protected static final I18n i18n = I18n.get(AbstractUiController.class);
    protected final Element element;

    private final static Logger log = Loggers.getLogger(AbstractUiController.class);

    protected final GameApis gameApis = GameApis.getInstance();
    protected final MoreOptionsConfigurator configurator = MoreOptionsConfigurator.getInstance();
    protected final ConfigStore configStore = ConfigStore.getInstance();
    protected final ConfigDefaults configDefaults = ConfigDefaults.getInstance();
    protected final UiMapper uiMapper = UiMapper.getInstance();
    protected final UiFactory uiFactory = UiFactory.getInstance();
    protected final Notificator notificator = Notificator.getInstance();

    protected @Nullable MoreOptionsV3Config apply(MoreOptionsPanel moreOptionsPanel) {
        // only save when changes were made
        if (moreOptionsPanel.isDirty()) {
            MoreOptionsV3Config config = moreOptionsPanel.getValue();

            if (config == null) {
                log.warn("Could read config from modal. Got null");
                return null;
            }

            // notify when metric collection status changes
            MoreOptionsV3Config currentConfig = configStore.getCurrentConfig();
            Objects.requireNonNull(currentConfig);
            if (currentConfig.getMetrics().isEnabled() != config.getMetrics().isEnabled()) {
                if (config.getMetrics().isEnabled()) {
                    notificator.notify(i18n.t("notification.metrics.start"));
                } else {
                    notificator.notify(i18n.t("notification.metrics.stop"));
                }
            }

            configurator.applyConfig(config);
            configStore.setCurrentConfig(config);
            return config;
        }

        return null;
    }

    protected boolean applyAndSave(MoreOptionsPanel moreOptionsPanel) {
        try {
            MoreOptionsV3Config appliedConfig = apply(moreOptionsPanel);

            if (appliedConfig != null) {
                return configStore.saveConfig(appliedConfig);
            }
        } catch (Exception e) {
            log.error("Could not apply config to game.", e);
        }

        return false;
    }

    protected void undo(MoreOptionsPanel moreOptionsPanel) {
        MoreOptionsV3Config currentConfig = configStore.getCurrentConfig();
        moreOptionsPanel.setValue(currentConfig);
    }
}
