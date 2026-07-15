package com.github.argon.sos.mod.sdk.game.jvm;

import com.github.argon.sos.mod.sdk.file.FileService;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

@RequiredArgsConstructor
public class JvmArgsService {
    private final static Logger log = Loggers.getLogger(JvmArgsService.class);

    public final static String JVM_ARGS_LAUNCHER_FILE = "jvmargs-launcher.txt";
    public final static Path JVM_ARGS_LAUNCHER_FOLDER = Path.of(System.getProperty("user.dir"));
    public final static Path JVM_ARGS_LAUNCHER_FILE_PATH = JVM_ARGS_LAUNCHER_FOLDER.resolve(JVM_ARGS_LAUNCHER_FILE);

    private final FileService fileService;

    public JvmArgs read() {
        String content = null;

        try {
            content = fileService.read(JVM_ARGS_LAUNCHER_FILE_PATH);
        } catch (IOException e) {
            log.warn("Could not read jvm arguments from %s", JVM_ARGS_LAUNCHER_FILE_PATH, e);
        }

        return JvmArgs.fromString(content);
    }

    public void write(@Nullable final JvmArgs jvmArgs) throws IOException {
        String content = "";
        if (jvmArgs != null) {
            content = jvmArgs.toString();
        }

        fileService.write(JVM_ARGS_LAUNCHER_FILE_PATH, content);
    }
}
