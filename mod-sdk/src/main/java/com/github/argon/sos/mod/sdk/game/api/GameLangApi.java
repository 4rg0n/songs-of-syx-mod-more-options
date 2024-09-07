package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.ReflectionUtil;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.sets.LIST;

import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameLangApi {

    private final static Logger log = Loggers.getLogger(GameLangApi.class);

    public final static Locale DEFAULT_LOCALE = Locale.ENGLISH;

    @Getter(lazy = true)
    private final static GameLangApi instance = new GameLangApi();

    /**
     * @return the current selected language of the game
     */
    public Locale getCurrent() {
        try {
            LIST<Path> paths = ReflectionUtil.<PATHS>getDeclaredFieldValue("i", PATHS.class)
                .flatMap(gamePaths -> ReflectionUtil.<LIST<Path>>getDeclaredFieldValue("paths", gamePaths))
                .orElse(null);

            if (paths == null) {
                return DEFAULT_LOCALE;
            }

            for (int i = 0; i < paths.size(); i ++) {
                Path path = paths.get(i);

                Pattern pattern = Pattern.compile("/langs/(?<lang>[A-Za-z]*)");
                Matcher matcher = pattern.matcher(path.toString());

                // found the language?
                if (matcher.find()) {
                    String lang = matcher.group("lang");
                    return Locale.forLanguageTag(lang);
                }
            }

        } catch (Exception e) {
            log.warn("Could not check for current language of game", e);
        }

        return DEFAULT_LOCALE;
    }
}
