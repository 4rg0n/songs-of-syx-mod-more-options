package com.github.argon.sos.mod.sdk.game.jvm;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class JvmArgs {

    @Getter
    private final List<JvmArg> jvmArgs;

    public void addArg(@Nullable final String key, final String value) {
        jvmArgs.add(new JvmArg(key, value));
    }

    public void removeByKey(final String key) {
        getByKey(key).forEach(jvmArgs::remove);
    }

    public List<JvmArg> getByKey(String key) {
        return jvmArgs.stream()
            .filter(jvmArg -> key.equals(jvmArg.key()))
            .toList();
    }

    public List<JvmArg> getByValue(String value) {
        return jvmArgs.stream()
            .filter(jvmArg -> value.equals(jvmArg.value()))
            .toList();
    }

    public void removeByValue(final String value) {
        getByValue(value).forEach(jvmArgs::remove);
    }

    public List<JvmArg> getByValueContains(String value) {
        return jvmArgs.stream()
            .filter(jvmArg -> jvmArg.value().contains(value))
            .toList();
    }

    private static List<JvmArg> parse(@Nullable final String content) {
        List<JvmArg> jvmArgs = new ArrayList<>();

        if (content == null) {
            return jvmArgs;
        }

        JvmArgsParser jvmArgsParser = new JvmArgsParser(content);

        while (!jvmArgsParser.atEnd()) {
            String value = jvmArgsParser.getNextValue();

            if (value.contains("=")) {
                String[] split = value.split("=");
                jvmArgs.add(new JvmArg(split[0], split[1]));
            } else {
                jvmArgs.add(new JvmArg(null, value));
            }

            jvmArgsParser.indexMove();
        }

        return jvmArgs;
    }

    public static JvmArgs fromString(@Nullable final String content) {
        return new JvmArgs(parse(content));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        jvmArgs.forEach(jvmArg -> {
            stringBuilder.append(jvmArg);
            stringBuilder.append("\n");
        });

        return stringBuilder.toString();
    }

    public record JvmArg(@Nullable String key, String value) {
        @NonNull
        @Override
        public String toString() {
            if (key == null) {
                return value;
            }

            return key + "=" + value;
        }
    }
}
