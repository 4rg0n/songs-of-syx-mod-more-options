package com.github.argon.sos.mod.sdk.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {

    @Test
    void testToString() {
        Assertions.assertThat(StringUtil.toString(new Integer[]{1, 2, 3})).isEqualTo("[1, 2, 3]");
        Assertions.assertThat(StringUtil.toString(Maps.of("key1", 1, "key2", 2))).isEqualTo("{key1=1, key2=2}");

    }


    @Test
    void removeTrailing() {
        Assertions.assertThat(StringUtil.removeTrailing("test", "st")).isEqualTo("te");
        Assertions.assertThat(StringUtil.removeTrailing("test", "n")).isEqualTo("test");
        Assertions.assertThat(StringUtil.removeTrailing("test", "test")).isEqualTo("");
    }

    @Test
    void unCapitalize() {
        Assertions.assertThat(StringUtil.unCapitalize("Test")).isEqualTo("test");
        Assertions.assertThat(StringUtil.unCapitalize("test")).isEqualTo("test");
        Assertions.assertThat(StringUtil.unCapitalize("")).isEqualTo("");
    }

    @Test
    void extractTail() {
        Assertions.assertThat(StringUtil.extractTail("test.test.blub", "\\.")).isEqualTo("blub");
        Assertions.assertThat(StringUtil.extractTail("blub", "\\.")).isEqualTo("blub");
        Assertions.assertThat(StringUtil.extractTail("abc", "")).isEqualTo("c");
    }

    @Test
    void toStringPrimitiveArray() {
        Assertions.assertThat(StringUtil.toStringPrimitiveArray("test")).isEqualTo("test");
        Assertions.assertThat(StringUtil.toStringPrimitiveArray(null)).isEqualTo("null");
        Assertions.assertThat(StringUtil.toStringPrimitiveArray(new int[]{1})).isEqualTo("[1]");
        Assertions.assertThat(StringUtil.toStringPrimitiveArray(new long[]{1})).isEqualTo("[1]");
        Assertions.assertThat(StringUtil.toStringPrimitiveArray(new short[]{1})).isEqualTo("[1]");
        Assertions.assertThat(StringUtil.toStringPrimitiveArray(new byte[]{1})).isEqualTo("[1]");
        Assertions.assertThat(StringUtil.toStringPrimitiveArray(new boolean[]{true})).isEqualTo("[true]");
        Assertions.assertThat(StringUtil.toStringPrimitiveArray(new char[]{'a'})).isEqualTo("[a]");
        Assertions.assertThat(StringUtil.toStringPrimitiveArray(new float[]{1F})).isEqualTo("[1.0]");
        Assertions.assertThat(StringUtil.toStringPrimitiveArray(new double[]{1D})).isEqualTo("[1.0]");
    }

    @Test
    void stringifyValues() {
        Assertions.assertThat(StringUtil.stringifyValues(new Object[]{})).isEqualTo(new Object[]{});
        Assertions.assertThat(StringUtil.stringifyValues(new Object[]{
            "string",
            1,
            1.0D,
            1.0F,
            new boolean[]{false},
            new String[]{"string"},
            Maps.of("key1", 1, "key2", 2),
        })).isEqualTo(new Object[]{"string", "1", "1.0000", "1.0000", "[false]", "[string]", "{key1=1, key2=2}"});
    }

    @Test
    void shortenClassName() {
        Assertions.assertThat(StringUtil.shortenClassName(String.class)).isEqualTo("j.l.String");
    }

    @Test
    void shortenPackageName() {
        Assertions.assertThat(StringUtil.shortenPackageName("test.foo.bar")).isEqualTo("t.f.b");
        Assertions.assertThat(StringUtil.shortenPackageName("test")).isEqualTo("t");
        Assertions.assertThat(StringUtil.shortenPackageName("")).isEqualTo("");
        Assertions.assertThat(StringUtil.shortenPackageName("t")).isEqualTo("t");
        Assertions.assertThat(StringUtil.shortenPackageName(".")).isEqualTo("");
        Assertions.assertThat(StringUtil.shortenPackageName("test.")).isEqualTo("t");
        Assertions.assertThat(StringUtil.shortenPackageName(".test")).isEqualTo("t");
    }

    @Test
    void cutOrFill() {
        Assertions.assertThat(StringUtil.cutOrFill("test", 4, false)).isEqualTo("test");
        Assertions.assertThat(StringUtil.cutOrFill("test", 3, false)).isEqualTo("est");
        Assertions.assertThat(StringUtil.cutOrFill("test", 3, true)).isEqualTo("tes");
        Assertions.assertThat(StringUtil.cutOrFill("test", 5, false)).isEqualTo("test ");
    }

    @Test
    void repeat() {
        Assertions.assertThat(StringUtil.repeat('a', 3)).isEqualTo("aaa");
        Assertions.assertThat(StringUtil.repeat('a', 0)).isEqualTo("");
    }

    @Test
    void capitalize() {
        Assertions.assertThat(StringUtil.capitalize("test")).isEqualTo("Test");
        Assertions.assertThat(StringUtil.capitalize("Test")).isEqualTo("Test");
        Assertions.assertThat(StringUtil.capitalize("")).isEqualTo("");
    }

    @Test
    void unwrap() {
        Assertions.assertThat(StringUtil.unwrap("{test}", '{', '}')).isEqualTo("test");
        Assertions.assertThat(StringUtil.unwrap("[test}", '{', '}')).isEqualTo("[test");
        Assertions.assertThat(StringUtil.unwrap("{test]", '{', '}')).isEqualTo("test]");
        Assertions.assertThat(StringUtil.unwrap("", '{', '}')).isEqualTo("");
    }

    @Test
    void quote() {
        Assertions.assertThat(StringUtil.quote("test")).isEqualTo("\"test\"");
        Assertions.assertThat(StringUtil.quote("")).isEqualTo("\"\"");
        Assertions.assertThat(StringUtil.quote(Lists.of("test", "test"))).isEqualTo(Lists.of("\"test\"", "\"test\""));
    }

    @Test
    void unquote() {
        Assertions.assertThat(StringUtil.unquote("\"test\"")).isEqualTo("test");
        Assertions.assertThat(StringUtil.unquote("test")).isEqualTo("test");
        Assertions.assertThat(StringUtil.unquote("")).isEqualTo("");
    }

    @Test
    void toScreamingSnakeCase() {
        Assertions.assertThat(StringUtil.toScreamingSnakeCase("")).isEqualTo("");
        Assertions.assertThat(StringUtil.toScreamingSnakeCase("test")).isEqualTo("TEST");
        Assertions.assertThat(StringUtil.toScreamingSnakeCase("testTest")).isEqualTo("TEST_TEST");
        Assertions.assertThat(StringUtil.toScreamingSnakeCase("TestTest")).isEqualTo("TEST_TEST");
    }

    @Test
    void replaceTokens() {
        Assertions.assertThat(StringUtil.replaceTokens("{0} {1}", 1, "test")).isEqualTo("1 test");
        Assertions.assertThat(StringUtil.replaceTokens("{1} {0}", 1, "test")).isEqualTo("test 1");
        Assertions.assertThat(StringUtil.replaceTokens("", 1, "test")).isEqualTo("");
    }

    @Test
    void removeFileExtension() {
        Assertions.assertThat(StringUtil.removeFileExtension("")).isEqualTo("");
        Assertions.assertThat(StringUtil.removeFileExtension("test.txt")).isEqualTo("test");
        Assertions.assertThat(StringUtil.removeFileExtension("test.txt.txt")).isEqualTo("test.txt");
        Assertions.assertThat(StringUtil.removeFileExtension("test")).isEqualTo("test");
    }

    @Test
    void removeBeginning() {
        Assertions.assertThat(StringUtil.removeBeginning("", "test")).isEqualTo("");
        Assertions.assertThat(StringUtil.removeBeginning("test", "test")).isEqualTo("");
        Assertions.assertThat(StringUtil.removeBeginning("testtest", "test")).isEqualTo("test");
    }

    @Test
    void wrap() {
        Assertions.assertThat(StringUtil.wrap("", "'")).isEqualTo("''");
        Assertions.assertThat(StringUtil.wrap("", "")).isEqualTo("");
        Assertions.assertThat(StringUtil.wrap("test", "'")).isEqualTo("'test'");
    }

    @Test
    void countLines() {
        Assertions.assertThat(StringUtil.countLines("")).isEqualTo(1);
        Assertions.assertThat(StringUtil.countLines("test")).isEqualTo(1);
        Assertions.assertThat(StringUtil.countLines("test\ntest")).isEqualTo(2);
        Assertions.assertThat(StringUtil.countLines("test\ntest\n")).isEqualTo(3);
    }

    @Test
    void countChar() {
        Assertions.assertThat(StringUtil.countChar("test", 't')).isEqualTo(2);
        Assertions.assertThat(StringUtil.countChar("", 't')).isEqualTo(0);
        Assertions.assertThat(StringUtil.countChar(null, 't')).isEqualTo(0);
    }

    @Test
    void isNumeric() {
        Assertions.assertThat(StringUtil.isNumeric("")).isFalse();
        Assertions.assertThat(StringUtil.isNumeric('a')).isFalse();
        Assertions.assertThat(StringUtil.isNumeric('1')).isTrue();
        Assertions.assertThat(StringUtil.isNumeric("")).isFalse();
        Assertions.assertThat(StringUtil.isNumeric("a")).isFalse();
        Assertions.assertThat(StringUtil.isNumeric("1")).isTrue();
        Assertions.assertThat(StringUtil.isNumeric("1.0")).isTrue();
    }

    @Test
    void isInteger() {
        Assertions.assertThat(StringUtil.isInteger(null)).isFalse();
        Assertions.assertThat(StringUtil.isInteger("")).isFalse();
        Assertions.assertThat(StringUtil.isInteger("a")).isFalse();
        Assertions.assertThat(StringUtil.isInteger("1")).isTrue();
        Assertions.assertThat(StringUtil.isInteger("1.0")).isFalse();
    }

    @Test
    void isDecimal() {
        Assertions.assertThat(StringUtil.isDecimal(null)).isFalse();
        Assertions.assertThat(StringUtil.isDecimal("")).isFalse();
        Assertions.assertThat(StringUtil.isDecimal("a")).isFalse();
        Assertions.assertThat(StringUtil.isDecimal("1")).isFalse();
        Assertions.assertThat(StringUtil.isDecimal("1.0")).isTrue();
    }
}