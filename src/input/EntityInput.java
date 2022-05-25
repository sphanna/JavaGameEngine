package input;

import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import entities.Component;
import entities.Entity;
import events.EventManager;
import game.GameSystem;
import math.Vector3f;
import util.PR;

public class EntityInput extends GameSystem {

	Entity inputEntity;
	static final float PUSH_COEFF = 30f;
	
	private static final Vector3f PUSH_RIGHT = new Vector3f(1,0,0);
	private static final Vector3f PUSH_LEFT = new Vector3f(-1,0,0);
	private static final Vector3f PUSH_UP = new Vector3f(0,1,0);
	private static final Vector3f PUSH_DOWN = new Vector3f(0,-1,0);

	private static java.util.HashMap<Integer, Runnable> keyPressedCommands = new HashMap<>();
	private static java.util.HashMap<Integer, Runnable> keyReleasedCommands = new HashMap<>();
	
	public EntityInput(EventManager eventManager) {
		super(eventManager);
		this.eventManager = eventManager;
		initializeKeyCommands();
		subscribeEvents();
	}

	EventManager eventManager;

	private void push(Vector3f dir){
		if(inputEntity != null){
			Vector3f delta = dir.scalarMultN(PUSH_COEFF);
			Component entityAccComponent = inputEntity.get("ACC");
			Vector3f acc = (Vector3f)entityAccComponent.getData();
			Vector3f newAcc = acc.addn(delta);
			entityAccComponent.set(newAcc);
		}
	}
		
	private void onNewEntity(Object data){
		inputEntity = (Entity)data;
	}
	
	private void onEntityRemoved(Object data){
		inputEntity = null;
	}
	
	private void onKeyPress(Object data){
		int key = (int)data;
		if(keyPressedCommands.containsKey(key))
			keyPressedCommands.get(key).run();
	}
	
	private void onKeyReleased(Object data){
		int key = (int)data;
		if(keyReleasedCommands.containsKey(key))
			keyReleasedCommands.get(key).run();
	}

	public void initializeKeyCommands(){
		keyPressedCommands.put(GLFW.GLFW_KEY_W, ()->push(PUSH_UP));
		keyPressedCommands.put(GLFW.GLFW_KEY_A, ()->push(PUSH_LEFT));
		keyPressedCommands.put(GLFW.GLFW_KEY_S, ()->push(PUSH_DOWN));
		keyPressedCommands.put(GLFW.GLFW_KEY_D, ()->push(PUSH_RIGHT));
		
		keyReleasedCommands.put(GLFW.GLFW_KEY_W, ()->push(PUSH_UP.flipn()));
		keyReleasedCommands.put(GLFW.GLFW_KEY_A, ()->push(PUSH_LEFT.flipn()));
		keyReleasedCommands.put(GLFW.GLFW_KEY_S, ()->push(PUSH_DOWN.flipn()));
		keyReleasedCommands.put(GLFW.GLFW_KEY_D, ()->push(PUSH_RIGHT.flipn()));
	}
	
	private void subscribeEvents(){
		eventMap.put("REGISTER_TO_INPUT", this::onNewEntity);
		eventMap.put("UNREGISTER_FROM_INPUT", this::onEntityRemoved);
		eventMap.put("KEY_FIRST_PRESS", this::onKeyPress);
		eventMap.put("KEY_RELEASED", this::onKeyReleased);

		eventMap.entrySet().stream()
						   .forEach(e->eventManager.subscribe(this, e.getKey()));
	}
}
