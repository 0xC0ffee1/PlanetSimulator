package water;

import Misc.DisplayTime;
import Misc.WorldConstants;
import Textures.Texture;
import models.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import postProcessing.Fbo;
import renderEngine.Loader;
import renderEngine.Transformation;

public class UnderWaterRenderer {
    private final Mesh quad;
    private UnderWaterShader uShader;
    private int dudvTexture = new Texture(WorldConstants.RES_DIR + "water/waterDUDV.png").getID();
    private Fbo sceneFbo;
    private Fbo ref;
    public UnderWaterRenderer(Loader loader, Fbo sceneFbo, Fbo ref) throws Exception{
        float[] positions = {-1,1,-1,-1,1,1,1,-1};
        quad = loader.loadToVAO(positions, 2);
        uShader = new UnderWaterShader();
        this.sceneFbo = sceneFbo;
        this.ref = ref;
    }
    public void render(){
        uShader.start();
        GL30.glBindVertexArray(quad.getVaoID());
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glDisable(GL30.GL_DEPTH_TEST);

        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL11.GL_TEXTURE_2D, dudvTexture);
        GL30.glActiveTexture(GL30.GL_TEXTURE1);
        GL30.glBindTexture(GL11.GL_TEXTURE_2D, ref.getDepthTexture());
        Matrix4f tMatrix = Transformation.getTransformation(new Vector2f(0,0), new Vector2f(1,1));
        uShader.setUniform("transformationMatrix", tMatrix);
        uShader.setUniform("dudvMap", 0);
        uShader.setUniform("depthMap", 1);
        uShader.setUniform("time", DisplayTime.getWaveTime());
        GL30.glDrawArrays(GL30.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());

        GL30.glEnable(GL30.GL_DEPTH_TEST);
        GL30.glDisable(GL30.GL_BLEND);
        GL30.glBindVertexArray(0);
        uShader.unbind();
    }
}
