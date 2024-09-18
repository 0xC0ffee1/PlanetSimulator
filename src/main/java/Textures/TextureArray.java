package Textures;

import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;
import renderEngine.Loader;

public abstract class TextureArray {
    private int textureID;
    private final int layerCount;
    private final int textureSize;
    public final String filePath;

    public TextureArray(int texSize, int layers, String srcPath){
        this.textureSize = texSize;
        this.layerCount = layers;
        this.filePath = srcPath;
        loadToGPU();
    }

    public int getID(){
        return textureID;
    }

    public void loadToLayer(String fileName, int layer){
        TextureData file = Loader.loadToBuffer(filePath + fileName);
        GL45.glTexSubImage3D(GL45.GL_TEXTURE_2D_ARRAY, 0, 0, 0, layer, file.getWidth(), file.getHeight(), 1, GL45.GL_RGBA, GL45.GL_UNSIGNED_BYTE, file.getBuffer());
    }

    public abstract void loadLayers();

    public void loadToGPU(){

        int textureID = GL30.glGenTextures();
        this.textureID = textureID;
        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, textureID);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
        GL30.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, GL30.GL_RGBA, textureSize, textureSize,
                layerCount, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, 0);

        loadLayers();
        GL45.glGenerateMipmap(GL45.GL_TEXTURE_2D_ARRAY);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D_ARRAY, GL45.GL_TEXTURE_MIN_FILTER,
                GL45.GL_LINEAR_MIPMAP_LINEAR);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D_ARRAY, GL45.GL_TEXTURE_MAG_FILTER,
                GL45.GL_NEAREST);
        GL45.glTexParameterf(GL45.GL_TEXTURE_2D_ARRAY, GL45.GL_TEXTURE_LOD_BIAS, -2);

    }
}
