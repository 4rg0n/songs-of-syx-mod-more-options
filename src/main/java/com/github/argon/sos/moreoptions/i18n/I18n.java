package com.github.argon.sos.moreoptions.i18n;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.StringUtil;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class I18n {
    private final static Logger log = Loggers.getLogger(I18n.class);
    private final static Map<String, I18n> i18ns = new HashMap<>();

    private final I18nMessages i18nMessages;

    private final String prefix;

    public I18n(Class<?> clazz, I18nMessages i18nMessages) {
        this.i18nMessages = i18nMessages;
        this.prefix = prefix(clazz);
    }

    public static String prefix(Class<?> clazz) {
        return clazz.getSimpleName();
    }

    public static I18n get(Class<?> clazz) {
        if (!i18ns.containsKey(clazz.getName())) {
            i18ns.put(clazz.getName(), new I18n(clazz, I18nMessages.getInstance()));
        }

        return i18ns.get(clazz.getName());
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

    public String t(String key, Object... args) {
        return translate(key, args);
    }

    @Nullable
    public String tn(String key, Object... args) {
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
    private String translateNullable(String key, Object[] args) {
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
}
