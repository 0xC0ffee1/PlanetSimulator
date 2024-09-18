package Textures;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL30;
import renderEngine.Loader;

import static org.lwjgl.glfw.GLFW.glfwExtensionSupported;

public class Texture {
    private int textureID;
    private String fileName;

    public Texture(String fileName){
        this.fileName = fileName;
        loadToGPU();
    }

    public void loadToGPU(){
        int textureID = GL30.glGenTextures();
        this.textureID = textureID;
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureID); //Bind texture ID

        GL30.glPixelStorei(GL30.GL_UNPACK_ALIGNMENT, 1);

        TextureData buf = Loader.loadToBuffer(fileName);

        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA, buf.getWidth(),
                buf.getHeight(), 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buf.getBuffer());

        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR_MIPMAP_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
        if(glfwExtensionSupported("GL_EXT_texture_filter_anisotropic")){
            float amount = Math.min(4f, GL30.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
            GL30.glTexParameterf(GL30.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
        }
        else {
            System.out.println("Anisotropic filtering is not supported");
        }

        GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D);
    }

    public int getID(){
        return textureID;
    }
}
