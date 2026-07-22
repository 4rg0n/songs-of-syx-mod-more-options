package com.github.argon.sos.mod.sdk.game.util;

import game.save.SaveFile;
import lombok.experimental.UtilityClass;

import java.nio.file.Path;

/**
 * For dealing with the game save systems
 */
@UtilityClass
public class SaveUtil {

    /**
     * File ending used by the game save files
     */
    public static final String SAVE_FILE_ENDING = "save";

    /**
     * Reads a unique identifier from the save file
     *
     * @param saveFile to read the stamp from
     * @return unique save stamp identifier
     */
    public static String extractSaveStamp(SaveFile saveFile) {
        return saveFile.fullName.toString();
    }

    /**
     * Reads a unique identifier from the save file path
     *
     * @param saveFilePath to read the stamp from
     * @return unique save stamp identifier
     */
    public static String extractSaveStamp(Path saveFilePath) {
        String saveFileName = saveFilePath.getFileName().toString();

        String saveStamp = saveFileName;
        // remove file ending
        if (saveFileName.endsWith("." + SAVE_FILE_ENDING)) {
            saveStamp = saveFileName.substring(0, saveFileName.indexOf("." + SAVE_FILE_ENDING));
        }

        return saveStamp;
    }
}
