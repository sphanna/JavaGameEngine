package renderables;

import math.Matrix4f;
import renderUtils.Shader;


/*Renderable has a shader and a mesh
 *
 */
public interface Renderable {

	public Shader getShader();
	
	public void applyTransform(String type, Matrix4f transform);
	
	public void render();
	
	public void setHash(long hash);
	
	public long getHash();

}
