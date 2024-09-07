package com.github.argon.sos.mod.sdk.game.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import util.save.SaveFile;

import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SaveUtil {

    public static final String SAVE_FILE_ENDING = ".save";

    public static String extractSaveStamp(SaveFile saveFile) {
        return saveFile.fullName.toString();
    }

    public static String extractSaveStamp(Path saveFilePath) {
        String saveFileName = saveFilePath.getFileName().toString();

        String saveStamp = saveFileName;
        // remove file ending
        if (saveFileName.endsWith(SAVE_FILE_ENDING)) {
            saveStamp = saveFileName.substring(saveFileName.indexOf(SAVE_FILE_ENDING), saveFileName.length() - 1);
        }

        return saveStamp;
    }
}
