package com.github.argon.sos.moreoptions.properties;

import com.github.argon.sos.moreoptions.io.ResourceService;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

public class PropertiesStore {

    private final static Logger log = Loggers.getLogger(PropertiesStore.class);

    public final static String MOD_PROPERTIES_PATH = "mod.properties";

    @Getter(lazy = true)
    private final static PropertiesStore instance = new PropertiesStore(
        ResourceService.getInstance()
    );

    private final ResourceService resourceService;

    @Nullable
    private ModProperties modProperties;

    private PropertiesStore(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public Optional<ModProperties> getModProperties() {
        if (modProperties == null) {
            try {
                this.modProperties = resourceService.readProperties(MOD_PROPERTIES_PATH)
                    .map(properties -> ModProperties.builder()
                        .errorReportUrl(properties.getProperty("error.report.url"))
                        .build()
                    ).orElse(null);
            } catch (IOException e) {
                throw new PropertiesException(String.format("Could not load properties from %s", MOD_PROPERTIES_PATH), e);
            }
        }

        return Optional.ofNullable(modProperties);
    }
}
