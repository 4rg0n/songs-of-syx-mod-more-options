package com.github.argon.sos.moreoptions.game.data;


import com.github.argon.sos.moreoptions.game.data.init.Init;
import com.github.argon.sos.moreoptions.game.data.sprite.Sprite;
import com.github.argon.sos.moreoptions.game.data.text.Text;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.StringUtil;
import init.paths.PATH;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameResources {

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static Init init = new Init();
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static Sprite sprite = new Sprite();
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static Text text = new Text();

    @Getter(lazy = true)
    private final static List<String> resources = init().resource()
        .fileTitles().stream()
        .map(resource -> StringUtil.removeBeginning(resource, "_"))
        .collect(Collectors.toList());

    @Getter(lazy = true)
    private final static List<String> races = init().race().fileTitles();

    @Getter(lazy = true)
    private final static List<String> humanClasses = Lists.of("CITIZEN", "SLAVE", "NOBLE", "CHILD", "OTHER");

    @Getter(lazy = true)
    private final static List<String> edibles = init().resource().edible().fileTitles();

    @Getter(lazy = true)
    private final static List<String> growables = init().resource().growable().fileTitles();

    @Getter(lazy = true)
    private final static List<String> drinkables = init().resource().drinkable().fileTitles();

    @Getter(lazy = true)
    private final static List<String> minables = init().resource().minable().fileTitles();

    @Getter(lazy = true)
    private final static List<String> floors = init().settlement().floor().fileTitles();

    @Getter(lazy = true)
    private final static List<String> structures = init().settlement().structure().fileTitles();

    @Getter(lazy = true)
    private final static List<String> rooms = init().room().fileTitles();

    @Getter(lazy = true)
    private final static List<String> environments = init().settlement().environment().fileTitles();

    @Getter(lazy = true)
    private final static List<String> fences = init().settlement().fence().fileTitles();

    @Getter(lazy = true)
    private final static List<String> animals = init().animal().fileTitles();

    @Getter(lazy = true)
    private final static List<String> religions = init().religion().fileTitles();

    @Getter(lazy = true)
    private final static List<String> needs = init().stats().folder("need").fileTitles();

    @Getter(lazy = true)
    private final static List<String> damages = init().stats().folder("damage").fileTitles();

    @Getter(lazy = true)
    private final static List<String> climates = Lists.of("COLD", "TEMPERATE", "HOT");

    @Getter(lazy = true)
    private static final List<String> monuments = getRooms().stream()
        .filter(name -> name.startsWith("MONUMENT_"))
        .collect(Collectors.toList());

    @Getter(lazy = true)
    private final static List<String> stats = init().stats().json("NAMES")
        .map(JsonObject::keys)
        .orElse(Lists.of());

    public static GameFolder get(PATH path) {
        if (path.get().startsWith(init().getFolder().path().get())) {
            // init?
            return init().getFolder().folder(path);
        } else if (path.get().startsWith(sprite().getFolder().path().get())) {
            // sprite?
            return sprite().getFolder().folder(path);
        } else if (path.get().startsWith(text().getFolder().path().get())) {
            // text?
            return text().getFolder().folder(path);
        } else {
            return GameFolder.of(path);
        }
    }
}
