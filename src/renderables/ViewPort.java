package renderables;

import static org.lwjgl.opengl.GL11.*;

import math.Vector3f;

public class ViewPort extends Quad{


	public ViewPort(Vector3f bl, float width, float height){
		super(bl,width,height);
	}
	
	public ViewPort(Vector3f bl, Vector3f tr){
		super(bl,tr);
	}
	
	@Override
	public void render() {
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		shader.enable();
		mesh.render();
		shader.disable();
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	}

}
