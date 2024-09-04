package com.github.argon.sos.moreoptions.io;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.Lists;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
 * For reading files from the class path a.k.a. "resources" folder
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceService extends AbstractIOService {

    private final static Logger log = Loggers.getLogger(ResourceService.class);

    @Getter(lazy = true)
    private final static ResourceService instance = new ResourceService();

    public Optional<Path> getResourcePath(String path) {
        URL url = getClass().getClassLoader().getResource(path);

        return Optional.ofNullable(url).map(url1 -> {
            try {
                return Paths.get(url1.toURI());
            } catch (URISyntaxException e) {
                return null;
            }
        });
    }

    public Optional<String> readResource(String path) {
        try (InputStream inputStream = getInputStream(path)) {
            if (inputStream == null) {
                return Optional.empty();
            }

            return Optional.of(readFromInputStream(inputStream));
        } catch (Exception e) {
            log.warn("Could not read resource from path %s", path, e);
            return Optional.empty();
        }
    }

    @Nullable
    public InputStream getInputStream(String path) {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream(path);
    }

    public List<String> readResourceLines(String path) {
        ClassLoader classLoader = getClass().getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            if (inputStream == null) {
                return Lists.of();
            }

            return readLinesFromInputStream(inputStream);
        } catch (Exception e) {
            log.warn("Could not read lines from file %s", path, e);
            return Lists.of();
        }
    }

    public Optional<Properties> readProperties(String path) {
        InputStream inputStream = getInputStream(path);
        Properties properties = new Properties();

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            log.warn("Could not read properties from file %s", path, e);
            return Optional.empty();
        }

        return Optional.of(properties);
    }
}
