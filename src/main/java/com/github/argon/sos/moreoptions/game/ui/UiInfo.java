package com.github.argon.sos.moreoptions.game.ui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * For describing ui elements e.g. a button with a name and description
 */
@Builder
@Data
@AllArgsConstructor
public class UiInfo<Key> {
    private final Key key;

    private String title;

    private String description;
}
