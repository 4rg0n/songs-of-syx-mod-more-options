package com.github.argon.sos.moreoptions.json.element;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class JsonNull implements JsonElement {
    @Override
    public String toString() {
        return "null";
    }
}
