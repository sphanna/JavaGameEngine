package renderables;

import math.Matrix4f;
import math.Vector3f;
import renderUtils.IndexedVertexArray;
import renderUtils.Shader;
import renderUtils.Texture;

public class Quad implements Renderable{

	long hash;
	
	public IndexedVertexArray mesh;
	public Shader shader;
	public Texture texture;
	public float width;
	public float height;
	public Vector3f bottomLeft;
	public Vector3f topRight;
	
	
	public Quad(Vector3f bl, Vector3f tr){
		bottomLeft = bl;
		topRight = tr;
		width = getWidth();
		height = getHeight();
	}
	
	public Quad(Vector3f bl, float width, float height){
		bottomLeft = bl;
		this.width = width;
		this.height = height;
		topRight = new Vector3f(bl._x + width, bl._y + height, 0);
	}
		
	public void setMesh(){
		float x = bottomLeft._x;
		float y = bottomLeft._y;
		float z = bottomLeft._z;
				
		float[] vertices = new float[] {
			x, y, z,			//bottomleft
			x,  y + height, z,		//topleft
			x + width,  y + height, z,	//topRight
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
		
		mesh = new IndexedVertexArray(vertices, indices, tcs);
	}
	
	public void setShader(Shader s){
		shader = s;
	}
	
	public void setTexture(Texture t){
		texture = t;
	}
	
	public Vector3f getBottomLeftPos(){
		return bottomLeft;
	}
	
	public Vector3f getTopRightPos(){
		return topRight;
	}
	
	public Vector3f getTopLeftPos(){
		return new Vector3f(bottomLeft._x, topRight._y, bottomLeft._z);
	}
	
	public Vector3f getBottomRightPos(){
		return new Vector3f(topRight._x, bottomLeft._y, bottomLeft._z);
	}
	
	public float getWidth(){
		return topRight._x - bottomLeft._x;
	}
	
	public float getHeight(){
		return topRight._y - bottomLeft._y;
	}
	
	public Shader getShader(){ return shader; }
	public Texture getTexture(){ return texture; }
	public IndexedVertexArray getMesh(){ return mesh; }
	
	public void render(){
		texture.bind();
		shader.enable();
		mesh.render();
		shader.disable();
		texture.unbind();
	}
	
	public void onlyRender(){
		mesh.render();
	}

	@Override
	public void applyTransform(String type, Matrix4f transform) {
		shader.loadMatrix4f(type, transform);
	}
	
	public void transform(Matrix4f t){
		bottomLeft = t.transform(bottomLeft);
		topRight = t.transform(topRight);
		width = getWidth();
		height = getHeight();
	}
	
	public ViewPort transformed(Matrix4f t){
		Vector3f bl = t.transform(bottomLeft);
		Vector3f tr = t.transform(topRight);
		return new ViewPort(bl,tr);
	}
	
	public Vector3f[] getFourCorners(){
		Vector3f[] corners = {bottomLeft, getTopLeftPos(), topRight, getBottomRightPos()};
		return corners;
	}
	
	public void setHash(long hash){
		this.hash = hash;
	}
	
	public long getHash(){
		return hash;
	}
	
	
}
