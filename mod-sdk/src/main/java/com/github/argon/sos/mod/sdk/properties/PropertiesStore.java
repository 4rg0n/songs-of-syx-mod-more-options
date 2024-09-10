package com.github.argon.sos.mod.sdk.properties;

import com.github.argon.sos.mod.sdk.file.ResourceService;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**
 * Holds and serves the mod properties
 */
public class PropertiesStore {

    private final static Logger log = Loggers.getLogger(PropertiesStore.class);

    public final static String MOD_PROPERTIES_PATH_DEFAULT = "mod.properties";

    private final String modPropertiesPath;

    private final ResourceService resourceService;

    @Nullable
    private ModSdkProperties modSdkProperties;

    @Nullable
    private Properties properties;

    public PropertiesStore(ResourceService resourceService) {
        this(resourceService, MOD_PROPERTIES_PATH_DEFAULT);
    }

    public PropertiesStore(ResourceService resourceService, String modPropertiesPath) {
        this.modPropertiesPath = modPropertiesPath;
        this.resourceService = resourceService;
    }

    /**
     * @return {@link ModSdkProperties} when present
     * @throws PropertiesException when reading the properties fails
     */
    public Optional<ModSdkProperties> getModSdkProperties() {
        if (modSdkProperties == null) {
            this.modSdkProperties = getProperties()
                .map(properties -> ModSdkProperties.builder()
                    .errorReportUrl(properties.getProperty("error.report.url"))
                    .build()
                ).orElse(null);
        }

        return Optional.ofNullable(modSdkProperties);
    }

    /**
     * @return raw properties with key value
     */
    public Optional<Properties> getProperties() {
        if (properties == null) {
            try {
                resourceService.readProperties(modPropertiesPath)
                    .ifPresent(prop -> properties = prop);
            } catch (IOException e) {
                throw new PropertiesException(String.format("Could not read properties from resource %s", modPropertiesPath), e);
            }
        }

        return Optional.ofNullable(properties);
    }
}
