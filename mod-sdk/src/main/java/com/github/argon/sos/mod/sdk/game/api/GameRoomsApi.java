package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.util.Lists;
import lombok.RequiredArgsConstructor;
import settlement.main.SETT;
import settlement.room.main.RoomBlueprintImp;

import java.util.List;
import java.util.Optional;

/**
 * For interacting with the various rooms of the game.
 */
@RequiredArgsConstructor
public class GameRoomsApi {

    /**
     * Returns a list of all available rooms (also  modded ones).
     *
     * @return list of all rooms
     */
    public List<RoomBlueprintImp> getRooms() {
        return Lists.fromGameLIST(SETT.ROOMS().imps());
    }

    /**
     * Returns a room by its sound key.
     *
     * @param soundKey used for this room
     * @return found room if present
     */
    public Optional<RoomBlueprintImp> getRoomBySound(String soundKey) {
        String roomKey = soundKey.replace(GameSoundsApi.KEY_PREFIX + ".", "")
            .replace("ROOM_", "");

        return getRoomByKey(roomKey);
    }

    /**
     * Returns a room by its room key.
     *
     * @param roomKey of the room
     * @return found room if present
     */
    public Optional<RoomBlueprintImp> getRoomByKey(String roomKey) {
        return getRooms().stream()
            .filter(roomBlueprint -> roomBlueprint.key.equals(roomKey))
            .findFirst();
    }
}
