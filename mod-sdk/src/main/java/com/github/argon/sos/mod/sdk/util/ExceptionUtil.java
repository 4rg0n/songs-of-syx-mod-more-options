package com.github.argon.sos.mod.sdk.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionUtil {

    /**
     * @return whether the needle exception is in the cause chain of the haystack exception
     */
    public static boolean contains(@Nullable Throwable haystack, @Nullable Throwable needle) {
        if (haystack == null || needle == null) {
            return false;
        }

        Throwable cause;
        Throwable current = haystack;

        while((cause = current.getCause()) != null && (current != cause) ) {
            if (needle.equals(cause)) {
                return true;
            }

            current = cause;
        }

        return false;
    }

    /**
     * @return when the last argument is a Throwable, it will be returned
     */
    @Nullable
    public static Throwable extractThrowableLast(Object[] args) {
        Object lastArg = null;
        int lastPos = args.length - 1;

        if (lastPos >= 0) {
            lastArg = args[lastPos];
        }

        if (lastArg instanceof Throwable) {
            return (Throwable) lastArg;
        }

        return null;
    }
}
