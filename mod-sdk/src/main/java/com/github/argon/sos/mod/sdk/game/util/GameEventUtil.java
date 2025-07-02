package com.github.argon.sos.mod.sdk.game.util;

import com.github.argon.sos.mod.sdk.util.ReflectionUtil;
import game.events.EVENTS;

public class GameEventUtil {

    /**
     * @return whether given event will execute or not
     */
    public static Boolean isEnabled(EVENTS.EventResource event) {
        return ReflectionUtil.getDeclaredField("supress", EVENTS.EventResource.class).map(field -> {
            // checks whether event is suppressed
            return !(Boolean) ReflectionUtil.getDeclaredFieldValue(field, event)
                .orElseThrow(() -> new RuntimeException("Got empty 'supress' from event class " + event.getClass().getName()));
        }).orElse(true);
    }
}
