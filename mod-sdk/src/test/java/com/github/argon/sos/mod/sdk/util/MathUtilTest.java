package com.github.argon.sos.mod.sdk.util;

import com.github.argon.sos.mod.sdk.testing.ModSdkExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ModSdkExtension.class)
class MathUtilTest {

    @Test
    void sum() {
        Assertions.assertThat(MathUtil.sum(Lists.of(1, 2, 3))).isEqualTo(6);
        Assertions.assertThat(MathUtil.sum(Lists.of(0, 0, 0))).isEqualTo(0);
        Assertions.assertThat(MathUtil.sum(Lists.of())).isEqualTo(0);
        Assertions.assertThat(MathUtil.sum(null)).isEqualTo(0);
    }

    @Test
    void toPercentage() {
        Assertions.assertThat(MathUtil.toPercentage(100)).isEqualTo(1.00);
        Assertions.assertThat(MathUtil.toPercentage(100D)).isEqualTo(1.00);
    }

    @Test
    void fromPercentage() {
        Assertions.assertThat(MathUtil.fromPercentage(1.0D)).isEqualTo(100);
    }

    @Test
    void fromResolution() {
        Assertions.assertThat(MathUtil.fromResolution(1.0D, 3)).isEqualTo(1000);
    }

    @Test
    void toResolution() {
        Assertions.assertThat(MathUtil.toResolution(1000.0D, 3)).isEqualTo(1.0D);

    }

    @Test
    void precisionMulti() {
        Assertions.assertThat(MathUtil.precisionMulti(2)).isEqualTo(10);
    }

    @Test
    void nearest() {
        Assertions.assertThat(MathUtil.nearest(11, Lists.of(10, 23, 47, 13))).isEqualTo(10);
    }
}