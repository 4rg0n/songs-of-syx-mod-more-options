package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.init.InitPhases;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import util.save.SaveFile;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameSaveApi implements InitPhases {

    private final static Logger log = Loggers.getLogger(GameSaveApi.class);

    @Getter(lazy = true)
    private final static GameSaveApi instance = new GameSaveApi();

    public final static String DEFAULT_SAVE_NAME = "A New Beginning";

    @Nullable
    @Getter
    private SaveFile currentFile;

    public List<SaveFile> getFiles() {
        return Arrays.asList(SaveFile.list());
    }

    @Override
    public void initGameSaveLoaded(MoreOptionsV2Config config) {

    }
}
