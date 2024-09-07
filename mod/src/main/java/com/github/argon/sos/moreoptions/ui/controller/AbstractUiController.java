package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.config.ConfigApplier;
import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.mod.sdk.game.api.GameApis;
import com.github.argon.sos.mod.sdk.i18n.I18n;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.moreoptions.ui.MoreOptionsPanel;
import com.github.argon.sos.moreoptions.ui.UiFactory;
import com.github.argon.sos.moreoptions.ui.UiMapper;
import com.github.argon.sos.moreoptions.ui.msg.Message;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.net.URI;
import java.util.Objects;

@RequiredArgsConstructor
public abstract class AbstractUiController<Element> implements Phases {
    protected static final I18n i18n = I18n.get(AbstractUiController.class);
    protected final Element element;

    private final static Logger log = Loggers.getLogger(AbstractUiController.class);

    protected final GameApis gameApis = GameApis.getInstance();
    protected final ConfigStore configStore = ConfigStore.getInstance();
    protected final ConfigDefaults configDefaults = ConfigDefaults.getInstance();
    protected final UiMapper uiMapper = UiMapper.getInstance();
    protected final UiFactory uiFactory = UiFactory.getInstance();
    protected final ConfigApplier configApplier = ConfigApplier.getInstance();

    protected boolean applyAndSave(MoreOptionsPanel moreOptionsPanel) {
        MoreOptionsV5Config currentConfig = configStore.getCurrentConfig();
        // only save when changes were made
        if (!moreOptionsPanel.isDirty(currentConfig)) {
            return true;
        }
        MoreOptionsV5Config config = moreOptionsPanel.getValue();
        if (config == null) {
            log.warn("Could read config from modal. Got null");
            return false;
        }

        // notify when metric collection status changes
        Objects.requireNonNull(currentConfig);
        if (currentConfig.getMetrics().isEnabled() != config.getMetrics().isEnabled()) {
            if (config.getMetrics().isEnabled()) {
                Message.notify(i18n.t("notification.metrics.start"));
            } else {
                Message.notify(i18n.t("notification.metrics.stop"));
            }
        }

        boolean success = configApplier.applyToGameAndSave(config);
        log.debug("Config applied? %s", success);

        return success;
    }

    protected void undo(MoreOptionsPanel moreOptionsPanel) {
        MoreOptionsV5Config currentConfig = configStore.getCurrentConfig();
        moreOptionsPanel.setValue(currentConfig);
    }

    protected void openWebsite(String url) {
        Desktop desktop = java.awt.Desktop.getDesktop();

        try {
            URI uri = new URI(url);
            desktop.browse(uri);
        } catch (Exception e) {
            Message.notifyError(i18n.t("notification.website.not.open"));
            log.warn("Could not open website %s", url, e);
        }
    }
}
