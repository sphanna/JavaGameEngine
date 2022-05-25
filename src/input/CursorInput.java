package input;

import org.lwjgl.glfw.GLFWCursorPosCallback;

import math.Vector2f;

public class CursorInput extends GLFWCursorPosCallback {

	public static Vector2f mousePos = new Vector2f();
	
	public void invoke(long window, double xpos, double ypos){
		mousePos.set(xpos,ypos);
	}
}
