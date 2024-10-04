package com.github.argon.sos.mod.testing;

import init.paths.PATHS;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.util.Arrays;
import java.util.Optional;

/**
 * Will provide game classes and resources for testing
 */
public class GameExtension implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) throws Exception {
        // init paths
        String[] modNames = extractMods(testInstance.getClass());
        PATHS.init(modNames, "en", false);
    }

    private String[] extractMods(Class<?> testClass) {
        return Arrays.stream(
                Optional.ofNullable(testClass.getAnnotation(TestMods.class))
                .map(TestMods::value)
                .orElse(new String[]{}))
            .toArray(String[]::new);
    }
}
