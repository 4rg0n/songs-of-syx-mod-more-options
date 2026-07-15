package com.github.argon.sos.mod.sdk.util;

import com.github.argon.sos.mod.sdk.data.ByteUnit;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class ArgsUtil {

    public static Optional<ByteUnit> extractByteUnit(String argument) {
        String bytesString;

        if (argument.startsWith("-Xmx")) {
            bytesString = argument.replace("-Xmx", "");
        } else if (argument.startsWith("-Xms")) {
            bytesString = argument.replace("-Xms", "");
        } else {
            bytesString = argument;
        }

        return ByteUtil.toByteUnit(bytesString);
    }
}
