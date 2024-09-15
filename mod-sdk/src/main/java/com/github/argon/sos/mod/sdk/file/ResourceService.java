package com.github.argon.sos.mod.sdk.file;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Lists;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * For reading files from the class path a.k.a. "src/main/resources" folder a.k.a. from inside a *.jar file.
 *
 * All files in the "resource" folder will be packaged into the built *.jar file.
 * This is where all the compiled "byte code" of your Java classes lives for example.
 * These files are than called "Resources" and can be accessed through this service.
 *
 * This service can not write into or delete resource files from inside a *.jar file.
 */
public class ResourceService extends AbstractFileService {

    private final static Logger log = Loggers.getLogger(ResourceService.class);

    @Override
    public String read(Path path) throws IOException {
        return read(path.toString()).orElse(null);
    }

    @Override
    public List<String> readLines(Path path) throws IOException {
        return readLines(path.toString());
    }

    @Override
    public void write(Path path, String content) throws IOException {
        throw new UnsupportedOperationException("Can not write into resources");
    }

    @Override
    public boolean delete(Path path) throws IOException {
        throw new UnsupportedOperationException("Can not delete resources");
    }

    @Override
    @Nullable
    public Properties readProperties(Path filePath) throws IOException {
        return readProperties(filePath.toString());
    }

    /**
     * For reading content from a resource file
     *
     * @return the content of the resource file as string if file is present
     */
    public Optional<String> read(String filePath) throws IOException {
        log.debug("Reading from resource file %s", filePath);
        try (InputStream inputStream = getResourceInputStream(filePath)) {
            if (inputStream == null) {
                return Optional.empty();
            }

            return Optional.of(readFromInputStream(inputStream));
        } catch (Exception e) {
            log.error("Could not read resource from path %s", filePath);
            throw e;
        }
    }

    /**
     * For reading the content from a resource file as a list of lines.
     *
     * @return the lines of the file as list of strings
     * @throws IOException when file is inaccessible
     */
    public List<String> readLines(String filePath) throws IOException {
        log.debug("Reading from resource file %s", filePath);
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(filePath)) {
            if (inputStream == null) {
                return Lists.of();
            }

            return readLinesFromInputStream(inputStream);
        } catch (Exception e) {
            log.error("Could not read lines from file %s", filePath);
            throw e;
        }
    }

    /**
     * For reading *.properties files.
     *
     * @return {@link Properties} read and parsed from a file path
     * @throws IOException when property file can not be accessed
     */
    @Nullable
    public Properties readProperties(String filePath) throws IOException {
        log.debug("Reading from resource properties file %s", filePath);

        Properties properties = new Properties();
        try (InputStream inputStream = getResourceInputStream(filePath)) {
            if (inputStream == null) {
                log.info("%s is not a file, does not exists or is not readable", filePath);
                return null;
            }

            properties.load(inputStream);
        } catch (IOException e) {
            log.warn("Could not read properties from file %s", filePath);
            throw e;
        }

        return properties;
    }

    /**
     * For getting a {@link InputStream} from a resource file
     *
     * @return an {@link InputStream} for the given file path
     */
    @Nullable
    private InputStream getResourceInputStream(String filePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream(filePath);
    }
}
