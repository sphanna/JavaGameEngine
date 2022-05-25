package game;

import java.util.HashMap;
import java.util.function.Consumer;

import events.Event;
import events.EventListener;
import events.EventManager;

public abstract class GameSystem implements EventListener{

	protected EventManager eventManager;
	protected HashMap<String, Consumer<Object>> eventMap = new HashMap<String, Consumer<Object>>();
	
	public GameSystem(EventManager eventManager){
		this.eventManager = eventManager;
	}
	
	@Override
	public void handle(Event e) {
		eventMap.get(e.type).accept(e.data);
	}

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}
}
