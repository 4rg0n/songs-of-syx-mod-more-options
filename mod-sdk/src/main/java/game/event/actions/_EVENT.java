package game.event.actions;

import game.GAME;
import game.event.engine.EContext;
import game.event.engine.Event;
import game.event.engine.EventCollection;
import snake2d.util.file.Json;
import snake2d.util.sets.LISTE;

// MODDED: made public
public final class _EVENT extends EventActionConstructor{

	
	
	_EVENT() {
		super("EVENT");
	}
	
	@Override
	public EventAction action(Data data) {
		return new Imp(key, data.parent, data.json, data.all, data.engine);
	}


	
	public final class Imp extends EventAction  {


		public final Event other;
		private boolean keepInfo;
		private boolean clearContent;
		private boolean message;
		private boolean duration;
		
		Imp(String key, Event parent, Json data, LISTE<EventAction> all, EventCollection engine) {
			super(key, all);
			other = engine.read(parent, data.value("EVENT"), data, "EVENT");
			keepInfo = data.bool("KEEP_INFO", false);
			clearContent = data.bool("CLEAR_CONTEXT", false);
			message = data.bool("MESSAGE", true);
			duration = data.bool("KEEP_TIME", false);
			data.checkUnused();
		}


		@Override
		public void exe(Event event, EContext data) {
			if (other != null)
				GAME.EVENT().set(other, keepInfo, duration, clearContent, message);
		}
		
		
		
	}
	
	
}
