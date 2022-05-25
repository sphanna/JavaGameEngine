package game;

import input.CursorInput;
import input.EntityInput;
import input.InputSystem;
import input.KeyInput;
import input.MouseButtonInput;
import input.MouseScrollInput;
import math.Matrix4f;
import math.Vector3f;
import rendering.RenderSystem;
import util.Clock;
import util.PR;
import world.World;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFW;

import Physics.PhysicsSystem;
import entities.EntityManager;
import events.EventManager;

import util.FileUtils;

public class Main implements Runnable {

	static final double DESIRED_UPS = 60;
	static final double DESIRED_UPDATE_DELTA = 1 / DESIRED_UPS;
	
	private long window;
	
	private Thread thread;
	
	private static RenderSystem screenRenderer;
	private static World world;
	private static InputSystem inputManager;
	private static EventManager eventManager;
	private static EntityManager entityManager;
	private static PhysicsSystem physicsSystem;
	
	public static void main(String[] args) {
		Main game = new Main();
		game.start();
	}
	
	public void run() {
		
		init();

		double previousTime = util.Clock.getTime();
		double lag = 0.0;
		double ftimer = 0;
		long updates = 0;
		long frames = 0;
		
		while ( !screenRenderer.isClosed() ) {
			glfwPollEvents();
			double currentTime = util.Clock.getTime();
			double elapsed = currentTime - previousTime;
			previousTime = currentTime;
			lag += elapsed;
			
			//update 60 times a second
			while( lag >= DESIRED_UPDATE_DELTA ){
				update(DESIRED_UPDATE_DELTA);
				updates++;
				lag -= DESIRED_UPDATE_DELTA;
			}
			
			screenRenderer.render();
			frames++;
			
			//every second
			if(Clock.getTime() - ftimer >= 1){
				//System.out.println("ups: " + updates + ", fps: " + frames);
				updates = 0;
				frames = 0;
				ftimer += 1;
			}

		}
		
		screenRenderer.closeOut();
		
	}
	
	private void update(double dt){ //update all systems
		inputManager.process();
		physicsSystem.update((float)dt);
	}
		
	private void init() {
		eventManager = new EventManager();
		inputManager = new InputSystem(eventManager);
		world = new World(eventManager);
		screenRenderer = new RenderSystem(eventManager);
		entityManager = new EntityManager(eventManager);
		physicsSystem = new PhysicsSystem(eventManager);
		
		window = screenRenderer.getWindow();
		inputManager.initialize(window);
		entityManager.initialize();
		world.createOverviewMap();
		screenRenderer.scaleCamera(1);
	}
	
	public void start(){
		thread = new Thread(this,"Runner");
		thread.start();
	}

}
