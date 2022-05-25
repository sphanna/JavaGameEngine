package input;

import org.lwjgl.glfw.GLFWScrollCallback;

public class MouseScrollInput extends GLFWScrollCallback{

	public static double yoffset;
	public static double xoffset;
	
	@Override
	public void invoke(long window, double xoffset, double yoffset) {
		MouseScrollInput.yoffset = yoffset;
		MouseScrollInput.xoffset = xoffset;
	}
	
	public static void reset(){
		yoffset = 0.0;
		xoffset = 0.0;
	}

}
