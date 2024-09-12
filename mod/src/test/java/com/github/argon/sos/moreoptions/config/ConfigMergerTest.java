package com.github.argon.sos.moreoptions.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigMergerTest {

    @Test
    void merge() {
        // Nothing should happen
        assertThat(ConfigMerger.merge(TestData.newConfig(), TestData.newConfig())).isEqualTo(TestData.newConfig());
        assertThat(ConfigMerger.merge(TestData.newEmptyConfig(), TestData.newEmptyConfig())).isEqualTo(TestData.newEmptyConfig());

        assertThat(ConfigMerger.merge(TestData.newConfig(), TestData.newEmptyConfig())).isEqualTo(TestData.newMergedEmptyConfig());
        assertThat(ConfigMerger.merge(TestData.newConfig(), TestData.newDefaultConfig())).isEqualTo(TestData.newMergedDefaultConfig());
    }
}