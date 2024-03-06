package com.github.argon.sos.moreoptions.i18n;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.StringUtil;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class I18n {
    private final static Logger log = Loggers.getLogger(I18n.class);
    private final static Map<String, I18n> i18ns = new HashMap<>();

    private final static String PREFIX_TO_REMOVE_FROM_CLASS = "com.github.argon.sos.moreoptions.";

    @Getter
    private final Class<?> clazz;
    private final I18nMessages i18nMessages;
    private final String prefix;

    public I18n(Class<?> clazz, I18nMessages i18nMessages) {
        this.clazz = clazz;
        this.i18nMessages = i18nMessages;
        this.prefix = prefix(clazz);
    }

    public static String prefix(Class<?> clazz) {
        return clazz.getName().replace(PREFIX_TO_REMOVE_FROM_CLASS, "");
    }

    public static I18n get(Class<?> clazz) {
        if (!i18ns.containsKey(clazz.getName())) {
            i18ns.put(clazz.getName(), new I18n(clazz, I18nMessages.getInstance()));
        }

        return i18ns.get(clazz.getName());
    }

    public String d(String key, Object... args) {
        return translate(key + ".desc", args);
    }

    public String n(String key, Object... args) {
        return translate(key + ".name", args);
    }

    public String t(String key, Object... args) {
        return translate(key, args);
    }

    private String translate(String key, Object[] args) {
        ResourceBundle messages = i18nMessages.getMessages();
        String messageKey = prefix + "." + key;

        if (messages == null) {
            // todo this could spam a lot :x
            log.warn("No messages for translation");
            return key;
        }

        if (!messages.containsKey(messageKey)) {
            log.debug("No key '%s' in messages for translation", messageKey);
            return key;
        }

        try {
            String messageText = messages.getString(messageKey);
            return StringUtil.replaceTokens(messageText, args);
        } catch (Exception e) {
            log.warn("Could not translate message for key '%s': %s", messageKey, e.getMessage());
            log.trace("", e);
        }

        return key;
    }
}
