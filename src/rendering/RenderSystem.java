package rendering;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import events.Event;
import events.EventManager;
import game.GameSystem;
import math.Matrix4f;
import math.Vector3f;
import renderUtils.Shader;
import renderUtils.Texture;
import renderUtils.TextureArray;
import renderables.Background;
import renderables.GraphicsCollection;
import renderables.Quad;
import renderables.TileMap;
import renderables.ViewPort;
import util.PR;
import world.Map;
import world.Zone;

public class RenderSystem extends GameSystem{
	
	private static final Vector3f TRANSLATE_LEFT = new Vector3f(0.1f,0.0f,0f);
	private static final Vector3f TRANSLATE_RIGHT = new Vector3f(-0.1f,0.0f,0f);
	private static final Vector3f TRANSLATE_DOWN = new Vector3f(0.0f,0.1f,0f);
	private static final Vector3f TRANSLATE_UP = new Vector3f(0.0f,-0.1f,0f);
	public static final float ZOOM_IN_RATE = 1.01f;
	public static final float ZOOM_OUT_RATE = 0.99f;
	public static final float SCROLL_COEFFICIENT = 0.05f;

	public static final int LEVEL_WORLD = 0;
	public static final int LEVEL_REGION = 1;
	public static final int LEVEL_LOCAL = 2;
	public static final int LEVEL_CHARACTER = 3;
	
	public static final int SCREEN_WIDTH = 1280;
	public static final int SCREEN_HEIGHT = 720;
	public static final float SCREEN_RATIO = 9.0f / 16.0f;
	
	public static final float ORTHOG_SCALE = 10.0f;
	public static final float SCREEN_DEPTH = 1.0f;
	
	public static final int NUM_LAYERS = 6;
	
	public static float scale = 1;
	public static int zoomLevel = 0;
	
	Quad background;
	ViewPort viewPort;
	GraphicsCollection[] graphicsLayers = new GraphicsCollection[NUM_LAYERS];
	EntityRenderer entityRenderer;
	
	Shader tileShader;
	Shader bgShader;
	Shader viewPortShader;
	Texture bgTexture;
	TextureArray tileTextures;
		
	public static Matrix4f proj_matrix;
	public static Matrix4f inverse_proj_matrix;
	public static Matrix4f screen_matrix;
	public static Matrix4f inverse_screen_matrix;
	public static Matrix4f view_matrix;
	
	Vector3f cursorScreenPos;
	
	private long window;
	
	private static java.util.HashMap<Integer, Runnable> keyCommands = new HashMap<>();
	
	public RenderSystem(EventManager eventManager){
		super(eventManager);
		initializeKeyCommands();
		subscribeEvents();
		initialize();
	}
	
	public void render(){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		background.render();
		graphicsLayers[zoomLevel].renderAll();
		entityRenderer.renderAll();
		
		if(entityRenderer.hasCameraFocus())
			this.moveCameraTo(entityRenderer.getCameraPos());//TODO really bad way of doing this right now.  We should only move camera when we know the player moved.
		
		int error = glGetError();
		if( error != GL_NO_ERROR){
			System.out.println(error);
		}
		
		glfwSwapBuffers(window);
	}
	
	private void initialize(){	
		initOpenGL();
		initMatricies();
		initRendering();
	}

	private void initRendering(){
		graphicsLayers[LEVEL_WORLD] = new GraphicsCollection(LEVEL_WORLD);
		graphicsLayers[LEVEL_REGION] = new GraphicsCollection(LEVEL_REGION);
		graphicsLayers[LEVEL_LOCAL] = new GraphicsCollection(LEVEL_LOCAL);
		graphicsLayers[LEVEL_CHARACTER] = new GraphicsCollection(LEVEL_CHARACTER);

		initBackground();
		initViewPort();
		initEntityRenderer();
		
		loadTileMap();
	}
	
	private void loadTileMap(){
		Texture tileTexture = new Texture("res/grassTile.jpg");
		Texture waterTexture = new Texture("res/waterTile.jpg");
		tileTextures = new TextureArray();
		tileTextures.add(tileTexture);
		tileTextures.add(waterTexture);
		tileTextures.load();
		
		tileShader = new Shader("shaders/tile.vert", "shaders/tile.frag");
		tileShader.loadMatrix4f("proj_matrix", proj_matrix);
		tileShader.loadMatrix4f("view_matrix", view_matrix);
		tileShader.loadInt("texArray", 1);
	}
	
	private void initEntityRenderer(){
		entityRenderer = new EntityRenderer(eventManager);
		entityRenderer.initialize(proj_matrix, view_matrix);
	}
	
	private void initBackground(){	
		Shader bgShader = new Shader("shaders/bg.vert", "shaders/bg.frag");
		bgShader.loadMatrix4f("proj_matrix", proj_matrix);
		bgShader.loadInt("tex", 1);
		
		Texture bgTexture = new Texture("res/bg1.jpg");
		bgTexture.load();
		
		Vector3f bl = new Vector3f(-ORTHOG_SCALE, -ORTHOG_SCALE*SCREEN_RATIO, -0.1f);
		float width = ORTHOG_SCALE*2;
		float height = width*SCREEN_RATIO;
		
		background = new Quad(bl, width, height);
		background.setMesh();
		background.setShader(bgShader);
		background.setTexture(bgTexture);
	}
	
