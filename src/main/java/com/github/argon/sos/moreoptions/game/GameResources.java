package com.github.argon.sos.moreoptions.game;

import com.github.argon.sos.moreoptions.game.json.GameJsonService;
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
    private final static GameJsonService gameJsonService = GameJsonService.getInstance();

    @Getter(lazy = true)
    private final static List<String> animals = read(PATHS.INIT().getFolder("animal"));
    @Getter(lazy = true)
    private final static List<String> resources = read(PATHS.INIT().getFolder("resource")).stream().map(resource -> {
        if (resource.startsWith("_")) {
            return resource.substring(1);
        }

        return resource;
    }).collect(Collectors.toList());
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
    private final static List<String> raceTraits = read(PATHS.RACE().init.getFolder("trait"));
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
    private final static List<String> needs = read(PATHS.STATS().init.getFolder("need"));
    @Getter(lazy = true)
    private final static List<String> damages = read(PATHS.STATS().init.getFolder("damage"));
    @Getter(lazy = true)
    private final static List<String> statTraits = read(PATHS.STATS().init.getFolder("trait"));
    @Getter(lazy = true)
    private final static List<String> equipBattle = read(PATHS.STATS().init.getFolder("equip").getFolder("battle"));
    @Getter(lazy = true)
    private final static List<String> equipCivic = read(PATHS.STATS().init.getFolder("equip").getFolder("civic"));
    @Getter(lazy = true)
    private final static List<String> equipRanged = read(PATHS.STATS().init.getFolder("equip").getFolder("ranged"));
    @Getter(lazy = true)
    private final static List<String> climates = Lists.of("COLD", "TEMPERATE", "HOT");
    @Getter(lazy = true)
    private final static List<String> stats = gameJsonService.get(PATHS.TEXT().getFolder("stats").get("NAMES"))
        .map(gameJsonResource -> gameJsonResource.getJson().keys())
        .orElse(Lists.of());
    @Getter(lazy = true)
    private static final List<String> humanClasses = Lists.of("CITIZEN", "SLAVE", "NOBLE", "CHILD", "OTHER");
    @Getter(lazy = true)
    private static final List<String> monuments = getRooms().stream()
        .filter(name -> name.startsWith("MONUMENT_"))
        .collect(Collectors.toList());
    
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
