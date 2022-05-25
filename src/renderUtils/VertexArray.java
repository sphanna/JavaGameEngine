package renderUtils;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL44;

import util.BufferUtils;

public class VertexArray {
	private int vao, vbo, tbo;
	private int count;
	private static long fence;
	
	ByteBuffer tboData;
	
	public VertexArray(float[] vertices, float[] textureCoordinates){
		fence = GL32.glFenceSync(GL32.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
		count = vertices.length;
		
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(vertices), GL_STATIC_DRAW);
		glVertexAttribPointer(Shader.VERTEX_ATTRIB, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(Shader.VERTEX_ATTRIB);
		
		int flags = GL_MAP_WRITE_BIT | GL44.GL_MAP_PERSISTENT_BIT | GL44.GL_MAP_COHERENT_BIT;
		
		tbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, tbo);
		GL44.glBufferStorage(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(textureCoordinates), flags);
		tboData = GL30.glMapBufferRange(GL_ARRAY_BUFFER, 0, count*4, flags);
		//glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(textureCoordinates), GL15.GL_DYNAMIC_DRAW);
		glVertexAttribPointer(Shader.TEXTURECOORD_ATTRIB, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(Shader.TEXTURECOORD_ATTRIB);

		
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	
	}
		
	public void bind() {
		glBindVertexArray(vao);
	}
	
	public void unbind() {
		glBindVertexArray(0);

	}
	public void draw(){
		GL11.glDrawArrays(GL_TRIANGLES, 0, count);
	}
		
	public void render() {
		bind();
		draw();
		unbind();
	}
		
	public void updateTexture(int location, float tileID) {
		waitBuffer();
		
		location += 2; //location of texture ID
		int end = location + 16;
		
	    FloatBuffer fdata = tboData.asFloatBuffer();
	    for(int i = location; i < end; i = i + 3)
	    	fdata.put(i, tileID);
	    
	    lockBuffer();
	}
	
	private void waitBuffer(){
		int waitReturn;
		while(true){
			waitReturn = GL32.glClientWaitSync(fence, GL32.GL_SYNC_FLUSH_COMMANDS_BIT, GL32.GL_TIMEOUT_IGNORED);
			if( waitReturn == GL32.GL_ALREADY_SIGNALED || waitReturn == GL32.GL_CONDITION_SATISFIED){
				return;
			}
		}
	}
	
	private void lockBuffer(){
		fence = GL32.glFenceSync(GL32.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
	}
	
}