	private void initViewPort(){
		viewPortShader = new Shader("shaders/viewPort.vert", "shaders/viewPort.frag");
		viewPortShader.loadMatrix4f("proj_matrix", proj_matrix);

		float viewScale = 1.0f;
		
		Vector3f bl = new Vector3f(-ORTHOG_SCALE*viewScale, -ORTHOG_SCALE*viewScale*SCREEN_RATIO, 0.1f);
		float width = ORTHOG_SCALE*2*viewScale;
		float height = width*SCREEN_RATIO;
		
		viewPort = new ViewPort(bl, width, height);
		viewPort.setMesh();
		viewPort.setShader(viewPortShader);
	}
	
	
	
	private void initMatricies(){
		proj_matrix = Matrix4f.orthographic(-ORTHOG_SCALE, ORTHOG_SCALE, -ORTHOG_SCALE * SCREEN_RATIO, ORTHOG_SCALE * SCREEN_RATIO, -SCREEN_DEPTH, SCREEN_DEPTH);
		inverse_proj_matrix = proj_matrix.inverse();
		setScreenMatrix();
		view_matrix = Matrix4f.scaleXY(scale);
	}
	
	public void setScreenMatrix(){
		float sr = SCREEN_RATIO;
		float screenScaleWidth = (SCREEN_WIDTH / ORTHOG_SCALE) / 2.0f;
		float screenScaleHeight = (SCREEN_HEIGHT / ORTHOG_SCALE) / (sr * 2.0f);
		Vector3f scaleVector = new Vector3f(screenScaleWidth,screenScaleHeight,1);
		Matrix4f flipy = Matrix4f.reflectY();
		screen_matrix = flipy.multiply(Matrix4f.scale(scaleVector));
		screen_matrix = screen_matrix.multiply(Matrix4f.translate(new Vector3f(ORTHOG_SCALE,-ORTHOG_SCALE*sr,0)));
		inverse_screen_matrix = screen_matrix.inverse();
	}
	
	public Matrix4f getInverseCameraTransform(){
		return view_matrix.inverse();
	}
	
	public Vector3f screenToWorld(Vector3f screen){
		Matrix4f t = getInverseCameraTransform().multiply(RenderSystem.inverse_screen_matrix);
		return t.transform(screen);
	}
	
	public void transformCamera(Matrix4f transform){
		view_matrix = transform.multiply(view_matrix);
		updateCamera();
	}
	
	public void updateCamera(){
		eventManager.dispatch(new Event("VIEWPORT_CHANGE", viewPort.transformed(getInverseCameraTransform())));
		entityRenderer.updateCamera(view_matrix);
		TileMap.updateCamera(view_matrix);
	}
	
	public void moveCameraTo(Vector3f worldPos){
		Matrix4f posMat = Matrix4f.translate(worldPos.flipn());
		Matrix4f scaleM = Matrix4f.scaleXY(scale);
		view_matrix = scaleM.multiply(posMat);
		updateCamera();
	}

	public void scaleCamera(float change){
		scale*=change;
		Matrix4f scaleMatrix = Matrix4f.scaleXY(change);
		transformCamera(scaleMatrix);
		eventManager.dispatch(new Event("ZOOM_CHANGE", viewPort.transformed(getInverseCameraTransform())));
	}
	
	public void translateCamera(Vector3f translate){
		Vector3f translateScaled = translate.scalarMultN(RenderSystem.scale); //not sure why I have to scale here.  Shouldn't it be accounted for in the cameraTranslation?
		Matrix4f translateMatrix = Matrix4f.translate(translateScaled);
		transformCamera(translateMatrix);
	}
	
	public int getNumRenderedObjects(){
		int num = 0;
		for(int i = 0; i < 4; i++){
			num += graphicsLayers[i].getSize();
		}
		
		return num;
	}
	
	private void tester(){
		moveCameraTo(new Vector3f(10,0,0));
	}

	private void subscribeEvents(){
		eventMap.put("MOUSE_CLICKED", this::onMouseClick);
		eventMap.put("MOUSE_PRESSED", this::onMousePressed);
		eventMap.put("MOUSE_DRAGGED", this::onMouseDragged);
		eventMap.put("MOUSE_MOVED", this::onMouseMoved);
		eventMap.put("MOUSE_SCROLL", this::onMouseScroll);
		eventMap.put("KEY_PRESSED", this::onKeyPress);
		eventMap.put("MAP_ADDED", this::onNewMap);
		eventMap.put("MAP_REMOVED", this::onRemoveMap);
		eventMap.put("MAP_TEX_CHANGE", this::onMapTexChange);
		eventMap.put("ZOOM_STATE_CHANGE", this::onZoomStateChange);
		
		eventMap.entrySet().stream()
						   .forEach(e->eventManager.subscribe(this, e.getKey()));
	}
	
