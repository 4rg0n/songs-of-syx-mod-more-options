package game.event.engine;

import game.event.actions.EventAction;
import game.event.actions.EventActions;
import game.faction.Faction;
import game.values.GVALUES;
import game.values.Lockable;
import snake2d.util.file.Json;
import snake2d.util.sets.LIST;

// MODDED: made public
public class ECondition {

	public final LIST<EventAction> on_fulfill;
	public final Lockable<Faction> request = GVALUES.FACTION.LOCK.push();
	
	
	ECondition(String key, Json data, EventActions actions, Event parent){
		if (key != null)
			data = data.json(key);
		request.push("REQUIRES", data);
		on_fulfill = EActions.actions(parent, actions, data);
		
	}
	
}
