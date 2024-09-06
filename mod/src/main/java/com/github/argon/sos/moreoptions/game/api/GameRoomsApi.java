package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Lists;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import settlement.main.SETT;
import settlement.room.main.RoomBlueprintImp;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameRoomsApi {

    private final static Logger log = Loggers.getLogger(GameRoomsApi.class);

    @Getter(lazy = true)
    private final static GameRoomsApi instance = new GameRoomsApi();

    public List<RoomBlueprintImp> getRooms() {
        return Lists.fromGameLIST(SETT.ROOMS().imps());
    }

    public Optional<RoomBlueprintImp> getBySound(String soundKey) {
        String roomKey = soundKey.replace(GameSoundsApi.KEY_PREFIX + ".ROOM_", "");

        return getRooms().stream()
            .filter(roomBlueprint -> roomBlueprint.key.equals(roomKey))
            .findFirst();
    }


}
