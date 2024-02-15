package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.game.api.GameBoosterApi;
import com.github.argon.sos.moreoptions.ui.builder.Translatable;
import com.github.argon.sos.moreoptions.util.StringUtil;
import game.boosting.Boostable;
import lombok.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Not all "keys" for each option used by the game and by the mod are understandable in a UI.
 * So this will provide a mapping for the option key to a simple dictionary entry with a human-readable title and a description.
 * The description will be displayed in a tooltip.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Dictionary {

    @Getter(lazy = true)
    private final static Dictionary instance = new Dictionary();

    /**
     * Name of the config file
     */
    public final static String FILE_NAME = "MoreOptionsDictionary";

    @Getter
    private final Map<String, Entry> entries = new HashMap<>();

    public void addAll(Map<String, Entry> entries) {
        this.entries.putAll(entries);
    }

    /**
     * @return same list, but translated
     */
    public <T extends Translatable> Collection<T> translate(Collection<T> translatables) {
        translatables.forEach(this::translate);
        return translatables;
    }

    /**
     * Will look into {@link this#entries} for a match and replace title and description in the given object.
     */
    public <T extends Translatable> T translate(T translatable) {
        String key = translatable.getKey();

        if (key != null && translatable.isTranslate()) {
            Entry entry = get(key);

            translatable.setTitle(entry.getTitle());
            translatable.setDescription(entry.getDescription());
        }

        return translatable;
    }

    public Dictionary add(String key, String title, String description) {
        entries.put(key, Entry.builder()
            .key(key)
            .title(title)
            .description(description)
            .build());

        return this;
    }

    public Dictionary add(Entry entry) {
        entries.put(entry.getKey(), entry);

        return this;
    }

    public Dictionary add(Boostable boostable) {
        Dictionary.Entry dictEntry = Dictionary.Entry.fromBoostable(boostable);
        entries.put(dictEntry.getKey(), dictEntry);

        return this;
    }

    /**
     * @return Found entry or a "No dictionary entry available" entry
     */
    public Entry get(String key) {
        if (!entries.containsKey(key)) {
            return Entry.builder()
                .key(key)
                .title(StringUtil.extractTail(key, "\\."))
                .description("No dictionary entry available for: " + key)
                .build();
        }

        return entries.get(key);
    }

    @Data
    @Builder
    public static class Entry {

        private String key;

        private String title;

        private String description;

        public static Entry fromBoostable(Boostable boostable) {
            return Entry.builder()
                .key(GameBoosterApi.KEY_PREFIX + "." + boostable.key)
                .title(boostable.name.toString())
                .description(boostable.desc.toString())
                .build();
        }
    }
}
