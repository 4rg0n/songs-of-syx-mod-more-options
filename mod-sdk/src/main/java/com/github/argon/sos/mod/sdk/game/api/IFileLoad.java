package com.github.argon.sos.mod.sdk.game.api;

public interface IFileLoad{
    <T> T get(String key, Class<T> type);
}
