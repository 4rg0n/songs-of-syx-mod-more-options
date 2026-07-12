package com.github.argon.sos.mod.sdk.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Method;

/**
 * Utility class for {@link Method}s
 */
@UtilityClass
public class MethodUtil {
    /**
     * Checks whether given method is a "getter" method.
     *
     * @param method to check
     * @return whether a given method counts as a "getter" method, based on some naming conventions
     */
    public static boolean isGetterMethod(Method method) {
        // don't call methods with params
        if (method.getParameterCount() > 0) {
            return false;
        }
        String methodName = method.getName();

        // get()
        if (methodName.startsWith("get") && methodName.length() == 3) {
            return true;
        }

        // is()
        if (methodName.startsWith("is") && methodName.length() == 2) {
            return true;
        }

        // isXxx...()
        if (methodName.startsWith("is") && methodName.length() > 2 && Character.isUpperCase(methodName.charAt(2))) {
            return true;
        }

        // getXxx..()
        if (methodName.startsWith("get") && methodName.length() > 3 && Character.isUpperCase(methodName.charAt(3))) {
            return true;
        }

        return false;
    }

    /**
     * Checks whether given method is a "setter" method.
     *
     * @param method to check
     * @return whether a given method counts as a "setter" method, based on some naming conventions
     */
    public static boolean isSetterMethod(Method method) {
        // only methods with 1 parameter
        if (method.getParameterCount() != 1) {
            return false;
        }

        // only methods without return value
        if (!method.getReturnType().equals(Void.TYPE)) {
            return false;
        }

        String methodName = method.getName();

        // set(...)
        if (methodName.startsWith("set") && methodName.length() == 3) {
            return true;
        }

        // setXxx..(...)
        if (methodName.startsWith("set") && methodName.length() > 3 && Character.isUpperCase(methodName.charAt(3))) {
            return true;
        }

        return false;
    }

    /**
     * Extract the field name from a "getter" or "setter" method.
     *
     * @param method to extract the name from
     * @return the name of the method without "set", "get" or "is"
     */
    public static String extractSetterGetterFieldName(Method method) {
        String methodName = method.getName();
        String name = stripSetterGetterMethodPrefix(methodName);

        return StringUtil.unCapitalize(name);
    }

    private static String stripSetterGetterMethodPrefix(String methodName) {
        String name = "";

        if (methodName.length() <= 3) {
            if (methodName.equals("get") || methodName.equals("set") || methodName.equals("is")) {
                name = "";
            }
        } else if (methodName.startsWith("get") || methodName.startsWith("set")) {
            name = methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            name = methodName.substring(2);
        } else {
            name = methodName;
        }

        return name;
    }
}
