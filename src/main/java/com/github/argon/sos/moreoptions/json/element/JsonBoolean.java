package com.github.argon.sos.moreoptions.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class JsonBoolean implements JsonElement {
    private final Boolean value;

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
