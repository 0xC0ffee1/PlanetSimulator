package renderEngine;

import Misc.WorldConstants;
import Textures.TextureData;
import models.Mesh;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;
import shaders.FileLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Loader {
    //Loads models to the vao
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public Mesh loadToVAO(float[] vertices, int[] indices, float[] texCoords, float[] normals){
        int vaoID = createVAO();
        bindIndexBuffer(indices);
        storeDataInAttributeList(0, vertices, 3);
        storeDataInAttributeList(1, texCoords, 2);
        storeDataInAttributeList(2, normals, 3);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        unbindVAO();
        return new Mesh(indices.length, vaoID);
    }

    public Mesh loadToVAO(float[] vertices, int[] indices, float[] texCoords){
        int vaoID = createVAO();
        bindIndexBuffer(indices);
        storeDataInAttributeList(0, vertices, 3);
        storeDataInAttributeList(1, texCoords, 2);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        unbindVAO();
        return new Mesh(indices.length, vaoID);
    }

    public Mesh loadToVAO(int[] vertexData, int[] indices){
        int vaoID = createVAO();
        bindIndexBuffer(indices);
        storeDataInAttributeList(0, vertexData, 1);
        GL30.glEnableVertexAttribArray(0);
        unbindVAO();
        return new Mesh(indices.length, vaoID);
    }

    public Mesh loadToVAO(float[] positions, int dimensions){
        int vaoID = createVAO();
        storeDataInAttributeList(0, positions, dimensions);
        GL30.glEnableVertexAttribArray(0);
        unbindVAO();
        return new Mesh(positions.length / dimensions, vaoID);
    }

    public int createEmptyVbo(int floatCount){
        int vbo = GL30.glGenBuffers();
        vbos.add(vbo);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, floatCount * 4, GL30.GL_STREAM_DRAW);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    public void updateVbo(int vbo, float[] data, FloatBuffer buffer){
        buffer.clear();
        buffer.put(data);
        buffer.flip();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, buffer.capacity(), GL30.GL_STREAM_DRAW);
        GL30.glBufferSubData(GL30.GL_ARRAY_BUFFER, 0, buffer);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
    }

    public void addInstancedAttribute(int vao, int vbo, int attribute, int dataSize, int instanceDataLength, int offset){
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        GL30.glBindVertexArray(vao);
        GL30.glVertexAttribPointer(attribute, dataSize, GL30.GL_FLOAT, false, instanceDataLength * 4, offset * 4);
        GL33.glVertexAttribDivisor(attribute, 1);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public int loadToVAO(float[] positions, float[] textureCoords) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, positions, 2);
        storeDataInAttributeList(1, textureCoords, 2);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        unbindVAO();
        return vaoID;
    }

    public int createVAO(){
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        GL30.glEnableVertexAttribArray(0);
        return vaoID;
    }

    public void bindIndexBuffer(int[] indices){
        int indexVboID = GL30.glGenBuffers();
        vbos.add(indexVboID);
        GL30.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexVboID);
        IntBuffer buffer = loadToIntBuffer(indices);
        GL30.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);

    }

    public int storeDataInAttributeList(int index, float[] positions, int numElements){
        int vboID = GL30.glGenBuffers();
        vbos.add(vboID);
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = loadToFloatBuffer(positions);
        GL30.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        GL30.glVertexAttribPointer(index, numElements, GL30.GL_FLOAT, false, 0, 0);

        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        MemoryUtil.memFree(buffer);

        return vboID;
    }


    public int storeDataInAttributeList(int index, int[] positions, int numElements){
        int vboID = GL30.glGenBuffers();
        vbos.add(vboID);
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        IntBuffer buffer = loadToIntBuffer(positions);
        GL30.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        GL30.glVertexAttribIPointer(index,numElements,GL30.GL_UNSIGNED_INT, 0, 0);

        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        MemoryUtil.memFree(buffer);

        return vboID;
    }

    public static FloatBuffer loadToFloatBuffer(float[] data){
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }


    public static IntBuffer loadToIntBuffer(int[] data){
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public static BufferedImage loadImage(String loc)
    {
        try {
            System.out.println(loc);
            return ImageIO.read(FileLoader.class.getClassLoader().getResourceAsStream(loc));
        } catch (IOException e) {
            //Error Handling Here
            e.printStackTrace();
        }
        return null;
    }

    public int loadCubeMap(String[] textureFiles){
        int texID = GL30.glGenTextures();
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, texID);

        for(int i = 0; i< textureFiles.length; i++){
            TextureData data = loadToBuffer(WorldConstants.RES_DIR + "skybox/" + textureFiles[i] + ".png");
            GL30.glTexImage2D(GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X+i, 0, GL30.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, data.getBuffer());
        }

        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL11.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
        textures.add(texID);
        return texID;
    }

    public static TextureData loadToBuffer(String filename){
        BufferedImage image = loadImage(filename);

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }

        buffer.flip();
        return new TextureData(image.getWidth(), image.getHeight(), buffer);
    }

    public void cleanUp(){
        for(int vao : vaos){
            GL30.glDeleteVertexArrays(vao);
        }
        for(int vbo : vbos){
            GL30.glDeleteBuffers(vbo);
        }
        for(int tex : textures){
            GL30.glDeleteTextures(tex);
        }
    }

    public void unbindVAO(){
        GL30.glBindVertexArray(0);
    }

}
