package atmosphere;

import Misc.WorldConstants;
import bloom.BrightFilterShader;
import models.Mesh;
import models.Planet;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import postProcessing.Fbo;
import postProcessing.ImageRenderer;
import renderEngine.*;
import terrain.TerrainChunkManager;

import java.util.Collection;
import java.util.List;

public class AtmosFilter {
    private ImageRenderer renderer;
    private AtmosShader shader;
    private final Camera camera;

    public AtmosFilter(int width, int height, Window window, Camera camera) throws Exception{
        shader = new AtmosShader();
        renderer = new ImageRenderer(width, height, window);
        this.camera = camera;
    }

    public void render(int texture, int depthTexture, Planet planet){
        shader.start();
        shader.setUniform("colourTexture", 0);
        shader.setUniform("depthTexture",1);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL30.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL30.GL_TEXTURE_2D, depthTexture);
        shader.setUniforms(camera, planet);
        renderer.renderQuad();
        shader.unbind();
    }

    public int getOutputTexture(){
        return renderer.getOutputTexture();
    }

    public void cleanUp(){
        renderer.cleanUp();
        shader.cleanUp();
    }
}
