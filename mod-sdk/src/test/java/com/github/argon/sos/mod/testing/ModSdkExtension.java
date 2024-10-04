package com.github.argon.sos.mod.testing;

import com.github.argon.sos.mod.sdk.file.ResourceService;
import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import com.github.argon.sos.mod.sdk.util.ReflectionUtil;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

public class ModSdkExtension implements TestInstancePostProcessor {

    private final static Logger log = Loggers.getLogger(ModSdkExtension.class);

    private final ResourceService resourceService = new ResourceService();

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) throws Exception {
        Loggers.setLevels(Level.DEBUG);
        injectTestResourceService(testInstance);
    }

    private void injectTestResourceService(Object testInstance) {
        ReflectionUtil.getDeclaredFieldsWithAnnotation(TestResourceService.class, testInstance.getClass()).stream()
            .filter(field -> ClassUtil.instanceOf(field.getType(), ResourceService.class))
            .filter(field -> !ReflectionUtil.getDeclaredFieldValue(field, testInstance).isPresent())
            .forEach(field -> {
                try {
                    ReflectionUtil.setField(field, testInstance, resourceService);
                } catch (IllegalAccessException e) {
                    log.warn("Could not inject TestResourceService into field %s#%s",
                        testInstance.getClass().getSimpleName(), field.getName());
                }
            });
    }
}
