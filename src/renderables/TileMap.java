package renderables;

import math.Matrix4f;
import math.Vector3f;
import renderUtils.Shader;
import renderUtils.VertexArray;
import util.PR;
import world.Map;
import world.Zone;


public class TileMap implements Renderable {
	
	long hash;
	
	int mapWidthInt;
	int mapHeightInt;
	
	float mapSize;
	float tileWidth;
	float tileHeight;
	Vector3f pos;
	
	Map map;
	int rendererLevel;
	
	VertexArray mesh;
	static Shader shader;

	
	public TileMap(){}
	
	public TileMap(Map m){
		map = m;
		pos = map.getPos();
		rendererLevel = m.getLevel();
		
		mapWidthInt = map.getMapDimensions().x();
		mapHeightInt = map.getMapDimensions().y();
		mapSize = map.getSize();
		tileWidth = tileHeight = map.getSubZoneSize();
	}
	
	public void setShader(Shader shader){
		this.shader = shader;
	}
	
	public void setMesh(){
		
		float[] vertices = new float[(mapWidthInt)*(mapHeightInt)*2*3*3];
		Zone zones[][] = map.getZones();
		
		int index = 0;

		for(int y = 0; y < (mapHeightInt); y++){
			for(int x = 0; x < (mapWidthInt); x++) {

				//top left Triangle
				vertices[index++] = pos._x + x*tileWidth;
				vertices[index++] = pos._y + y*tileHeight;
				vertices[index++] = pos._z;
						
				vertices[index++] = pos._x + x*tileWidth;
				vertices[index++] = pos._y + (y+1)*tileHeight;
				vertices[index++] = pos._z;
				
				vertices[index++] = pos._x + (x+1)*tileWidth;
				vertices[index++] = pos._y + (y+1)*tileHeight;
				vertices[index++] = pos._z;
				
				//Bottom Right Triangle
				vertices[index++] = pos._x + (x+1)*tileWidth;
				vertices[index++] = pos._y + (y+1)*tileHeight;
				vertices[index++] = pos._z;
				
				vertices[index++] = pos._x + (x+1)*tileWidth;
				vertices[index++] = pos._y + y*tileHeight;
				vertices[index++] = pos._z;
				
				vertices[index++] = pos._x + x*tileWidth;
				vertices[index++] = pos._y + y*tileHeight;
				vertices[index++] = pos._z;
			}
		}
		
		float [] tcs = new float[(mapWidthInt)*(mapHeightInt)*2*3*3];
		index = 0;
		float texID = 0;
		for(int y = 0; y < (mapHeightInt); y++){
			for(int x = 0; x < (mapWidthInt); x++) {
				
				texID = zones[x][y].getTexID();
				
				//top left Triangle
				tcs[index++] = 0;
				tcs[index++] = 0;
				tcs[index++] = texID;
						
				tcs[index++] = 0;
				tcs[index++] = 1;
				tcs[index++] = texID;
				
				tcs[index++] = 1;
				tcs[index++] = 1;
				tcs[index++] = texID;
				
				//Bottom Right Triangle
				tcs[index++] = 1;
				tcs[index++] = 1;
				tcs[index++] = texID;
				
				tcs[index++] = 1;
				tcs[index++] = 0;
				tcs[index++] = texID;
				
				tcs[index++] = 0;
				tcs[index++] = 0;
				tcs[index++] = texID;
			}
		}
		
		mesh = new VertexArray(vertices, tcs);

	}
	
	public void render(){
		//glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		shader.enable();
		mesh.render();
		shader.disable();
		//glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	}
	
	public Shader getShader(){
		return shader;
	}
	
	public Vector3f getPos(){
		return pos;
	}
	
	public int getLevel(){
		return rendererLevel;
	}
	
	public static void updateCamera(Matrix4f view_matrix){
		shader.enable();
		shader.loadMatrix4f("view_matrix", view_matrix);
		shader.disable();
	}
	
	public void updateTileTexture(int x, int y, float tileID){
		int bufferLocation = 0;
		
		bufferLocation = ((y * mapHeightInt * 3) + (x*3)) * 6;
		
		mesh.updateTexture(bufferLocation, tileID);
	}

	@Override
	public void applyTransform(String type, Matrix4f transform) {
		shader.enable();
		shader.loadMatrix4f(type, transform);
		shader.disable();
	}
	
	//old code for indexed tile map.
	/*
	float[] vertices = new float[(size+1)*(size+1)*3];
	int count = 0;
	for(int y = 0; y < size+1; y++){
		for(int x = 0; x < size+1; x++) {
			vertices[count++] = pos._x + x*tileWidth;
			vertices[count++] = pos._y + y*tileHeight;
			vertices[count++] = pos._z;
		}
	}*/
	
	/*
	float [] tcs = new float[(size+1)*(size+1)*3];
	count = 0;		
	for(int y = 0; y < size+1; y++){
		for(int x = 0; x < size+1; x++){
			tcs[count++] = x;
			tcs[count++] = y;
			if(x == 1)
				tcs[count++] = 1;
			else
				tcs[count++] = 0;
		}
	}*/
	
	/*
	int[] indices = new int[(size)*(size)*2*3];
	count = 0;
	int loc = 0;
	for(int y = 0; y < (size); y++){
		for(int x = 0; x < (size); x++) {
			loc = x + (y*(size+1)); //this row
			
			//triangle top left
			indices[count++] = loc;
			indices[count++] = (loc + size+1);
			indices[count++] = ((loc+1) + size+1);
			
			//triangle bottom right
			indices[count++] = ((loc+1) + size+1);  
			indices[count++] = (loc+1);
			indices[count++] = loc;
		}
	}*/
	
	public void setHash(long hash){
		this.hash = hash;
	}
	
	public long getHash(){
		return hash;
	}
}
