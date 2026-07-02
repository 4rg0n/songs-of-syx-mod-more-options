package com.github.argon.sos.mod.sdk.json.element;

/**
 * Represents a parsed json value
 */
public interface JsonElement {
    /**
     * Creates a new copy of the json element.
     *
     * @return copied json element
     */
    JsonElement copy();
}
