package com.github.argon.sos.mod.sdk.i18n;

import java.util.HashMap;
import java.util.Map;

/**
 * Access to translated messages.
 * Will look for messages by a given key. See: {@link I18nMessageBundle}
 */
public class I18n {
    private final static Map<String, I18nTranslator> i18ns = new HashMap<>();

    private final I18nMessageBundle i18NMessageBundle;

    public I18n(I18nMessageBundle i18NMessageBundle) {
        this.i18NMessageBundle = i18NMessageBundle;
    }

    /**
     * @param clazz will be used for prefix
     * @return built translation class
     */
    public I18nTranslator get(Class<?> clazz) {
        if (!i18ns.containsKey(clazz.getName())) {
            i18ns.put(clazz.getName(), new I18nTranslator(clazz, i18NMessageBundle));
        }

        return i18ns.get(clazz.getName());
    }
}
