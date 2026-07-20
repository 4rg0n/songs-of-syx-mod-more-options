package com.github.argon.sos.mod.sdk.config;

import java.nio.file.Path;

public class ConfigDefaults {
    public final static Path GAME_INSTALL_FOLDER = Path.of(System.getProperty("user.dir"));
    public final static String JVM_ARGS_LAUNCHER_FILE = "jvmargs-launcher.txt";
    public final static Path JVM_ARGS_LAUNCHER_FILE_PATH = GAME_INSTALL_FOLDER.resolve(JVM_ARGS_LAUNCHER_FILE);
}
