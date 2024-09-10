package com.github.argon.sos.mod.sdk.file;

import com.github.argon.sos.mod.sdk.util.Lists;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Properties;

class ResourceServiceTest {

    private final ResourceService resourceService = new ResourceService();

    @Test
    void readResource() throws Exception {
        Assertions.assertThat(resourceService.read(Paths.get("not.present"))).isNull();
        Assertions.assertThat(resourceService.read(Paths.get("test.properties"))).isNotNull();
        Assertions.assertThat(resourceService.read("not.present")).isEmpty();
        Assertions.assertThat(resourceService.read("test.properties")).isPresent();
        Assertions.assertThat(resourceService.read("test.properties")).hasValue("test=test\n");
    }

    @Test
    void readResourceLines() throws Exception {
        Assertions.assertThat(resourceService.readLines("not.present")).isEqualTo(Lists.of());
        Assertions.assertThat(resourceService.readLines("test.properties")).isEqualTo(Lists.of("test=test"));
    }

    @Test
    void readProperties() throws Exception  {
        Properties expected = new Properties();
        expected.put("test", "test");

        Assertions.assertThat(resourceService.readProperties("not.present")).isEmpty();
        Assertions.assertThat(resourceService.readProperties("test.properties")).hasValue(expected);
    }
}