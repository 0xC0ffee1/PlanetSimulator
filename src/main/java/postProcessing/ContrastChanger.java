package postProcessing;

import org.lwjgl.opengl.GL30;
import renderEngine.Window;

public class ContrastChanger {
    private ImageRenderer renderer;
    private ContrastShader shader;

    public ContrastChanger(int width, int height, Window window){
        try {
            shader = new ContrastShader();
            renderer = new ImageRenderer(width, height, window);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void render(int texture){
        shader.start();
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture);
        renderer.renderQuad();
        shader.unbind();
    }

    public int getOutput(){
        return renderer.getOutputTexture();
    }

    public void cleanUp(){
        renderer.cleanUp();
        shader.cleanUp();
    }
}
