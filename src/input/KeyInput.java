package input;

import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

public class KeyInput extends GLFWKeyCallback {

	//public static boolean[] keys = new boolean[65535];
	public static List<Integer> pressedKeys = new ArrayList<Integer>();

	public void invoke(long window, int key, int scancode, int action, int mods){
		//keys[key] = action != GLFW.GLFW_RELEASE;
		
		if( !pressedKeys.contains(key) && action != GLFW.GLFW_RELEASE ){
			pressedKeys.add(key);
			InputSystem.keyPressed(key);
		}
		
		if( pressedKeys.contains(key) && action == GLFW.GLFW_RELEASE ){
			pressedKeys.remove(pressedKeys.indexOf(key));
			InputSystem.keyReleased(key);
		}
	}

}
