package com.github.argon.sos.mod.sdk.i18n;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;

import java.util.HashMap;
import java.util.Map;

/**
 * Access to translated messages.
 * Will look for messages by a given key. See: {@link I18nMessages}
 */
public class I18n {
    private final static Map<String, I18nTranslator> i18ns = new HashMap<>();

    private final I18nMessages i18nMessages;

    public I18n(I18nMessages i18nMessages) {
        this.i18nMessages = i18nMessages;
    }

    /**
     * @param clazz will be used for prefix
     * @return built translation class
     */
    public I18nTranslator get(Class<?> clazz) {
        if (!i18ns.containsKey(clazz.getName())) {
            i18ns.put(clazz.getName(), new I18nTranslator(clazz, i18nMessages));
        }

        return i18ns.get(clazz.getName());
    }
}
