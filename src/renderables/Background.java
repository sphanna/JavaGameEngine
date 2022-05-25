package renderables;

import math.Matrix4f;
import math.Vector3f;
import renderUtils.IndexedVertexArray;
import renderUtils.Shader;
import renderUtils.Texture;

public class Background implements Renderable{

	long hash; 
	
	private IndexedVertexArray background;
	private Shader bgShader;
	private Texture bgTexture;
	
	private Vector3f bottomLeft;
	public Vector3f topRight;
	private float width;
	private float height;
	
	public Background(Vector3f pos, float width, float height){
		bottomLeft = pos;
		this.width = width;
		this.height = height;
	}
	
	public Background(Shader s, Texture t){
		bgTexture = t;
		bgShader = s;
	}
	
	public void setMesh(){
		float x = bottomLeft._x;
		float y = bottomLeft._y;
		float z = bottomLeft._z;
		
		/*
		float[] vertices = new float[] {
			-width/2, -height/2, -0.1f,			//bottomleft
			-width/2,  height/2, -0.1f,			//topleft
			width/2,  height/2, -0.1f,			//topRight
			width/2, -height/2, -0.1f			//bottomRight
		};*/
		
		float[] vertices = new float[] {
				x, y, z,			//bottomleft
				x,  y + height, z,			//topleft
				x + width,  y + height, z,			//topRight
				x + width, y, z			//bottomRight
			};
		
		int[] indices = new int[] {
			0, 1, 2,
			2, 3, 0
		};
		
		float [] tcs = new float[] {
			0, 1, 0,
			0, 0, 0,
			1, 0, 0,
			1, 1, 0
		};
		
		background = new IndexedVertexArray(vertices, indices, tcs);
	}
		
	public void setShader(Shader s){
		bgShader = s;
	}
	
	public void setTexture(Texture t){
		bgTexture = t;
	}
	
	public Shader getShader(){
		return bgShader;
	}
	
	public void render() {
		bgTexture.bind();
		bgShader.enable();
		background.render();
		bgShader.disable();
		bgTexture.unbind();
	}

	@Override
	public void applyTransform(String type, Matrix4f transform) {
		// TODO Auto-generated method stub
		
	}
	
	public void setHash(long hash){
		this.hash = hash;
	}
	
	public long getHash(){
		return hash;
	}
}
