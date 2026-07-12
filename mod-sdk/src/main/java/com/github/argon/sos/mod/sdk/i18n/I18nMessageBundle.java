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
 * Loads and provides access to localized message bundles.
 * Message .properties files are located in the "resources" folder.
 */
public class I18nMessageBundle implements Phases {

    private final static Logger log = Loggers.getLogger(I18nMessageBundle.class);
    /**
     * Used as default bundle name.
     * The bundle name is the name of the properties file located in the .jar file in src/main/resources.
     * In this case, the files would be named:
     * i18n.properties, i18n_fr.properties, i18n_ru.properties... and so on.
     */
    public static final String BUNDLE_NAME_DEFAULT = "i18n";
    /**
     * Fallback locale used when no translation for the current locale is found.
     */
    public final static Locale LOCALE_FALLBACK = GameLangApi.DEFAULT_LOCALE;

    private final String bundleName;
    private final GameLangApi gameLangApi;

    @Getter
    @Nullable
    private Locale locale;
    @Getter
    @Nullable
    private ResourceBundle messages;

    /**
     * Creates a new {@link I18nMessageBundle} with {@link I18nMessageBundle#BUNDLE_NAME_DEFAULT} as bundle name.
     *
     * @param gameLangApi used for accessing the games language
     */
    public I18nMessageBundle(GameLangApi gameLangApi) {
        this(BUNDLE_NAME_DEFAULT, gameLangApi);
    }

    /**
     * Creates a new {@link I18nMessageBundle} with given bundle name.
     *
     * @param bundleName prefix name for the translation property files
     * @param gameLangApi used for accessing the games language
     */
    public I18nMessageBundle(String bundleName, GameLangApi gameLangApi) {
        this.bundleName = bundleName;
        this.gameLangApi = gameLangApi;
    }

    /**
     * Reads the translation message properties for the given {@link Locale}.
     * If the {@link Locale} is null, the {@link I18nMessageBundle#LOCALE_FALLBACK} will be used.
     * Also, if there's no message properties file for the given {@link Locale},
     * the {@link I18nMessageBundle#LOCALE_FALLBACK} will also be used.
     *
     * @param locale to load messages from
     * @return messages with given local or default english
     */
    public ResourceBundle load(@Nullable Locale locale) {
        log.debug("Loading message bundle %s for locale %s", bundleName, locale);
        if (locale == null) {
            log.debug("No no locale given. Using fallback %s", LOCALE_FALLBACK);
            return ResourceBundle.getBundle(bundleName, LOCALE_FALLBACK);
        }

        try {
            return ResourceBundle.getBundle(bundleName, locale);
        } catch (MissingResourceException e) {
            log.warn("No translation messages for locale %s found. Using fallback %s", locale, LOCALE_FALLBACK);
            return ResourceBundle.getBundle(bundleName, LOCALE_FALLBACK);
        }
    }

    /**
     * Uses current game language to load messages
     */
    public void loadWithCurrentGameLocale() {
        this.locale = gameLangApi.getCurrent();
        this.messages = load(locale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initBeforeGameCreated() {
        loadWithCurrentGameLocale();
    }
}
