package com.github.argon.sos.mod.sdk.i18n;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.StringUtil;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Access to translated messages.
 * Will look for messages by a given key. See: {@link I18nMessages}
 */
public class I18nTranslator {
    private final static Logger log = Loggers.getLogger(I18nTranslator.class);
    private final static Map<String, I18nTranslator> i18ns = new HashMap<>();

    private final I18nMessages i18nMessages;

    /**
     * Used for adding a prefix to the translation key
     * e.g. when the class is RacesPanel.class, the prefix will be "RacesPanel".
     * Methods starting with "p" will use the prefix for their message key generation.
     */
    private final String prefix;

    public I18nTranslator(Class<?> clazz, I18nMessages i18nMessages) {
        this.i18nMessages = i18nMessages;
        this.prefix = prefix(clazz);
    }

    /**
     * Description: <pre>key + ".desc"</pre>
     *
     * @return found translation for ".desc" or the key
     */
    public String d(String key, Object... args) {
        return translate(key + ".desc", args);
    }

    /**
     * Prefixed Description: <pre>prefix + "." + key +  ".desc"</pre>
     *
     * @return found translation for ".desc" or the key
     */
    public String pd(String key, Object... args) {
        return translate(prefix + "." + key +  ".desc", args);
    }

    /**
     * Description: <pre>key + ".desc" nullable</pre>
     *
     * @return found translation for ".desc" or null
     */
    @Nullable
    public String dn(String key, Object... args) {
        return translateNullable(key + ".desc", args);
    }

    /**
     * Prefixed description: <pre>prefix + "." + key +  ".desc" nullable</pre>
     *
     * @return found translation for ".desc" or the key
     */
    @Nullable
    public String pdn(String key, Object... args) {
        return translateNullable(prefix + "." + key +  ".desc", args);
    }

    /**
     * Name: <pre>key + ".name"</pre>
     *
     * @return found translation for ".name" or the key
     */
    public String n(String key, Object... args) {
        return translate(key + ".name", args);
    }

    /**
     * Prefixed name: <pre>prefix + "." + key + ".name"</pre>
     *
     * @return found translation for ".name" or the key
     */
    public String pn(String key, Object... args) {
        return translate(prefix + "." + key +  ".name", args);
    }

    /**
     * @return found translation for key or the key itself
     */
    public String t(String key, Object... args) {
        return translate(key, args);
    }

    /**
     * @return found translation for key or null
     */
    @Nullable
    public String tn(@Nullable String key, Object... args) {
        return translateNullable(key, args);
    }
    private String translate(String key, Object[] args) {
        String translated = translateNullable(key, args);

        if (translated == null) {
            return key;
        }

        return translated;
    }

    @Nullable
    private String translateNullable(@Nullable String key, Object[] args) {
        if (key == null) {
            return null;
        }

        ResourceBundle messages = i18nMessages.getMessages();

        if (messages == null) {
            log.trace("No messages for translation");
            return null;
        }

        if (!messages.containsKey(key)) {
            log.trace("No key '%s' in messages for translation", key);
            return null;
        }

        try {
            String messageText = messages.getString(key);
            return StringUtil.replaceTokens(messageText, args);
        } catch (Exception e) {
            log.warn("Could not translate message for key '%s': %s", key, e.getMessage());
            log.trace("", e);
        }

        return null;
    }

    /**
     * @return simple class name
     */
    private static String prefix(Class<?> clazz) {
        return clazz.getSimpleName();
    }
}
