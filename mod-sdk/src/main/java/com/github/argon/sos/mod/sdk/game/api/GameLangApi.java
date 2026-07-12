package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import init.paths.PATHSAccess;
import lombok.RequiredArgsConstructor;
import snake2d.util.sets.LIST;

import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * For interaction with the game language.
 */
@RequiredArgsConstructor
public class GameLangApi {
    private final static Logger log = Loggers.getLogger(GameLangApi.class);

    /**
     * The games default language is english
     */
    public final static Locale DEFAULT_LOCALE = Locale.ENGLISH;

    /**
     * Returns the current set language of the game as a {@link Locale}
     *
     * @return the current selected language of the game
     */
    public Locale getCurrent() {
        try {
            LIST<Path> paths = PATHSAccess.getPaths();

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
