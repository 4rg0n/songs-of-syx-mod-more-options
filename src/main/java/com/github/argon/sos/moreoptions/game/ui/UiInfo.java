package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.ui.builder.Translatable;
import lombok.*;

/**
 * For describing ui elements e.g. a button with a name and description
 */
@Builder
@Data
@AllArgsConstructor
public class UiInfo<Key> implements Translatable<Key> {
    private Key key;

    private String title;

    private String description;

    private boolean translatable;

    public static UiInfo<String> fromDictionary(Dictionary.Entry entry) {
        return UiInfo.<String>builder()
            .key(entry.getKey())
            .description(entry.getDescription())
            .title(entry.getTitle())
            .build();
    }
}
