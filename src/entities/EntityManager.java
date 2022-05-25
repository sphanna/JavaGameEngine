package entities;

import events.*;
import game.GameSystem;
import math.Vector3f;
import util.PR;

public class EntityManager extends GameSystem {

	EntityList entities;
	
	public EntityManager(EventManager eventManager){
		super(eventManager);
		Entity.registerEventManager(eventManager);
		Entity.setSystemMapping();
	}
	
	public void initialize(){
		entities = new EntityList();
		
		Entity player = new Entity();
		int entityID = player.ID;
		player.add(new Component("POSITION", entityID, new Vector3f(0,0,0)));
		player.add(new Component("MOTION", entityID, new Vector3f(0,0,0)));
		player.add(new Component("ACC", entityID, new Vector3f(0,0,0)));
		player.add(new Component("SIZE", entityID, 1.0f));
		player.add(new Component("TEXTURE", entityID, 0.0f));
		player.registerTo("RENDER");
		player.registerTo("INPUT");
		player.registerTo("CAMERA_FOCUS");
		player.registerTo("PHYSICS");
		entities.put(player);
		
		Entity otherDude = new Entity();
		int dudeID = otherDude.ID;
		otherDude.add(new Component("POSITION", dudeID, new Vector3f(-1.3f,0,0)));
		otherDude.add(new Component("MOTION", entityID, new Vector3f(-3.0f,0,0)));
		otherDude.add(new Component("ACC", entityID, new Vector3f(0,0,0)));
		otherDude.add(new Component("SIZE", dudeID, 2.0f));
		otherDude.add(new Component("TEXTURE", entityID, 0.0f));
		otherDude.registerTo("RENDER");
		otherDude.registerTo("PHYSICS");
		entities.put(otherDude);

	}

}
