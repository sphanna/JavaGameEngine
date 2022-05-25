package entities;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import events.Event;
import events.EventManager;
import util.PR;

public class Entity {

	static EventManager eventManager;
	public static HashMap<String, String[]> systemMap = new HashMap<String, String[]>();
	
	static int entityCount = 0;
	public int ID;
	
	HashMap<String, Component> components = new HashMap<String, Component>();
	ArrayList<String> subscribedSystems = new ArrayList<String>();
	
	public Entity(){
		entityCount++;
		ID = entityCount;
	}

	public Component get(String type){
		if(components.containsKey(type))
			return components.get(type);
		else
			return null;
	}
	
	public Object getData(String type){
		if(components.containsKey(type))
			return components.get(type).getData();
		else
			return null;
	}
	
	public void add(Component component){
		String type = component.getType();
		
		if( !components.containsKey(type) )
			components.put(type, component);
	}

	public void remove(String type){
		if( this.hasComponent(type) ){
			components.remove(type);
			updateSystems();
		}	
	}
	
	//check every system: if we no longer contain the necessary components for a given system then we
	//need to unregister that system.
	private void updateSystems(){
		
		subscribedSystems.stream()
						 .filter(system -> !this.hasNecessaryComponentsFor(system))
						 .collect(Collectors.toList()) //I have to create a new list here avoid removing items from a list I'm currently iterating over.  (Avoids concurrent modification exception)
						 .forEach(this::removeSystem);
		
	}
	
	public boolean hasNecessaryComponentsFor(String system){
		String[] neededComponents = systemMap.get(system);
		List<String> neededComponentsList = Arrays.asList(neededComponents);

		boolean hasComponents = true;
		for(String type: neededComponentsList){
			hasComponents = this.hasComponent(type);
			if(hasComponents == false) return false;
		}
		
		return hasComponents;
	}
	
	public boolean hasComponent(String component){
		return components.containsKey(component);
	}
	
	public void registerTo(String system){
		if(this.hasNecessaryComponentsFor(system)){
			subscribedSystems.add(system);
			String register = "REGISTER_TO_" + system;
			eventManager.dispatch(new Event(register, this));
		}
	}
	
	public void removeSystem(String system){
		subscribedSystems.remove(system);
		String unregister = "UNREGISTER_FROM_" + system;
		PR.ln("Unregistering from: " + system);
		eventManager.dispatch(new Event(unregister, this));
	}
	
	public void removeAllSystems(){
			subscribedSystems.stream()
							 .collect(Collectors.toList()) //(Avoids concurrent modification exception)
							 .forEach(system -> this.removeSystem(system));
	}

	public static void registerEventManager(EventManager e){
		eventManager = e;
	}
	
	public static void setSystemMapping(){
		//TODO create a reverse map using this data: For every component type get every system that
		//it is a dependent for.  This will speed up the hasNecessaryComponentsFor() check.
		String[] physicsDependents = {"POSITION","MOTION", "ACC"};
		String[] renderDependents = {"POSITION","SIZE", "TEXTURE"};
		String[] inputDependents = {"POSITION"};
		String[] cameraFocusDependents = {"POSITION"};
		
		systemMap.put("PHYSICS", physicsDependents);
		systemMap.put("CAMERA_FOCUS", cameraFocusDependents);
		systemMap.put("RENDER", renderDependents);
		systemMap.put("INPUT", inputDependents);
	}
}
