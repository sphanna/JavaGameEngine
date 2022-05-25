package events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

public class EventManager {
	
	public static int totalEventsDispatched = 0;
	
	public String[] eventCatalogue = {
			"MOUSE_PRESSED", "MOUSE_RELEASED", "MOUSE_MOVED", "MOUSE_CLICKED", "MOUSE_DRAGGED", "MOUSE_SCROLL",	"KEY_FIRST_PRESS", "KEY_PRESSED", "KEY_RELEASED",
			"MAP_CURSOR_CLICK",	"MAP_CREATED", "MAP_ADDED", "MAP_REMOVED", "MAP_TEX_CHANGE",
			"ZOOM_CHANGE", "ZOOM_STATE_CHANGE", "VIEWPORT_CHANGE",
			"REGISTER_TO_RENDER", "REGISTER_TO_PHYSICS", "REGISTER_TO_INPUT", "REGISTER_TO_CAMERA_FOCUS",
			"UNREGISTER_FROM_RENDER", "UNREGISTER_FROM_RENDER", "UNREGISTER_FROM_RENDER", "UNREGISTER_FROM_CAMERA_FOCUS"
	};
	
	HashMap<String, ArrayList<EventListener>> eventList;
	
	public EventManager(){
		eventList = new HashMap<String, ArrayList<EventListener>>();
		initializeList();
	}
	
	public void dispatch(Event event){
		totalEventsDispatched++;
		eventList.get(event.type).stream()
						     .forEach((subscriber)->subscriber.handle(event));
	}
	
	public void subscribe(EventListener listener, String type){
		if(eventList.containsKey(type)){
			eventList.get(type).add(listener);
			//System.out.println(listener + " subscribed to " + type);
		}
	}
	
	public void unSubscribe(EventListener listener, String type){
		if(eventList.containsKey(type)){
			eventList.get(type).remove(listener);
		}
	}
	
	public void unSubscribeAll(EventListener listener){
		eventList.entrySet().stream()
							.filter((event)->event.getValue().contains(listener))
							.forEach((event)->event.getValue().remove(listener));
	}
	
	public void initializeList(){
		Arrays.stream(eventCatalogue)
			  .forEach(type -> eventList.put(type, new ArrayList<EventListener>()) );
	}
	
	public void printTotalEvents(){
		System.out.println(totalEventsDispatched + " events dispatched.");
	}
}
