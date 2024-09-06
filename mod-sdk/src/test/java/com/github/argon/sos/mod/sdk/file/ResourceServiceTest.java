package com.github.argon.sos.mod.sdk.file;

import com.github.argon.sos.mod.sdk.util.Lists;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

class ResourceServiceTest {

    private final ResourceService resourceService = ResourceService.getInstance();

    @Test
    void getResourcePath() {
        Assertions.assertThat(resourceService.getResourcePath("test.properties")).isPresent();
        Assertions.assertThat(resourceService.getResourcePath("test.properties")).get()
            .matches(path -> "test.properties".equals(path.getFileName().toString()));
        Assertions.assertThat(resourceService.getResourcePath("not.present")).isEmpty();
    }

    @Test
    void readResource() throws Exception {
        Assertions.assertThat(resourceService.readResource("not.present")).isEmpty();
        Assertions.assertThat(resourceService.readResource("test.properties")).isPresent();
        Assertions.assertThat(resourceService.readResource("test.properties")).hasValue("test=test\n");
    }

    @Test
    void getInputStream() {
        Assertions.assertThat(resourceService.getInputStream("not.present")).isNull();
        Assertions.assertThat(resourceService.getInputStream("test.properties")).isNotNull();
    }

    @Test
    void readResourceLines() throws Exception {
        Assertions.assertThat(resourceService.readResourceLines("not.present")).isEqualTo(Lists.of());
        Assertions.assertThat(resourceService.readResourceLines("test.properties")).isEqualTo(Lists.of("test=test"));
    }

    @Test
    void readProperties() throws Exception  {
        Properties expected = new Properties();
        expected.put("test", "test");

        Assertions.assertThat(resourceService.readProperties("not.present")).isEmpty();
        Assertions.assertThat(resourceService.readProperties("test.properties")).hasValue(expected);
    }
}