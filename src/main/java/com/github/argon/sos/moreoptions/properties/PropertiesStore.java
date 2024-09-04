package com.github.argon.sos.moreoptions.properties;

import com.github.argon.sos.moreoptions.io.ResourceService;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PropertiesStore {

    private final static Logger log = Loggers.getLogger(PropertiesStore.class);

    public final static String MOD_PROPERTIES_FILENAME = "mod.properties";

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
            this.modProperties = resourceService.readProperties(MOD_PROPERTIES_FILENAME)
                .map(properties -> ModProperties.builder()
                    .errorReportUrl(properties.getProperty("error.report.url"))
                    .build()
                ).orElse(null);
        }

        return Optional.ofNullable(modProperties);
    }
}
