package com.github.argon.sos.moreoptions.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class JsonLong implements JsonElement {
    private final Long value;

    @Override
    public String toString() {
        return Long.toString(value);
    }
}
