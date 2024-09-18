package postProcessing;

import bloom.CombineShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class EmptyFilter {
    private ImageRenderer renderer;
    private EmptyShader shader;

    public EmptyFilter(){
        try {
            shader = new EmptyShader();
            shader.start();
            shader.connectTextureUnits();
            shader.unbind();
            renderer = new ImageRenderer();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getOutputTexture(){
        return renderer.getOutputTexture();
    }

    public void render(int colourTexture){
        shader.start();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
        renderer.renderQuad();
        shader.unbind();
    }

    public void cleanUp(){
        renderer.cleanUp();
        shader.cleanUp();
    }
}
