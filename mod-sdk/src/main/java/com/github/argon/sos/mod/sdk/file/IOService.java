package com.github.argon.sos.mod.sdk.file;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

/**
 * For reading, writing and deleting files and resources.
 */
public interface IOService {
    /**
     * Reads content from file in given path.
     *
     * @param path of file to read from
     * @return content of file
     * @throws IOException if something goes wrong when reading
     */
    @Nullable String read(Path path) throws IOException;


    /**
     * Reads content as lines from file in given path.
     *
     * @param path of file to read from
     * @return content as lines from file
     * @throws IOException if something goes wrong when reading
     */
    List<String> readLines(Path path) throws IOException;

    /**
     * Writes given content into file by replacing it.
     *
     * @param path of file to write into
     * @param content to write
     * @throws IOException if something goes wrong when writing
     */
    void write(Path path, String content) throws IOException;

    /**
     * Deletes a file under given path.
     *
     * @param path of file to delete
     * @return whether file is present anymore
     * @throws IOException if something goes wrong when deleting
     */
    boolean delete(Path path) throws IOException;

    /**
     * Reads meta information such as creation or update date of a file.
     * See {@link FileMeta}
     *
     * @param filePath to read meta info from
     * @return meta info or null when file is not accessible
     */
    @Nullable FileMeta readMeta(Path filePath);

    /**
     * Reads meta information of all files in a given folder.
     * See {@link FileMeta}
     *
     * @param folderPath to read meta info from
     * @return meta info or empty when files in folder are not accessible
     */
    List<FileMeta> readMetas(Path folderPath);

    /**
     * Reads content of a *.properties file as Java properties.
     *
     * @param filePath of *.properties file
     * @return parsed properties
     * @throws IOException if reading properties fails
     */
    @Nullable Properties readProperties(Path filePath) throws IOException;
}
