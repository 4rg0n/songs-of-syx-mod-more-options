package com.github.argon.sos.mod.sdk.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for {@link Exception}s / {@link Throwable}s.
 */
@UtilityClass
public class ExceptionUtil {

    /**
     * Checks whether the needle exception is in the cause chain of the haystack exception.
     *
     * @param haystack exception to search the cause chain of
     * @param needle exception to search for
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
     * Extracts the last object from an array of objects.
     * If it is an {@link Throwable}, it will be returned.
     *
     * @param args to extract the last {@link Throwable} from
     * @return when the last argument is a Throwable, it will be returned. Else it will return null.
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
