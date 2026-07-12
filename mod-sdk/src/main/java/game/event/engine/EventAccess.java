package game.event.engine;

import snake2d.util.sets.ArrayListGrower;

public class EventAccess {
    public static ArrayListGrower<Event> getAll() {
        return Event.all;
    }
}
