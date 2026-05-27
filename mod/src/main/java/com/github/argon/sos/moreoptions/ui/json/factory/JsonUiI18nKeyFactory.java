package com.github.argon.sos.moreoptions.ui.json.factory;

import com.github.argon.sos.mod.sdk.util.StringUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.util.List;

/**
 * For building keys used in translation property files
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUiI18nKeyFactory {

    /**
     * Files or folders, which are or contain unique files and don't follow a common reused structure
     */
    private final static List<String> UNIQUE_FILES = List.of(
        "/data/assets/init/config",
        "/data/assets/init/disease/_CONFIG.txt",
        "/data/assets/init/world/config"
    );

    /**
     * Depending on whether the file has a unique structure or a reused one, it will build the key differently.
     * For common reused structure "<folder>.<jsonKey>" e.g.: "race.PLAYABLE"
     * For unique structure "<folder>.<fileName>.<jsonKey>" e.g.: "config.Battle.DAMAGE_REDUCTION"
     *
     * @param filePath full path of the file
     * @param key of the json element
     * @return built key
     */
    public static String build(Path filePath, String key) {
        Path folder = filePath.getParent().getFileName();

        for (String uniqueFile : UNIQUE_FILES) {
            if (filePath.startsWith(uniqueFile)) {
                return folder + "." + StringUtil.removeFileExtension(filePath.getFileName().toString()) + "." + key;
            }
        }

        return folder + "." + key;
    }
}
