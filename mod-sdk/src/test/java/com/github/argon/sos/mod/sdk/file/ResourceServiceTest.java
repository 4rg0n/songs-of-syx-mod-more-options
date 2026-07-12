package com.github.argon.sos.mod.sdk.file;

import com.github.argon.sos.mod.testing.ModSdkExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

@ExtendWith(ModSdkExtension.class)
class ResourceServiceTest {

    private final ResourceService resourceService = new ResourceService();

    @Test
    void readResource() throws Exception {
        Assertions.assertThat(resourceService.read(Paths.get("not.present"))).isNull();

        String propertyContent = resourceService.read(Paths.get("test.properties"));
        Assertions.assertThat(propertyContent).isNotNull();
        Assertions.assertThat(propertyContent).contains("test=test");
    }

    @Test
    void readResourceLines() throws Exception {
        Assertions.assertThat(resourceService.readLines(Paths.get("not.present"))).isEqualTo(List.of());
        Assertions.assertThat(resourceService.readLines(Paths.get("test.properties"))).isEqualTo(List.of("test=test"));
    }

    @Test
    void readProperties() throws Exception  {
        Properties expected = new Properties();
        expected.put("test", "test");

        Assertions.assertThat(resourceService.readProperties(Paths.get("not.present"))).isNull();
        Assertions.assertThat(resourceService.readProperties(Paths.get("test.properties"))).isEqualTo(expected);
    }
}