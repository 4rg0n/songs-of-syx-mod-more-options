package com.github.argon.sos.mod.sdk.game;

import lombok.RequiredArgsConstructor;

/**
 * WIP do not use :P
 */
@RequiredArgsConstructor
public class IconPath {
    private final int size;
    private final String name;
    private final int index;

    public IconPath of(String iconPath) {
        String[] parts = iconPath.split("->");

        if (parts.length != 3) {
            throw new RuntimeException("TODO");
        }

        int size = Integer.parseInt(parts[0]);
        String name = parts[1];
        int index = Integer.parseInt(parts[2]);

        return new IconPath(size, name, index);
    }
}
