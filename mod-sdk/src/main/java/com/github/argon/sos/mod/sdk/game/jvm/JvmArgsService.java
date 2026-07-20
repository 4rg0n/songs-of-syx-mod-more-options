package com.github.argon.sos.mod.sdk.game.jvm;

import com.github.argon.sos.mod.sdk.file.FileService;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@RequiredArgsConstructor
public class JvmArgsService {
    private final static Logger log = Loggers.getLogger(JvmArgsService.class);

    private final FileService fileService;
    private final Path jvmArgsFile;

    public Optional<JvmArgs> read() {
        String content = null;

        try {
            content = fileService.read(jvmArgsFile);
        } catch (IOException e) {
            log.warn("Could not read jvm arguments from %s", jvmArgsFile, e);
        }

        if (content == null) {
            return Optional.empty();
        }

        return Optional.of(JvmArgs.fromString(content));
    }

    @Nullable
    public Path write(@Nullable final JvmArgs jvmArgs)  {
        String content = "";
        if (jvmArgs != null) {
            content = jvmArgs.toString();
        }

        try {
            fileService.write(jvmArgsFile, content);
        } catch (IOException e) {
            log.error("Error while writing JVM arguments.", e);
            return null;
        }

        return jvmArgsFile;
    }
}
