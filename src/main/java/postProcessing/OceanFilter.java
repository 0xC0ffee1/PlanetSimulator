package postProcessing;

import atmosphere.AtmosShader;
import models.Planet;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import renderEngine.Camera;
import renderEngine.Window;

public class OceanFilter {
    private ImageRenderer renderer;
    private OceanShader shader;
    private final Camera camera;

    public OceanFilter(int width, int height, Window window, Camera camera) throws Exception{
        shader = new OceanShader();
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
