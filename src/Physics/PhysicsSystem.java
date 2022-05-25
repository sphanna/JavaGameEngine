package Physics;

import java.util.HashMap;

import entities.Component;
import entities.Entity;
import events.EventManager;
import game.GameSystem;
import math.Vector3f;
import util.PR;

public class PhysicsSystem extends GameSystem{

	static final float FRICTION_COEFF = 0.0f;
	
	
	HashMap<Integer, Entity> physicsObjects = new HashMap<Integer, Entity>();
	
	public PhysicsSystem(EventManager eventManager) {
		super(eventManager);
		subscribeEvents();
	}
	
	public void update(float dt){
		for(Entity entity: physicsObjects.values()){
			Component posComp = entity.get("POSITION");
			Component velComp = entity.get("MOTION");
			Component accComp = entity.get("ACC");
			
			Vector3f pos = (Vector3f)posComp.getData();
			Vector3f vel = (Vector3f)velComp.getData();
			Vector3f acc = (Vector3f)accComp.getData();
			
			//Velocity Verlet Integration
			Vector3f newPos = pos.addn(vel.scalarMultN(dt).addn(acc.scalarMultN(0.5f*dt*dt)));
			posComp.set(newPos);

			Vector3f fricAcc = vel.flipn().scalarMultN(FRICTION_COEFF);			
			Vector3f avgAcc = acc.scalarDivideN(2);
			Vector3f newVel = vel.addn(avgAcc.scalarMultN(dt).addn(fricAcc));
			
			//PR.ln("velocity: " + vel + " friction acc: " + fricAcc);
						
			if(newVel.distSquared() < 0.001){
				velComp.set(new Vector3f(0,0,0));
			} else{
				velComp.set(newVel);
			}
		}
	}
	
	public void onNewEntity(Object data){
		Entity obj = (Entity)data;
		physicsObjects.put(obj.ID, obj);
	}
	
	public void onEntityRemoved(Object data){
		Entity obj = (Entity)data;
		int ID = obj.ID;
		
		//if( !physicsObjects.containsKey(ID) )
			physicsObjects.remove(ID);
	}
	
	
	private void subscribeEvents(){
		eventMap.put("REGISTER_TO_PHYSICS", this::onNewEntity);
		eventMap.put("UNREGISTER_FROM_PHYSICS", this::onEntityRemoved);

		eventMap.entrySet().stream()
						   .forEach(e->eventManager.subscribe(this, e.getKey()));
	}
	

	
	
}
