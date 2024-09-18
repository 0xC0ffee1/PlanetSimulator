package renderEngine;

import models.Mesh;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class LiteLoader {
    private final List<Integer> vaos = new ArrayList<>();
    private final List<Integer> vbos = new ArrayList<>();
    private final List<Integer> textures = new ArrayList<>();

    public Mesh loadToVAO(float[] vertices, int[] indices, float[] texCoords, float[] normals, float[] texSlices){
        int vaoID = createVAO();
        bindIndexBuffer(indices);
        storeDataInAttributeList(0, vertices, 3);
        storeDataInAttributeList(1, texCoords, 2);
        storeDataInAttributeList(2, normals, 3);
        storeDataInAttributeList(3, texSlices, 1);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnableVertexAttribArray(3);
        unbindVAO();
        return new Mesh(indices.length, vaoID);
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

    public void storeDataInAttributeList(int index, float[] positions, int numElements){
        int vboID = GL30.glGenBuffers();
        vbos.add(vboID);
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = loadToFloatBuffer(positions);
        GL30.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        GL30.glVertexAttribPointer(index, numElements, GL30.GL_FLOAT, false, 0, 0);

        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        MemoryUtil.memFree(buffer);

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
