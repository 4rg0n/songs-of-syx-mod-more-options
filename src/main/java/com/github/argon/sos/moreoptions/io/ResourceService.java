package com.github.argon.sos.moreoptions.io;

import com.github.argon.sos.moreoptions.util.Lists;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * For reading files from the class path a.k.a. "resources" folder
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceService extends AbstractIOService {
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
        ClassLoader classLoader = getClass().getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            if (inputStream == null) {
                return Optional.empty();
            }

            return Optional.of(readFromInputStream(inputStream));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<String> readResourceLines(String path) {
        ClassLoader classLoader = getClass().getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            if (inputStream == null) {
                return Lists.of();
            }

            return readLinesFromInputStream(inputStream);
        } catch (Exception e) {
            return Lists.of();
        }
    }
}
