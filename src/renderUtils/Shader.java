package renderUtils;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.util.HashMap;
import java.util.Map;

import math.Matrix4f;
import math.Vector2f;
import math.Vector3f;

public class Shader {
	
	public static final int VERTEX_ATTRIB = 0;
	public static final int TEXTURECOORD_ATTRIB = 1;
	
	private boolean enabled = false;
	
	private final int ID;
	private Map<String, Integer> locationCache = new HashMap<String, Integer>();
	
	public Shader(String vertex, String fragment){
		ID = ShaderUtils.load(vertex, fragment);
	}
	
	public static void loadAll() {

	}

	public int getUniform(String name){
		if (locationCache.containsKey(name))
			return locationCache.get(name);
		
		int result =  glGetUniformLocation(ID, name);
		if( result == -1 ){
			System.err.println("Could not find uniform variable " + name + "!");
		}else{
			locationCache.put(name, result);
		}
		return result;
	}
	
	
	public void loadInt(String name, int value) {
		if (!enabled) enable();
		glUniform1i(getUniform(name), value);
	}
	
	public void loadFloat(String name, float value) {
		if (!enabled) enable();
		glUniform1f(getUniform(name), value);
	}
	
	public void loadVec2f(String name, Vector2f vec) {
		if (!enabled) enable();
		glUniform2f(getUniform(name), (float)vec.x(), (float)vec.y());
	}
	
	public void loadVec3f(String name, Vector3f vec) {
		if (!enabled) enable();
		glUniform2f(getUniform(name), vec._x, vec._x);
	}
	
	public void loadMatrix4f(String name, Matrix4f matrix){
		if (!enabled) enable();
		glUniformMatrix4fv(getUniform(name), false, matrix.toFloatBuffer());
	}
	
	public void enable(){
		glUseProgram(ID);
		enabled = true;
	}
	
	public void disable() {
		glUseProgram(0);
		enabled = false;
	}
}
