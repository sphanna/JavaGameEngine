package input;

import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class MouseButtonInput extends GLFWMouseButtonCallback {

	public static int[] buttons = new int[256];

	public void invoke(long window, int button, int action, int mods){
		buttons[button] = action;
	}

}
