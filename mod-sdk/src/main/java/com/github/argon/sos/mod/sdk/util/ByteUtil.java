package com.github.argon.sos.mod.sdk.util;

import com.github.argon.sos.mod.sdk.data.ByteUnit;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for dealing with byte amounts.
 */
@UtilityClass
public class ByteUtil {

    /**
     * Converts a given {@link String} to a {@link ByteUnit}
     *
     * @param bytesString to convert. E.g. 1G, 1024m, 10k
     * @return converted byte unit with the bytes as value
     */
    public static Optional<ByteUnit> toByteUnit(String bytesString) {
        Pattern pattern = Pattern.compile("(\\d*)([g|G|m|M|k|K])");
        Matcher matcher = pattern.matcher(bytesString);

        if (!matcher.matches()) {
            return Optional.empty();
        }

        double value = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2);

        return Optional.of(new ByteUnit(value, ByteUnit.Unit.of(unit)));
    }

    /**
     * Converts a byte amount into a given {@link ByteByteUnit.Unit.ByteUnit.Unit}.
     *
     * @param bytes to convert
     * @param unit to convert the bytes into
     * @return e.g. 1073741824 bytes with {@link ByteUnit.Unit#GIGABYTE} will be 1.0
     */
    public static Double fromBytes(double bytes, ByteUnit.Unit unit) {
        return bytes / unit.getBytes();
    }
}
