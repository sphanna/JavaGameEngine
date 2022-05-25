package renderUtils;

import java.util.ArrayList;
import java.util.List;

import util.BufferUtils;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class TextureArray {
	
	static final int TEX_WIDTH = 64;
	static final int TEX_HEIGHT = 64;
	
	int numTextures = 0;
	
	List<Texture> textures = new ArrayList<Texture>();
	
	public TextureArray(){
		
	}
	
	public void add(Texture t){
		textures.add(t);
		numTextures++;
	}
	
	public void load(){		
		int texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D_ARRAY,texture);
		
		glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, TEX_WIDTH, TEX_HEIGHT, numTextures);
		
		int[] texels = new int[TEX_WIDTH * TEX_HEIGHT];
		for(int i = 0; i < numTextures; i++){
			texels = textures.get(i).getData();
			glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, TEX_WIDTH, TEX_HEIGHT, 1, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.createIntBuffer(texels));	
		}

		glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
		//glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_EDGE);
		//glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE);

	}
	
}
