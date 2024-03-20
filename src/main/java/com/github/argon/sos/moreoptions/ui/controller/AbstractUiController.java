package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.config.ConfigApplier;
import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV3Config;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.moreoptions.ui.MoreOptionsPanel;
import com.github.argon.sos.moreoptions.ui.Notificator;
import com.github.argon.sos.moreoptions.ui.UiFactory;
import com.github.argon.sos.moreoptions.ui.UiMapper;
import com.github.argon.sos.moreoptions.util.Clipboard;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

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
    protected final Notificator notificator = Notificator.getInstance();
    protected final ConfigApplier configApplier = ConfigApplier.getInstance();

    protected boolean copyToClipboard(@Nullable String content, String successMessage, String errorMessage) {
        boolean written = false;

        try {
            if (content != null) {
                written = Clipboard.write(content);

                if (written) {
                    notificator.notifySuccess(successMessage);
                } else {
                    notificator.notifyError(errorMessage);
                }
            } else {
                notificator.notifyError(errorMessage);
            }
        } catch (Exception e) {
            notificator.notifyError(errorMessage, e);
        }

        return written;
    }

    protected boolean applyAndSave(MoreOptionsPanel moreOptionsPanel) {
        MoreOptionsV3Config currentConfig = configStore.getCurrentConfig();
        // only save when changes were made
        if (!moreOptionsPanel.isDirty(currentConfig)) {
            return true;
        }
        MoreOptionsV3Config config = moreOptionsPanel.getValue();
        if (config == null) {
            log.warn("Could read config from modal. Got null");
            return false;
        }

        // notify when metric collection status changes
        Objects.requireNonNull(currentConfig);
        if (currentConfig.getMetrics().isEnabled() != config.getMetrics().isEnabled()) {
            if (config.getMetrics().isEnabled()) {
                notificator.notify(i18n.t("notification.metrics.start"));
            } else {
                notificator.notify(i18n.t("notification.metrics.stop"));
            }
        }

        try {
            return configApplier.applyToGameAndSave(config);
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
