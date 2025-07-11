package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.game.api.GameApis;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.ui.controller.AbstractController;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.ConfigApplier;
import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.ui.MoreOptionsPanel;
import com.github.argon.sos.moreoptions.ui.UiFactory;
import com.github.argon.sos.moreoptions.ui.UiMapper;
import com.github.argon.sos.moreoptions.ui.msg.Messages;

import java.awt.*;
import java.net.URI;
import java.util.Objects;

public abstract class AbstractUiController<Element> extends AbstractController<Element> {
    protected static final I18nTranslator i18n = ModModule.i18n().get(AbstractUiController.class);
    private final static Logger log = Loggers.getLogger(AbstractUiController.class);

    protected final GameApis gameApis = ModSdkModule.gameApis();
    protected final ConfigStore configStore = ModModule.configStore();
    protected final ConfigDefaults configDefaults = ModModule.configDefaults();
    protected final UiMapper uiMapper = ModModule.uiMapper();
    protected final UiFactory uiFactory = ModModule.uiFactory();
    protected final ConfigApplier configApplier = ModModule.configApplier();
    protected final Messages messages = ModModule.messages();

    public AbstractUiController(Element element) {
        super(element);
    }

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
                messages.notify(i18n.t("notification.metrics.start"));
            } else {
                messages.notify(i18n.t("notification.metrics.stop"));
            }
        }

        boolean success = configApplier.applyToGameAndSave(config);
        if (success) {
            log.info("Config SUCCESSFULLY applied");
        } else {
            log.info("Config NOT applied =(");
        }



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
            messages.notifyError(i18n.t("notification.website.not.open"));
            log.warn("Could not open website %s", url, e);
        }
    }
}