	public void initializeKeyCommands(){
		keyCommands.put(GLFW.GLFW_KEY_EQUAL, this::scaleCameraIn);
		keyCommands.put(GLFW.GLFW_KEY_MINUS, this::scaleCameraOut);
		keyCommands.put(GLFW.GLFW_KEY_LEFT, this::moveCameraLeft);
		keyCommands.put(GLFW.GLFW_KEY_RIGHT, this::moveCameraRight);
		keyCommands.put(GLFW.GLFW_KEY_UP, this::moveCameraUp);
		keyCommands.put(GLFW.GLFW_KEY_DOWN, this::moveCameraDown);
		keyCommands.put(GLFW.GLFW_KEY_SPACE, this::tester);
	}
	
	private void onNewMap(Object data){
		TileMap tile = (TileMap)data;
		int level = tile.getLevel();
		
		if(!graphicsLayers[level].contains(tile)){
			
			//PR.ln("renderer:onNewMap new tile");
			tile.setShader(tileShader);
			tile.setMesh();
			graphicsLayers[level].add(tile);
			//PR.ln("num rendered objs " + this.getNumRenderedObjects());
		}else
		{
			PR.ln("renderer:onNewMap contained tile");
		}

	}
	
	private void onRemoveMap(Object data){
		TileMap tile = (TileMap)data;
		int level = tile.getLevel();
		
		if(graphicsLayers[level].contains(tile)){
			//PR.ln("renderer:onRemoveMap removing tile");
			graphicsLayers[level].remove(tile);
		}
	}
	
	private void onMapTexChange(Object data){
		TileMap[] tileMaps = (TileMap[])data;
		TileMap oldMesh = tileMaps[0];
		TileMap newMesh = tileMaps[1];
		int level = oldMesh.getLevel();
		
		//if(graphicsLayers[level].getObjs().containsValue(oldMesh)){
			//PR.ln("removing oldmesh");
		graphicsLayers[level].remove(oldMesh);
		//}
		
		newMesh.setShader(tileShader);
		newMesh.setMesh();
		graphicsLayers[level].add(newMesh);
	}
	
	private void onZoomStateChange(Object data){
		zoomLevel = (int)data;
	}
	
	private void onKeyPress(Object data){
		int key = (int)data;
		if(keyCommands.containsKey(key))
			keyCommands.get(key).run();
		
	}
	
	private void onMouseScroll(Object data){
		double scrolly = (double)data;
		scaleCamera((float)(scrolly * SCROLL_COEFFICIENT + 1));
	}
	
	
	private void onMousePressed(Object data){
		cursorScreenPos = (Vector3f)data;
	}
	
	private void onMouseDragged(Object data){
		Vector3f newCursorPos = (Vector3f)data;
		Vector3f newCursorPosWorld = screenToWorld(newCursorPos);
		Vector3f cursorPosWorld = screenToWorld(cursorScreenPos);
		Vector3f delta = newCursorPosWorld.subtractn(cursorPosWorld);
		
		translateCamera(delta);
		
		cursorScreenPos = newCursorPos;
	}
	
	private void onMouseMoved(Object data){
		
	}
	
	private void onMouseClick(Object data){
		cursorScreenPos = (Vector3f)data;
		Vector3f cursorPosWorld = screenToWorld(cursorScreenPos);
		//PR.ln("cursor world pos: " + cursorPosWorld);
		eventManager.dispatch(new Event("MAP_CURSOR_CLICK", cursorPosWorld));

		PR.ln("");
	}
	
	public void scaleCameraIn(){
		scaleCamera(ZOOM_IN_RATE);
	}
	
	public void scaleCameraOut(){
		scaleCamera(ZOOM_OUT_RATE);
	}
	
	public void moveCameraLeft(){
		translateCamera(TRANSLATE_LEFT.scalarMultN(1 / scale));
	}
	
	public void moveCameraRight(){
		translateCamera(TRANSLATE_RIGHT.scalarMultN(1 / scale));
	}
	
	public void moveCameraDown(){
		translateCamera(TRANSLATE_DOWN.scalarMultN(1 / scale));
	}
	
	public void moveCameraUp(){
		translateCamera(TRANSLATE_UP.scalarMultN(1 / scale));
	}

	public long getWindow(){
		return window;
	}
	
	private long initOpenGL(){
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(SCREEN_WIDTH, SCREEN_HEIGHT, "", NULL, NULL);
		if ( window == NULL ){
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically
			
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// v-sync
		glfwSwapInterval(0);

		// Make the window visible
		glfwShowWindow(window);
		
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glEnable(GL_DEPTH_TEST);
		glActiveTexture(GL_TEXTURE1);
		
		return window;
	}
	
	public boolean isClosed(){
		return glfwWindowShouldClose(window);
	}
	
	public void closeOut(){
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
}
