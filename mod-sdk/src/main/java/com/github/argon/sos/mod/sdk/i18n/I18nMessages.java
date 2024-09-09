package com.github.argon.sos.mod.sdk.i18n;

import com.github.argon.sos.mod.sdk.game.api.GameLangApi;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phases;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Loads and provides access to localized messages.
 * Message .properties files are located in the "resources" folder.
 */
public class I18nMessages implements Phases {

    private final static Logger log = Loggers.getLogger(I18nMessages.class);
    public static final String BUNDLE_NAME_DEFAULT = "messages";
    public final String bundleName;

    private final GameLangApi gameLangApi;

    @Getter
    @Nullable
    private Locale locale;
    @Getter
    @Nullable
    private ResourceBundle messages;

    private final static Locale LOCALE_FALLBACK = GameLangApi.DEFAULT_LOCALE;

    public I18nMessages(GameLangApi gameLangApi) {
        this(BUNDLE_NAME_DEFAULT, gameLangApi);
    }

    public I18nMessages(String bundleName, GameLangApi gameLangApi) {
        this.bundleName = bundleName;
        this.gameLangApi = gameLangApi;
    }

    /**
     * @param locale to load messages from
     * @return messages with given local or default english
     */
    public static ResourceBundle load(@Nullable Locale locale) {
        if (locale == null) {
            log.debug("No no locale given. Using fallback %s", LOCALE_FALLBACK);
            return ResourceBundle.getBundle(BUNDLE_NAME_DEFAULT, LOCALE_FALLBACK);
        }

        try {
            return ResourceBundle.getBundle(BUNDLE_NAME_DEFAULT, locale);
        } catch (MissingResourceException e) {
            log.debug("No translation messages for locale %s found. Using fallback %s", locale, LOCALE_FALLBACK);
            return ResourceBundle.getBundle(BUNDLE_NAME_DEFAULT, LOCALE_FALLBACK);
        }
    }

    /**
     * Uses current game language to load messages
     */
    public void loadWithCurrentGameLocale() {
        this.locale = gameLangApi.getCurrent();
        log.debug("loading messages for locale: %s", locale);
        this.messages = load(locale);
    }

    @Override
    public void initBeforeGameCreated() {
        loadWithCurrentGameLocale();
    }
}
