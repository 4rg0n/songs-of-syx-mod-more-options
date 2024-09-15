package com.github.argon.sos.mod.sdk.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For mapping a field to a json key
 *
 * <pre>{@code
 *   @JsonProperty(key = "RACE_NAME");
 *   private String name;
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface JsonProperty {
    String key() default "";
}
