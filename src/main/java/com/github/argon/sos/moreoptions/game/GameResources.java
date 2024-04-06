package com.github.argon.sos.moreoptions.game;

import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.StringUtil;
import init.paths.PATH;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameResources {

    @Getter(lazy = true)
    private final static List<String> resources = read(PATHS.INIT().getFolder("resource"));
    @Getter(lazy = true)
    private final static List<String> edibles = read(PATHS.INIT().getFolder("resource").getFolder("edible"));
    @Getter(lazy = true)
    private final static List<String> drinkables = read(PATHS.INIT().getFolder("resource").getFolder("drinkable"));
    @Getter(lazy = true)
    private final static List<String> growables = read(PATHS.INIT().getFolder("resource").getFolder("growable"));
    @Getter(lazy = true)
    private final static List<String> minables = read(PATHS.INIT().getFolder("resource").getFolder("minable"));
    @Getter(lazy = true)
    private final static List<String> workables = read(PATHS.INIT().getFolder("resource").getFolder("work"));
    @Getter(lazy = true)
    private final static List<String> armySupplies = read(PATHS.INIT().getFolder("resource").getFolder("armySupply"));
    @Getter(lazy = true)
    private final static List<String> races = read(PATHS.RACE().init);
    @Getter(lazy = true)
    private final static List<String> traits = read(PATHS.RACE().init.getFolder("trait"));
    @Getter(lazy = true)
    private final static List<String> religions = read(PATHS.INIT().getFolder("religion"));
    @Getter(lazy = true)
    private final static List<String> structures = read(PATHS.SETT().init.getFolder("structure"));
    @Getter(lazy = true)
    private final static List<String> fortifications = read(PATHS.SETT().init.getFolder("fortification"));
    @Getter(lazy = true)
    private final static List<String> floors = read(PATHS.SETT().init.getFolder("floor"));
    @Getter(lazy = true)
    private final static List<String> fences = read(PATHS.SETT().init.getFolder("fence"));
    @Getter(lazy = true)
    private final static List<String> environments = read(PATHS.SETT().init.getFolder("environment"));
    @Getter(lazy = true)
    private final static List<String> raceHomes = read(PATHS.RACE().init.getFolder("home"));
    @Getter(lazy = true)
    private final static List<String> rooms = read(PATHS.INIT().getFolder("room"));
    @Getter(lazy = true)
    private final static List<String> climates = Lists.of("COLD", "TEMPERATE", "HOT");

    private static List<String> read(PATH path) {
        try (Stream<Path> stream = Files.list(path.get())) {
            return stream
                .filter(file -> !Files.isDirectory(file))
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(path1 -> path1.endsWith(".txt"))
                .map(fileName -> StringUtil.removeTrailing(fileName, ".txt"))
                .collect(Collectors.toList());

        } catch (Exception e) {
            return Lists.of();
        }
    }
}
