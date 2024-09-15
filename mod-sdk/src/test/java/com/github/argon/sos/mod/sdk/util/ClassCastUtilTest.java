package com.github.argon.sos.mod.sdk.util;

import com.github.argon.sos.mod.sdk.testing.ModSdkExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ModSdkExtension.class)
class ClassCastUtilTest {

    @Test
    void toLong() {
        assertThat(ClassCastUtil.toLong(1)).isEqualTo(Long.valueOf(1L));
        assertThat(ClassCastUtil.toLong(-1)).isEqualTo(Long.valueOf(-1L));
    }

    @Test
    void toByte() {
        assertThat(ClassCastUtil.toByte(1)).isEqualTo(Byte.valueOf((byte) 1));
        assertThat(ClassCastUtil.toByte(-1)).isEqualTo(Byte.valueOf((byte) -1));
        // max byte is 63
        assertThat(ClassCastUtil.toByte(999999)).isEqualTo(Byte.valueOf((byte) 63));
        assertThat(ClassCastUtil.toByte(-999999)).isEqualTo(Byte.valueOf((byte) -63));
    }

    @Test
    void toShort() {
        assertThat(ClassCastUtil.toShort(1)).isEqualTo(Short.valueOf((short) 1));
        assertThat(ClassCastUtil.toShort(-1)).isEqualTo(Short.valueOf((short) -1));
        // max byte is 16959
        assertThat(ClassCastUtil.toShort(999999)).isEqualTo(Short.valueOf((short) 16959));
        assertThat(ClassCastUtil.toShort(-999999)).isEqualTo(Short.valueOf((short) -16959));
    }

    @Test
    void toFloat() {
        assertThat(ClassCastUtil.toFloat(1.0)).isEqualTo(Float.valueOf(1.0F));
        assertThat(ClassCastUtil.toFloat(-1.0)).isEqualTo(Float.valueOf(-1.0F));
    }

    @Test
    void toBigDecimal() {
        assertThat(ClassCastUtil.toBigDecimal(1.0)).isEqualTo(BigDecimal.valueOf(1.0D));
        assertThat(ClassCastUtil.toBigDecimal(-1.0)).isEqualTo(BigDecimal.valueOf(-1.0D));
    }

    @Test
    void toEnum() {
        assertThat(ClassCastUtil.toEnum("TEST1", TestEnum.class)).isEqualTo(TestEnum.TEST1);
    }

    @Test
    void toStringArray() {
        TestEnum[] enums = new TestEnum[]{TestEnum.TEST1, TestEnum.TEST2};
        String[] expected = new String[]{"TEST1", "TEST2"};
        assertThat(ClassCastUtil.toStringArray(enums)).isEqualTo(expected);
    }

    @Test
    void toStringArrayString() {
        String[] expected = new String[]{"TEST1", "TEST2"};
        assertThat(ClassCastUtil.toStringArrayString(Lists.of("TEST1", "TEST2"))).isEqualTo(expected);
    }

    @Test
    void toStringArrayEnum() {
        String[] expected = new String[]{"TEST1", "TEST2"};
        assertThat(ClassCastUtil.toStringArrayEnum(Lists.of(TestEnum.TEST1, TestEnum.TEST2))).isEqualTo(expected);
    }

    @Test
    void testToString() {
        assertThat(ClassCastUtil.toString(TestEnum.TEST1)).isEqualTo("TEST1");
        assertThat(ClassCastUtil.toString("TEST1")).isEqualTo("TEST1");
        assertThat(ClassCastUtil.toString(LocalDate.of(1970, 1, 1))).isEqualTo("1970-01-01");
        assertThat(ClassCastUtil.toString(Integer.valueOf(1))).isEqualTo("1");
    }

    @Test
    void toEnumArray() {
        TestEnum[] expected = new TestEnum[]{TestEnum.TEST1, TestEnum.TEST2};
        assertThat(ClassCastUtil.toEnumArray(new String[]{"TEST1", "TEST2"}, TestEnum.class)).isEqualTo(expected);
    }

    @Test
    void toDoubleArray() {
        double[] actual = {1.0D, 2.0D};
        Double[] expected = new Double[]{1.0D, 2.0D};
        assertThat(ClassCastUtil.toDoubleArray(actual)).isEqualTo(expected);
    }

    @Test
    void toFloatArray() {
        double[] actual = {1.0D, 2.0D};
        Float[] expected = new Float[]{1.0F, 2.0F};
        assertThat(ClassCastUtil.toFloatArray(actual)).isEqualTo(expected);
    }

    @Test
    void toIntegerArray() {
        int[] actual = {1, 2};
        Integer[] expected = new Integer[]{1, 2};
        assertThat(ClassCastUtil.toIntegerArray(actual)).isEqualTo(expected);
    }

    @Test
    void toLongArray() {
        int[] actual = {1, 2};
        Long[] expected = new Long[]{1L, 2L};
        assertThat(ClassCastUtil.toLongArray(actual)).isEqualTo(expected);
    }

    @Test
    void toByteArray() {
        int[] actual = {1, 2};
        Byte[] expected = new Byte[]{1, 2};
        assertThat(ClassCastUtil.toByteArray(actual)).isEqualTo(expected);
    }

    @Test
    void toShortArray() {
        int[] actual = {1, 2};
        Short[] expected = new Short[]{1, 2};
        assertThat(ClassCastUtil.toShortArray(actual)).isEqualTo(expected);
    }

    @Test
    void toBigDecimalArray() {
        double[] actual = {1.0D, 2.0D};
        BigDecimal[] expected = new BigDecimal[]{BigDecimal.valueOf(1.0D), BigDecimal.valueOf(2.0D)};
        assertThat(ClassCastUtil.toBigDecimalArray(actual)).isEqualTo(expected);
    }

    @Test
    void isArray() {
        double[] doubles = {1.0D, 2.0D};
        String[] strings = new String[]{"TEST1", "TEST2"};
        assertThat(ClassCastUtil.isArray(doubles)).isTrue();
        assertThat(ClassCastUtil.isArray(strings)).isTrue();
        assertThat(ClassCastUtil.isArray("STRING")).isFalse();
    }

    @Test
    void toCollection() {
        assertThat(ClassCastUtil.toCollection(new String[]{"TEST1", "TEST2"})).isEqualTo(Lists.of("TEST1", "TEST2"));
    }

    private static enum TestEnum {
        TEST1,
        TEST2
    }
}