package com.github.argon.sos.mod.sdk.file;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Lists;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * For reading files from the class path a.k.a. "resources" folder a.k.a. *.jar file.
 *
 * All files in the "resource" folder will be packaged into the built *.jar file.
 * This is where all the compiled "byte code" of your Java classes lives for example.
 * These files are than called "Resources" and can be accessed through this service.
 */
public class ResourceService extends AbstractFileService {

    private final static Logger log = Loggers.getLogger(ResourceService.class);

    /**
     * For getting a {@link Path} of a resource from a path.
     *
     * @return {@link Path} of a given file, when present
     */
    public Optional<Path> getResourcePath(String filePath) {
        URL url = getClass().getClassLoader().getResource(filePath);

        return Optional.ofNullable(url).map(url1 -> {
            try {
                return Paths.get(url1.toURI());
            } catch (URISyntaxException e) {
                log.warn("Invalid path %s", filePath, e);
                return null;
            }
        });
    }

    /**
     * For reading content from a resource file
     *
     * @return the content of the resource file as string if file is present
     */
    public Optional<String> readResource(String filePath) throws IOException {
        try (InputStream inputStream = getInputStream(filePath)) {
            if (inputStream == null) {
                return Optional.empty();
            }

            return Optional.of(readFromInputStream(inputStream));
        } catch (Exception e) {
            log.warn("Could not read resource from path %s", filePath);
            throw e;
        }
    }

    /**
     * For getting a {@link InputStream} from a resource file
     *
     * @return an {@link InputStream} for the given file path
     */
    @Nullable
    public InputStream getInputStream(String filePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream(filePath);
    }

    /**
     * For reading the content from a resource file as a list of lines.
     *
     * @return the lines of the file as list of strings
     * @throws IOException when file is inaccessible
     */
    public List<String> readResourceLines(String filePath) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(filePath)) {
            if (inputStream == null) {
                return Lists.of();
            }

            return readLinesFromInputStream(inputStream);
        } catch (Exception e) {
            log.warn("Could not read lines from file %s", filePath);
            throw e;
        }
    }

    /**
     * For reading *.properties files.
     *
     * @return {@link Properties} read and parsed from a file path
     * @throws IOException when property file can not be accessed
     */
    public Optional<Properties> readProperties(String filePath) throws IOException {
        InputStream inputStream = getInputStream(filePath);
        if (inputStream == null) {
            return Optional.empty();
        }

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            log.warn("Could not read properties from file %s", filePath);
            throw e;
        }

        return Optional.of(properties);
    }
}
