package rendering;

import java.util.HashMap;

import entities.Component;
import entities.Entity;
import events.EventManager;
import game.GameSystem;
import math.Matrix4f;
import math.Vector3f;
import renderUtils.Shader;
import renderUtils.Texture;
import renderables.Quad;
import util.PR;

public class EntityRenderer extends GameSystem {

	HashMap<Integer, Entity> renderableEntities = new HashMap<Integer, Entity>();
	Entity cameraFocusEntity;
	
	static Shader entityShader;
	Texture entityTexture;
	Quad entityMesh;
	
	public EntityRenderer(EventManager eventManager){
		super(eventManager);
		subscribeEvents();
	}
	
	private void subscribeEvents(){
		eventMap.put("REGISTER_TO_RENDER", this::onNewEntity);
		eventMap.put("UNREGISTER_FROM_RENDER", this::onEntityRemoved);
		eventMap.put("REGISTER_TO_CAMERA_FOCUS", this::onCameraFocused);
		eventMap.put("UNREGISTER_FROM_CAMERA_FOCUS", this::onCameraFocusLost);
		
		eventMap.entrySet().stream()
						   .forEach(e->eventManager.subscribe(this, e.getKey()));
	}
	
	public boolean hasCameraFocus(){
		return cameraFocusEntity != null;
	}
	
	public void onCameraFocused(Object data){
		cameraFocusEntity = (Entity)data;
	}
	
	public void onCameraFocusLost(Object data){
		cameraFocusEntity = null;
	}
	
	public Vector3f getCameraPos(){
		Vector3f pos = (Vector3f)cameraFocusEntity.getData("POSITION");
		float size = (float)cameraFocusEntity.getData("SIZE");
		float halfSize = size / 2;
		Vector3f midPos = pos.addn(new Vector3f(halfSize,halfSize, 0));
		return midPos;
	}
	
	public void onNewEntity(Object data){
		Entity renderable = (Entity)data;
		renderableEntities.put(renderable.ID, renderable);
		//PR.ln("renderer adding entity: " + renderable.ID);
	}
	
	public void onEntityRemoved(Object data){
		Entity renderable = (Entity)data;
		int ID = renderable.ID;
		
		//PR.ln("EntityRenderer - entity removed: " + ID);
		assert renderableEntities.containsKey(ID) : "renderable " + ID + " does not exist";
		renderableEntities.remove(ID);
	}
	
	public void initialize(Matrix4f proj_matrix, Matrix4f view_matrix){
		setShader(proj_matrix, view_matrix);
		setTexture();
		createMesh();		
	}
	
	
	public void createMesh(){
		entityMesh = new Quad(new Vector3f(0,0,0.5f),1,1);
		entityMesh.setShader(entityShader);
		entityMesh.setTexture(entityTexture);
		entityMesh.setMesh();
	}
	
	public void setShader(Matrix4f proj_matrix, Matrix4f view_matrix){
		entityShader = new Shader("shaders/entity.vert", "shaders/entity.frag");
		entityShader.loadMatrix4f("proj_matrix", proj_matrix);
		entityShader.loadMatrix4f("view_matrix", view_matrix);
		entityShader.loadInt("tex", 1);
	}
	
	public void setTexture(){
		entityTexture = new Texture("res/Player.png");
		entityTexture.load();
	}
	
	public void updateCamera(Matrix4f view_matrix){
		entityShader.enable();
		entityShader.loadMatrix4f("view_matrix", view_matrix);
		entityShader.disable();
	}
	
	public void renderAll(){
		Vector3f pos;
		float size;
		
		Matrix4f posMat;
		Matrix4f scaleMat;
		Matrix4f model_Matrix;
		
		entityTexture.bind();
		entityShader.enable();
		
		if(renderableEntities.size() > 0){
			for(Entity renderable: renderableEntities.values()){
				Component posComponent = renderable.get("POSITION");
				pos = (Vector3f)posComponent.getData();
				size = (float)renderable.get("SIZE").getData();
				
				posMat = Matrix4f.translate(pos);
				scaleMat = Matrix4f.scaleXY(size);
				
				model_Matrix = scaleMat.multiply(posMat);
				
				entityShader.loadMatrix4f("mv_matrix", model_Matrix);
				entityMesh.onlyRender();
			}
		}
		
		entityShader.enable();
		entityTexture.unbind();
	}
}
