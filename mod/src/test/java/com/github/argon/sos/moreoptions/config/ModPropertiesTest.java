package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.properties.PropertiesStore;
import com.github.argon.sos.moreoptions.testing.ModExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ModExtension.class)
class ModPropertiesTest {
    @Test
    void test() {
        PropertiesStore propertiesStore = ModSdkModule.propertiesStore();

        propertiesStore.bind(ModProperties.class, Paths.get("mo-mod.properties"), false);
        Optional<ModProperties> modProperties = propertiesStore.get(ModProperties.class);

        assertThat(modProperties).isPresent();
        assertThat(modProperties.get().getErrorReportUrl()).isNotNull();
    }
}