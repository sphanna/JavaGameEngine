package util;

import org.lwjgl.glfw.*;

public class Clock {

	//private static boolean paused = false;
	public static double lastFrame;
	public static double totalTime;
	
	public static double delta = 0;
	public static double multiplier = 1;
	
	private Clock(){}
	
	public static double getTime(){
		return GLFW.glfwGetTime();
	}
	
	public static double delta() {
		double curTime = getTime();
		double delta = curTime - lastFrame;
		lastFrame = getTime();
		return delta;
	}
	
}
