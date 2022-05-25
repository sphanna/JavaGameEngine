package input;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import org.lwjgl.glfw.GLFW;

import events.Event;
import events.EventManager;
import util.Clock;

import math.Vector3f;

public class InputSystem {
	
	private static final double MOUSE_CLICK_THROTTLE = 0.250;
	
	private static Vector3f cursorPos = new Vector3f();
	private static int oldState = GLFW.GLFW_RELEASE;
	
	static EventManager eventManager;
	static EntityInput entityInput;
	
	double time;
	double timeDelta;
	
	public InputSystem(EventManager e){
		eventManager = e;
		entityInput = new EntityInput(e);
	}
	
	public void process(){
		processKeysPressed();
		processMouseInput();
		processScrollInput();
	}
	
	
	public void processKeysPressed(){
		KeyInput.pressedKeys.stream()
							.forEach((key)->eventManager.dispatch(new Event("KEY_PRESSED",key)));
	}
	
	public static void keyPressed(int key){
		eventManager.dispatch(new Event("KEY_FIRST_PRESS", key));
	}
	
	public static void keyReleased(int key){
		eventManager.dispatch(new Event("KEY_RELEASED", key));
	}
	
	public void processMouseInput(){
		int newState = MouseButtonInput.buttons[GLFW_MOUSE_BUTTON_LEFT];
		
		//first press
		if( newState == GLFW_PRESS && oldState == GLFW_RELEASE){
			cursorPos = CursorInput.mousePos.toVector3f();
			time = Clock.getTime();
			eventManager.dispatch(new Event("MOUSE_PRESSED", cursorPos));
		}
		
		//dragging
		if( newState == GLFW_PRESS && oldState == GLFW_PRESS){
			timeDelta = Clock.getTime() - time;
			Vector3f newCursorPos = CursorInput.mousePos.toVector3f();
			
			eventManager.dispatch(new Event("MOUSE_DRAGGED", newCursorPos));
				
			cursorPos = newCursorPos;
		}
		
		//clicked
		if( newState == GLFW_RELEASE && oldState == GLFW_PRESS && timeDelta < MOUSE_CLICK_THROTTLE){
			eventManager.dispatch(new Event("MOUSE_CLICKED", cursorPos));
		}
		
		//released
		if( newState == GLFW_RELEASE && oldState == GLFW_PRESS){
			timeDelta = 0;
		}
		
		oldState = newState;
	}
	
	public void processScrollInput(){
		double scrolly = MouseScrollInput.yoffset;
		
		if(scrolly != 0)
			eventManager.dispatch(new Event("MOUSE_SCROLL", scrolly));
		
		MouseScrollInput.reset();
	}
	
	public void initialize(long window){
		glfwSetCursorPosCallback(window, new CursorInput());
		glfwSetKeyCallback(window, new KeyInput());
		glfwSetMouseButtonCallback(window, new MouseButtonInput());
		glfwSetScrollCallback(window, new MouseScrollInput());
	}
	
}
